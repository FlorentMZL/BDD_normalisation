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
    public void standardChase(final List<Constraint> constraints) {
        int totalTGDs = 0;
        for (var constraint : constraints) {
            if (constraint instanceof TGD)
                totalTGDs++;
        }
        var appliedTGDs = 0; // Nombre de contraintes TGD appliquées à la base de données
        var loopCount = 0; // Nombre de contraintes TGD appliquées à la base de données lors d'un tour de boucle
        boolean onceStabilized = false, twiceStabilized = false;
        while (!onceStabilized && !twiceStabilized && appliedTGDs != totalTGDs) {
            loopCount = 0;
            appliedTGDs = 0;
            var allTuples = new HashMap<String, Set<Record>>();
            for (var table : this.getTables())
                allTuples.put(table.getName(), table.getRecords()); // Dictionnaire qui associe à chaque table l'ensemble de ses tuples pour faciliter les manipulations
            for (final var constraint : constraints) {
                loopCount = 0;
                if (constraint instanceof TGD) {
                    TGD tgd = (TGD) constraint;
                    List<Set<Record>> tupleSets = new ArrayList<Set<Record>>(); // Liste des ensembles de tuples qui satisfont une partie du corps de la TGD
                    Set<Set<Record>> satisfyingTuples = new HashSet<Set<Record>>(); // Liste des ensembles de tuples qui satisfont le corps de la TGD
                    for (var b : tgd.getBody()) {
                        var toFind = stripNumerals(b.get(0)); // On cherche la table dans le dictionnaire
                        if (allTuples.get(toFind) != null && allTuples.get(toFind).size() != 0)
                            tupleSets.add(allTuples.get(toFind)); // Si la table existe et n'est pas vide, on l'ajoute à la liste des ensembles de tuples qui satisfont une partie du corps de la TGD
                    }
                    if (tupleSets.size() == tgd.getBody().size()) {
                        satisfyingTuples = generateCombinations(tupleSets); // Generation de tous les ensembles de tuples qui peuvent satisfaire le corps de la TGD
                        for (var satisfyingTuple : satisfyingTuples) {
                            if (!(tgd.hasBeenAltered(satisfyingTuple))) {
                                for (int i = 0; i < tgd.getHead().size(); i++) {
                                    var h = tgd.getHead().get(i);
                                    if (allTuples.get(stripNumerals(h.get(0))) == null || allTuples.get(stripNumerals(h.get(0))).size() == 0) {
                                        allTuples.put(stripNumerals(h.get(0)), new HashSet<Record>());
                                        List<String> keys = new ArrayList<String>();
                                        List<Object> values = new ArrayList<Object>();
                                        for (int j = 1; j < h.size(); j++) {
                                            keys.add(stripNumerals(h.get(j)));
                                            values.add("nullvalue" + nullvalue);
                                            nullvalue++;
                                        }
                                        boolean egal = true;
                                        for (int j = 1; j < h.size(); j++) {
                                            egal = true;
                                            for (var b : tgd.getBody()) {
                                                for (var t : b) {
                                                    if (egal == false) break;
                                                    if (t.equals(h.get(j))) { // si il est egal à la clé
                                                        for (var tuple : satisfyingTuple) { // On cherche le record qui appartient à la table concernée
                                                            if (tuple.getTable().equals(stripNumerals(b.get(0)))) {
                                                                values.set(j - 1, tuple.get(stripNumerals(h.get(j)))); // On met a jour la valeur liée a la clé
                                                                egal = false;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Record r = new Record(keys, values);
                                        allTuples.get(stripNumerals(h.get(0))).add(r); // Ajouter le record à l'ensemble des records de la table
                                        boolean existe2 = false;
                                        for (var tabl : this.getTables()) { // ajouter le record à la base de données
                                            if (tabl.getName().equals(stripNumerals(h.get(0)))) {
                                                tabl.addRecord(r);
                                                existe2 = true;
                                                break;
                                            }
                                        }
                                        if (!existe2) {
                                            Table ajoute = new Table(stripNumerals(h.get(0)), keys);
                                            ajoute.addRecord(r);
                                            this.getTables().add(ajoute);
                                        }
                                        tgd.markAsAltered(satisfyingTuple);
                                    } else {
                                        boolean satisfait = false;
                                        boolean egal = true;
                                        for (var t : allTuples.get(stripNumerals(h.get(0)))) { // Pour les tuples correspondant à la table concernée dans une partie de la tête:
                                            egal = true;
                                            for (int j = 1; j < h.size(); j++) { // pour chaque clé de cette partie de la tête
                                                if (egal == false) break;
                                                for (var b : tgd.getBody()) { // Pour chaque tuple dans le corps
                                                    if (egal == false) break;
                                                    for (var cle : b) { // pour chaque clé du tuple dans le corps
                                                        if (egal == false) break;
                                                        if (satisfait) break;
                                                        if (cle.equals(h.get(j))) { // si la clé est égale à la clé de la tete
                                                            for (var tuple : satisfyingTuple) { // Pour chaque tuple dans les tuples qui satisfont le corps
                                                                if (satisfait) break;
                                                                if (tuple.getTable().equals(stripNumerals(b.get(0)))) { // si ce tuple appartient à la table concernée
                                                                    if (tuple.get(stripNumerals(h.get(j))) != t.get(stripNumerals(h.get(j)))) { // si la valeur de la clé dans le tuple de la tete est différente de la valeur de la clé dans le tuple du corps
                                                                        egal = false; // la contrainte n'est pas
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
                                            break; // Si on a trouvé un tuple qui satisfait la la partie de la tête concernée, on sort de la boucle et on passe à la partie de la tête d'après
                                        }
                                        // Si on arrive ici, c'est qu'on a parcouru tous les tuples de la table concernée et qu'aucun ne satisfait la partie de la tête concernée
                                        List<String> keys = new ArrayList<String>();
                                        List<Object> values = new ArrayList<Object>();
                                        for (int j = 1; j < h.size(); j++) {
                                            // String s = "nullvalue";
                                            // Object o = s;
                                            keys.add(stripNumerals(h.get(j)));
                                            values.add("nullvalue" + nullvalue);
                                            nullvalue++;
                                        }
                                        egal = true;
                                        for (int j = 1; j < h.size(); j++) { // Pour chaque clé de la tête
                                            egal = true;
                                            for (var b : tgd.getBody()) { // on regarde dans le corps les clés qui sont les memes que la clé de la tête qu'on regarde
                                                for (var t : b) {// pour chaque string dans la partie du corps qu'on regarde
                                                    if (egal == false) break;
                                                    if (t.equals(h.get(j))) { // si il est egal à la clé
                                                        for (var tuple : satisfyingTuple) { // On cherche le record qui appartient la table concernée
                                                            if (tuple.getTable().equals(stripNumerals(b.get(0)))) {
                                                                values.set(j - 1, tuple.get(stripNumerals(h.get(j))));// On met a jour la valeur liée a la clé
                                                                egal = false;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Record r = new Record(keys, values);
                                        allTuples.get(stripNumerals(h.get(0))).add(r); // ajouter le record à l'ensemble des records de la table
                                        boolean existe2 = false;
                                        for (var tabl : this.getTables()) { // ajouter le record à la base de données
                                            if (tabl.getName().equals(stripNumerals(h.get(0)))) {
                                                tabl.addRecord(r);
                                                existe2 = true;
                                                break;
                                            }
                                        }
                                        if (existe2 == false) {
                                            Table ajoute = new Table(stripNumerals(h.get(0)), keys);
                                            ajoute.addRecord(r);
                                            this.getTables().add(ajoute);
                                        }
                                    }
                                }
                            }
                            tgd.markAsAltered(satisfyingTuple);
                            loopCount += 1; // Si le tuple a déjà été satisfait par la TGD
                        }
                    }
                    if (loopCount == satisfyingTuples.size()) { // Si la TGD a été appliquée à tous les tuples qui la satisfont
                        appliedTGDs += 1;
                    }
                } else {
                    if (!onceStabilized) {
                        onceStabilized = ((EGD) constraint).apply(this);
                    } else {
                        twiceStabilized = ((EGD) constraint).apply(this);
                        if (!twiceStabilized) onceStabilized = false;
                    }
                }
            }
        }
    }

    public void obliviousChase(List<TGD> contraintes, int milliseconds) {
        int loopCount = 0;
        int appliedTGDs = 0;
        long startTime = System.currentTimeMillis();
        Map<String, Set<Record>> allTuples = new HashMap<String, Set<Record>>();

        while (System.currentTimeMillis() - startTime < milliseconds * 1000 && appliedTGDs < contraintes.size()) {
            loopCount = 0;
            appliedTGDs = 0;
            for (Table t : this.getTables()) {
                allTuples.put(t.getName(), t.getRecords());// Creation d'un dictionnaire qui associe à chaque table
                                                           // l'ensemble de ses tuples pour pouvoir manipuler
                                                           // facilemlent
            }

            for (final var tgd : contraintes) {
                loopCount = 0;
                List<Set<Record>> tupleSets = new ArrayList<Set<Record>>();// Liste des ensembles de tuples qui
                                                                           // satisfont
                                                                           // une partie du corps de la TGD
                Set<Set<Record>> satisfyingTuples = new HashSet<Set<Record>>();//
                for (var b : tgd.getBody()) {
                    System.out.println("On regarde la table " + b.get(0));
                    if (allTuples.get(stripNumerals(b.get(0))) != null
                            && allTuples.get(stripNumerals(b.get(0))).size() != 0) {
                        tupleSets.add(allTuples.get(stripNumerals(b.get(0))));
                    }

                }
                if (tupleSets.size() == tgd.getBody().size()) {// Si on a autant de tables dans le corps de la TGD que
                                                               // de
                                                               // tables dans le dictionnaire : alors le corps est
                                                               // satisfait
                    satisfyingTuples = generateCombinations(tupleSets);
                    for (var satisfyingTuple : satisfyingTuples) {// Pour chaque ensemble de tuples qui satisfait le
                                                                  // corps
                        // de la TGD
                        // On a créé tous les sous ensembles de tuples qui satisfont le corps de la TGD
                        if (!(tgd.hasBeenAltered(satisfyingTuple))) {// Si l'ensemble n'a pas été deja satisfait :
                            for (int i = 0; i < tgd.getHead().size(); i++) {// Pour chaque table de la tête
                                var h = tgd.getHead().get(i);
                                if (allTuples.get(stripNumerals(h.get(0))) == null) {
                                    allTuples.put(stripNumerals(h.get(0)), new HashSet<Record>());
                                }
                                List<String> keys = new ArrayList<String>();
                                List<Object> values = new ArrayList<Object>();
                                for (int j = 1; j < h.size(); j++) {
                                    // String s = "nullvalue";
                                    // Object o = s;
                                    keys.add(stripNumerals(h.get(j)));
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
                                            if (t.equals(h.get(j))) {// si il est egal à la clé
                                                for (var tuple : satisfyingTuple) {// On cherche le record qui
                                                                                   // appartient la table concernée
                                                    if (tuple.getTable().equals(stripNumerals(b.get(0)))) {
                                                        values.set(j - 1, tuple.get(stripNumerals(h.get(j))));// On met
                                                                                                              // a
                                                                                                              // jour la
                                                                                                              // valeur
                                                                                                              // liée a
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
                                allTuples.get(stripNumerals(h.get(0))).add(r);// ajouter le record à l'ensemble des
                                                                              // records de la table
                                System.out.println("On a ajouté le record " + r + " à la table " + h.get(0));
                                boolean existe2 = false;
                                for (var tabl : this.getTables()) { // ajouter le record à la base de données
                                    if (tabl.getName().equals(stripNumerals(h.get(0)))) {
                                        tabl.addRecord(r);
                                        existe2 = true;
                                        break;
                                    }
                                }
                                if (existe2 == false) {
                                    Table ajoute = new Table(stripNumerals(h.get(0)), keys);
                                    ajoute.addRecord(r);
                                    this.getTables().add(ajoute);

                                }
                                tgd.markAsAltered(satisfyingTuple);
                                for (var a : tgd.getAlteredTuples()) {
                                    System.out
                                            .println("On a appliqué la TGD " + tgd + " sur l'ensemble de tuples " + a);

                                }
                            }
                        } else {// Si le tuple a déjà été satisfait par la TGD
                            loopCount += 1;

                        }
                    }
                }
                if (loopCount == satisfyingTuples.size()) {
                    appliedTGDs += 1;
                }
            }
        }

    }

    public void obliviousSkolemChase(List<TGD> contraintes) {
        int loopCount = 0;
        int appliedTGDs = 0;
        Map<String, Set<Record>> allTuples = new HashMap<String, Set<Record>>();

        while (appliedTGDs < contraintes.size()) {
            loopCount = 0;
            appliedTGDs = 0;
            for (Table t : this.getTables()) {
                allTuples.put(t.getName(), t.getRecords());// Creation d'un dictionnaire qui associe à chaque table
                                                           // l'ensemble de ses tuples pour pouvoir manipuler
                                                           // facilemlent
            }

            for (final var tgd : contraintes) {
                loopCount = 0;
                List<Set<Record>> tupleSets = new ArrayList<Set<Record>>();// Liste des ensembles de tuples qui
                                                                           // satisfont
                                                                           // une partie du corps de la TGD
                Set<Set<Record>> satisfyingTuples = new HashSet<Set<Record>>();//
                for (var b : tgd.getBody()) {
                    System.out.println("On regarde la table " + b.get(0));
                    if (allTuples.get(stripNumerals(b.get(0))) != null
                            && allTuples.get(stripNumerals(b.get(0))).size() != 0) {
                        tupleSets.add(allTuples.get(stripNumerals(b.get(0))));
                    }

                }
                if (tupleSets.size() == tgd.getBody().size()) {// Si on a autant de tables dans le corps de la TGD que
                                                               // de
                                                               // tables dans le dictionnaire : alors le corps est
                                                               // satisfait
                    satisfyingTuples = generateCombinations(tupleSets);

                    for (var satisfyingTuple : satisfyingTuples) {// Pour chaque ensemble de tuples qui satisfait le
                                                                  // corps
                        // de la TGD
                        // On a créé tous les sous ensembles de tuples qui satisfont le corps de la TGD
                        if (!(tgd.hasBeenAltered(satisfyingTuple))) {// Si l'ensemble n'a pas été deja satisfait :
                            for (int i = 0; i < tgd.getHead().size(); i++) {// Pour chaque table de la tête
                                var h = tgd.getHead().get(i);
                                if (allTuples.get(stripNumerals(h.get(0))) == null) {
                                    allTuples.put(stripNumerals(h.get(0)), new HashSet<Record>());
                                }
                                List<String> keys = new ArrayList<String>();
                                List<Object> values = new ArrayList<Object>();
                                for (int j = 1; j < h.size(); j++) {
                                    // String s = "nullvalue";
                                    // Object o = s;

                                    keys.add(stripNumerals(h.get(j)));

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
                                            if (t.equals(h.get(j))) {// si il est egal à la clé
                                                egalise.add(stripNumerals(h.get(j)));
                                                for (var tuple : satisfyingTuple) {// On cherche le record qui
                                                                                   // appartient la table concernée
                                                    if (tuple.getTable().equals(stripNumerals(b.get(0)))) {
                                                        values.set(j - 1, tuple.get(stripNumerals(h.get(j))));// On met
                                                                                                              // a
                                                                                                              // jour la
                                                                                                              // valeur
                                                                                                              // liée a
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
                                Record r;
                                System.out.println(h.get(0));
                                if (stripNumerals(h.get(0)).equals("TEMP")) {
                                    List<String> keys2 = List.of("temp1", "temp2");
                                    r = new Record(keys2, values);
                                } else {
                                    r = new Record(keys, values);
                                }

                                allTuples.get(stripNumerals(h.get(0))).add(r);// ajouter le record à l'ensemble des
                                                                              // records de la table
                                System.out.println("On a ajouté le record " + r + " à la table " + h.get(0));
                                boolean existe2 = false;
                                for (var tabl : this.getTables()) { // ajouter le record à la base de données
                                    if (tabl.getName().equals(stripNumerals(h.get(0)))) {
                                        tabl.addRecord(r);
                                        existe2 = true;
                                        break;
                                    }
                                }
                                if (existe2 == false) {
                                    for (var l : keys) {
                                        System.out.println("On a ajouté la clé " + l + " à la table " + h.get(0));
                                    }
                                    Table ajoute;
                                    if (h.get(0).equals("TEMP")) {
                                        ajoute = new Table(stripNumerals(h.get(0)), List.of("temp1", "temp2"));
                                    } else {
                                        ajoute = new Table(stripNumerals(h.get(0)), keys);
                                    }
                                    ajoute.addRecord(r);
                                    this.getTables().add(ajoute);

                                }
                                tgd.markAsAltered(satisfyingTuple);
                                for (var a : tgd.getAlteredTuples()) {
                                    System.out
                                            .println("On a appliqué la TGD " + tgd + " sur l'ensemble de tuples " + a);

                                }
                            }
                        } else {// Si le tuple a déjà été satisfait par la TGD
                            loopCount += 1;

                        }
                    }
                }
                if (loopCount == satisfyingTuples.size()) {// Si tous les tuples ont été satisfaits par la TGD
                    appliedTGDs += 1;
                }

            }
        }

    }

    public void skolemEGD(List<Constraint> constraints) {
        // Converting all EGDs to TGDs
        List<TGD> tgds = new ArrayList<TGD>();

        for (var c : constraints) {
            if (c instanceof EGD) {
                EGD egd = (EGD) c;
                TGD tgd = egd.convertToTGD();
                tgds.add(tgd);
            } else {
                TGD tgd = (TGD) c;
                tgds.add(tgd);
            }
        }

        obliviousSkolemChase(tgds);
        // On rétablit les égalités
        // TODO : On a la table TEMP qui contient (temp1,temp2) et on veut rétablir les
        // égalités : Si on a (nullvalue + "integer", constante) on cherche toutes les occurences de
        // cette nullvalue + "integer" dans le bdd et on remplace par la constante. Si on a 2 null, on cherche toutes les occ d'un des deux et on remplace par l'autre, si on a 2
        // constantes, on met les 2 à une valeur nulle
        var tempTable = this.getTable("TEMP");

        for (var record : tempTable.getRecords()) {
            var temp1str = (String) record.get("temp1");
            var temp2str = (String) record.get("temp2");
            if (temp1str.startsWith("nullvalue")) {
                if (temp2str.startsWith("nullvalue")) {
                    // On cherche toutes les occurences de temp1str et on les remplace par temp2str et vice versa
                    for (var table : this.getTables()) {
                        for (var record2 : table.getRecords()) {
                            for (var key : record2.getAttributes()) {
                                if (record2.get(key).equals(temp1str)) record2.set(key, temp2str);
                                else if (record2.get(key).equals(temp2str)) record2.set(key, temp1str);
                            }
                        }
                    }
                } else {
                    // On cherche toutes les occurences de temp1str et on les remplace par temp2str
                    for (var table : this.getTables()) {
                        for (var record2 : table.getRecords()) {
                            for (var key : record2.getAttributes()) {
                                if (record2.get(key).equals(temp1str)) record2.set(key, temp2str);
                            }
                        }
                    }
                }
            } else {
                if (temp2str.startsWith("nullvalue")) {
                    // On cherche toutes les occurences de temp2str et on les remplace par temp1str
                    for (var table : this.getTables()) {
                        for (var record2 : table.getRecords()) {
                            for (var key : record2.getAttributes()) {
                                if (record2.get(key).equals(temp2str)) record2.set(key, temp1str);
                            }
                        }
                    }
                } else {
                    // On met les 2 à une valeur nulle
                    for (var table : this.getTables()) {
                        for (var record2 : table.getRecords()) {
                            for (var key : record2.getAttributes()) {
                                if (record2.get(key).equals(temp1str)) {
                                    record2.set(key, "nullvalue" + String.valueOf(Database.nullvalue));
                                    Database.nullvalue += 1;
                                } else if (record2.get(key).equals(temp2str)) {
                                    record2.set(key, "nullvalue" + String.valueOf(Database.nullvalue));
                                    Database.nullvalue += 1;
                                }
                            }
                        }
                    }
                }
            }
        }
        // On supprime la table TEMP
        this.getTables().remove(tempTable);
        
    }

   

    private static Set<Set<Record>> generateCombinations(List<Set<Record>> tupleSets) {
        Set<Set<Record>> combinaisons = new HashSet<Set<Record>>();
        generateCombinationsRec(tupleSets, 0, new HashSet<Record>(), combinaisons);
        return combinaisons;
    }

    private static void generateCombinationsRec(List<Set<Record>> tupleSets, int index,
            Set<Record> combinaisonActuelle,
            Set<Set<Record>> combinaisons) {
        if (index == tupleSets.size()) {
            combinaisons.add(combinaisonActuelle);
            return;
        }
        Set<Record> tuplesActuel = tupleSets.get(index);
        for (var tuple : tuplesActuel) {
            Set<Record> nouvelleComb = new HashSet<Record>(combinaisonActuelle);
            nouvelleComb.add(tuple);
            generateCombinationsRec(tupleSets, index + 1, nouvelleComb, combinaisons);
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

    public static String stripNumerals(String s) {// Pour passer de attribut1 à attribut sans le numéro
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                return s.substring(0, i);
            }
        }
        return s;
    }
}
