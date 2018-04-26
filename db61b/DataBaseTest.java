package db61b;

//import org.junit.Assert;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertEquals;

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
        Assert.assertEquals(testTable, dataBase.get("test"));

        String[] columnTitles1 = new String[]{"ID", "Name"};
        Table testTable1 = new Table(columnTitles1);
        try {
            dataBase.put("", testTable1);
            assertTrue(false);
        } catch (DBException excp) {
            assertTrue(true);
        }
    }

    @Test
    public void testPut() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);
        HashMap<String, Table> dataBase = new HashMap<String, Table>();
    }

}
