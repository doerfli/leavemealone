package li.doerf.leavemealone.db.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Strings;

import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import li.doerf.leavemealone.db.annotations.Column;
import li.doerf.leavemealone.db.annotations.Table;

/**
 * Created by moo on 29/01/15.
 */
@Table(name = "phone_number")
public class PhoneNumber extends TableBase {
    @Column(name = "_id", type = "INTEGER", isPrimaryKey = true, isAutoincrement = true)
    private Long id;
    @Column(name = "source", type = "INTEGER", isReference = true)
    private PhoneNumberSource source;
    @Column(name = "number", type = "TEXT")
    private String number;
    @Column(name = "name", type = "TEXT")
    private String name;
    @Column(name = "date_modified", type = "INTEGER")
    private Long dateModified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PhoneNumberSource getSource() {
        return source;
    }
    public void setSource(PhoneNumberSource source) {
        this.source = source;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public Long getDateModified() {
        return dateModified;
    }
    public void setDateModified(DateTime aLastModified) {
        this.dateModified = aLastModified != null ? aLastModified.getMillis() : null;
    }

    public static PhoneNumber create(PhoneNumberSource aSource, String aNumber, String aName, DateTime aDateModified) {
        PhoneNumber number = new PhoneNumber();
        number.setSource(aSource);
        number.setNumber(aNumber);
        number.setName(aName);
        number.setDateModified(aDateModified);
        return number;
    }

    public static PhoneNumber create(SQLiteDatabase db, Cursor aCursor) {
        PhoneNumber item = new PhoneNumber();
        Map<String, Field> columnNamesAndFields = item.getColumnNamesWithFields();
        item.fillFromCursor(db, aCursor, columnNamesAndFields);
        return item;
    }

    public static PhoneNumber update(SQLiteDatabase db, PhoneNumberSource aSource, String aNumber, String aName, DateTime aDateModified) {
        PhoneNumber item = PhoneNumber.findBySourceAndNumber(db, aSource, aNumber);
        if (item != null) {
            // source and number are already ok
            item.setName(aName);
            item.setDateModified(aDateModified);
            item.update(db);
        } else {
            item = PhoneNumber.create(aSource, aNumber, aName, aDateModified);
            item.insert(db);
        }
        return item;
    }

    public static void deleteOldEntries(SQLiteDatabase db, PhoneNumberSource aSource, DateTime aNow) {
        PhoneNumber item = new PhoneNumber();
        db.delete(
                item.getTableName(),
                "source = ? AND date_modified != ?",
                new String[]{aSource.getId().toString(), Long.toString(aNow.getMillis())});
    }

    public static Cursor listAll(SQLiteDatabase db) {
        PhoneNumber item = new PhoneNumber();
        return db.query(
                item.getTableName(),
                item.getColumnNames(),
                null,
                null,
                null,
                null,
                "number");
    }

    public static Cursor listAllExcept(SQLiteDatabase db, String[] aExcludedSources) {
        String whereString = null;
        String[] whereParams = null;

        if (aExcludedSources.length > 0) {
            whereParams = new String[aExcludedSources.length];

            for (int i = 0; i < aExcludedSources.length; i++) {
                PhoneNumberSource source = PhoneNumberSource.findByName(db, aExcludedSources[i]);
                String sourceId = "";
                if (source != null) {
                    sourceId = source.getId().toString();
                }
                whereParams[i] = sourceId;
            }

            String q = Strings.repeat("?,", aExcludedSources.length);
            q = q.substring(0, q.length() - 1);
            whereString = "source NOT IN (" + q + ")";
        }

        PhoneNumber item = new PhoneNumber();
        return db.query(
                item.getTableName(),
                item.getColumnNames(),
                whereString,
                whereParams,
                null,
                null,
                "number");
    }

    public static PhoneNumber findByNumber(SQLiteDatabase db, String aNumber) {
        Cursor c = null;

        try {
            PhoneNumber item = new PhoneNumber();
            c = db.query(
                    item.getTableName(),
                    item.getColumnNames(),
                    "number LIKE ?",
                    new String[]{aNumber + "%"},
                    null,
                    null,
                    "number");

            if (c.moveToFirst()) {
                return PhoneNumber.create(db, c);
            }

            return null;
        } finally {
            if ( c != null ) {
                c.close();
            }
        }
    }

    public static PhoneNumber findBySourceAndNumber(SQLiteDatabase db, PhoneNumberSource aSource, String aNumber) {
        Cursor c = null;

        try {
            PhoneNumber item = new PhoneNumber();
            c = db.query(
                    item.getTableName(),
                    item.getColumnNames(),
                    "source = ? AND number = ?",
                    new String[]{aSource.getId().toString(), aNumber},
                    null,
                    null,
                    "number");

            if (c.moveToFirst()) {
                return PhoneNumber.create(db, c);
            }

            return null;
        } finally {
            if ( c != null ) {
                c.close();
            }
        }
    }

    private static Map<Long, PhoneNumberSource> cache = new HashMap<>();

    @Override
    protected TableBase getReference(SQLiteDatabase db, String aReferenceName, Long anId) {
        Cursor c = null;
        try {
            if ("source".equals(aReferenceName)) {
                PhoneNumberSource item = cache.get(anId);
                if (item != null) {
                    return item;
                }
                item = new PhoneNumberSource();
                c = db.query(
                        item.getTableName(),
                        item.getColumnNames(),
                        "_id = ?",
                        new String[]{anId.toString()},
                        null,
                        null,
                        "_id");

                if (c.moveToFirst()) {
                    item = PhoneNumberSource.create(c);
                    cache.put(anId, item);
                    return item;
                }
            }
            return null;
        } finally {
            if ( c != null ) {
                c.close();
            }
        }
    }
}
