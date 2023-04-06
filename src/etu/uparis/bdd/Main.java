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
        students.setPrimaryKey("id");

        // Add records to the table
        students.addRecord("{ id = 21957008, surname = Bazelet, name = Florent, gender = M }");
        students.addRecord(new Record(List.of("id", "name", "age", "gender"), List.of("21957008", "Skander", 22, "M")));

        // Add the table to the database
        database.addTable(students);

        // Print the database
        System.out.println(database);

        System.out.println("\nApplying EGD...\n");

        students.applyEGD(() -> {
            return List.of(List.of("id"), List.of("surname", "name", "age", "gender"));
        });

        // Print the database
        System.out.println(database);
    }
}
