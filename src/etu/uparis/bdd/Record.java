package etu.uparis.bdd;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

/**
 * A record is a set of key-value pairs.
 * 
 * @author Skander
 */
public final class Record {
    private final Map<String, Object> fields;
    
    public Record(final Map<String, Object> fields) {
        this.fields = fields;
    }

    /**
     * Create a record from a list of keys and a list of values.
     * 
     * @param keys
     * @param values
     * @throws InputMismatchException
     */
    public Record(final List<String> keys, final List<Object> values) throws InputMismatchException {
        this.fields = new HashMap<String, Object>();
        if (keys.size() != values.size()) {
            throw new InputMismatchException("The number of keys and values must be the same");
        }
        for (int i = 0; i < keys.size(); i++) {
            this.fields.put(keys.get(i), values.get(i));
        }
    }

    /**
     * Create a record from a string representation of the form "{ key1 = value1, key2 = value2, ... }".
     * 
     * @param representation
     */
    public Record(final String representation) {
        this.fields = new HashMap<String, Object>();
        final var tokens = representation.split(" ");
        for (int i = 1; i < tokens.length - 1; i += 3) {
            final var key = tokens[i];
            var value = tokens[i + 2];
            if (value.endsWith(",")) {
                value = value.substring(0, value.length() - 1); 
            }
            this.fields.put(key, value);
        }
    }

    /**
     * Get the value of a field. If the field does not exist, return null.
     * 
     * @param key
     * @return the value of the field
     */
    public Object get(final String key) {
        return fields.get(key);
    }

    /**
     * Set the value of a field.
     * 
     * @param key
     * @param value
     */
    public void set(final String key, final Object value) {
        fields.put(key, value);
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append("{ ");
        for (final var entry : fields.entrySet()) {
            builder.append(entry.getKey());
            builder.append(" = ");
            builder.append(entry.getValue());
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        builder.append("}");
        return builder.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object.getClass() != getClass()) {
            return false;
        }
        final var other = (Record) object;
        return fields.equals(other.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }
}
