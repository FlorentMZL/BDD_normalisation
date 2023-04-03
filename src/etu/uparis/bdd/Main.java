package etu.uparis.bdd;

import java.util.List;

/**
 * @author Skander
 */
public final class Main {
    public static void main(final String[] args) {
        // Create a database
        Database database = new Database("projet-bdd");

        // Create a table
        Table etudiants = new Table("etudiants", List.of("id", "nom", "prenom", "age", "sexe"));
        etudiants.setPrimaryKey("id");

        // Add records to the table
        etudiants.addRecord("{ id = 21958561, nom = Bazelet, prenom = Florent, sexe = M }");
        etudiants.addRecord(new Record(List.of("id", "prenom", "age", "sexe"), List.of("21957008", "Skander", 22, "M")));

        // Add the table to the database
        database.addTable(etudiants);

        // Print the database
        System.out.println(database);
    }
}
