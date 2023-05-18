package etu.uparis.bdd;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fuck me.
 * 
 * @author Skander
 */
public class EGD extends Constraint {
    private final Map<String, List<String>> body;
    private final Set<String> equalities;
    private final Set<String> head;

    private int innocents, previouslyAccused;

    public EGD(final Map<String, List<String>> body, final Set<String> equalities, final Set<String> head) {
        this.body = body;
        this.equalities = equalities;
        this.head = head;
        this.innocents = -1;
        this.previouslyAccused = -2;
    }
  
    @Override
    public boolean apply(Database database) {
        // Etape 1
        var bystanders = new HashSet<Record>();
        if (this.innocents == -1) {
            for (var tableName : this.body.keySet()) bystanders.addAll(database.getTable(tableName).getRecords());
            this.innocents = bystanders.size();
            System.out.println("There were " + innocents + " innocent people at first");
        } else {
            if (this.innocents == this.previouslyAccused) return true;
        }
        // Etape 2
        var suspects = new HashSet<Record>();
        for (var equality : this.equalities) {
            var split = equality.split("=");
            String firstAttribute = split[0], secondAttribute = split[1];
            for (var bystander1 : bystanders) {
                for (var bystander2 : bystanders) {
                    if (bystander1 == bystander2) continue;
                    if (this.body.get(bystander1.getTable()).contains(firstAttribute) && this.body.get(bystander2.getTable()).contains(secondAttribute)) {
                        System.out.println("Comparing " + bystander1 + " and " + bystander2);
                        if (bystander1.get(firstAttribute).equals(bystander2.get(secondAttribute))) {
                            System.out.println("They are the same in " + firstAttribute + " and " + secondAttribute);
                            suspects.add(bystander1);
                            suspects.add(bystander2);
                        } else {
                            System.out.println("They are not the same in " + firstAttribute + " and " + secondAttribute);
                            suspects.remove(bystander1);
                            suspects.remove(bystander2);
                        }
                    }
                }
            }
        }
        // Etape 3
        for (var equality : this.head) {
            var split = equality.split("=");
            String firstAttribute = split[0], secondAttribute = split[1];
            for (var suspect1 : suspects) {
                for (var suspect2 : suspects) {
                    if (suspect1 == suspect2) continue;
                    if (this.body.get(suspect1.getTable()).contains(firstAttribute) && this.body.get(suspect2.getTable()).contains(secondAttribute)) {
                        String variable1 = (String) suspect1.get(firstAttribute);
                        String variable2 = (String) suspect2.get(secondAttribute);
                        if (variable1.equals(variable2)) {
                            System.out.println(variable1 + " = " + variable2);
                            continue;
                        } else if (variable1.startsWith("nullvalue") && !variable2.startsWith("nullvalue")) { 
                            /* alteredTuples.add(new Record(suspect1.toString().replace(variable1, variable2))); */
                            suspect1.set(firstAttribute, variable2);
                            System.out.println("Altered " + suspect1 + " to " + variable2);
                        } else if (!variable1.startsWith("nullvalue") && variable2.startsWith("nullvalue")) {
                            /* alteredTuples.add(new Record(suspect2.toString().replace(variable2, variable1))); */
                            suspect2.set(secondAttribute, variable1);
                            System.out.println("Altered " + suspect2 + " to " + variable1);
                        } else if (variable1.startsWith("nullvalue") && variable2.startsWith("nullvalue")) {
                            if (Math.random() < 0.5) /* alteredTuples.add(new Record(suspect1.toString().replace(variable1, variable2))); */ suspect1.set(firstAttribute, variable2);
                            else /* alteredTuples.add(new Record(suspect2.toString().replace(variable2, variable1))); */ suspect2.set(secondAttribute, variable1);
                        } else {
                            suspect1.set(firstAttribute, "nullvalue" + Database.nullvalue);
                            Database.nullvalue++;
                            System.out.println("Altered " + suspect1 + " to nullvalue" + Database.nullvalue);
                            suspect2.set(secondAttribute, "nullvalue" + Database.nullvalue);
                            Database.nullvalue++;
                            System.out.println("Altered " + suspect2 + " to nullvalue" + Database.nullvalue);
                        }
                    }
                }
            }
        }
        this.previouslyAccused = this.innocents;
        this.innocents -= suspects.size();
        System.out.println("There are now " + innocents + " innocent people");
        return false;
    }  
}
