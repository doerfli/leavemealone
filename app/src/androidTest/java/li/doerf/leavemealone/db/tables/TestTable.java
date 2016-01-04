package li.doerf.leavemealone.db.tables;

import android.database.sqlite.SQLiteDatabase;

import li.doerf.leavemealone.db.annotations.Column;
import li.doerf.leavemealone.db.annotations.Table;

/**
 * Created by moo on 29/01/15.
 */
@Table(name = "test_table")
public class TestTable extends TableBase {

    @Column(name = "_id", type = "INTEGER", isPrimaryKey = true, isAutoincrement = true)
    public long id;

    @Column(name = "name", type = "TEXT")
    public String name;

    @Column(name = "android", type = "INTEGER")
    public String android;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long anId) {
        id = anId;
    }

    @Override
    protected TableBase getReferredObject(SQLiteDatabase db, String aReferenceName, Long anId) {
        return null;
    }
}
