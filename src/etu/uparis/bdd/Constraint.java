package etu.uparis.bdd;

/**
 * A constraint is a rule that must be satisfied by a database.
 * It can be a tuple-generating dependency (TGD) or an equality-generating dependency (EGD).
 * 
 * @author Florent
 */
public abstract class Constraint {
    public abstract boolean apply(Database database);
}

