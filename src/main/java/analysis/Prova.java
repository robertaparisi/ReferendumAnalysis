/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import static UsersCandidatesSupporter.UserYesNoSupporter.classifyUsers;
import static UsersCandidatesSupporter.UserYesNoSupporter.getUserID;
import static analysis.SAXanalysisAndKMeans.cluster_directory;
import com.google.common.collect.ObjectArrays;
import static com.google.common.math.IntMath.mod;
import static com.google.common.math.LongMath.mod;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import indexing.TweetIndex;
import it.stilo.g.algo.HubnessAuthority;
import it.stilo.g.algo.SubGraph;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.LongIntDict;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedUndirectedGraph;
import it.stilo.g.util.GraphReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.math3.stat.inference.TestUtils.g;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

/**
 *
 * @author Roberta
 */
public class Prova {
    public static void countFreq(int arr[], int n){ 
            Map<Integer, Integer> mp = new HashMap<>(); 

            // Traverse through array elements and  
            // count frequencies  
            for (int i = 0; i < n; i++) 
            { 
                mp.put(arr[i], mp.get(arr[i]) == null ? 1 : mp.get(arr[i]) + 1); 
            } 

            // To print elements according to first  
            // occurrence, traverse array one more time  
            // print frequencies of elements and mark  
            // frequencies as -1 so that same element  
            // is not printed multiple times.  
            for (int i = 0; i < n; i++)  
            { 
                if (mp.get(arr[i]) != -1)  
                { 
                    System.out.println(arr[i] + " " + mp.get(arr[i])); 
                    mp.put(arr[i], -1); 
                } 
            } 
        } 
    
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
        
        
        
        String top500yes = "src/main/resources/outputData/top500_centralNO.txt";    
        BufferedReader br = new BufferedReader( new FileReader(top500yes));
        List<String> user_id = new ArrayList<>();
        String row;
        while((row=br.readLine())!=null){
            String[] users_yes = br.readLine().split(" ");//.split(",");
            user_id.add(users_yes[users_yes.length-2]);
        }
       
        
        
        int arr[] = {10, 20, 20, 10, 10, 20, 5, 20}; 
        int n = arr.length; 
        countFreq(arr, n); 
     
        
        
//        String[] users_list_yes = users_yes.substring(1, users_yes.length()-1).split(", ");
    
        

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
        
                
//        String yes_filename = "src/main/resources/data/yes_user.txt";
//        String no_filename = "src/main/resources/data/no_user.txt";
//        String graphFilename = "src/main/resources/data/Official_SBN-ITA-2016-Net.gz";
//        String auth_yes =  "src/main/resources/outputData/yes_authorities.txt";
        
//        IndexReader index_reader = DirectoryReader.open(FSDirectory.open(new File(TweetIndex.tweets_index_directory)));
//        IndexSearcher searcher = new IndexSearcher(index_reader); 
//
//        BufferedReader br;
//        br = new BufferedReader(new FileReader(yes_filename)); 
//        String users_yes = br.readLine();//.split(",");
//        String[] users_list_yes = users_yes.substring(1, users_yes.length()-1).split(", ");
//        System.out.println("SIZE: " + users_list_yes.length );
//        
//        br = new BufferedReader(new FileReader(auth_yes)); 
//        List<String> users_list_yes_auth = new ArrayList<>();
//        String users_yes_auth;
//        while((users_yes_auth = br.readLine())!= null){
//            users_list_yes_auth.add(users_yes);
//        }
//       
//        System.out.println("SIZE auth: " + users_list_yes_auth.size());
//        PrintWriter pw = new PrintWriter("src/main/resources/data/yes_user_name.txt");
//        for(String user: users_list_yes){
//            BytesRef ref = new BytesRef();
//            NumericUtils.longToPrefixCoded(Long.parseLong(user), 0, ref);
//            TermQuery q = new TermQuery(new Term("id", ref));
//            TopDocs top = searcher.search(q, 1);
//            ScoreDoc[] hits = top.scoreDocs;
//            Document doc = searcher.doc(hits[0].doc);
//            String name = doc.get("name");
//            String screenname = doc.get("screenname");
//            String[] results = {name, screenname};
//            pw.println(name +" "+ screenname+" "+ user);
//        }
//        pw.close();
//        
//        br = new BufferedReader(new FileReader(no_filename)); 
//        String users_no = br.readLine();//.split(",");
//        String[] users_list_no = users_no.substring(1, users_yes.length()-1).split(", ");
        
//        Set<String> user_id = getUserID();
//
//        for (String user : user_id) {
// 
//            System.out.println("User id: " + user);
//            BytesRef ref = new BytesRef();
//            NumericUtils.longToPrefixCoded(Long.parseLong(user), 0, ref);
//            System.out.println("Ref "+ ref);
//        LongIntDict mapLong2Int = new LongIntDict();    
//        List<String> list_ids = Arrays.asList(ObjectArrays.concat(users_list_no, users_list_yes, String.class));
//        WeightedUndirectedGraph graph = new WeightedUndirectedGraph(450193);
//        GraphReader.readGraphLong2IntRemap(graph, graphFilename, mapLong2Int, true);
        
//        System.out.println("sizeeeee      " + list_ids.size());
////        max             798190783299928064
//        long max = 0;
//        for (int i = 0; i<list_ids.size(); i++){
//            System.out.println(i+" "+ list_ids.get(i));
//            if (max< Long.parseLong(list_ids.get(i))){
//                max = Long.parseLong(list_ids.get(i));
//            }
//        }
//        System.out.println("max             "+max);
//        b = Arrays.asList(ArrayUtils.toObject(list_ids));
        
//        List<Integer> intList = list_ids.stream().map((s) -> mapLong2Int.get(Long.parseLong(s))).collect(Collectors.toList());
//        
//        Integer[] intlist = (Integer[]) list_ids.stream().map((String x)->Integer.valueOf(x)).toArray();
//        newThing = Collections.sort(int_list);
//        Optional<Integer> max = Arrays.stream(intlist).reduce((x, y) -> x > y ? x : y);

//        WeightedUndirectedGraph graph;// = new WeightedUndirectedGraph(list_ids.size());
////    
//        FileInputStream fstream = new FileInputStream("src/main/resources/data/Newgraph.gz");
//        GZIPInputStream gzStream = new GZIPInputStream(fstream);
//        InputStreamReader input_stream_reader = new InputStreamReader(gzStream, "UTF-8");
//        br = new BufferedReader(input_stream_reader);   
//        String edge;
//        
//        while ((edge = br.readLine())!= null){
//            System.out.println("Nuovo : "+ edge);
//        }
//        LongIntDict mapLong2Int = new LongIntDict();
//        graph = new WeightedUndirectedGraph(16815934);
////        graphFilename = "Official_SBN-ITA-2016-Net.gz";
//        mapLong2Int = new LongIntDict();
//        GraphReader.readGraphLong2IntRemap(graph, graphFilename, mapLong2Int, false);
        
        

//        String edge;
//        String nodes1;
//        String nodes2;
//        int weight;
//        while ((edge = br.readLine()) != null) {
//            nodes1 = edge.split("\t")[0];
//            nodes2 = edge.split("\t")[1];
//            weight = Integer.parseInt(edge.split("\t")[2]);
//            if (list_ids.contains(nodes1) & list_ids.contains(nodes2)){
//                graph.add(Integer.parseInt(nodes1), Integer.parseInt(nodes2), weight);
//            }
//            System.out.println("Peso: " );
//        }
//  // PROVA
//        Set<Integer> n = new HashSet<>();
//        for (int i = 0; i < gnew.in.length; i++) {
//            if (gnew.in[i] != null) {
//                for (int j = 0; j < gnew.in[i].length; j++) {
//                    n.add(gnew.in[i][j]);
//                }
//            }
//
//        }
//        System.out.println(n.size());
//        //
//
//        Set<Integer> lc = largestComponent(gnew);
//        System.out.println("lcc size: " + lc.size());
//        int[] lca = lc.stream().mapToInt(i -> i).toArray();
//        WeightedDirectedGraph lcGraph = SubGraph.extract(g, lca, 2);
//        ga.saveGraph(lcGraph, results);
//        ArrayList<ArrayList<DoubleValues>> hits = HubnessAuthority.compute(lcGraph, 0.00001, 2);
//        ga.getAuthorities(hits, 1000, results + "/authorities.txt");
//        ga.getHubs(hits, 1000, results + "/hubs.txt");
//
//
//
//
//
//        
//         if (loadGraph) {
//            graphSize = 16815933;
//            g = new WeightedDirectedGraph(graphSize + 1);
//            
//            mapLong2Int = new LongIntDict();
//            GraphReader.readGraphLong2IntRemap(g, RESOURCES_LOCATION + graphFilename, mapLong2Int, false);
//        }
//        
//        if (calculateTopAuthorities) {
//            LinkedHashSet<Integer> users = analysis.GraphAnalysis.getUsersMentionedPolitician(useCache, mapLong2Int);
//            // convert the set to array of int, needed by the method "SubGraph.extract"
//            int[] usersIDs = new int[users.size()];
//            i = 0;
//            for (Integer userId : users) {
//                usersIDs[i] = userId;
//                i++;
//            }
//
//            MappedWeightedGraph gmap = analysis.GraphAnalysis.extractLargestCCofM(g, usersIDs, mapLong2Int);
//            analysis.GraphAnalysis.saveTopKAuthorities(gmap, users, mapLong2Int, 1000, useCache);
//            TweetsOpinion.saveTop500HubnessAuthorities(gmap, users, mapLong2Int, 3);
//            TweetsOpinion.hubnessGraph13();
           
        }
     
                

    }   
  
