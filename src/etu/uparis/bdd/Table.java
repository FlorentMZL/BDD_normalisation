package etu.uparis.bdd;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A table is a set of records.
 * 
 * @author Skander
 */
public final class Table {
    // The width of a column in the table (for printing)
    public static final int COLUMN_WIDTH = 16;

    private String name;
    private List<String> keys;
    private Set<Record> records;

    /**
     * Create a new table with the given name and keys.
     * 
     * @param name
     * @param keys
     * @throws IllegalArgumentException if the keys contain duplicates
     */
    public Table(final String name, final List<String> keys) throws IllegalArgumentException {
        this.name = name;
        // Check for duplicate keys
        final var keysAsSet = new HashSet<String>(keys);
        if (keysAsSet.size() != keys.size()) {
            throw new IllegalArgumentException("Duplicate keys are not allowed");
        }
        this.keys = keys;
        this.records = new HashSet<Record>();
    }

    /**
     * Add a record to the table.
     * 
     * @param representation the representation of the record
     * @return the record
     */
    public Record addRecord(final String representation) {
        final var record = new Record(representation);
        this.records.add(record);
        record.setTable(this.name);
        return record;
    }

    /**
     * Add a record to the table.
     * 
     * @param record the record
     * @return the record
     */
    public Record addRecord(final Record record) {
        this.records.add(record);
        record.setTable(this.name);
        return record;
    }
    
    /**
     * @return the name of the table
     */
    public String getName() {
        return name;
    }

    /**
     * @return the records of the table
     */
    public Set<Record> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append("Table: ");
        builder.append(this.name);
        builder.append("\n");
        // Print a line of dashes
        builder.append("-".repeat((COLUMN_WIDTH + 1) * this.keys.size() + this.keys.size() + 1));
        builder.append("\n| ");
        for (var key : this.keys) { // For each key
                key = key.concat(" ");
            if (key.length() > COLUMN_WIDTH) { // If the key is too long
                builder.append(key.substring(0, COLUMN_WIDTH - 5));
                builder.append("... | ");
            } else {
                builder.append(key);
                builder.append(" ".repeat(COLUMN_WIDTH - key.length())); // Pad the key
                builder.append("| ");
            }
        }
        builder.append("\n|");
        builder.append(" ".repeat((COLUMN_WIDTH + 1) * this.keys.size() + this.keys.size() - 1)); 
        builder.append("|\n| ");
        for (final var record : this.records) { // For each record
            for (final var key : this.keys) { // For each key
                final var value = record.get(key); // Get the value of the record for the key
                if (value == null) { // If the value is null
                    builder.append(" ".repeat(COLUMN_WIDTH)); // Pad the value
                    builder.append("| ");
                } else {
                    final var valueAsString = value.toString(); // Get the value as a string
                    if (valueAsString.length() > COLUMN_WIDTH) { // If the value is too long
                        builder.append(valueAsString.substring(0, COLUMN_WIDTH - 5));
                        builder.append("... | "); 
                    } else {
                        builder.append(valueAsString);
                        builder.append(" ".repeat(COLUMN_WIDTH - valueAsString.length())); // Pad the value
                        builder.append("| ");
                    }
                }
            }
            builder.append("\n| ");
        }
        builder.delete(builder.length() - 3, builder.length()); // Delete the last "| "
        builder.append("\n");
        builder.append("-".repeat((COLUMN_WIDTH + 1) * this.keys.size() + this.keys.size() + 1)); // Print a line of dashes
        return builder.toString().strip(); // Strip the string
    }
}
