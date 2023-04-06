package etu.uparis.bdd;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A database is a set of tables.
 * 
 * @author Skander
 */
public final class Database {
    private String name;
    private final Set<Table> tables;

    /**
     * Create a new database with the given name.
     * 
     * @param name the name of the database
     */
    public Database(final String name) {
        this.name = name;
        this.tables = new HashSet<Table>();
    }

    /**
     * Add a table to the database.
     * 
     * @param table the table to add
     */
    public void addTable(final Table table) {
        this.tables.add(table);
    }

    /**
     * Add a table to the database.
     * 
     * @param name the name of the table
     * @param keys the keys of the table
     * @return the table
     * @throws IllegalArgumentException if the keys contain duplicates
     */
    public Table addTable(final String name, final List<String> keys) throws IllegalArgumentException {
        final var table = new Table(name, keys);
        this.tables.add(table);
        return table;
    }
    
    /**
     * Remove a table from the database.
     * 
     * @param table the table to remove
     */
    public void removeTable(final Table table) {
        this.tables.remove(table);
    }

    /**
     * Remove a table from the database.
     * 
     * @param name the name of the table to remove
     */
    public void removeTable(final String name) {
        final var table = this.getTable(name);
        if (table != null) {
            this.tables.remove(table);
        }
    }

    /**
     * Get a table by its name.
     * 
     * @param name the name of the table
     * @return the table or null if it does not exist
     */
    public Table getTable(final String name) {
        for (final var table : this.tables) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        return null;
    }

    /**
     * @return the tables
     */
    public Set<Table> getTables() {
        return this.tables;
    }

    @Override
    public final String toString() {
        final var builder = new StringBuilder();
        builder.append("Database: ");
        builder.append(this.name);
        builder.append("\n\n");
        for (final var table : this.tables) {
            builder.append(table);
            builder.append("\n");
        }
        return builder.toString().strip();
    }
}
