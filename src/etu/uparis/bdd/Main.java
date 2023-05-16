package etu.uparis.bdd;

import java.util.List;

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
        TGD tgd = new TGD(List.of(List.of ("t3", "parent", "nationalité"), List.of("enseignantparticulier", "nom", "matiere", "id", "age")), List.of (List.of("students", "id", "surname", "name", "age", "gender"), List.of("t2", "matiere", "nationalité"))); 

       // Add records to the table
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
        System.out.println(tgd.satisfyingBody(database));
        database.standardChase(List.of(tgd));        
       // System.out.println("\nApplying EGD...\n");

       /* students.applyEGD(() -> {
            return List.of(List.of("id"), List.of("surname", "name", "age", "gender"));
        });
        */

        // Print the database
        System.out.println(database);
    }
}
