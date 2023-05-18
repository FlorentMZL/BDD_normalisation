package etu.uparis.bdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A database is a set of tables.
 * 
 * @author Skander
 */
public final class Database {
    static int nullvalue = 0; // pas super super niveau programmation mais bon c'est pour faire des valeurs
                              // nulls différentes

    // The name of the database
    private String name;

    // The tables of the database
    private final Set<Table> tables;

    /**
     * Create a new database with the given name.
     * 
     * @param name the name of the database
     */
    public Database(final String name) {
        this.name = name;
        this.tables = new HashSet<Table>();
    }

    /**
     * Add a table to the database.
     * 
     * @param table the table to add
     */
    public void addTable(final Table table) {
        this.tables.add(table);
    }

    /**
     * Add a table to the database.
     * 
     * @param name the name of the table
     * @param keys the keys of the table
     * @return the table
     * @throws IllegalArgumentException if the keys contain duplicates
     */
    public Table addTable(final String name, final List<String> keys) throws IllegalArgumentException {
        final var table = new Table(name, keys);
        this.tables.add(table);
        return table;
    }

    /**
     * Remove a table from the database.
     * 
     * @param table the table to remove
     */
    public void removeTable(final Table table) {
        this.tables.remove(table);
    }

    /**
     * Remove a table from the database.
     * 
     * @param name the name of the table to remove
     */
    public void removeTable(final String name) {
        final var table = this.getTable(name);
        if (table != null) {
            this.tables.remove(table);
        }
    }

    /**
     * Get a table by its name.
     * 
     * @param name the name of the table
     * @return the table or null if it does not exist
     */
    public Table getTable(final String name) {
        for (final var table : this.tables) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        return null;
    }

    /**
     * @return the tables
     */
    public Set<Table> getTables() {
        return this.tables;
    }

    /**
     * Generate a textual representation of the database.
     * 
     * @return the textual representation of the database
     */
    @Override
    public final String toString() {
        final var builder = new StringBuilder();
        builder.append("Database: ");
        builder.append(this.name);
        builder.append("\n\n");
        for (final var table : this.tables) { // For each table in the database
            builder.append(table);
            builder.append("\n");
        }
        return builder.toString().strip();
    }

    /**
     * Apply the standard chase algorithm to the database.
     *
     * @param constraints the constraints to apply
     */
    public final void standardChase(final List<Constraint> constraints) {
        var countApplied = 0; // Nombre de contraintes appliquées à la base de données
        var count = 0; // Nombre de contraintes appliquées à la base de données lors d'un tour de
                       // boucle
        while (countApplied < constraints.size()) {
            count = 0;
            countApplied = 0;
            var allTuples = new HashMap<String, Set<Record>>();
            for (var table : this.getTables()) {
                allTuples.put(table.getName(), table.getRecords()); // Dictionnaire qui associe à chaque table
                                                                    // l'ensemble de ses tuples pour faciliter les
                                                                    // manipulations
            }
            for (final var constraint : constraints) {
                count = 0;
                if (constraint instanceof TGD) { // TGD : sous forme R(w)^S(w)^.. -> R'(w)^S'(w)^..
                    TGD tgd = (TGD) constraint;
                    System.out.println("TGD: " + tgd);
                    var ontuples = new ArrayList<Set<Record>>(); // Liste des ensembles de tuples qui satisfont une
                                                                 // partie du corps de la TGD
                    var applyOnTuples = new HashSet<HashSet<Record>>(); // Liste des ensembles de tuples qui satisfont
                                                                        // le corps de la TGD
                    for (var b : tgd.getBody()) { // Pour chaque table dans le corps de la TGD
                        var toFind = beforeequal(b.get(0)); // On cherche la table dans le dictionnaire
                        // Si la table existe et n'est pas vide, on l'ajoute à la liste des ensembles de
                        // tuples qui satisfont une partie du corps de la TGD
                        if (allTuples.get(toFind) != null && allTuples.get(toFind).size() != 0)
                            ontuples.add(allTuples.get(toFind));
                    }
                    if (ontuples.size() == tgd.getBody().size()) { // Si on a autant de tables dans le corps de la TGD
                                                                   // que de tables dans le dictionnaire alors le corps
                                                                   // est satisfait
                        applyOnTuples = genererCombinaisons(ontuples); // Generation de tous les ensembles de tuples qui
                                                                       // peuvent satisfaire le corps de la TGD
                        for (var tuplesatisfying : applyOnTuples) { // Pour chaque ensemble de tuples qui satisfait le
                                                                    // corps de la TGD
                            if (!(tgd.hasBeenAltered(tuplesatisfying))) { // Si l'ensemble n'a pas été deja satisfait
                                for (int i = 0; i < tgd.getHead().size(); i++) { // Pour chaque table de la tête
                                    var h = tgd.getHead().get(i);
                                    if (allTuples.get(beforeequal(h.get(0))) == null
                                            || allTuples.get(beforeequal(h.get(0))).size() == 0) { // Si la table est
                                                                                                   // vide, alors il
                                                                                                   // faut créer un
                                                                                                   // record pour
                                                                                                   // satisfaire la tête
                                        allTuples.put(beforeequal(h.get(0)), new HashSet<Record>());
                                        List<String> keys = new ArrayList<String>(); // Creation du record à ajouter
                                        List<Object> values = new ArrayList<Object>();
                                        for (int j = 1; j < h.size(); j++) {
                                            // String s = "nullvalue";
                                            // Object o = s;
                                            keys.add(beforeequal(h.get(j)));// On met les clés
                                            values.add("nullvalue" + nullvalue); // On met toutes les valeurs à null. On
                                                                                 // egalisera celles qu'il faut en
                                                                                 // dessous
                                            nullvalue++;
                                        }
                                        boolean egal = true;
                                        for (int j = 1; j < h.size(); j++) { // Pour chaque clé de la tête
                                            egal = true;
                                            for (var b : tgd.getBody()) { // on regarde dans le corps les clés qui sont
                                                                          // les memes que la clé de la tête qu'on
                                                                          // regarde
                                                for (var t : b) { // pour chaque string dans la partie du corps qu'on
                                                                  // regarde
                                                    if (egal == false)
                                                        break;
                                                    System.out.println("On regarde si la clé " + afterequal(t)
                                                            + " est égale à la clé " + afterequal(h.get(j)));
                                                    if (afterequal(t).equals(afterequal(h.get(j)))) {// si il est egal à
                                                                                                     // la clé
                                                        System.out.println(
                                                                "On a trouvé une clé égale à la clé de la tête");
                                                        for (var tuple : tuplesatisfying) { // On cherche le record qui
                                                                                            // appartient à la table
                                                                                            // concernée
                                                            if (tuple.getTable().equals(beforeequal(b.get(0)))) {
                                                                values.set(j - 1, tuple.get(beforeequal(h.get(j))));// On
                                                                                                                    // met
                                                                                                                    // a
                                                                                                                    // jour
                                                                                                                    // la
                                                                                                                    // valeur
                                                                                                                    // liée
                                                                                                                    // a
                                                                                                                    // la
                                                                                                                    // clé
                                                                egal = false;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Record r = new Record(keys, values);
                                        allTuples.get(beforeequal(h.get(0))).add(r); // Ajouter le record à l'ensemble
                                                                                     // des records de la table
                                        System.out.println("On a ajouté le record " + r + " à la table " + h.get(0));
                                        for (var tabl : this.getTables()) { // ajouter le record à la base de données
                                            if (tabl.getName().equals(beforeequal(h.get(0)))) {
                                                tabl.addRecord(r);
                                            }
                                        }
                                        tgd.markAsAltered(tuplesatisfying);
                                    } else {
                                        boolean satisfait = false;
                                        boolean egal = true;
                                        for (var t : allTuples.get(beforeequal(h.get(0)))) { // Pour les tuples
                                                                                             // correspondant à la table
                                                                                             // concernée dans une
                                                                                             // partie de la tête :
                                            egal = true;
                                            for (int j = 1; j < h.size(); j++) { // pour chaque clé de cette partie de
                                                                                 // la tête
                                                if (egal == false)
                                                    break;
                                                for (var b : tgd.getBody()) { // Pour chaque tuple dans le corps
                                                    if (egal == false)
                                                        break;
                                                    for (var cle : b) { // pour chaque clé du tuple dans le corps
                                                        if (egal == false)
                                                            break;
                                                        if (satisfait)
                                                            break;
                                                        if (afterequal(cle).equals(afterequal(h.get(j)))) { // si la clé
                                                                                                            // est égale
                                                                                                            // à la clé
                                                                                                            // de la
                                                                                                            // tete
                                                            for (var tuple : tuplesatisfying) { // Pour chaque tuple
                                                                                                // dans les tuples qui
                                                                                                // satisfont le corps
                                                                if (satisfait)
                                                                    break;
                                                                if (tuple.getTable().equals(beforeequal(b.get(0)))) { // si
                                                                                                                      // ce
                                                                                                                      // tuple
                                                                                                                      // appartient
                                                                                                                      // à
                                                                                                                      // la
                                                                                                                      // table
                                                                                                                      // concernée
                                                                    if (tuple.get(beforeequal(h.get(j))) != t
                                                                            .get(beforeequal(h.get(j)))) { // si la
                                                                                                           // valeur de
                                                                                                           // la clé
                                                                                                           // dans le
                                                                                                           // tuple de
                                                                                                           // la tete
                                                                                                           // est
                                                                                                           // différente
                                                                                                           // de la
                                                                                                           // valeur de
                                                                                                           // la clé
                                                                                                           // dans le
                                                                                                           // tuple du
                                                                                                           // corps
                                                                        egal = false; // la contrainte n'est pas
                                                                                      // satisfaite par le tuple t
                                                                        satisfait = false;
                                                                        break;
                                                                    } else {
                                                                        satisfait = true;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break; // Si on a trouvé un tuple qui satisfait la la partie de la tête
                                                   // concernée, on sort de la boucle et on passe à la partie de la tête
                                                   // d'après

                                        }
                                        // Si on arrive ici, c'est qu'on a parcouru tous les tuples de la table
                                        // concernée et qu'aucun ne satisfait la partie de la tête concernée
                                        List<String> keys = new ArrayList<String>();
                                        List<Object> values = new ArrayList<Object>();
                                        for (int j = 1; j < h.size(); j++) {
                                            // String s = "nullvalue";
                                            // Object o = s;
                                            keys.add(beforeequal(h.get(j)));
                                            values.add("nullvalue" + nullvalue);
                                            nullvalue++;
                                        }
                                        egal = true;
                                        for (int j = 1; j < h.size(); j++) {// Pour chaque clé de la tête
                                            egal = true;
                                            for (var b : tgd.getBody()) {// on regarde dans le corps les clés qui sont
                                                                         // les memes que la clé de la tête qu'on
                                                                         // regarde
                                                for (var t : b) {// pour chaque string dans la partie du corps qu'on
                                                                 // regarde
                                                    if (egal == false)
                                                        break;
                                                    if (afterequal(t).equals(afterequal(h.get(j)))) {// si il est egal à
                                                                                                     // la clé
                                                        for (var tuple : tuplesatisfying) {// On cherche le record qui
                                                                                           // appartient la table
                                                                                           // concernée
                                                            if (tuple.getTable().equals(beforeequal(b.get(0)))) {
                                                                values.set(j - 1, tuple.get(beforeequal(h.get(j))));// On
                                                                                                                    // met
                                                                                                                    // a
                                                                                                                    // jour
                                                                                                                    // la
                                                                                                                    // valeur
                                                                                                                    // liée
                                                                                                                    // a
                                                                                                                    // la
                                                                                                                    // clé
                                                                egal = false;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Record r = new Record(keys, values);
                                        allTuples.get(beforeequal(h.get(0))).add(r);// ajouter le record à l'ensemble
                                                                                    // des records de la table
                                        System.out.println("On a ajouté le record " + r + " à la table " + h.get(0));

                                        for (var tabl : this.getTables()) { // ajouter le record à la base de données
                                            if (tabl.getName().equals(beforeequal(h.get(0)))) {
                                                tabl.addRecord(r);
                                            }
                                        }

                                    }
                                }
                            }
                            tgd.markAsAltered(tuplesatisfying);
                            count += 1; // Si le tuple a déjà été satisfait par la TGD
                        }
                    }
                    if (count == applyOnTuples.size()) { // Si la TGD a été appliquée à tous les tuples qui la satisfont
                        countApplied += 1;
                    }
                } else {
                    var tablesAsList = new ArrayList<>(this.tables);
                    var egd = (EGD) constraint;
                    var matchedRecords = new ArrayList<Record>(); // Liste des records qui satisfont les contraintes
                    var firstTableName = egd.getBody().get(0).get(0); 
                    var firstTable = findTableByName(tablesAsList, firstTableName);
                    if (firstTable != null) {
                        var body = egd.getBody();
                        var firstTableBody = body.get(0);
                        var firstAttributeName = firstTableBody.get(1);
                        for (var record : firstTable.getRecords()) { // Pour chaque record de la table
                            var isSatisfied = true;
                            for (int i = 1; i < body.size(); i++) { // Pour chaque table dans le corps
                                var tableName = body.get(i).get(0); 
                                var otherTable = findTableByName(tablesAsList, tableName); 
                                if (otherTable != null) { 
                                    var otherTableBody = body.get(i); // Liste des attributs de la table étrangère
                                    var otherAttributeName = otherTableBody.get(1);
                                    var attributeMatch = false;
                                    for (var otherRecord : otherTable.getRecords()) { // Pour chaque record de la table étrangère
                                        if (record.get(firstAttributeName).equals(otherRecord.get(otherAttributeName))) { // Si les valeurs des attributs sont égales
                                            attributeMatch = true;
                                            break;
                                        }
                                    }
                                    if (!attributeMatch) {
                                        isSatisfied = false;
                                        // On sort de la boucle car le record ne satisfait pas la contrainte
                                        break;
                                    }
                                }
                            }
                            if (isSatisfied) {
                                matchedRecords.add(record); // On ajoute le record à la liste des records qui satisfont la contrainte
                            }
                        }
                    } else {
                        System.out.println("Table " + firstTableName + " not found");
                        // On ne fait rien car la table n'existe pas
                    }
                    for (var record : matchedRecords) { // Pour chaque record qui satisfait la contrainte
                        for (var attributeList : egd.getHead()) { // Pour chaque attribut de la tête
                            var tableName = attributeList.get(0);
                            var table = findTableByName(tablesAsList, tableName); // On cherche la table correspondante
                            if (table != null) {
                                for (int i = 1; i < attributeList.size(); i++) { // Pour chaque attribut de la table
                                    var attributeName = attributeList.get(i);
                                    String value = (String) record.get(attributeName);
                                    if (value != null && value.startsWith("nullvalue")) {
                                        record.set(attributeName, "nullvalue" + nullvalue);
                                        nullvalue++;
                                    } // On met à jour la valeur de l'attribut si elle commence par "nullvalue"
                                }
                            }
                        }
                    }
                    // Rebelotte pour les autres tables
                    for (var record : matchedRecords) {
                        for (var attributeList : egd.getHead()) {
                            var tableName = attributeList.get(0);
                            var table = findTableByName(tablesAsList, tableName);
                            if (table != null) {
                                for (int i = 1; i < attributeList.size(); i++) {
                                    var attributeName = attributeList.get(i);
                                    String value = (String) record.get(attributeName);
                                    if (value != null && !value.startsWith("nullvalue")) {
                                        for (var otherRecord : matchedRecords) {
                                            if (otherRecord != record && ((String) otherRecord.get(attributeName)).startsWith("nullvalue")) {
                                                otherRecord.set(attributeName, value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Finds a table by its name
     * 
     * @param tables
     * @param tableName
     * @return the table if it exists, null otherwise
     */
    private Table findTableByName(List<Table> tables, String tableName) {
        for (Table table : tables) {
            if (table.getName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    public void obliviousChase(List<TGD> contraintes, int milliseconds) {
        int count = 0;
        int countApplied = 0;
        long startTime = System.currentTimeMillis();
        Map<String, Set<Record>> allTuples = new HashMap<String, Set<Record>>();

        while (System.currentTimeMillis() - startTime < milliseconds * 1000 && countApplied < contraintes.size()) {
            count = 0;
            countApplied = 0;
            for (Table t : this.getTables()) {
                allTuples.put(t.getName(), t.getRecords());// Creation d'un dictionnaire qui associe à chaque table
                                                           // l'ensemble de ses tuples pour pouvoir manipuler
                                                           // facilemlent
            }

            for (final var tgd : contraintes) {
                count = 0;
                List<Set<Record>> ontuples = new ArrayList<Set<Record>>();// Liste des ensembles de tuples qui satisfont
                                                                          // une partie du corps de la TGD
                Set<Set<Record>> applyOnTuples = new HashSet<Set<Record>>();//
                for (var b : tgd.getBody()) {
                    System.out.println("On regarde la table " + b.get(0));
                    if (allTuples.get(beforeequal(b.get(0))) != null
                            && allTuples.get(beforeequal(b.get(0))).size() != 0) {
                        ontuples.add(allTuples.get(beforeequal(b.get(0))));
                    }

                }
                if (ontuples.size() == tgd.getBody().size()) {// Si on a autant de tables dans le corps de la TGD que de
                                                              // tables dans le dictionnaire : alors le corps est
                                                              // satisfait
                    applyOnTuples = genererCombinaisons(ontuples);
                    for (var tuplesatisfying : applyOnTuples) {// Pour chaque ensemble de tuples qui satisfait le corps
                                                               // de la TGD
                        // On a créé tous les sous ensembles de tuples qui satisfont le corps de la TGD
                        if (!(tgd.hasBeenAltered(tuplesatisfying))) {// Si l'ensemble n'a pas été deja satisfait :
                            for (int i = 0; i < tgd.getHead().size(); i++) {// Pour chaque table de la tête
                                var h = tgd.getHead().get(i);
                                if (allTuples.get(beforeequal(h.get(0))) == null) {
                                    allTuples.put(beforeequal(h.get(0)), new HashSet<Record>());
                                }
                                List<String> keys = new ArrayList<String>();
                                List<Object> values = new ArrayList<Object>();
                                for (int j = 1; j < h.size(); j++) {
                                    // String s = "nullvalue";
                                    // Object o = s;
                                    keys.add(beforeequal(h.get(j)));
                                    values.add("nullvalue" + nullvalue);
                                    nullvalue++;
                                }
                                boolean egal = true;
                                for (int j = 1; j < h.size(); j++) {// Pour chaque clé de la tête
                                    egal = true;
                                    for (var b : tgd.getBody()) {// on regarde dans le corps les clés qui sont les memes
                                                                 // que la clé de la tête qu'on regarde
                                        for (var t : b) {// pour chaque string dans la partie du corps qu'on regarde
                                            if (egal == false)
                                                break;
                                            if (afterequal(t).equals(afterequal(h.get(j)))) {// si il est egal à la clé
                                                for (var tuple : tuplesatisfying) {// On cherche le record qui
                                                                                   // appartient la table concernée
                                                    if (tuple.getTable().equals(beforeequal(b.get(0)))) {
                                                        values.set(j - 1, tuple.get(beforeequal(h.get(j))));// On met a
                                                                                                            // jour la
                                                                                                            // valeur
                                                                                                            // liée a la
                                                                                                            // clé
                                                        egal = false;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Record r = new Record(keys, values);
                                allTuples.get(beforeequal(h.get(0))).add(r);// ajouter le record à l'ensemble des
                                                                            // records de la table
                                System.out.println("On a ajouté le record " + r + " à la table " + h.get(0));
                                for (var tabl : this.getTables()) { // ajouter le record à la base de données
                                    if (tabl.getName().equals(beforeequal(h.get(0)))) {
                                        tabl.addRecord(r);
                                    }
                                }
                                tgd.markAsAltered(tuplesatisfying);
                                for (var a : tgd.getAlteredTuples()) {
                                    System.out
                                            .println("On a appliqué la TGD " + tgd + " sur l'ensemble de tuples " + a);

                                }
                            }
                        } else {// Si le tuple a déjà été satisfait par la TGD
                            count += 1;

                        }
                    }
                }
                if (count == applyOnTuples.size()) {
                    countApplied += 1;
                }
            }
        }

    }

    public void obliviousSkolemChase(List<TGD> contraintes) {
        int count = 0;
        int countApplied = 0;
        Map<String, Set<Record>> allTuples = new HashMap<String, Set<Record>>();

        while (countApplied < contraintes.size()) {
            count = 0;
            countApplied = 0;
            for (Table t : this.getTables()) {
                allTuples.put(t.getName(), t.getRecords());// Creation d'un dictionnaire qui associe à chaque table
                                                           // l'ensemble de ses tuples pour pouvoir manipuler
                                                           // facilemlent
            }

            for (final var tgd : contraintes) {
                count = 0;
                List<Set<Record>> ontuples = new ArrayList<Set<Record>>();// Liste des ensembles de tuples qui satisfont
                                                                          // une partie du corps de la TGD
                Set<Set<Record>> applyOnTuples = new HashSet<Set<Record>>();//
                for (var b : tgd.getBody()) {
                    System.out.println("On regarde la table " + b.get(0));
                    if (allTuples.get(beforeequal(b.get(0))) != null
                            && allTuples.get(beforeequal(b.get(0))).size() != 0) {
                        ontuples.add(allTuples.get(beforeequal(b.get(0))));
                    }

                }
                if (ontuples.size() == tgd.getBody().size()) {// Si on a autant de tables dans le corps de la TGD que de
                                                              // tables dans le dictionnaire : alors le corps est
                                                              // satisfait
                    applyOnTuples = genererCombinaisons(ontuples);

                    for (var tuplesatisfying : applyOnTuples) {// Pour chaque ensemble de tuples qui satisfait le corps
                                                               // de la TGD
                        // On a créé tous les sous ensembles de tuples qui satisfont le corps de la TGD
                        if (!(tgd.hasBeenAltered(tuplesatisfying))) {// Si l'ensemble n'a pas été deja satisfait :
                            for (int i = 0; i < tgd.getHead().size(); i++) {// Pour chaque table de la tête
                                var h = tgd.getHead().get(i);
                                if (allTuples.get(beforeequal(h.get(0))) == null) {
                                    allTuples.put(beforeequal(h.get(0)), new HashSet<Record>());
                                }
                                List<String> keys = new ArrayList<String>();
                                List<Object> values = new ArrayList<Object>();
                                for (int j = 1; j < h.size(); j++) {
                                    // String s = "nullvalue";
                                    // Object o = s;
                                    keys.add(beforeequal(h.get(j)));
                                    values.add("nullvalue" + nullvalue);
                                    nullvalue++;
                                }
                                Set<String> egalise = new HashSet<String>();
                                boolean egal = true;
                                for (int j = 1; j < h.size(); j++) {// Pour chaque clé de la tête
                                    egal = true;
                                    for (var b : tgd.getBody()) {// on regarde dans le corps les clés qui sont les memes
                                                                 // que la clé de la tête qu'on regarde
                                        for (var t : b) {// pour chaque string dans la partie du corps qu'on regarde
                                            if (egal == false)
                                                break;
                                            if (afterequal(t).equals(afterequal(h.get(j)))) {// si il est egal à la clé
                                                egalise.add(beforeequal(h.get(j)));
                                                for (var tuple : tuplesatisfying) {// On cherche le record qui
                                                                                   // appartient la table concernée
                                                    if (tuple.getTable().equals(beforeequal(b.get(0)))) {
                                                        values.set(j - 1, tuple.get(beforeequal(h.get(j))));// On met a
                                                                                                            // jour la
                                                                                                            // valeur
                                                                                                            // liée a la
                                                                                                            // clé
                                                        egal = false;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                // modifier les nullvalue à f(x) ou x!=nullvalue
                                String image = "";
                                for (var val : values) {
                                    if (val instanceof String) {
                                        String val1 = (String) val;
                                        if (egalise.contains(keys.get(values.indexOf(val)))) {// Si la valeur est une
                                                                                              // valeur egalisée, on
                                                                                              // applique la fonction f
                                                                                              // dessus pour la suite
                                            image = f(val1);
                                            break;
                                        }
                                    }
                                }
                                for (int k = 0; k < values.size(); k++) {
                                    if (values.get(k) instanceof String) {
                                        if (!egalise.contains(keys.get(k))) {
                                            values.set(k, image);
                                            image = f(image);
                                        }
                                    }
                                }
                                Record r = new Record(keys, values);
                                allTuples.get(beforeequal(h.get(0))).add(r);// ajouter le record à l'ensemble des
                                                                            // records de la table
                                System.out.println("On a ajouté le record " + r + " à la table " + h.get(0));
                                for (var tabl : this.getTables()) { // ajouter le record à la base de données
                                    if (tabl.getName().equals(beforeequal(h.get(0)))) {
                                        tabl.addRecord(r);
                                    }
                                }
                                tgd.markAsAltered(tuplesatisfying);
                                for (var a : tgd.getAlteredTuples()) {
                                    System.out
                                            .println("On a appliqué la TGD " + tgd + " sur l'ensemble de tuples " + a);

                                }
                            }
                        } else {// Si le tuple a déjà été satisfait par la TGD
                            count += 1;

                        }
                    }
                }
                if (count == applyOnTuples.size()) {// Si tous les tuples ont été satisfaits par la TGD
                    countApplied += 1;
                }

            }
        }

    }

    private static String afterequal(String s) {
        String[] parts = s.split("=");
        return parts[1];
    }

    private static String beforeequal(String s) {
        String[] parts = s.split("=");
        return parts[0];
    }

    private static Set<Set<Record>> genererCombinaisons(List<Set<Record>> ontuples) {
        Set<Set<Record>> combinaisons = new HashSet<Set<Record>>();
        genererCombinaisonsRec(ontuples, 0, new HashSet<Record>(), combinaisons);
        return combinaisons;
    }

    private static void genererCombinaisonsRec(List<Set<Record>> ontuples, int index,
            Set<Record> combinaisonActuelle,
            Set<Set<Record>> combinaisons) {
        if (index == ontuples.size()) {
            combinaisons.add(combinaisonActuelle);
            return;
        }
        Set<Record> tuplesActuel = ontuples.get(index);
        for (var tuple : tuplesActuel) {
            Set<Record> nouvelleComb = new HashSet<Record>(combinaisonActuelle);
            nouvelleComb.add(tuple);
            genererCombinaisonsRec(ontuples, index + 1, nouvelleComb, combinaisons);
        }
    }

    private static String f(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            return ("nullvalue" + "_" + s);
        } else if (o instanceof Integer) {
            int i = (int) o;
            return ("nullvalue" + "_" + i);
        } else {
            return ("");
        }
    }

}
