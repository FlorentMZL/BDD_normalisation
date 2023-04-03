package etu.uparis.bdd;

import java.util.HashSet;
import java.util.Set;

/**
 * A database is a set of tables.
 * 
 * @author Skander
 */
public final class Database {
    private String name;
    private final Set<Table> tables;

    public Database(final String name) {
        this.name = name;
        this.tables = new HashSet<Table>();
    }

    public void addTable(final Table table) {
        this.tables.add(table);
    }

    public void removeTable(final Table table) {
        this.tables.remove(table);
    }

    public Table getTable(final String name) {
        for (final var table : this.tables) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        return null;
    }

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
