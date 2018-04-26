package db61b;

import java.util.List;

import static db61b.Utils.error;

/**
 * Represents a single 'where' condition in a 'select' command.
 *
 * @author Alice Yang
 */
class Condition {

    /**
     * A Condition representing COL1 RELATION COL2, where COL1 and COL2
     * are column designators. and RELATION is one of the
     * strings "<", ">", "<=", ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, Column col2) {
        if (!relation.matches("[<>!]?=|[<>]")) {
            throw error("no such relation exists");
        }
        _col1 = col1;
        _col2 = col2;
        _relation = relation;
    }

    /**
     * A Condition representing COL1 RELATION 'VAL2', where COL1 is
     * a column designator, VAL2 is a literal value (without the
     * quotes), and RELATION is one of the strings "<", ">", "<=",
     * ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }

    /**
     * Assuming that ROWS are row indices in the respective tables
     * from which my columns are selected, returns the result of
     * performing the test I denote.
     */
    boolean test(Integer... rows) {
        for (int i : rows) {
            if (_val2 == null) {
                int order =
                        _col1.getFrom(i).compareTo(_col2.getFrom(i));
                if (_relation.equals("=")) {
                    if (order != 0) {
                        return false;
                    }
                } else if (_relation.equals(">=")) {
                    if (order < 0) {
                        return false;
                    }
                } else if (_relation.equals(">")) {
                    if (order <= 0) {
                        return false;
                    }
                } else if (_relation.equals("<")) {
                    if (order >= 0) {
                        return false;
                    }
                } else if (_relation.equals("<=")) {
                    if (order > 0) {
                        return false;
                    }
                } else if (_relation.equals("!=")) {
                    if (order == 0) {
                        return false;
                    }
                }

            } else {
                int order = _col1.getFrom(i).compareTo(_val2);
                if (_relation.equals("=")) {
                    if (order != 0) {
                        return false;
                    }
                } else if (_relation.equals(">=")) {
                    if (order < 0) {
                        return false;
                    }
                } else if (_relation.equals(">")) {
                    if (order <= 0) {
                        return false;
                    }
                } else if (_relation.equals("<")) {
                    if (order >= 0) {
                        return false;
                    }
                } else if (_relation.equals("<=")) {
                    if (order > 0) {
                        return false;
                    }
                } else if (_relation.equals("!=")) {
                    if (order == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Return true iff ROWS satisfies all CONDITIONS.
     */
    static boolean test(List<Condition> conditions, Integer... rows) {
        for (Condition cond : conditions) {
            if (!cond.test(rows)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Getter for column1.
     *
     * @return Column
     */
    public Column getCol1() {
        return _col1;
    }

    /**
     * Getter for column2.
     *
     * * @return Column
     */
    public Column getCol2() {
        return _col2;
    }

    /**
     * Getter for value2.
     *
     * @return value string
     */
    public String getVal2() {
        return _val2;
    }

    /**
     * Getter for relation.
     *
     * @return relation string
     */
    public String getRelation() {
        return _relation;
    }

    /**
     * The operands of this condition.  _col2 is null if the second operand
     * is a literal.
     */
    private Column _col1, _col2;
    /**
     * Second operand, if literal (otherwise null).
     */
    private String _val2;
    /**
     * Second operand, if literal (otherwise null).
     */
    private String _relation;
}
