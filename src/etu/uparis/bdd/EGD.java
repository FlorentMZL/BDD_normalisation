package etu.uparis.bdd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An equality-generating dependency (EGD) is a pair of a body and a head that represents a constraint on the database.
 * The body is a map of tables and their attributes that must be in the database for the constraint to be valid.
 * The head is a set of attributes that must be equal if the constraint is valid.
 * 
 * @author Skander
 */
public class EGD extends Constraint {
    private final Map<String, List<String>> body;
    private final Set<String> equalities;
    private final Set<String> head;

    // The number of records in the database before and after applying the constraint
    private int currentNumberOfRecords, previousNumberOfRecords;

    /**
     * Create an EGD from a body, a set of equalities and a head.
     * 
     * @param body
     * @param equalities
     * @param head
     */
    public EGD(final Map<String, List<String>> body, final Set<String> equalities, final Set<String> head) {
        this.body = body;
        this.equalities = equalities;
        this.head = head;
        this.currentNumberOfRecords = -1;
        this.previousNumberOfRecords = -2;
    }
  
    @Override
    public boolean apply(Database database) {
        var allRecords = new HashSet<Record>();
        if (this.currentNumberOfRecords == -1) { // -1 means that the constraint has never been applied
            for (var tableName : this.body.keySet()) allRecords.addAll(database.getTable(tableName).getRecords()); // Push all relevant records into a set
            this.currentNumberOfRecords = allRecords.size(); // Save the number of records
        } else {
            if (this.currentNumberOfRecords == this.previousNumberOfRecords) return true; // If the number of records hasn't changed, the constraint is still valid
        }
        var matchingRecords = new HashSet<Record>(); // Gather all records that match the constraint
        for (var equality : this.equalities) { 
            var split = equality.split("=");
            String firstAttribute = split[0], secondAttribute = split[1];
            for (var record1 : allRecords) {
                for (var record2 : allRecords) {
                    if (record1 == record2) continue; // No need to compare a record with itself
                    if (this.body.get(record1.getTable()).contains(firstAttribute) && this.body.get(record2.getTable()).contains(secondAttribute)) { // If the records have the right attributes
                        String firstAttributeNumberless = Database.stripNumerals(firstAttribute);
                        String secondAttributeNumberless = Database.stripNumerals(secondAttribute);
                        if (record1.get(firstAttributeNumberless).equals(record2.get(secondAttributeNumberless))) {  // If the records have the same value for the attributes
                            matchingRecords.add(record1);
                            matchingRecords.add(record2); // We add them to the set of matching records
                        } else {
                            matchingRecords.remove(record1); // If they don't match, we remove them from the set
                            matchingRecords.remove(record2); 
                            // This will ensure that the set only contains records that match all equalities
                        }
                    }
                }
            }
        }
        for (var equality : this.head) {
            var split = equality.split("=");
            String firstAttribute = split[0], secondAttribute = split[1];
            for (var matchingRecord1 : matchingRecords) { // Parse the matching records
                for (var matchingRecord2 : matchingRecords) { // This is a double loop because we need to compare every record to every other record
                    if (matchingRecord1 == matchingRecord2) continue; // Except itself
                    if (this.body.get(matchingRecord1.getTable()).contains(firstAttribute) && this.body.get(matchingRecord2.getTable()).contains(secondAttribute)) { // Only compare records that have the right attributes
                        String firstAttributeNumberless = Database.stripNumerals(firstAttribute); // We need to remove the numerals from the attributes
                        String secondAttributeNumberless = Database.stripNumerals(secondAttribute); // Because the numerals are only used to differentiate attributes with the same name
                        String value1 = (String) matchingRecord1.get(firstAttributeNumberless);
                        String value2 = (String) matchingRecord2.get(secondAttributeNumberless); // We get the values of the attributes
                        if (value1.equals(value2)) continue; // If the values are already equal, we don't need to do anything
                        else if (value1.startsWith("nullvalue") && !value2.startsWith("nullvalue")) matchingRecord1.set(firstAttributeNumberless, value2); // If one of the values is a nullvalue, we replace it with the other value
                        else if (!value1.startsWith("nullvalue") && value2.startsWith("nullvalue")) matchingRecord2.set(secondAttributeNumberless, value1); // (...)
                        else if (value1.startsWith("nullvalue") && value2.startsWith("nullvalue")) { // If both values are nullvalues, we replace one of them with the other
                            if (Math.random() < 0.5) 
                                matchingRecord1.set(firstAttributeNumberless, value2);
                            else
                                matchingRecord2.set(secondAttributeNumberless, value1);
                        } else { // If none of the values are nullvalues, we replace both of them with a nullvalue
                            matchingRecord1.set(firstAttributeNumberless, "nullvalue" + Database.nullvalue); // On remplace la valeur de l'attribut du record
                            Database.nullvalue++; // On incrémente le nullvalue
                            matchingRecord2.set(secondAttributeNumberless, "nullvalue" + Database.nullvalue);
                            Database.nullvalue++;
                        }
                    }
                }
            }
        }
        this.previousNumberOfRecords = this.currentNumberOfRecords; // Save the number of records
        this.currentNumberOfRecords -= matchingRecords.size(); // Remove the matching records from the number of records
        return false;
    }

    /**
     * Convert the EGD to a TGD.
     * 
     * @return the TGD corresponding to the EGD
     */
    public TGD convertToTGD() {
        var tgdBody = new ArrayList<List<String>>();
        var tgdHead = new ArrayList<List<String>>();
        for (var m : this.body.keySet()) {
            var tgdM = new ArrayList<String>();
            tgdM.add(m);
            for (var a : this.body.get(m))
                tgdM.add(a);
            tgdBody.add(tgdM);
        }
        for (var equality : this.equalities) {
            var split = equality.split("=");
            var leftEqual = split[0];
            var rightEqual = split[1];
            for (var cle : tgdBody) {
                if (cle.contains(leftEqual))
                    cle.set(cle.indexOf(leftEqual), rightEqual);
            }
        }
        for (var m : this.head) {
            var tgdM = new ArrayList<String>();
            tgdM.add("TEMP");
            tgdM.add(m.split("=")[0]);
            tgdM.add(m.split("=")[1]);
            tgdHead.add(tgdM);
        }
        return new TGD(tgdBody, tgdHead);
    }
}
