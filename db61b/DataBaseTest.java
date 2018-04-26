package db61b;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/** The JUnit tests for the database class.
 *  @author Alice
 */

public class DataBaseTest {

    @Test
    public void testGet() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table testTable = new Table(columnTitles);
        HashMap<String, Table> dataBase = new HashMap<String, Table>();
        dataBase.put("test", testTable);
        assertEquals(testTable, dataBase.get("test"));

        String[] columnTitles1 = new String[]{"ID", "Name"};
        Table testTable1 = new Table(columnTitles1);
        try {
            dataBase.put("", testTable1);
            Assert.assertTrue(false);
        } catch (DBException excp) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testPut() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);
        HashMap<String, Table> dataBase = new HashMap<String, Table>();
    }

}
