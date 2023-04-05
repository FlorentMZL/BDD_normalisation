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

    public void setPrimaryKey(String primaryKey) {
        if (!this.keys.contains(primaryKey)) {
            throw new IllegalArgumentException("The primary key must be one of the keys of the table");
        }
        this.primaryKey = primaryKey;
    }

    public Record addRecord(final String representation) {
        final var record = new Record(representation);
        if (this.primaryKey != null && record.get(this.primaryKey) == null) {
            throw new IllegalArgumentException("The primary key must be set");
        }
        this.records.add(record);
        return record;
    }

    public Record addRecord(final Record record) {
        if (this.primaryKey != null && record.get(this.primaryKey) == null) {
            throw new IllegalArgumentException("The primary key must be set");
        }
        this.records.add(record);
        return record;
    }

    public void applyEGD(final EGD egd) {
        final var values = egd.values();
        final var leftHandSide = values.get(0);
        final var rightHandSide = values.get(1);
        for (final var firstRecord : this.records) {
            for (final var secondRecord : this.records) {
                if (firstRecord == secondRecord) {
                    continue;
                }
                for (final var leftHandeSideValue : leftHandSide) {
                    if (this.keys.contains(leftHandeSideValue)) {
                        final var firstRecordValue = firstRecord.get(leftHandeSideValue);
                        final var secondRecordValue = secondRecord.get(leftHandeSideValue);
                        if (firstRecordValue == null || secondRecordValue == null) {
                            continue;
                        }
                        if (firstRecordValue.equals(secondRecordValue)) {
                            for (final var rightHandSideValue : rightHandSide) {
                                if (this.keys.contains(rightHandSideValue)) {
                                    secondRecord.set(rightHandSideValue, firstRecord.get(rightHandSideValue));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public Set<Record> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append("Table: ");
        builder.append(this.name);
        builder.append("\n");
        builder.append("-".repeat((COLUMN_WIDTH + 1) * this.keys.size() + this.keys.size() + 1));
        builder.append("\n| ");
        for (var key : this.keys) {
            if (this.primaryKey != null && key.equals(this.primaryKey)) {
                key = key.concat(" (*)");
            } else {
                key = key.concat(" ");
            }
            if (key.length() > COLUMN_WIDTH) {
                builder.append(key.substring(0, COLUMN_WIDTH - 5));
                builder.append("... | ");
            } else {
                builder.append(key);
                builder.append(" ".repeat(COLUMN_WIDTH - key.length()));
                builder.append("| ");
            }
        }
        builder.append("\n|");
        builder.append(" ".repeat((COLUMN_WIDTH + 1) * this.keys.size() + this.keys.size() - 1));
        builder.append("|\n| ");
        for (final var record : this.records) {
            for (final var key : this.keys) {
                final var value = record.get(key);
                if (value == null) {
                    builder.append(" ".repeat(COLUMN_WIDTH));
                    builder.append("| ");
                } else {
                    final var valueAsString = value.toString();
                    if (valueAsString.length() > COLUMN_WIDTH) {
                        builder.append(valueAsString.substring(0, COLUMN_WIDTH - 5));
                        builder.append("... | ");
                    } else {
                        builder.append(valueAsString);
                        builder.append(" ".repeat(COLUMN_WIDTH - valueAsString.length()));
                        builder.append("| ");
                    }
                }
            }
            builder.append("\n| ");
        }
        builder.delete(builder.length() - 3, builder.length());
        builder.append("\n");
        builder.append("-".repeat((COLUMN_WIDTH + 1) * this.keys.size() + this.keys.size() + 1));
        return builder.toString().strip();
    }
}
