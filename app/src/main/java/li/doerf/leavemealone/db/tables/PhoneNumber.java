package li.doerf.leavemealone.db.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import java.lang.reflect.Field;
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
    @Column(name = "source", type = "TEXT")
    private String source;
    @Column(name = "number", type = "TEXT")
    private String number;
    @Column(name = "name", type = "TEXT")
    private String name;
    @Column(name = "last_modified", type = "INTEGER")
    private Long lastModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
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

    public void setName(String name) {
        this.name = name;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public static PhoneNumber create( String aSource, String aNumber, String aName, DateTime aLastModified) {
        PhoneNumber number = new PhoneNumber();
        number.setSource( aSource);
        number.setNumber(aNumber);
        number.setName(aName);
        number.setLastModified(aLastModified != null ? aLastModified.getMillis() : null);
        return number;
    }

    public static PhoneNumber create(Cursor aCursor) {
        PhoneNumber item = new PhoneNumber();
        Map<String, Field> columnNamesAndFields = item.getColumnNamesWithFields();
        item.fillFromCursor(aCursor, columnNamesAndFields);
        return item;
    }

    public static Cursor listAll(SQLiteDatabase myDb) {
        PhoneNumber item = new PhoneNumber();
        return myDb.query(
                item.getTableName(),
                item.getColumnNames(),
                null,
                null,
                null,
                null,
                "number");
    }

    public static PhoneNumber findByNumber(SQLiteDatabase myDb, String number) {
        PhoneNumber item = new PhoneNumber();
        Cursor c = myDb.query(
                item.getTableName(),
                item.getColumnNames(),
                "number = ?",
                new String[] { number },
                null,
                null,
                "number");

        if ( c.moveToFirst() ) {
            return PhoneNumber.create(c);
        }

        return null;
    }

}
