package li.doerf.leavemealone.db.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.Map;

import li.doerf.leavemealone.db.annotations.Column;
import li.doerf.leavemealone.db.annotations.Table;

/**
 * Created by pamapa on 04.01.16.
 */
@Table(name = "phone_number_source")
public class PhoneNumberSource extends TableBase {
    @Column(name = "_id", type = "INTEGER", isPrimaryKey = true, isAutoincrement = true)
    private Long id;
    @Column(name = "name", type = "TEXT")
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private static PhoneNumberSource create(String aName) {
        PhoneNumberSource source = new PhoneNumberSource();
        source.setName(aName);
        return source;
    }

    public static PhoneNumberSource create(Cursor aCursor) {
        PhoneNumberSource item = new PhoneNumberSource();
        Map<String, Field> columnNamesAndFields = item.getColumnNamesWithFields();
        item.fillFromCursor(null, aCursor, columnNamesAndFields);
        return item;
    }

    public static PhoneNumberSource update(SQLiteDatabase db, String aName) {
        PhoneNumberSource item = PhoneNumberSource.findByName(db, aName);
        if (item == null) {
            item = PhoneNumberSource.create(aName);
            item.insert(db);
        }
        return item;
    }

    public static PhoneNumberSource findByName(SQLiteDatabase db, String aName) {
        Cursor c = null;

        try {
            PhoneNumberSource item = new PhoneNumberSource();
            c = db.query(
                    item.getTableName(),
                    item.getColumnNames(),
                    "name = ?",
                    new String[]{aName},
                    null,
                    null,
                    "name");

            if (c.moveToFirst()) {
                return PhoneNumberSource.create(c);
            }
            return null;
        } finally {
            if ( c != null ) {
                c.close();
            }
        }
    }

    @Override
    protected TableBase getReference(SQLiteDatabase db, String aReferenceName, Long anId) {
        // no reference used in this class
        return null;
    }
}
