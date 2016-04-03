package li.doerf.leavemealone.db.tables;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by moo on 29/01/15.
 */
public class TableBaseTest extends TestCase {

    public void testGetTableName()
    {
        TestTable table = new TestTable();
        assertEquals("test_table", table.getTableName());
    }

    public void testGetTableNameInvalid()
    {
        TestTableInvalid table = new TestTableInvalid();

        try
        {
            table.getTableName();
            Assert.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    public void testGetColumns()
    {
        TestTable table = new TestTable();
        String[] columns = table.getColumnNames();

        Assert.assertEquals("_id", columns[0]);
        Assert.assertEquals("name1", columns[1]);
        Assert.assertEquals("name0", columns[2]);
    }

    /*
    public void testFieldTypes()
    {
        TestTable table = new TestTable();

        table.name0 = "hello0";
        table.name1 = 1;

        try {
            ContentValues values = table.getFilledContentValues();

            for (String key : values.keySet()) {
                String myKey = key;
                Object myValue = values.get(key);
            }
        } catch (IllegalAccessException e) {
        }
    }
    */
}
