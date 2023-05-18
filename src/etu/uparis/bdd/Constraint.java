package etu.uparis.bdd;


/**
 * An EGD is a pair of lists of keys. 
 * The first list contains the keys of the left-hand side of the EGD, the second list contains the keys of the right-hand side of the EGD.
 * 
 * @author Skander
 */
public abstract class Constraint {
    public abstract boolean apply(Database database);
}

