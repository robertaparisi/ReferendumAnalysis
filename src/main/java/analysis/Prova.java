/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.util.LinkedHashSet;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Roberta
 */
public class Prova {
     
    public static void main(String[] args)  
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
        
                
        

        
    }   
}   
