package li.doerf.leavemealone.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.db.tables.PhoneNumberSource;
import li.doerf.leavemealone.db.tables.Property;
import li.doerf.leavemealone.util.NotificationHelper;
import li.doerf.leavemealone.util.PhoneNumberHelper;

/**
 * Service to download the K-Tipp Blocklist and store into database.
 *
 * See:
 * https://www.ktipp.ch/service/warnlisten/detail/w/unerwuenschte-oder-laestige-telefonanrufe/
 *
 * Created by pamapa on 15/12/15.
 */
public class KtippBlocklistRetrievalService extends IntentService {
    private final String LOGTAG = getClass().getSimpleName();
    private int myNotificationId;

    public KtippBlocklistRetrievalService() {
        super("KtippBlocklistRetrievalService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOGTAG, "onHandleIntent");
        myNotificationId = 0;

        try {
            myNotificationId = NotificationHelper.showSyncingNotification(getBaseContext());
            doSync();
        } finally {
            NotificationHelper.hideSyncingNotification(getBaseContext(), myNotificationId);
        }
    }



    private void doSync() {
        Context context = getBaseContext();
        SQLiteDatabase db;

        try {
            NotificationHelper.updateSyncingNotification( getBaseContext(), myNotificationId, getString(R.string.notification_sync_initial_page));
            String content = fetchPage(0);

            String sourceDate = extractSubString(content, "Letzte Aktualisierung:", "<");
            Log.d(LOGTAG, "source date: " + sourceDate);

            db = AloneSQLiteHelper.getInstance(context).getReadableDatabase();
            Property item = Property.findByKey(db, "ktipp_source_date");
            if (item != null) {
                if (item.getValue().equals(sourceDate)) {
                    Log.i(LOGTAG, "We already have this version: " + sourceDate);
                    return;
                }
            }

            List<Map<String, String>> result = parsePages(content);
            //Log.d(LOGTAG, "raw result: " + result);

            result = cleanupEntries(context, result);
            //Log.d(LOGTAG, "cleaned result size: " + result.size());
            //Log.d(LOGTAG, "cleaned result: " + result);

            NotificationHelper.updateSyncingNotification( getBaseContext(), myNotificationId, getString(R.string.notification_sync_saving_numbers));

            // db: insert new entries
            db = AloneSQLiteHelper.getInstance(context).getWritableDatabase();
            Property.update(db, "ktipp_source_date", sourceDate);

//            db.beginTransaction();
            PhoneNumberSource source = PhoneNumberSource.update(db, "_ktipp");
            DateTime now = DateTime.now(); // must be same for all
            for (Map<String, String> map : result) {
                // TODO insert/update is very slow when done repeately. Implement a insert method that takes a list of elements to insert
                // and use beginTransaction/endTransaction around the insert, then all insert should be done in one batch which is said to be faster.
                PhoneNumber.update(db, source, map.get("number"), map.get("name"), now);
            }
            // db: remove old entries
            PhoneNumber.deleteOldEntries(db, source, now);
//            db.setTransactionSuccessful();
            // TODO update ui when sync complete
        } catch (IOException e) {
            Log.e(LOGTAG, "caught IOException", e);
        } finally {
//            db.endTransaction();
            Log.d(LOGTAG, "finished");
        }
    }

    private List<Map<String,String>> cleanupEntries(Context context, List<Map<String,String>> in) {
        ArrayList<Map<String,String>> uniq = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Map<String,String> map : in) {
            String n = map.get("number");

            n = PhoneNumberHelper.normalize(context, n);
            map.put("number", n);

            // filter
            if (n.length() < 5) {
                // too dangerous
                //Log.d(LOGTAG, "Skip too small number: " + n);
                continue;
            }
            if (!n.startsWith("+")) {
                // not in international format
                //Log.d(LOGTAG, "Skip unknown format number: " + n);
                continue;
            }
            if (n.length() > 16) {
                // see spec E.164 for international numbers: 15 (including country code) + 1 ("+")
                //Log.d(LOGTAG, "Skip too long number: " + n);
                continue;
            }

            // filter duplicates
            if (!seen.contains(n)) {
                uniq.add(map);
                seen.add(n);
            }
        }
        return uniq;
    }

    private List<Map<String,String>> parsePages(String content) throws IOException {
        ArrayList<Map<String,String>> ret = new ArrayList<>();

        Document doc = Jsoup.parse(content);

        // extract last page
        Elements pages = doc.select("li");
        if (pages == null) throw new IOException("parsePages: last page not found (no li elements)");
        String tmp = pages.last().toString();
        String strMaxPage = extractSubString(tmp, "ajaxPagerWarnlisteLoadIndex(", ")");
        int lastPage = Integer.parseInt(strMaxPage);
        //Log.d(LOGTAG, "lastPage: " + lastPage);

        // handle first page
        ret.addAll(parsePage(doc));

        // handle remaining pages
        for (int p = 1; p <= lastPage; p++) {
            String notifyText = getString(R.string.notification_sync_subtitle_prefix,
                    p,
                    lastPage);
            NotificationHelper.updateSyncingNotification( getBaseContext(), myNotificationId, notifyText);
            content = fetchPage(p);
            doc = Jsoup.parse(content);
            ret.addAll(parsePage(doc));
        }

        return ret;
    }

    private List<Map<String,String>> parsePage(Document doc) throws IOException {
        ArrayList<Map<String,String>> ret = new ArrayList<>();
        Elements sections = doc.select("section");
        if (sections == null) throw new IOException("parsePage: no sections found");
        for (Element section : sections) {
            List<String> numbers = extractNumbers(section.select("strong").text());
            String name = extractName(section.select("p").text());
            //Log.d(LOGTAG, "name: " + name + " numbers: " + numbers);
            for (String number : numbers) {
                HashMap<String,String> map = new HashMap<>();
                map.put("number", number);
                map.put("name", name);
                ret.add(map);
            }
        }
        return ret;
    }

    private String extractName(String s)  {
        s = s.replaceAll("\n", "").replaceAll("\r", "");
        s = s.replaceAll("<[^>]*>", " "); // remove tags
        s = s.replaceAll("&amp", "&");
        s = s.replaceAll("  ", " ");
        s = s.trim();
        if (s.startsWith("Firma: ")) {
            s = s.substring(7);
        }
        if (s.length() <= 200) return s;
        else return s.substring(0, 200 - 3) + "...";
    }

    private List<String> extractNumbers(final String str) {
        ArrayList<String> ret = new ArrayList<>();
        //Log.d(LOGTAG, "extractNumbers: " + str);
        String[] arr = str.split("und|oder|sowie|auch|,|;");
        for (String a : arr) {
            if (a.contains("/")) {
                ret.addAll(extractSlashedNumbers(a));
            }
            else if (a.contains("bis")) {
                ret.addAll(extractRangeNumbers(a));
            }
            else {
                a = extractNumber(a);
                if (!"".equals(a)) ret.add(a);
            }
        }
        return ret;
    }

    // 021 558 73 91/92/93/94/95
    private List<String> extractSlashedNumbers(final String str) {
        //Log.d(LOGTAG, "extractSlashedNumbers: " + str);
        ArrayList<String> ret = new ArrayList<>();
        String[] arr = str.split("/");
        String a0 = extractNumber(arr[0]);
        if (! "".equals(a0)) {
            //Log.d(LOGTAG, "a0: " + a0);
            ret.add(a0);
            String base = a0.substring(0, a0.length()-2);
            //Log.d(LOGTAG, "base: " + base);
            for (int i = 1; i < arr.length; i++) {
                String ax = extractNumber(arr[i]);
                if (! "".equals(ax)) {
                    ax = extractNumber(base + ax);
                    //Log.d(LOGTAG, "ax: " + ax);
                    ret.add(ax);
                }
            }
        }
        return ret;
    }

    // 044 400 00 00 bis 044 400 00 19
    private List<String> extractRangeNumbers(final String str) {
        //Log.d(LOGTAG, "extractRangeNumbers: " + str);
        ArrayList<String> ret = new ArrayList<>();
        String[] arr = str.split("bis");
        String s = extractNumber(arr[0]);
        String e = extractNumber(arr[1]);
        int start = Integer.parseInt(s.substring(s.length() - 4, s.length()));
        int end   = Integer.parseInt(e.substring(e.length() - 4, e.length()));
        //Log.d(LOGTAG, "start: " + start + " end: " + end);
        for (int i = start; i <= end; i++) {
            String a = s.substring(0, s.length() - 4) + String.format("%04d", i);
            //Log.d(LOGTAG, "a: " + a);
            ret.add(a);
        }
        return ret;
    }

    // 044 400 00 00 -> 0444000000
    private String extractNumber(String str) {
        str = str.replaceAll("[^0-9\\+]", "");
        return str;
    }

    private String fetchPage(final int page_nr) throws IOException {
        StringBuilder ret = new StringBuilder();
        try {
            URL url = new URL("https://www.ktipp.ch/service/warnlisten/detail/?warnliste_id=7&ajax=ajax-search-form&page="+Integer.toString(page_nr));
            Log.d(LOGTAG, "fetchPage: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            // allow both GZip and Deflate (ZLib) encodings
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            // obtain the encoding returned by the server
            String encoding = connection.getContentEncoding();

            InputStream is;
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                is = new GZIPInputStream(connection.getInputStream());
            }
            else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
                is = new InflaterInputStream(connection.getInputStream(), new Inflater(true));
            }
            else {
                is = connection.getInputStream();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = in.readLine()) != null) {
                ret.append(str);
            }
            in.close();
        } catch(MalformedURLException e) {
            Log.e(LOGTAG, "caught MalformedURLException", e);
        }
        return ret.toString();
    }

    private String extractSubString(final String sb, final String strStart, final String strEnd) throws IOException {
        int s = sb.indexOf(strStart);
        if (s == -1) throw new IOException("extractSubString: strStart (" + strStart + ") not found.)");
        s += strStart.length();
        int e = sb.indexOf(strEnd, s);
        if (e == -1) throw new IOException("extractSubString: strEnd (" + strEnd + ") not found.)");
        return sb.substring(s, e).trim();
    }
}
