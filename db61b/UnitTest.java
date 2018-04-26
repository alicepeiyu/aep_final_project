package db61b;

import org.junit.Test;
import ucb.junit.textui;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** The suite of all JUnit tests for the qirkat package.
 *  @author P. N. Hilfinger
 */
public class UnitTest {

    @Test
    public void testGetFrom() {
        List<String> columnTitles = new ArrayList<>();
        columnTitles.add("ID");
        columnTitles.add("Name");
        columnTitles.add("Major");
        Table table = new Table(columnTitles);
        String[] values = new String[]{"1", "Alice", "MIMS"};
        table.add(values);
        String[] values1 = new String[]{"2", "Jason", "EECS"};
        table.add(values1);

        List<String> columnTitles1 = new ArrayList<>();
        columnTitles1.add("SID");
        columnTitles1.add("Name");
        columnTitles1.add("Nationality");
        Table table1 = new Table(columnTitles);
        String[] values3 = new String[]{"2", "Jason", "USA"};
        table1.add(values3);

        String name = "ID";
        Column c = new Column(name, table);
        String a = c.getFrom(0);
        String b = "1";
        assertEquals(b, a);

        String name1 = "Name";
        Column c1 = new Column(name1, table, table1);
        String d = c1.getFrom(1, 0);
        String e = "Jason";
        assertEquals(e, d);
    }


    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(TableTest.class);
    }

}
