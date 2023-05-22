package etu.uparis.bdd;

import java.util.List;
import java.util.Set;

public final class Main {
    public static void main(final String[] args) {
        // Create a database
        Database database = new Database("bdd-project");

     
      /*
        database.addTable(t3);
        database.addTable(t2);
        database.addTable(students);
        database.addTable(enseignantsparticulier);
        // Print the database
        System.out.println(database);
*/
        
       


        /*--------------------EXEMPLE DU SUJET-----------------------*/


        Table R = new Table("R", List.of("A", "B"));
        Table P = new Table("P", List.of("B", "A"));
        Table Q = new Table("Q", List.of("A", "B", "G"));
        TGD c1 = new TGD(List.of(List.of("R1", "A1", "B1")), List.of(List.of("Q1", "A1", "B1", "G1")));
        TGD c2  = new TGD(List.of(List.of("Q1", "A1", "B1", "G1")),List.of(List.of("P1", "B1", "A2")));
        EGD c3 = new EGD(List.of(List.of("R1","A1", "B1"), List.of("P1","B2", "A2")), Set.of("B1=B2"), Set.of("A1=A2"));
       
       

        database.addTable(R);
        database.addTable(P);
        database.addTable(Q);
        
        R.addRecord("{ A = A1, B = B1 }");
        System.out.println(database);
        
       
        /*Standard Chase*/

        //database.standardChase(List.of(c1, c2,c3));

        /*Oblivious Chase*/
        
        //database.obliviousChase(List.of(c1, c2),3);
       

        //Oblivious chase avec contrainte redondante 
        
        TGD c4 = new TGD(List.of(List.of("R1", "A1", "B1")), List.of(List.of("R1","A1","B2")));
        //database.obliviousChase(List.of(c1, c2,c4),1);

        //Oblivious Skolem chase avec contrainte redondante
        
        //database.obliviousSkolemChase(List.of(c1, c2,c4));
        
        /*Skolem pour EGD*/
        
        database.skolemEGD(List.of(c1, c2, c3));
        
        /*------------------------------------------------------*/

        /*--------------------EXEMPLE 1, AVEC CONJONCTIONS A DROITE ET A GAUCHE-----------------------*/ 
        TGD tgd1 = new TGD(List.of(List.of("R1", "A1", "B1")), List.of(List.of("Q1", "A1", "B1", "G1"), List.of("P1" , "B1", "A2")));
        TGD tgd2 = new TGD(List.of(List.of("Q1", "A1", "B1", "G1"), List.of("P1", "B1", "A5")), List.of(List.of("P1", "B1", "A1")));
        //database.standardChase(List.of(tgd2));//N'affiche rien car P1 n'existe pas. 
        //database.standardChase(List.of(tgd1, tgd2));//Affiche P1
        EGD egd1 = new EGD((List.of(List.of("P","B1","A1"),List.of( "P","B2", "A2"))), Set.of("B1=B2"), Set.of("A1=A2"));
        
        //database.standardChase(List.of(tgd1, tgd2, egd1));
        /*----------------------------------------------------*/

        // Print the database
        System.out.println(database);
    }
}
