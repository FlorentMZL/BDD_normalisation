package etu.uparis.bdd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Skander
 */
public final class Main {
    public static void main(final String[] args) {
        // Create a database
        Database database = new Database("bdd-project");

        // Create a table
        Table students = new Table("students", List.of("id", "surname", "name", "age", "gender"));
        Table enseignantsparticulier = new Table("enseignantparticulier", List.of("nom", "matiere", "id", "age"));
        Table t2 = new Table("t2", List.of("matiere", "nationalité"));
        Table t3 = new Table("t3", List.of("parent","nationalité"));
        t2.setPrimaryKey("matiere");
        t3.setPrimaryKey("nationalité");
        students.setPrimaryKey("id");
        enseignantsparticulier.setPrimaryKey("nom");
        // TGD tgd1 = new TGD(List.of (List.of("t3=t3", "parent=parent1", "nationalité=nationalité1")), List.of(List.of("enseignantparticulier=enseignantparticulier1", "nom=nom1", "matiere=matiere1", "id=id1", "age=age1")));
        // TGD tgd2 = new TGD(List.of(List.of ("t3=t3", "parent=parent1", "nationalité=nationalité1"), List.of("enseignantparticulier=enseignantparticulier1", "nom=nom1", "matiere=matiere1", "id=id1", "age=age1")), List.of (List.of("students=students1", "id=id2", "surname=surname1", "name=name1", "age=age1", "gender=gender1"), List.of("t2=t2", "matiere=matiere1", "nationalité=nationalité1"))); 
        // TGD tgdOblivious = new TGD (List.of(List.of ("t3=t3", "parent=parent1", "nationalité=nationalité1")), List.of(List.of("t3=t3", "parent=parent1", "nationalité=nationalité2")));
       // Add records to the table
        t3.addRecord("{ parent = pipi, nationalité = français }");
        t3.addRecord("{ parent = prout, nationalité = français }");
        t2.addRecord("{ matiere = français, nationalité = caca }");
        students.addRecord("{ id = 22, surname = Bazelet, name = Florent, gender = M }");
        students.addRecord(new Record(List.of("id", "name", "age", "gender"), List.of("22", "Skander", 22, "M")));
        enseignantsparticulier.addRecord("{ nom = Bazelet, matiere = Math, id = 21957008, age = 18 }");

        // Add the table to the database
        database.addTable(t3);
        database.addTable(t2);
        database.addTable(students);
        database.addTable(enseignantsparticulier);
        // Print the database
        System.out.println(database);

        System.out.println("\nbody satisfied ? \n");
        // System.out.println(tgdOblivious.apply(database));
        // database.standardChase(List.of(tgd1, tgd2, tgdOblivious));

        Map<String, List<String>> egdTables = new HashMap<String, List<String>>();
        egdTables.put("t3", List.of("parent", "nationalité"));
        egdTables.put("t2", List.of("nationalité", "parent"));

        database.standardChase(List.of(new EGD(egdTables, Set.of("nationalité=nationalité"), Set.of("parent=parent"))));
        //database.obliviousChase(List.of( tgdOblivious), 2);
        //database.obliviousSkolemChase(List.of(tgdOblivious));
        // System.out.println("\nApplying EGD...\n");

       /* students.applyEGD(() -> {
            return List.of(List.of("id"), List.of("surname", "name", "age", "gender"));
        });
        */

        // Print the database
        System.out.println(database);
    }
}
