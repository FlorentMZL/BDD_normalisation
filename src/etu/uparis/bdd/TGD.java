package etu.uparis.bdd;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TGD extends Constraint{
    private List<List<String>> body; //liste de la forme ((NomTable1, NomAttribut1, NomAttribut2, ...), (NomTable2, NomAttribut1, NomAttribut2, ...), ...). Represente conjonction de tables. 
    private List<List<String>> head; 
    private Set<Set<Record>> applied; //liste des ensembles de tuples auxquels la contrainte a déjà été appliquée 
    public TGD(List<List<String>> body, List<List<String>> head){
        this.body = body;
        this.head = head;
        this.applied = new HashSet<Set<Record>>();
    }



    public void addApplied(Set<Record> s){
        this.applied.add(s);
    }
    public List<List<String>> getBody(){
        return this.body;
    }
    public List<List<String>> getHead(){
        return this.head;
    }
    public boolean isApplied(Set<Record> s){
        return this.applied.contains(s);
    }

    public boolean satisfyingBody (Database d){
        boolean checkIn = true; 
        for(List<String> l : this.body){
            if (checkIn) checkIn = false; 
            else return false;
            for (Table t : d.getTables()){
                if (t.getName().equals(l.get(0))){
                    if (t.getRecords().size()!=0){
                        checkIn = true; 
                        break; 
                    }
                   
                }
            }
            
        }
        return checkIn; 
    }
    

}
     

