/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SpreadOfInfluence;

import UsersCandidatesSupporter.GraphAnalysisUsers;
import static UsersCandidatesSupporter.GraphAnalysisUsers.mapper;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedGraph;
import it.stilo.g.util.NodesMapper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Roberta
 */
public class MainLPA {
    
    public static List<String> readTop500File(String filename) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader( new FileReader(filename));     
        List<String> user_id = new ArrayList<>();
        String row;
        while((row=br.readLine())!=null){
            String[] users_yes = br.readLine().split(" ");//.split(",");
            user_id.add(users_yes[users_yes.length-2]);
        }
        return(user_id);
    }
    
  
    
    public static List<String> readUserFile(String filename) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader( new FileReader(filename));
        String users_yes = br.readLine();//.split(",");
        String[] users_list_yes = users_yes.substring(1, users_yes.length()-1).split(", ");
        return(Arrays.asList(users_list_yes));
    }
    
    
    public static int[] mapNodes(List<String> list_id){        
        int[] nodes = new int[list_id.size()];
        for (int i = 0; i < list_id.size(); i++) {
            nodes[i] = GraphAnalysisUsers.mapper.getId(list_id.get(i));
        }
        return(nodes);
    }
            
            
    public static int[] getLabels(int graphSize, List<String> yes_user, List<String> no_user){
        int[] yes_nodes = mapNodes(yes_user);
        int[] no_nodes = mapNodes(no_user);
        int[] labels = new int[graphSize];
        for (int node: yes_nodes){
            labels[node] = 1;
        }
        for (int node: no_nodes){
            labels[node] = 2;
        }
        return(labels);
                     
    }
    
    public static void freq(int[] labels){
        int freq_yes = 0;
        int freq_no = 0 ;
        
        for (int i =0; i<labels.length; i++){
            if (labels[i]==1){
                freq_yes++;
            }else if(labels[i]==2){
                freq_no++;
            }
            
        }
        int unknown = labels.length - freq_yes - freq_no;
        System.out.println("Numero di si: "+ freq_yes+"\nNumero di no: "+ freq_no+ "\n Numero di non definiti: "+  unknown);
        
    }
    public static void countFreq(int[] labels, int n){ 
        Map<Integer, Integer> map = new HashMap<>(); 

        for (int i = 0; i < n; i++) 
        { 
            map.put(labels[i], map.get(labels[i]) == null ? 1 : map.get(labels[i]) + 1); 
        } 
        
        String[] label_string = {"Unknown Party", "Yes Party", "No Party"};
  
        for (int i = 0; i < n; i++)  
        { 
            if (map.get(labels[i]) != -1)  
            { 
                System.out.println(label_string[labels[i]] + " " + map.get(labels[i])); 
                map.put(labels[i], -1); 
            } 
        } 
    } 
    
    public static void getLPA(WeightedDirectedGraph graph) throws IOException{
        

        String yes_user_filename = "src/main/resources/data/yes_user.txt";
        String no_user_filename = "src/main/resources/data/no_user.txt";
        
        List<String> yes_user = readUserFile(yes_user_filename);
        List<String> no_user = readUserFile(no_user_filename);
        
        int[] labels;
        labels = getLabels(graph.size, yes_user, no_user);
        System.out.println("M user: ");
        int[] LPA_M_user = AlgoLPA.compute(graph, labels, 0.001, GraphAnalysisUsers.runner);
        
        countFreq(LPA_M_user, LPA_M_user.length);
//        freq(LPA_M_user);
        System.out.println("====================================");
        String top500yes = "src/main/resources/outputData/top500_centralNO.txt";
        String top500no = "src/main/resources/outputData/top500_centralYES.txt";
        
        System.out.println("M' user: ");
        List<String> yes_userTop500 = readTop500File(top500yes);
        List<String> no_userTop500 = readTop500File(top500no);
        
        int[] labels500 = getLabels(graph.size, yes_userTop500, no_userTop500);
        
        int[]  LPA_M500_user = AlgoLPA.compute(graph, labels500, 0.001,  GraphAnalysisUsers.runner);
        
        countFreq(LPA_M500_user, LPA_M500_user.length);
//        freq(labels);
       
    }
      
}
