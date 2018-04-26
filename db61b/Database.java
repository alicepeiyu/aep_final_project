package db61b;

import java.util.HashMap;

import static db61b.Utils.error;

/**
 * A collection of Tables, indexed by name.
 *
 * @author Alice Yang
 */
class Database {
    /**
     * Hashmap for storing database.
     */
    private HashMap<String, Table> dataBase;

    /**
     * An empty database.
     */
    public Database() {
        dataBase = new HashMap<String, Table>();
    }

    /**
     * Return the Table whose name is NAME stored in this database, or null
     * if there is no such table.
     */
    public Table get(String name) {
        if (dataBase.containsKey(name)) {
            return dataBase.get(name);
        }
        return null;
    }

    /**
     * Set or replace the table named NAME in THIS to TABLE.  TABLE and
     * NAME must not be null, and NAME must be a valid name for a table.
     */
    public void put(String name, Table table) {
        if (name == null || name.trim().isEmpty() || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (name.matches("^[a-zA-Z0-9_]*$") && !name.matches("^[0-9]+")) {
            dataBase.put(name, table);
        } else {
            throw error("invalid name");
        }
    }
}


