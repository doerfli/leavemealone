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
    public Long id;

    @Column(name = "name0", type = "TEXT")
    public String name0;

    @Column(name = "name1", type = "INTEGER")
    public Short name1;

    @Override
    public Long getId() { return id; }

    @Override
    public void setId(Long anId) { id = anId; }

    @Override
    protected TableBase getReference(SQLiteDatabase db, String aReferenceName, Long anId) {
        return null;
    }
}
