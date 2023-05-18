package etu.uparis.bdd;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A tuple-generating dependency (TGD) is a pair of lists of keys (body and head) that represents a constraint on the database.
 * The body is a list of keys that must be in the database for the constraint to be valid.
 * The head is a list of keys that must be in the database if the constraint is valid.
 * 
 * The body and the head are lists of lists of strings.
 * Each list of strings represents a table.
 * Each string represents a key of the table.
 * 
 * @author Florent
 */
public class TGD extends Constraint {
    /** 
     * Liste de la forme ((NomTable1=Nomtable1', NomAttribut1=Nomattribut1', NomAttribut2=Nomattribut2', ...), (NomTable2= Nomtable2', NomAttribut1=NomAttribut1', NomAttribut2=NomAttribut2', ...), ...).
     * Représente une conjonction de tables. 
     * Les "=" servent à distinguer deux tuples de la même table qui auraient des valeurs différentes pour une même clé.
     * La tête devrait donc savoir lequel des deux egaliser si elle doit egaliser un des deux.
     */
    private List<List<String>> body;

    /** Même forme que le body. */
    private List<List<String>> head;

    /** Liste des ensembles de tuples auxquels la contrainte a déjà été appliquée. */
    private Set<Set<Record>> alteredTuples;

    /**
     * Create a TGD from a body and a head.
     * 
     * @param body the body of the TGD
     * @param head the head of the TGD
     */
    public TGD(List<List<String>> body, List<List<String>> head) {
        this.body = body;
        this.head = head;
        this.alteredTuples = new HashSet<Set<Record>>();
    }

    /** 
     * Get the list of altered tuples.
     * 
     * @return the altered tuples
     */
    public Set<Set<Record>> getAlteredTuples() {
        return alteredTuples;
    }

    /**
     * Mark the given tuple as altered.
     * 
     * @param tuple the tuple to mark as altered
     */
    public void markAsAltered(Set<Record> tuple) {
        this.alteredTuples.add(tuple);
    }

    /**
     * Get the body of the TGD.
     * 
     * @return the body
     */
    public List<List<String>> getBody() {
        return this.body;
    }

    /**
     * Get the head of the TGD.
     * 
     * @return the head
     */
    public List<List<String>> getHead() {
        return this.head;
    }   

    /**
     * Check if the given tuple has been altered by the TGD.
     * 
     * @param tuple the tuple to check
     * @return true if the tuple has been altered, false otherwise
     */
    public boolean hasBeenAltered(Set<Record> tuple) {
        return this.alteredTuples.contains(tuple);
    }

    /**
     * Check if the given tuple is in the body of the TGD.
     * 
     * @param database the database
     * @return true if the tuple is in the body, false otherwise
     */
    @Override
    public boolean apply(Database database) {
        /** boolean tupleFound = true; 
        for (var pairs : this.body) { // Iterate over all pairs of (table, key) in the body
            if (tupleFound) tupleFound = false; else return false; // If the tuple has not been found yet, check if it is in the database
            for (var table : database.getTables()) { // Iterate over all tables in the database
                if (table.getName().equals(list.get(0))) { // If the table name is the same as the first element of the list (the table name)
                    if (table.getRecords().size() != 0) { // If the table is not empty
                        tupleFound = true;
                        break;
                    }
                }
            }
        }
        return tupleFound; **/
        for (var pairs : this.body) { // Iterate over all pairs of (table, key) in the body
            String tableName = pairs.get(0);
            String key = pairs.get(1);
            boolean tupleFound = false;
            for (var table : database.getTables()) { // Iterate over all tables in the database
                if (table.getName().equals(tableName)) { // If the table name is the same as the first element of the list (the table name)
                    for (var record : table.getRecords()) {
                        if (record.contains(key)) { // Check if the record contains the specified key
                            tupleFound = true;
                            break;
                        }
                    }
                    break; // Found the table, no need to continue searching
                }
            }
            if (!tupleFound) {
                return false; // Tuple not found in the database
            }
        }
        return true; // All tuples found in the database
    }
    @Override //toString
    public String toString() {
        String result = "TGD: ";
        for(var list : this.body) {
            for (var string : list) {
                result += string + " ";
            }
        }
        result += " -> ";
        for(var list : this.head) {
            for (var string : list) {
                result += string + " ";
            }
        }
        return result;
    }
}
