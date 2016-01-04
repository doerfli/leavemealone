package li.doerf.leavemealone.db.tables;

import android.database.sqlite.SQLiteDatabase;

import li.doerf.leavemealone.db.annotations.Column;

/**
 * Created by moo on 29/01/15.
 */
public class TestTableInvalid extends TableBase {
    @Column(name = "_id", type = "INTEGER")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return 0L;
    }

    @Override
    public void setId(Long anId) { }

    @Override
    protected TableBase getReference(SQLiteDatabase db, String aReferenceName, Long anId) {
        return null;
    }
}
