package db61b;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TableTest {

    @Test
    public void testDuplicateColumns() {
        try {
        String[] columnTitles = new String[]{"SID", "SID", "Major"};
        Table table = new Table(columnTitles); }
        catch (DBException e){
            System.out.print(e);
        }

    }

    @Test
    public void testGetTitle() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);
        String actual = table.getTitle(1);
        String expected = "Name";
        assertEquals(expected, actual);

        try {
            String actual1 = table.getTitle(3);
            String actual2 = table.getTitle(-1);
        } catch (DBException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testFindColumn() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);
        int actual = table.findColumn("SID");
        int expected = 0;
        assertEquals(expected, actual);

        int actual1 = table.findColumn("LastName");
        int expected1 = -1;
        assertEquals(expected1, actual1);
    }

    @Test
    public void testSize() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);
        int actual = table.size();
        int expected = 0;
        assertEquals(expected, actual);

        String[] values = new String[]{"1", "Alice", "MIMS"};
        table.add(values);
        int actual1 = table.size();
        int expected1 = 1;
        assertEquals(expected1, actual1);

        String[] values1 = new String[]{"2", "Jason", "EECS"};
        table.add(values1);
        int actual2 = table.size();
        int expected2 = 2;
        assertEquals(expected2, actual2);
    }

    @Test
    public void testGet() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);

        String[] values = new String[]{"1", "Alice", "MIMS"};
        table.add(values);
        String[] values1 = new String[]{"2", "Jason", "EECS"};
        table.add(values1);
        String actual = table.get(0, 0);
        String expected = "1";
        assertEquals(expected, actual);

        try {
            String actual1 = table.get(4, 0);
            Assert.assertTrue(false);
        } catch (DBException excp) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testAdd() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);

        try {
            String[] values = new String[]{"1", "Alice", "MIMS", "CS61B"};
            table.add(values);
        } catch (DBException excp) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testReadTable() {

    }

    @Test
    public void testWriteTable() {

    }

    @Test
    public void testPrint() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);
        String[] values = new String[]{"1", "Alice", "MIMS"};
        table.add(values);
        String[] values1 = new String[]{"2", "Jason", "EECS"};
        table.add(values1);
        String[] values2 = new String[]{"2", "Jason", "EECS23"};
        table.add(values2);
        String[] values3 = new String[]{"3", "Cindy", "Japanese"};
        table.add(values3);

        table.print();
    }

    @Test
    public void testPrint2() {
        String[] columnTitles = new String[]{"SID", "Name", "Major"};
        Table table = new Table(columnTitles);
        String[] values = new String[]{"1", "Alice", "MIMS"};
        table.add(values);
        table.print();
    }


}
