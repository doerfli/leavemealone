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
@Table(name = "property")
public class Property extends TableBase {
    @Column(name = "_id", type = "INTEGER", isPrimaryKey = true, isAutoincrement = true)
    private Long id;
    @Column(name = "key", type = "TEXT")
    private String key;
    @Column(name = "value", type = "TEXT")
    private String value;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value;}

    public static Property create(String aKey, String aValue) {
        Property property = new Property();
        property.setKey(aKey);
        property.setValue(aValue);
        return property;
    }

    public static Property create(SQLiteDatabase db, Cursor aCursor) {
        Property item = new Property();
        Map<String, Field> columnNamesAndFields = item.getColumnNamesWithFields();
        item.fillFromCursor(db, aCursor, columnNamesAndFields);
        return item;
    }

    public static Property update(SQLiteDatabase db, String aKey, String aValue) {
        Property item = Property.findByKey(db, aKey);
        if (item != null) {
            // key is already ok
            item.setValue(aValue);
            item.update(db);
        } else {
            item = Property.create(aKey, aValue);
            item.insert(db);
        }
        return item;
    }

    public static Property findByKey(SQLiteDatabase db, String aKey) {
        Cursor c = null;

        try {
            Property item = new Property();
            c = db.query(
                    item.getTableName(),
                    item.getColumnNames(),
                    "key = ?",
                    new String[]{aKey},
                    null,
                    null,
                    "key");

            if (c.moveToFirst()) {
                return Property.create(db, c);
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
        return null;
    }
}
