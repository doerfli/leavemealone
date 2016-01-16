package li.doerf.leavemealone.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.db.tables.PhoneNumberSource;
import li.doerf.leavemealone.db.tables.Property;

/**
 * Created by moo on 29/01/15.
 */
public class AloneSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alone.db";
    private static final int DATABASE_VERSION = 3;
    private static AloneSQLiteHelper myInstance;
    private static Context myContext;
    private final String LOGTAG = getClass().getSimpleName();

    public static AloneSQLiteHelper getInstance(Context aContext) {
        if (myInstance == null) {
            myInstance = new AloneSQLiteHelper(aContext);
            myContext = aContext;
        }
        return myInstance;
    }

    private AloneSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOGTAG, "Initializing Database: " + DATABASE_NAME);
        new PhoneNumber().createTable(db);
        new PhoneNumberSource().createTable(db);
        new Property().createTable(db);
        Log.v(LOGTAG, "done initializing");

        Cursor numbers = PhoneNumber.listAll(db);
        if ( ! numbers.moveToNext()) {
            PhoneNumberSource source = PhoneNumberSource.update(db, "manual");
            PhoneNumber number1 = PhoneNumber.create(source, "+41999999991", myContext.getString(R.string.sample_entry_add_new), DateTime.now());
            number1.insert(db);
            PhoneNumber number2 = PhoneNumber.create(source, "+41999999992", myContext.getString(R.string.sample_entry_remove), DateTime.now());
            number2.insert(db);
            PhoneNumber number3 = PhoneNumber.create(source, "+41999999993", myContext.getString(R.string.sample_entry_sync), DateTime.now());
            number3.insert(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOGTAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        Log.w(LOGTAG, "Dropping database: " + DATABASE_NAME);
        new PhoneNumber().dropTable(db);
        new PhoneNumberSource().dropTable(db);
        new Property().dropTable(db);
        Log.v(LOGTAG, "Done dropping");
        onCreate(db);
    }
}
