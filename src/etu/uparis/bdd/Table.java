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

    private String primaryKey;

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
     * Set the primary key of the table.
     * 
     * @param primaryKey the primary key
     */
    public void setPrimaryKey(String primaryKey) {
        if (!this.keys.contains(primaryKey)) {
            throw new IllegalArgumentException("The primary key must be one of the keys of the table");
        }
        this.primaryKey = primaryKey;
    }

    /**
     * Add a record to the table.
     * 
     * @param representation the representation of the record
     * @return the record
     */
    public Record addRecord(final String representation) {
        final var record = new Record(representation);
        if (this.primaryKey != null && record.get(this.primaryKey) == null) {
            throw new IllegalArgumentException("The primary key must be set");
        }
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
        if (this.primaryKey != null && record.get(this.primaryKey) == null) {
            throw new IllegalArgumentException("The primary key must be set");
        }
        this.records.add(record);
        record.setTable(this.name);
        return record;
    }
    
    /**
     * Apply the given EGD to the table.
     * 
     * @param egd the EGD to apply
     */
    /*public void applyEGD(final EGD egd) {
        final var values = egd.values();
        final var leftHandSide = values.get(0);
        final var rightHandSide = values.get(1);
        // For each record
        for (final var firstRecord : this.records) {
            // For each record
            for (final var secondRecord : this.records) {
                if (firstRecord == secondRecord) { // Skip the same record
                    continue;
                }
                for (final var leftHandeSideValue : leftHandSide) { // For each value in the left hand side
                    if (this.keys.contains(leftHandeSideValue)) { // If the value is a key
                        final var firstRecordValue = firstRecord.get(leftHandeSideValue); // Get the value of the first record
                        final var secondRecordValue = secondRecord.get(leftHandeSideValue); // Get the value of the second record
                        if (firstRecordValue == null || secondRecordValue == null) { // Skip if one of the values is null
                            continue;
                        }
                        if (firstRecordValue.equals(secondRecordValue)) { // If the values are equal
                            for (final var rightHandSideValue : rightHandSide) { // For each value in the right hand side
                                if (this.keys.contains(rightHandSideValue)) { // If the value is a key
                                    secondRecord.set(rightHandSideValue, firstRecord.get(rightHandSideValue)); // Set the value of the second record to the value of the first record
                                }
                            }
                        }
                    }
                }
            }
        }
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
            if (this.primaryKey != null && key.equals(this.primaryKey)) { // If the key is the primary key
                key = key.concat(" (*)");
            } else {
                key = key.concat(" ");
            }
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
