package li.doerf.leavemealone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import li.doerf.leavemealone.db.tables.PhoneNumber;

/**
 * Created by moo on 29/01/15.
 */
public class AloneSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alone.db";
    private static final int DATABASE_VERSION = 1;
    private final String LOGTAG = getClass().getSimpleName();

    public AloneSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOGTAG, "Initializing Database: " + DATABASE_NAME);
        new PhoneNumber().createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOGTAG, "Dropping database: " + DATABASE_NAME);
        new PhoneNumber().dropTable(db);
        onCreate( db);
    }
}