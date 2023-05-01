import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

public class A3Q1{
    
    public static void main(String[] args){

    if (args.length == 3){
        String file1 = args[0];
        String file2 = args[1];
        String joinCondition = args[2];
        readData(file1, file2, joinCondition);
    } else {
        System.out.println("ERROR: Invalid arguments passed in.");
    }
    
    System.out.println("End of processing.");
    
    }//main
    
/**
 *  readData()
 * 
 *  To start the join, the files are read in such that the first line containing
 *  the names of the attributes are stored in their own array, and then the tuples
 *  of values that follow the first line are stored as strings in ArrayLists.
 *  
 *  The join condition is stored in its own array.
 *  
 *  Altogether, these get passed into further methods for checking and processing.
 */
    private static void readData(String file1, String file2, String joinCondition){
        
        String[] joinAttributes = joinCondition.split(",");
        
        String[] relation1 = null;
        ArrayList<String> values1 = new ArrayList<String>();
        String[] relation2 = null;
        ArrayList<String> values2 = new ArrayList<String>();
        
        try {
            File toRead = new File(file1);
        
            Scanner in = new Scanner(toRead);
            relation1 = (in.nextLine()).split(",");
             
            while (in.hasNextLine()){
                String data = in.nextLine();
                values1.add(data);
            }
            
            toRead = new File(file2);
            in = new Scanner(toRead);
            relation2 = (in.nextLine()).split(",");
            
            while (in.hasNextLine()){
                String data = in.nextLine();
                values2.add(data);
            }
            
            //if the attributes in the join condition don't exist in either relation,
            //then we print an error message and stop the program
            if (checkAttributes(joinAttributes, relation1, relation2) == false){
                System.out.println("ERROR: Cannot join on the conditions provided as one or more of the attributes do not exist in one of the relations.");
            } else {
                outerJoin(joinAttributes, relation1, values1, relation2, values2);
            }
            
            in.close();
        } catch (FileNotFoundException fnfe){
            System.out.println("ERROR: "+fnfe);
        }
        
    }//readData

/**
 * outerJoin()
 * 
 * This is the primary method used to full outer join two relations together. It primarily uses a nested loop
 * algorithm, where the outside loop goes through the records of the first relation and the inside loop goes through the
 * records of the second relation. For each record in the first relation, every record in the second relation is checked
 * against it to find matching records based on the join condition. Then, to find any remaining records from
 * the second relation that were not matched, it switches the two relations in another nested loop algorithm.
 */
    private static void outerJoin(String[] attributes, String[] relation1, ArrayList<String> values1, String[] relation2, ArrayList<String> values2){
        
        //store the joined tuples in an ArrayList to be printed later
        ArrayList<String> joinedTuples = new ArrayList<String>();
        
        //create and print the header of attributes
        String header = "";
        
        for (String s : relation1){
            header += s+",";
        }
        for (String s : relation2){
            header += s+",";
        }
        System.out.println(header.substring(0,header.length()-1)); //remove trailing comma
        
        //get joined tuples by taking a record from the first relation, and comparing
        //it against each record in the second relation
        for (int i = 0; i < values1.size(); i++){
            String[] records1 = (values1.get(i)).split(",");
            boolean matchFound = false;
            String tuple = "";
            for (int j = 0 ; j < values2.size(); j++){
                String[] records2 = (values2.get(j)).split(",");
                
                //using a while loop to find matching records, in case one attribute does not match between
                //the two relations and we have to quit and move on to the next record
                boolean joining = true;
                int k = 0;
                tuple = ""; //need to clear the string in case there's multiple matches
                
                while(joining && k < attributes.length){
                    String attribute = attributes[k];
                    //find out where in the tuple we have to check for matching records between the two relations
                    int index1 = findIndex(attribute, relation1);
                    int index2 = findIndex(attribute, relation2);
                    
                    if (records1[index1].equalsIgnoreCase(records2[index2])){
                        k++;
                    } else {
                        joining = false;
                    }
                } //joining loop
                
                //If the value of k is equal to the length of the join attributes array, then we know
                //that we found all matching records required between the relations. Now we just need
                //to join the records from each relation together
                if (k == attributes.length){
                    matchFound = true;
                    for (String s : records1){
                        tuple += s+",";
                    }
                    for (String s : records2){
                        tuple += s+",";
                    }
                    //remove trailing comma
                    tuple = tuple.substring(0, tuple.length()-1);
                    //add the tuple to the list of records to be printed
                    System.out.println("Found a match on the left:" + tuple);
                    joinedTuples.add(tuple);
                }
                
            } //second relation loop
            
            // if a match wasn't found, then we add null values to the record from the first relation
            if (matchFound == false){
                for (String s : records1){
                    tuple += s+",";
                }
                for (int k = 0; k < relation2.length; k++){
                    tuple += "null,";
                }
                //remove trailing comma
                tuple = tuple.substring(0, tuple.length()-1);
                //add the tuple to the list of records to be printed
                
                joinedTuples.add(tuple);
            }
            
        } //first relation loop
        
        // Now, we find the remaining records from the second relation that could not match
        // Take these, and join nulls to them
        for (int i = 0; i < values2.size(); i++){
            String[] records2 = (values2.get(i)).split(",");
            boolean matchFound = false;
            String tuple = "";
            for (int j = 0 ; j < values1.size(); j++){
                String[] records1 = (values1.get(j)).split(",");
                
                boolean joining = true;
                int k = 0;
                tuple = ""; //need to clear the string in case there's multiple matches
                
                while(joining && k < attributes.length){
                    String attribute = attributes[k];
                    //find out where in the tuple we have to check for matching records between the two relations
                    int index1 = findIndex(attribute, relation1);
                    int index2 = findIndex(attribute, relation2);
                    
                    if (records1[index1].equalsIgnoreCase(records2[index2])){
                        k++;
                    } else {
                        joining = false;
                    }
                } //joining loop
                
                if (k == attributes.length){
                    matchFound = true;
                }
                
            } //second relation loop
            
            if (matchFound == false){
                for (int k = 0; k < relation1.length; k++){
                    tuple += "null,";
                }
                for (String s : records2){
                    tuple += s+",";
                }
                //remove trailing comma
                tuple = tuple.substring(0, tuple.length()-1);
                //add the tuple to the list of records to be printed
                joinedTuples.add(tuple);
            }
            
        } //first relation loop
        
        //print out the joined records
        for (String s : joinedTuples){
            System.out.println(s);
        }
       
    }//joinOn

/**
 * findIndex()
 * 
 * This is a private helper method to find the index in the relation array of the attribute
 * passed in. Because of the data structures used, knowing the index of where a join on attribute
 * is in the relation arrays helps with matching records.
 */
    private static int findIndex(String attribute, String[] relation){
        int index = -1;
        
        int i = 0;
        boolean found = false;
        
        while (found == false && i < relation.length){
            if (attribute.equalsIgnoreCase(relation[i])){
                index = i;
                found = true;
            } else {
                i++;
            }
        }
        
        return index;
    }//findIndex 
    
    
/**
 * checkAttributes()
 * 
 * This is a private helper method that is used to check if the attributes named in the join
 * condition actually exist in both relations. If they do, then the join on program continues.
 * Otherwise, the program stops if the method returns false.
 */
    private static boolean checkAttributes(String[] attributes, String[] relation1, String[] relation2){
        boolean exists = false;
        boolean contains1 = true;
        boolean contains2 = true;
        
        //for each attribute in the join condition, check it against the attributes in the
        //relations. If we've checked all attributes in the relation and could not find a match,
        //then we stop and output false to signal that a join attribute does not exist in
        //one of the relations.
        int i = 0;
        int j = 0;
        while (i < attributes.length && j < relation1.length){
            if (attributes[i].equalsIgnoreCase(relation1[j])){
                i++;
                j = 0; //reset to start of relation array when checking the next attribute
            } else {
                j++;
            }
        }
        
        if (i < attributes.length && j >= relation1.length){
            contains1 = false;
        }
        
        int k = 0;
        int l = 0;
        while (k < attributes.length && l < relation2.length){
            if (attributes[k].equalsIgnoreCase(relation2[l])){
                k++;
                l = 0;
            } else {
                l++;
            }
        }
        
        if (k < attributes.length && l >= relation2.length){
            contains2 = false;
        }
        
        //only output true if the join attributes are contained in both relations
        if (contains1 && contains2){
            exists = true;
        }
        
        return exists;
    }//checkAttributes
    
}//A3Q1
