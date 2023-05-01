import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

public class A2Q4{
    
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
                joinOn(joinAttributes, relation1, values1, relation2, values2);
            }
            
            in.close();
        } catch (FileNotFoundException fnfe){
            System.out.println("ERROR: "+fnfe);
        }
        
    }//readData

/**
 * joinOn()
 * 
 * This is the primary method used to join two relations together, given a join condition. It primarily uses a nested loop
 * algorithm, where the outside loop goes through the records of the first relation and the inside loop goes through the
 * records of the second relation. For each record in the first relation, every record in the second relation is checked
 * against it to find matching records based on the join condition.
 */
    private static void joinOn(String[] attributes, String[] relation1, ArrayList<String> values1, String[] relation2, ArrayList<String> values2){
        
        //get started header string of attributes, finish this later in the method if we managed to find a matching record to join on
        String header = "";
        boolean headerCreated = false;
        
        for (String s : attributes){
            header += s+",";
        }
        
        //store the joined tuples in an ArrayList to be printed later
        ArrayList<String> joinedTuples = new ArrayList<String>();
        
        //get joined tuples by taking a record from the first relation, and comparing
        //it against each record in the second relation
        for (int i = 0; i < values1.size(); i++){
            String[] records1 = (values1.get(i)).split(",");
            for (int j = 0 ; j < values2.size(); j++){
                String[] records2 = (values2.get(j)).split(",");
                
                //using a while loop to find matching records, in case one attribute does not match between
                //the two relations and we have to quit and move on to the next record
                boolean joining = true;
                int k = 0;
                String tuple = "";
                while(joining && k < attributes.length){
                    String attribute = attributes[k];
                    //find out where in the tuple we have to check for matching records between the two relations
                    int index1 = findIndex(attribute, relation1);
                    int index2 = findIndex(attribute, relation2);
                    
                    if (records1[index1].equalsIgnoreCase(records2[index2])){
                        tuple += records1[index1]+",";
                        k++;
                    } else {
                        joining = false;
                    }
                } //joining loop
                
                //If the value of k is equal to the length of the join attributes array, then we know
                //that we found all matching records required between the relations. Now we just need
                //to join the remaining attributes not in the join condition
                if (k == attributes.length){
                    
                    //check if the current element in the first relation is part of the join attributes.
                    //if so, then we should not re-add those attribute's values to the tuple we want to return
                    for (int n = 0; n < relation1.length; n++){
                        boolean remains = true;
                        int m = 0;
                        while (remains && m < attributes.length){
                            if ((relation1[n].equalsIgnoreCase(attributes[m]))){
                                remains = false;
                                m++;
                            } else {
                                m++;
                            }
                        }
                        
                        //if the remains boolean is still true, that means we didn't find a match with the current
                        //element in the relation and all of the join attributes. Its values still remain and
                        //need to be added. We will also modify our header string here
                        if (remains){
                            if(!headerCreated){
                                header += relation1[n]+",";
                            }
                            tuple += records1[n]+",";
                        }
                        
                    }
                    
                    //do the same with the second relation, while checking against the first relation as well
                    for (int o = 0; o < relation2.length; o++){
                        boolean remains = true;
                        int m = 0;
                        while (remains && m < attributes.length){
                            
                            //check first relation to see if any of the same attributes exist, don't want to count them in
                            int n = 0;
                            while (remains && n < relation1.length){
                                if (relation2[o].equalsIgnoreCase(relation1[n])){
                                    remains = false;
                                    n++;
                                } else {
                                    n++;
                                }
                            }
                            
                            //check remaining attributes
                            if ((relation2[o].equalsIgnoreCase(attributes[m]))){
                                remains = false;
                                m++;
                            } else {
                                m++;
                            }
                        }
                        
                        if (remains){
                            if (!headerCreated){
                                header += relation2[o]+",";
                            }
                            tuple += records2[o]+",";
                        }
                    }
                    
                    headerCreated = true; // only want to create the header the first time through
                    
                    //remove trailing comma
                    tuple = tuple.substring(0, tuple.length()-1);
                    //add the tuple to the list of records to be printed
                    joinedTuples.add(tuple);
                }
                
            } //second relation loop
            
        } //first relation loop
        
        if (joinedTuples.isEmpty() == false){
            System.out.println(header.substring(0,header.length()-1)); //remove trailing comma
            for (String s : joinedTuples){
                System.out.println(s);
            }
        } else {
            System.out.println("No matching records found.");
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
    
}//A5Q4
