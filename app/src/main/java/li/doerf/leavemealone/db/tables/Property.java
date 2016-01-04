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
    @Column(name = "name", type = "TEXT")
    private String name;
    @Column(name = "key", type = "TEXT")
    private String key;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public String getName() { return name; }
    public void setName(String name) { this.name = name;}

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public static Property create(String aName, String aKey) {
        Property property = new Property();
        property.setName(aName);
        property.setKey(aKey);
        return property;
    }

    public static Property create(SQLiteDatabase db, Cursor aCursor) {
        Property item = new Property();
        Map<String, Field> columnNamesAndFields = item.getColumnNamesWithFields();
        item.fillFromCursor(db, aCursor, columnNamesAndFields);
        return item;
    }

    public static Property update(SQLiteDatabase db, String aName, String aKey) {
        Property item = Property.findByName(db, aName);
        // TODO: more efficient via db update?
        if (item != null) {
            item.delete(db);
        }
        item = Property.create(aName, aKey);
        item.insert(db);
        return item;
    }

    public static Property findByName(SQLiteDatabase db, String aName) {
        Property item = new Property();
        Cursor c = db.query(
                item.getTableName(),
                item.getColumnNames(),
                "name = ?",
                new String[] {aName},
                null,
                null,
                "name");

        if (c.moveToFirst()) {
            return Property.create(db, c);
        }

        return null;
    }

    @Override
    protected TableBase getReference(SQLiteDatabase db, String aReferenceName, Long anId) {
        return null;
    }
}
