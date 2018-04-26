package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static db61b.Utils.error;

/**
 * A single table in a database.
 *
 * @author P. N. Hilfinger
 */
class Table {
    /**
     * A new Table whose columns are given by COLUMNTITLES, which may
     * not contain duplicate names.
     */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                            columnTitles[i]);
                }
            }
        }
        _titles = columnTitles;
        _columns = new ValueList[_rowSize];
        for (int i = 0; i < _rowSize; i++) {
            ValueList a = new ValueList();
            _columns[i] = a;
        }
    }

    /**
     * A new Table whose columns are give by COLUMNTITLES.
     */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /**
     * Return the number of columns in this table.
     */
    public int columns() {
        return _rowSize;
    }

    /**
     * Return the title of the Kth column.  Requires 0 <= K < columns().
     */
    public String getTitle(int k) {
        if (k < 0) {
            throw error("k must >= 0");
        }
        if (k >= columns()) {
            throw error("k must < columns()");
        }
        return _titles[k];
    }

    /**
     * Return the number of the column whose title is TITLE, or -1 if
     * there isn't one.
     */
    public int findColumn(String title) {
        for (int i = 0; i < _rowSize; i++) {
            if (_titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Return the number of rows in this table.
     */
    public int size() {
        return _size;
    }

    /**
     * Return the value of column number COL (0 <= COL < columns())
     * of record number ROW (0 <= ROW < size()).
     */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /**
     * Add a new row whose column values are VALUES to me if no equal
     * row already exists.  Return true if anything was added,
     * false otherwise.
     */
    public boolean add(String[] values) {
        try {
            if (values.length != _rowSize) {
                return false;
            }
            for (int i = 0; i < _size; i++) {
                for (int j = 0; j < _rowSize; j++) {
                    if (!values[j].equals(_columns[j].get(i))) {
                        break;
                    }
                    if (j == _rowSize - 1) {
                        return false;
                    }
                }
            }
        } catch (DBException e) {
            throw error("invalid column size");
        }
        for (int k = 0; k < _rowSize; k++) {
            _columns[k].add(values[k]);
        }
        _size++;
        return true;
    }

    /**
     * Add a new row whose column values are extracted by COLUMNS from
     * the rows indexed by ROWS, if no equal row already exists.
     * Return true if anything was added, false otherwise. See
     * Column.getFrom(Integer...) for a description of how Columns
     * extract values.
     */
    public boolean add(List<Column> columns, Integer... rows) {
        if (columns.size() != _rowSize || columns.size() != rows.length) {
            return false;
        }
        List<Integer> rowsList = Arrays.asList(rows);
        String[] values = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getName();
            String colVal = columns.get(i).getFrom(rowsList.get(i));
            int colIndex = this.findColumn(columnName);
            if (colIndex < 0) {
                throw error("invalid column name");
            }
            values[colIndex] = colVal;
        }
        this.add(values);
        return true;
    }

    /**
     * Read the contents of the file NAME.db, and return as a Table.
     * Format errors in the .db file cause a DBException.
     */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            if (columnNames.length == 0) {
                throw new DBException();
            }
            for (int i = 0; i < columnNames.length; i++) {
                if (columnNames[i] == null || columnNames[i].trim().isEmpty()) {
                    throw new DBException();
                }
            }
            table = new Table(columnNames);
            String value;

            while ((value = input.readLine()) != null) {
                String[] valueList = value.split(",");
                if (valueList.length == 0
                        || valueList.length != columnNames.length) {
                    throw new DBException();
                }
                table.add(valueList);
            }
        } catch (DBException e) {
            throw error("format error in %s.db", name);
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /**
     * Write the contents of TABLE into the file NAME.db. Any I/O errors
     * cause a DBException.
     */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = ",";
            output = new PrintStream(name + ".db");
            for (int i = 0; i < _titles.length; i++) {
                output.print(_titles[i]);
                if (i != _titles.length - 1) {
                    output.print(sep);
                }

            }
            output.println();

            for (int i = 0; i < _size; i++) {
                for (int j = 0; j < _rowSize; j++) {
                    output.print(_columns[j].get(i));
                    if (j != _rowSize - 1) {
                        output.print(sep);
                    }

                }
                if (i != _size - 1) {
                    output.println();
                }
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.flush();
                output.close();
            }
        }
    }

    /**
     * Print my contents on the standard output, separated by spaces
     * and indented by two spaces.
     */
    void print() {
        Set<Integer> set = new HashSet<>();
        _index.clear();
        while (_index.size() < _size) {
            int temp = -1;
            for (int i = 0; i < _size; i++) {
                if (!set.contains(i)) {
                    temp = i;
                    break;
                }
            }
            int next = temp + 1;
            for (int k = next; k < _size; k++) {
                if (set.contains(k)) {
                    continue;
                }
                if (compareRows(temp, k) > 0) {
                    temp = k;
                }
            }
            set.add(temp);
            _index.add(temp);
        }
        for (int j = 0; j < _index.size(); j++) {
            System.out.print("  ");
            for (int i = 0; i < _rowSize; i++) {
                System.out.print(_columns[i].get(_index.get(j)));

                if (i < _rowSize - 1) {
                    System.out.print(" ");
                }

                if (i == _rowSize - 1) {
                    System.out.println();
                }
            }
        }
    }

    /**
     * Return a new Table whose columns are COLUMNNAMES, selected from
     * rows of this table that satisfy CONDITIONS.
     */
    Table select(List<String> columnNames, List<Condition> conditions) {
        for (String col : columnNames) {
            if (findColumn(col) == -1) {
                throw error("missing column names");
            }
        }
        Table result = new Table(columnNames);
        if (conditions == null || conditions.isEmpty()) {
            for (int i = 0; i < size(); i++) {
                List<String> newTableRow = new ArrayList<>();
                for (int j = 0; j < columnNames.size(); j++) {
                    int correspondingIndex = findColumn(columnNames.get(j));
                    String columnVal = _columns[correspondingIndex].get(i);
                    newTableRow.add(columnVal);
                }
                String[] newTableRowArr = new String[newTableRow.size()];
                newTableRowArr = newTableRow.toArray(newTableRowArr);
                result.add(newTableRowArr);
            }
        } else {
            for (int i = 0; i < _size; i++) {
                if (Condition.test(conditions, i)) {
                    List<String> newTableRow = new ArrayList<>();
                    for (int j = 0; j < columnNames.size(); j++) {
                        int correspondingIndex = findColumn(columnNames.get(j));
                        String columnVal = _columns[correspondingIndex].get(i);
                        newTableRow.add(columnVal);
                    }
                    String[] newTableRowArr = new String[newTableRow.size()];
                    newTableRowArr = newTableRow.toArray(newTableRowArr);
                    result.add(newTableRowArr);
                }
            }
        }
        return result;
    }

    /**
     * Return a new Table whose columns are COLUMNNAMES, selected
     * from pairs of rows from this table and from TABLE2 that match
     * on all columns with identical names and satisfy CONDITIONS.
     */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        for (String col : columnNames) {
            if (findColumn(col) == -1 && table2.findColumn(col) == -1) {
                throw error("missing column names");
            }
        }
        List<String> thisTableColumnNames =
                new ArrayList<>(Arrays.asList(_titles));
        List<String> table2ColumnNames =
                new ArrayList<>(Arrays.asList(table2._titles));
        List<String> commonColumnNames =
                findCommonColumnNames(thisTableColumnNames, table2ColumnNames);
        List<String> joinedTableColumnName =
                new ArrayList<>();
        joinedTableColumnName.addAll(thisTableColumnNames);
        for (String table2Column : table2ColumnNames) {
            if (!commonColumnNames.contains(table2Column)) {
                joinedTableColumnName.add(table2Column);
            }
        }
        Table joinedTable = new Table(joinedTableColumnName);
        if (commonColumnNames.isEmpty()) {
            List<Column> listColumns = new ArrayList<>();


            for (int i = 0; i < thisTableColumnNames.size(); i++) {
                Column col = new Column(thisTableColumnNames.get(i), this);
                listColumns.add(col);
            }
            for (int k = 0; k < table2ColumnNames.size(); k++) {
                Column col = new Column(table2ColumnNames.get(k), table2);
                listColumns.add(col);
            }

            for (int thisTableRow = 0;
                 thisTableRow < this._size; thisTableRow++) {
                for (int table2Row = 0;
                     table2Row < table2._size; table2Row++) {
                    List<Integer> rowList = new ArrayList<>();
                    for (int thisTableColSize = 0;
                         thisTableColSize < thisTableColumnNames.size();
                         thisTableColSize++) {
                        rowList.add(thisTableRow);
                    }
                    for (int table2ColSize = 0;
                         table2ColSize < table2ColumnNames.size();
                         table2ColSize++) {
                        rowList.add(table2Row);
                    }
                    joinedTable.add(listColumns,
                            rowList.toArray(new Integer[rowList.size()]));
                }
            }
        } else {

            List<Column> thisTableCommonColumns = new ArrayList<>();
            List<Column> table2CommonColumns = new ArrayList<>();
            for (int commonNumColumns = 0;
                 commonNumColumns < commonColumnNames.size();
                 commonNumColumns++) {
                Column col1 =
                        new Column(commonColumnNames.get(commonNumColumns),
                                this);
                Column col2 =
                        new Column(commonColumnNames.get(commonNumColumns),
                                table2);
                thisTableCommonColumns.add(col1);
                table2CommonColumns.add(col2);
            }

            for (int thisTableRow = 0;
                 thisTableRow < this._size; thisTableRow++) {
                for (int table2Row = 0;
                     table2Row < table2._size; table2Row++) {
                    if (equijoin(thisTableCommonColumns,
                            table2CommonColumns, thisTableRow, table2Row)) {
                        List<Column> listColumns = new ArrayList<>();
                        List<Integer> rowList = new ArrayList<>();
                        for (int i = 0; i < thisTableColumnNames.size(); i++) {
                            Column col =
                                    new Column(thisTableColumnNames.get(i),
                                            this);
                            listColumns.add(col);
                            rowList.add(thisTableRow);
                        }
                        for (int k = 0; k < table2ColumnNames.size(); k++) {
                            if (!commonColumnNames.contains
                                    (table2ColumnNames.get(k))) {
                                Column col =
                                        new Column(table2ColumnNames.get(k),
                                                table2);
                                listColumns.add(col);
                                rowList.add(table2Row);
                            }
                        }
                        joinedTable.add(listColumns,
                                rowList.toArray(new Integer[rowList.size()]));
                    }
                }
            }
        }

        List<Condition> modifiedConditions = new ArrayList<>();
        for (int i = 0; i < conditions.size(); i++) {
            Condition oldCondition = conditions.get(i);
            if (oldCondition.getCol2() == null) {
                Condition c =
                    new Condition(new Column(oldCondition.getCol1().getName(),
                                joinedTable), oldCondition.getRelation(),
                                oldCondition.getVal2());
                modifiedConditions.add(c);
            } else {
                Condition c =
                    new Condition(new Column(oldCondition.getCol1().getName(),
                                joinedTable),
                        oldCondition.getRelation(),
                        new Column(oldCondition.
                                getCol2().getName(),
                                joinedTable));
                modifiedConditions.add(c);
            }

        }

        Table finalResult = new Table(columnNames);
        for (int i = 0; i < joinedTable.size(); i++) {
            if (Condition.test(modifiedConditions, i)) {
                List<String> newTableRow = new ArrayList<>();
                for (int j = 0; j < columnNames.size(); j++) {
                    int correspondingIndex =
                            joinedTable.findColumn(columnNames.get(j));
                    String columnVal =
                            joinedTable._columns[correspondingIndex].get(i);
                    newTableRow.add(columnVal);
                }
                String[] newTableRowArr = new String[newTableRow.size()];
                newTableRowArr = newTableRow.toArray(newTableRowArr);
                finalResult.add(newTableRowArr);
            }
        }
        return finalResult;
    }

    private List<String>
        findCommonColumnNames(List<String> columnNames1,
                          List<String> columnNames2) {
        List<String> commonColumnNames = new ArrayList<>();
        for (int i = 0; i < columnNames1.size(); i++) {
            for (int k = 0; k < columnNames2.size(); k++) {
                if (columnNames1.get(i).equals(columnNames2.get(k))) {
                    commonColumnNames.add(columnNames1.get(i));
                }
            }
        }
        return commonColumnNames;
    }

    /**
     * Return <0, 0, or >0 depending on whether the row formed from
     * the elements _columns[0].get(K0), _columns[1].get(K0), ...
     * is less than, equal to, or greater than that formed from elememts
     * _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     * the _index.
     */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /**
     * Return true if the columns COMMON1 from ROW1 and COMMON2 from
     * ROW2 all have identical values.  Assumes that COMMON1 and
     * COMMON2 have the same number of elements and the same names,
     * that the columns in COMMON1 apply to this table, those in
     * COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     * into those tables.
     */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        for (int i = 0; i < common1.size(); i++) {
            if (!common1.get(i).
                    getFrom(row1).equals(common2.get(i).getFrom(row2))) {
                return false;
            }
        }
        return true;
    }

    /**
     * A class that is essentially ArrayList<String>.  For technical reasons,
     * we need to encapsulate ArrayList<String> like this because the
     * underlying design of Java does not properly distinguish between
     * different kinds of ArrayList at runtime (e.g., if you have a
     * variable of type Object that was created from an ArrayList, there is
     * no way to determine in general whether it is an ArrayList<String>,
     * ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     * compiler warnings.  The trick of defining a new type avoids this
     * issue.
     */
    private static class ValueList extends ArrayList<String> {
    }

    /**
     * My column titles.
     */
    private final String[] _titles;
    /**
     * My columns. Row i consists of _columns[k].get(i) for all k.
     */
    private final ValueList[] _columns;

    /**
     * Rows in the database are supposed to be sorted. To do so, we
     * have a list whose kth element is the index in each column
     * of the value of that column for the kth row in lexicographic order.
     * That is, the first row (smallest in lexicographic order)
     * is at position _index.get(0) in _columns[0], _columns[1], ...
     * and the kth row in lexicographic order in at position _index.get(k).
     * When a new row is inserted, insert its index at the appropriate
     * place in this list.
     * (Alternatively, we could simply keep each column in the proper order
     * so that we would not need _index.  But that would mean that inserting
     * a new row would require rearranging _rowSize lists (each list in
     * _columns) rather than just one.
     */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /**
     * My number of rows (redundant, but convenient).
     */
    private int _size;
    /**
     * My number of columns (redundant, but convenient).
     */
    private final int _rowSize;
}
