package etu.uparis.bdd;

import java.util.Collections;
import java.util.List;

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
    private List<List<String>> leftHandSide;

    private List<List<String>> rightHandSide;

    /**
     * Create an EGD from a left-hand side and a right-hand side.
     * 
     * @param leftHandSide the left-hand side of the EGD
     * @param rightHandSide the right-hand side of the EGD
     */
    public EGD(List<List<String>> leftHandSide, List<List<String>> rightHandSide) {
        this.leftHandSide = Collections.unmodifiableList(leftHandSide);
        this.rightHandSide = Collections.unmodifiableList(rightHandSide);
    }

    /**
     * Get the left-hand side of the EGD.
     * 
     * @return the left-hand side of the EGD
     */
    public List<List<String>> getLeftHandSide() {
        return leftHandSide;
    }

    /**
     * Get the right-hand side of the EGD.
     * 
     * @return the right-hand side of the EGD
     */
    public List<List<String>> getRightHandSide() {
        return rightHandSide;
    }

    /**
     * Check if the EGD is valid for the given database.
     *
     * @param database the database to check
     * @return true if the EGD is valid, false otherwise
     */
    @Override
    public boolean validate(Database database) {
        // Check if the left-hand side and the right-hand side have the same size
        if (this.leftHandSide.size() != this.rightHandSide.size()) {
            return false;
        }
        // Check if the left-hand side and the right-hand side have the same keys
        for (int i = 0; i < this.leftHandSide.size(); i++) { // Go through the left-hand side and the right-hand side
            List<String> leftTuple = this.leftHandSide.get(i); // Get the left-hand side tuple in the form (table, key), (table, key), ...
            List<String> rightTuple = this.rightHandSide.get(i); // Get the right-hand side tuple in the form (table, key), (table, key), ...
            if (leftTuple.size() != rightTuple.size())  return false; // If the size of the left-hand side tuple is not the same as the size of the right-hand side tuple, return false
            for (int j = 0; j < leftTuple.size(); j++) { // Go through the left-hand side tuple and the right-hand side tuple
                String leftValue = leftTuple.get(j); // Get the left-hand side value
                String rightValue = rightTuple.get(j); // Get the right-hand side value 
                if (!leftValue.equals(rightValue)) return false; // If the left-hand side value is not the same as the right-hand side value, return false
            } 
        } // If the left-hand side and the right-hand side have the same keys,
        return true;
    } 
}
