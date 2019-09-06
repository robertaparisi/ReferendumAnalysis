/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import static analysis.SAXanalysisAndKMeans.cluster_directory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Roberta
 */
public class Prova {
     
    public static void main(String[] args) throws IOException  
    {   
        LinkedHashSet<String> linkedset =  new LinkedHashSet<String>();   
  
        // Adding element to LinkedHashSet   
        linkedset.add("A");   
        linkedset.add("B");   
        linkedset.add("C");   
        linkedset.add("D");  
  
        // This will not add new element as A already exists  
        linkedset.add("A");  
        linkedset.add("E");   
  
        System.out.println("Size of LinkedHashSet = " + 
                                    linkedset.size());   
        System.out.println("Original LinkedHashSet:" + linkedset);   
        System.out.println("Removing D from LinkedHashSet: " + 
                            linkedset.remove("D"));   
        System.out.println("Trying to Remove Z which is not "+ 
                            "present: " + linkedset.remove("Z"));   
        System.out.println("Checking if A is present=" +  
                            linkedset.contains("A")); 
        System.out.println("Updated LinkedHashSet: " + linkedset);  
        System.out.println("Updated LinkedHashSet: " + linkedset.add("A"));  
        System.out.println("Updated LinkedHashSet: " + linkedset.add("A"));
        System.out.println("Updated LinkedHashSet: " + linkedset); 
        int res = StringUtils.getLevenshteinDistance("aaaaa", "bbbbb");
        System.out.println(res);
        
        System.out.println((char)(4.0));
        
        double rand_num = Math.random();
//        finalnum = rand_num
//                .nextInt();
        String regex_match = "a+a*b+a*b*a*";
        String sax_string = "aaaaaaaaaaaabbbbaaaaaaaa";
        String sax_string2 = "aaabbbbaaaabbbbbbaaaaa";
        
        if (sax_string.matches(regex_match)){
            System.out.println("y");
            
        }
        else{
            System.out.println("n");
        }
        
                
        if (sax_string2.matches(regex_match)){
            System.out.println("y");
            
        }
        else{
            System.out.println("n");
        }
        
        int vuo = 5;
        int col = 6;
        
        double th = 0.8;
        String values ="sonsjdheihhhhhhhhhhhhhhhhhhhh"+ 0.3;
        
//        boolean bool = vuo>=col | values>=th;
        System.out.println(values);
        

        String cluster = cluster_directory + "yesClusterListGroup.txt"; 
        BufferedReader br = new BufferedReader(new FileReader(cluster)); 
        String[] lines_cluster = new String[13];
        for (int i = 0; i<13; i++){
            String cluster_terms = br.readLine();
            System.out.println(cluster_terms);
            
            String just_terms = cluster_terms.replace(cluster_terms.split(", ")[0].split(" ")[0]+" ", "");
            System.out.println(just_terms);
            String[] new_row = just_terms.substring(1, just_terms.length()-1 ).split(", ");
            for (int j = 0; j<new_row.length;j++){
                String term1 = new_row[j];
                System.out.println(term1);
            }
           
        }
           
        
                

    }   
}   
