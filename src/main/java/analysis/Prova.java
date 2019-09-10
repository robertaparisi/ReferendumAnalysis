/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import static UsersCandidatesSupporter.UserYesNoSupporter.classifyUsers;
import static analysis.SAXanalysisAndKMeans.cluster_directory;
import static com.google.common.math.IntMath.mod;
import static com.google.common.math.LongMath.mod;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import it.stilo.g.structures.WeightedDirectedGraph;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author Roberta
 */
public class Prova {
     
    public static void main(String[] args) throws IOException, ParseException  
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
        

//        String cluster = cluster_directory + "yesClusterListGroup.txt"; 
//        BufferedReader br = new BufferedReader(new FileReader(cluster)); 
//        String[] lines_cluster = new String[13];
//        for (int i = 0; i<13; i++){
//            String cluster_terms = br.readLine();
//            System.out.println(cluster_terms);
//            
//            String just_terms = cluster_terms.replace(cluster_terms.split(", ")[0].split(" ")[0]+" ", "");
//            System.out.println(just_terms);
//            String[] new_row = just_terms.substring(1, just_terms.length()-1 ).split(", ");
//            for (int j = 0; j<new_row.length;j++){
//                String term1 = new_row[j];
//                System.out.println(term1);
//            }

//        
//        FileInputStream fstream = new FileInputStream("src/main/resources/data/Official_SBN-ITA-2016-Net.gz");
//        GZIPInputStream gzStream = new GZIPInputStream(fstream);
//        InputStreamReader input_stream_reader = new InputStreamReader(gzStream, "UTF-8");
//        BufferedReader braun = new BufferedReader(input_stream_reader);
//        String[] miao = braun.read().split("\t");
//        
//        System.out.println(Arrays.toString(miao));
//        List<String> yes_supporters = new ArrayList<>();
//        List<String> no_supporters = new ArrayList<>();
//        Map<String, List<String>> yes_no_hashmap  = new HashMap<>();
//        int ciao = 10%2;
//        for (int i =0; i<100; i++){
//            if (i%2==0){
//                yes_supporters.add("user"+i);                
//            }
//            else{
//                no_supporters.add("user"+i);
//            }          
//        }
//        yes_no_hashmap.put("yes", yes_supporters);
//        yes_no_hashmap.put("no", no_supporters);
//        
//        PrintWriter user_yes_pw = new PrintWriter(new FileWriter("src/main/resources/data/provafile1.txt"));
//        user_yes_pw.println(yes_no_hashmap.get("yes"));
//        user_yes_pw.close();
//        
//        PrintWriter user_no_pw = new PrintWriter(new FileWriter("src/main/resources/data/provafile2.txt"));
//        user_no_pw.println(yes_no_hashmap.get("no"));
//        user_no_pw.close();
        
        
//        String filename = "src/main/resources/data/yes_user.txt";
//        BufferedReader br = new BufferedReader(new FileReader(filename)); 
//        String users = br.readLine();//.split(",");
//        String[] users_list = users.split(", ");//.substring(1, users.length()-1)
//        int i = 0;
//        for(String u: users_list){
//            i++;
//            System.out.println(u);
//            
//        }
//        System.out.println(i);
//
//        
        
        Graph ga = new Graph();
        WeightedDirectedGraph g = ga.getGraph();
        List<String> ids = new ArrayList<>();
          
        
        
        
            
        
           
        }
           
        
                

    }   
  
