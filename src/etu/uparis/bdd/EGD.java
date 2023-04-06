package etu.uparis.bdd;

import java.util.List;

/**
 * An EGD is a pair of lists of keys. 
 * The first list contains the keys of the left-hand side of the EGD, the second list contains the keys of the right-hand side of the EGD.
 * 
 * @author Skander
 */
public interface EGD {
    /**
     * @return a list of 2 lists: the first one contains the keys of the left-hand side of the EGD, the second one contains the keys of the right-hand side of the EGD
     */
    public List<List<String>> values();
}