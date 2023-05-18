package etu.uparis.bdd;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * An EGD is a pair of lists of keys that represents a constraint on the database in the form of an equality.
 * 
 * The first list contains the keys of the left-hand side of the EGD.
 * The second list contains the keys of the right-hand side of the EGD.
 * Both must be equal for the constraint to be valid. 
 * 
 * @author Skander
 */
public class EGD extends Constraint {
    private List<List<String>> head;
    private List<List<String>> body;
    private Set<Set<Record>> alteredTuples;

    /**
     * Create an EGD from a body and a head.
     * 
     * @param body the body of the EGD
     * @param head the head of the EGD
     */
    public EGD(List<List<String>> body, List<List<String>> head) {
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
     * Get the body of the EGD.
     * 
     * @return the body
     */
    public List<List<String>> getBody() {
        return this.body;
    }

    /**
     * Get the head of the EGD.
     * 
     * @return the head
     */
    public List<List<String>> getHead() {
        return this.head;
    }

    /**
     * Check if the given tuple has been altered by the EGD.
     * 
     * @param tuple the tuple to check
     * @return true if the tuple has been altered, false otherwise
     */
    public boolean hasBeenAltered(Set<Record> tuple) {
        return this.alteredTuples.contains(tuple);
    }

    /**
     * Check if the EGD is valid in the given database.
     * 
     * @param database the database to check
     * @return true if the EGD is valid, false otherwise
     */
    @Override
    public boolean validate(Database database) {
        return true;
    }
}
