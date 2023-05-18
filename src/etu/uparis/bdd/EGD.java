package etu.uparis.bdd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pardon.
 * 
 * @author Skander
 */
public class EGD extends Constraint {
    private final Map<String, List<String>> body;
    private final Set<String> equalities;
    private final Set<String> head;

    private int currentNumberOfRecords, previousNumberOfRecords;

    public EGD(final Map<String, List<String>> body, final Set<String> equalities, final Set<String> head) {
        this.body = body;
        this.equalities = equalities;
        this.head = head;
        this.currentNumberOfRecords = -1;
        this.previousNumberOfRecords = -2;
    }
  
    @Override
    public boolean apply(Database database) {
        // Etape 1
        var allRecords = new HashSet<Record>();
        if (this.currentNumberOfRecords == -1) { // Premier passage
            for (var tableName : this.body.keySet()) allRecords.addAll(database.getTable(tableName).getRecords()); // On récupère tous les records de toutes les tables du body
            this.currentNumberOfRecords = allRecords.size(); // On sauvegarde le nombre de records
            System.out.println(currentNumberOfRecords + " records on first iteration"); 
        } else {
            if (this.currentNumberOfRecords == this.previousNumberOfRecords) return true; // Si le nombre de records n'a pas changé, bingo
        }
        // Etape 2
        var matchingRecords = new HashSet<Record>(); // On crée un set de records qui matchent
        for (var equality : this.equalities) { 
            var split = equality.split("=");
            String firstAttribute = split[0], secondAttribute = split[1];
            for (var record1 : allRecords) {
                for (var record2 : allRecords) {
                    if (record1 == record2) continue; // On ne compare pas un record avec lui-même
                    if (this.body.get(record1.getTable()).contains(firstAttribute) && this.body.get(record2.getTable()).contains(secondAttribute)) { // On ne compare que les records qui ont les bons attributs
                        String firstAttributewithoutNumber= Database.withoutNumber(firstAttribute);
                        String secondAttributewithoutNumber= Database.withoutNumber(secondAttribute);
                        System.out.println("Comparing " + record1 + " and " + record2);
                        if (record1.get(firstAttributewithoutNumber).equals(record2.get(secondAttributewithoutNumber))) { // Si les attributs sont égaux
                            System.out.println("They are the same in " + firstAttributewithoutNumber + " and " + secondAttributewithoutNumber);
                            matchingRecords.add(record1);
                            matchingRecords.add(record2); // On ajoute les deux records au set
                        } else {
                            System.out.println("They are not the same in " + firstAttributewithoutNumber + " and " + secondAttributewithoutNumber);
                            matchingRecords.remove(record1); // Sinon on les retire
                            matchingRecords.remove(record2); 
                            // Cela va permettre de ne garder que les records qui matchent pour la suite
                        }
                    }
                }
            }
        }
        System.out.println("There are " + matchingRecords.size() + " matching records"); 
        // Etape 3
        for (var equality : this.head) {
            var split = equality.split("=");
            String firstAttribute = split[0], secondAttribute = split[1];
            for (var matchingRecord1 : matchingRecords) { // On parcourt les records qui matchent
                for (var matchingRecord2 : matchingRecords) { // On parcourt les records qui matchent
                    if (matchingRecord1 == matchingRecord2) continue; // On ne compare pas un record avec lui-même
                    if (this.body.get(matchingRecord1.getTable()).contains(firstAttribute) && this.body.get(matchingRecord2.getTable()).contains(secondAttribute)) { // On ne compare que les records qui ont les bons attributs
                        String firstAttributewithoutNumber=Database.withoutNumber(firstAttribute);
                        String secondAttributewithoutNumber=Database.withoutNumber(secondAttribute);
                        String value1 = (String) matchingRecord1.get(firstAttributewithoutNumber);
                        String value2 = (String) matchingRecord2.get(secondAttributewithoutNumber); // On récupère les valeurs des attributs
                        if (value1.equals(value2)) {
                            System.out.println(value1 + " = " + value2);
                            continue;
                        } else if (value1.startsWith("nullvalue") && !value2.startsWith("nullvalue")) { 
                            /* alteredTuples.add(new Record(matchingRecord1.toString().replace(value1, value2))); */
                            matchingRecord1.set(firstAttributewithoutNumber, value2); // On remplace la valeur de l'attribut du record
                            System.out.println("Altered " + matchingRecord1 + " to " + value2);
                        } else if (!value1.startsWith("nullvalue") && value2.startsWith("nullvalue")) {
                            /* alteredTuples.add(new Record(matchingRecord2.toString().replace(value2, value1))); */
                            matchingRecord2.set(secondAttributewithoutNumber, value1); // On remplace la valeur de l'attribut du record
                            System.out.println("Altered " + matchingRecord2 + " to " + value1);
                        } else if (value1.startsWith("nullvalue") && value2.startsWith("nullvalue")) {
                            if (Math.random() < 0.5) /* alteredTuples.add(new Record(matchingRecord1.toString().replace(value1, value2))); */ matchingRecord1.set(firstAttributewithoutNumber, value2);
                            else /* alteredTuples.add(new Record(matchingRecord2.toString().replace(value2, value1))); */ matchingRecord2.set(secondAttribute, value1);
                        } else {
                            matchingRecord1.set(firstAttributewithoutNumber, "nullvalue" + Database.nullvalue); // On remplace la valeur de l'attribut du record
                            Database.nullvalue++; // On incrémente le nullvalue
                            System.out.println("Altered " + matchingRecord1 + " to nullvalue" + Database.nullvalue);
                            matchingRecord2.set(secondAttributewithoutNumber, "nullvalue" + Database.nullvalue);
                            Database.nullvalue++;
                            System.out.println("Altered " + matchingRecord2 + " to nullvalue" + Database.nullvalue);
                        }
                    }
                }
            }
        }
        this.previousNumberOfRecords = this.currentNumberOfRecords; // On sauvegarde le nombre de records
        this.currentNumberOfRecords -= matchingRecords.size(); // On retire les records qui matchent
        System.out.println(currentNumberOfRecords + " records left");
        return false;
    }  


    public TGD convertToTGD(){
        List<List<String>> TGDbody = new ArrayList<>();
        List<List<String>> TGDhead = new ArrayList<>();
        for(var m : this.body.keySet()){//On crée le corps de la TGD
            List<String> TGDm = new ArrayList<>();
            TGDm.add(m);
            for(var a : this.body.get(m)){//On ajoute le nom de la table, puis les clés 
                TGDm.add(a);
            }
            TGDbody.add(TGDm);
        }
        //On suppose que equalities ne contient qu'une seule égalité. 
        for( var equality : this.equalities){//On ajoute les égalités au corps de la TGD en modifiant le nom des clés (ex : a1 = a2 -> remplacement de tous les a1 par des a2 dans le corps) 
            String[] split = equality.split("=");
            var leftequal= split[0];
            var rightequal = split[1];
            for(var cle : TGDbody){
                if(cle.contains(leftequal)){
                    cle.set(cle.indexOf(leftequal), rightequal);
                    
                }
            }
        }
        for(var m : this.head){
            List <String> TGDm = new ArrayList<>();
            TGDm.add("TEMP"); 
            TGDm.add(m.split("=")[0]);
            TGDm.add(m.split("=")[1]);
            TGDhead.add(TGDm);
        }
        return new TGD(TGDbody, TGDhead);

    }
}
