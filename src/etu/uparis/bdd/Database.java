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
    private String name;
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
    
    @Override
    public final String toString() {
        final var builder = new StringBuilder();
        builder.append("Database: ");
        builder.append(this.name);
        builder.append("\n\n");
        for (final var table : this.tables) {
            builder.append(table);
            builder.append("\n");
        }
        return builder.toString().strip();
    }
    


    public void standardChase(List<Constraint> constraints) {
        Map<String, Set<Record>> alltuples = new HashMap<String, Set<Record>>();
        for (Table t : this.getTables()){
            alltuples.put(t.getName(), t.getRecords());//Creation d'un dictionnaire qui associe à chaque table l'ensemble de ses tuples pour pouvoir manipuler facilemlent
        }


        for (final var constraint : constraints) {

            //TGD  : sous forme R(w)^S(w)^.. -> R'(w)^S'(w)^..
            if (constraint instanceof TGD){
                TGD tgd = (TGD) constraint; 
                System.out.println("TGD : " + tgd);
                List<Set<Record>> ontuples = new ArrayList<Set<Record>>();//Liste des ensembles de tuples qui satisfont une partie du corps de la TGD
                Set<Set<Record>> applyOnTuples = new HashSet<Set<Record>>();//
                for(var b: tgd.getBody()){

                    if (alltuples.get(b.get(0))!= null && alltuples.get(b.get(0)).size()!=0){
                        ontuples.add(alltuples.get(b.get(0)));
                    }
                   
                }
                if (ontuples.size() == tgd.getBody().size()){//Si on a autant de tables dans le corps de la TGD que de tables dans le dictionnaire : alors le corps est satisfait
                    applyOnTuples = genererCombinaisons(ontuples);
                //On a créé tous les sous ensembles de tuples qui satisfont le corps de la TGD
                    for (var tuplesatisfying : applyOnTuples){//Pour chaque ensemble de tuples qui satisfait le corps de la TGD
                        if (!(tgd.isApplied(tuplesatisfying))){//Si l'ensemble n'a pas été deja satisfait : 
                            for (int i = 0; i< tgd.getHead().size(); i++){//Pour chaque table de la tête
                                var h = tgd.getHead().get(i);
                                if (alltuples.get(h.get(0)) == null|| alltuples.get(h.get(0)).size()==0){//Si la table est vide, alors il faut créer un record pour satisfaire la tête 
                                    alltuples.put(h.get(0), new HashSet<Record>());
                                    List <String> keys = new ArrayList<String>();
                                    List <Object> values = new ArrayList<Object>();
                                    for(int j = 1; j<h.size(); j++){
                                        //String s = "nullvalue";
                                        //  Object o = s;  
                                        keys.add(h.get(j));
                                        values.add("nullvalue");
                                    }
                                    boolean egal = true; 
                                        for(int j = 1; j<h.size(); j++){//Pour chaque clé de la tête
                                        egal = true; 
                                        for (var b : tgd.getBody()){//on regarde dans le corps les clés qui sont les memes que la clé de la tête qu'on regarde
                                            for (var t : b){//pour chaque string dans la partie du corps qu'on regarde
                                                if (egal == false) break; 
                                                if  (t.equals(h.get(j))){//si il est egal à la clé
                                                    for (var tuple : tuplesatisfying){//On cherche le record qui appartient la table concernée 
                                                        if (tuple.getTable().equals(b.get(0))){
                                                            values.set(j-1, tuple.get(h.get(j)));//On met a jour la valeur liée a la clé 
                                                            egal = false; 
                                                            break;
                                                        }
                                                    }
                                                }                                    
                                            }
                                        }
                                    }
                                Record r = new Record(keys, values);
                                alltuples.get(h.get(0)).add(r);//ajouter le record à l'ensemble des records de la table
                                System.out.println("On a ajouté le record "+r+" à la table "+h.get(0));
                                for (var tabl : this.getTables()){ //ajouter le record à la base de données
                                    if (tabl.getName().equals(h.get(0))){
                                        tabl.addRecord(r);
                                    }
                                }
                                tgd.addApplied(tuplesatisfying);
                            }
                            else {
                                int comptSat = 0; 
                                boolean satisfait = false; 
                                boolean egal = true; 
                                for ( var t : alltuples.get(h.get(0))){//Pour les tuples correspondant à la table concernée dans une partie de la tête : 
                                    egal = true;
                                    for(int j = 1; j<h.size(); j++){//pour chaque clé de cette partie de la tête 
                                        if (egal == false)break;
                                            for (var b : tgd.getBody()){//Pour chaque tuple dans le corps 
                                                if (egal == false) break; 
                                            for(var cle : b){//pour chaque clé du tuple dans le corps 
                                                if (egal == false) break;
                                                if (satisfait)break; 
                                                if  (cle.equals(h.get(j))){//si la clé est égale à la clé de la tete
                                                    for(var tuple : tuplesatisfying){//Pour chaque tuple dans les tuples qui satisfont le corps 
                                                        if (satisfait) break; 
                                                        if (tuple.getTable().equals(b.get(0))){//si ce tuple appartient à la table concernée 
                                                            if (tuple.get(h.get(j))!= t.get(h.get(j))){ //si la valeur de la clé dans le tuple de la tete est différente de la valeur de la clé dans le tuple du corps
                                                                egal = false; //la contrainte n'est pas satisfaite par le tuple t 
                                                                satisfait = false;
                                                                break;
                                                            }
                                                            else{
                                                                satisfait = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }   
                                        }
                                    }
                                    break; //Si on a trouvé un tuple qui satisfait la la partie de la tête concernée, on sort de la boucle et on passe à la partie de la tête d'après

                                }
                                //Si on arrive ici, c'est qu'on a parcouru tous les tuples de la table concernée et qu'aucun ne satisfait la partie de la tête concernée
                                List <String> keys = new ArrayList<String>();
                                List <Object> values = new ArrayList<Object>();
                                for(int j = 1; j<h.size(); j++){
                                    //String s = "nullvalue";
                                    //  Object o = s;  
                                    keys.add(h.get(j));
                                    values.add("nullvalue");
                                }
                                egal = true; 
                                    for(int j = 1; j<h.size(); j++){//Pour chaque clé de la tête
                                    egal = true; 
                                    for (var b : tgd.getBody()){//on regarde dans le corps les clés qui sont les memes que la clé de la tête qu'on regarde
                                        for (var t : b){//pour chaque string dans la partie du corps qu'on regarde
                                            if (egal == false) break; 
                                            if  (t.equals(h.get(j))){//si il est egal à la clé
                                                for (var tuple : tuplesatisfying){//On cherche le record qui appartient la table concernée 
                                                    if (tuple.getTable().equals(b.get(0))){
                                                        values.set(j-1, tuple.get(h.get(j)));//On met a jour la valeur liée a la clé 
                                                        egal = false; 
                                                        break;
                                                    }
                                                }
                                            }                                    
                                        }
                                    }
                                }
                                Record r = new Record(keys, values);
                                alltuples.get(h.get(0)).add(r);//ajouter le record à l'ensemble des records de la table
                                System.out.println("On a ajouté le record "+r+" à la table "+h.get(0));
                                
                                for (var tabl : this.getTables()){ //ajouter le record à la base de données
                                    if (tabl.getName().equals(h.get(0))){
                                        tabl.addRecord(r);
                                    }
                                }
                                
                           }       
                        }
                    }
                    tgd.addApplied(tuplesatisfying);
                }
            }
        }
        else{
            //EGD 
        }
        }
    }
    private static Set<Set<Record>> genererCombinaisons (List<Set<Record>> ontuples){
        Set<Set<Record>> combinaisons = new HashSet<Set<Record>>();
        genererCombinaisonsRec (ontuples, 0,  new HashSet<Record>(), combinaisons);
        return combinaisons; 
    }
    private static void genererCombinaisonsRec(List<Set<Record>> ontuples, int index, Set<Record> combinaisonActuelle, Set<Set<Record>> combinaisons ){
        if (index == ontuples.size()) {
            combinaisons.add(combinaisonActuelle);
            return;
        }
        Set<Record> tuplesActuel = ontuples.get(index);
        for (var tuple : tuplesActuel ){
            Set<Record> nouvelleComb = new HashSet<Record>(combinaisonActuelle);
            nouvelleComb.add(tuple);
            genererCombinaisonsRec(ontuples, index+1, nouvelleComb, combinaisons);
        }
    }

}
