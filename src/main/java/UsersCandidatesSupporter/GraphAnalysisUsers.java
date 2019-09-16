/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UsersCandidatesSupporter;

import com.google.common.collect.ObjectArrays;
import indexing.TweetIndex;
import static indexing.TweetIndex.output_data_directory;
import it.stilo.g.algo.ConnectedComponents;
import it.stilo.g.algo.HubnessAuthority;
import it.stilo.g.algo.SubGraph;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.LongIntDict;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.GraphReader;
import it.stilo.g.util.NodesMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
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
public class GraphAnalysisUsers {
    
    public static final NodesMapper<String>  mapper = new NodesMapper<>();
    public static int runner = (int) (Runtime.getRuntime().availableProcessors());
    private static final String yes_filename = "src/main/resources/data/yes_user.txt";
    private static final String no_filename = "src/main/resources/data/no_user.txt";
    private static final  String graphFilename = "src/main/resources/data/Official_SBN-ITA-2016-Net.gz";
    public static final LongIntDict mapLongToInt = new LongIntDict();
    public static String[] users_list_yes;
    public static String[] users_list_no;
    public static List<String> list_ids;
    public static int[] nodes;
     //reading yes list
    private static BufferedReader br;
    
    public GraphAnalysisUsers() throws FileNotFoundException, IOException{
        GraphAnalysisUsers.br = new BufferedReader(new FileReader(yes_filename));
        String users_yes = br.readLine();//.split(",");
        GraphAnalysisUsers.users_list_yes = users_yes.substring(1, users_yes.length()-1).split(", ");
        
        GraphAnalysisUsers.br = new BufferedReader(new FileReader(no_filename)); 
        String users_no = br.readLine();//.split(",");
        GraphAnalysisUsers.users_list_no = users_no.substring(1, users_yes.length()-1).split(", ");
        
        GraphAnalysisUsers.list_ids = Arrays.asList(ObjectArrays.concat(users_list_no, users_list_yes, String.class));
        
        GraphAnalysisUsers.nodes = new int[GraphAnalysisUsers.list_ids.size()];
        for (int i = 0; i < GraphAnalysisUsers.nodes.length; i++) {
            GraphAnalysisUsers.nodes[i] = mapper.getId(GraphAnalysisUsers.list_ids.get(i));
        }

    
    }
 
    public Set<Integer> getLargestCC(WeightedDirectedGraph subgraph) throws InterruptedException {

        System.out.println("Computing connected components...");
        Set<Set<Integer>> connected_components = ConnectedComponents.rootedConnectedComponents(subgraph, nodes, runner);

        System.out.println("Looking for the largest connected components...\n We need to compare "+ connected_components.size()+" connected components");
        int largest_component_size = 0;
        Set<Integer> largest_component = new HashSet<>();
        // get largest component
        for (Set<Integer> component : connected_components) {
            if (component.size() > largest_component_size) {
                System.out.println("Size: "+ largest_component_size);
                largest_component = component;
                largest_component_size = component.size();
            }
        }

        return (largest_component);
    }
    
    public String[] getUserNameScreenname(String user_id, IndexSearcher searcher) throws IOException{
        BytesRef ref = new BytesRef();
        NumericUtils.longToPrefixCoded(Long.parseLong(user_id), 0, ref);
        TermQuery q = new TermQuery(new Term("id", ref));
        TopDocs top = searcher.search(q, 1);
        ScoreDoc[] hits = top.scoreDocs;
        Document doc = searcher.doc(hits[0].doc);
        String name = doc.get("name");
        String screenname = doc.get("screenname");
        String[] results = {name, screenname};
        return(results);
        
    }
    public void getAuthorities(ArrayList<DoubleValues> authorities, String[] users_list_yes, String[] users_list_no, IndexSearcher searcher) throws FileNotFoundException, IOException{
        
        HashMap<String, Double> authoritiesID = new HashMap<>();

        PrintWriter pw_yes;
        PrintWriter pw_no;
        pw_yes = new PrintWriter(output_data_directory + "yes_authorities.txt");
        pw_no = new PrintWriter(output_data_directory + "no_authorities.txt");
        List<String> yes_supporters_list = Arrays.asList(users_list_yes);
        
        List<String> no_supporters_list = Arrays.asList(users_list_no);
        List<String> yesA = new ArrayList<>();
        String authority;
        int yes_countA = 0;
        int no_countA = 0;
        String[] authority_data;
        for (int i = 0; i < 1000; i++) {
            authority = mapper.getNode(authorities.get(i).index);
//            String auth = String.valueOf(authorities.get(i).index);
            authoritiesID.put( authority, authorities.get(i).value);
            authority_data = getUserNameScreenname(authority, searcher);
            if (yes_supporters_list.contains(authority)){
                yes_countA++;
                pw_yes.println(authority_data[0] + " " +authority_data[1] + " " + authority + " " + authorities.get(i).value);
            }
            else{
                no_countA++;
                pw_no.println(authority_data[0] + " " +authority_data[1] + " " + authority + " " + authorities.get(i).value);
            }
            
        }
        
        System.out.println("Number of Yes Authorities: "+ yes_countA);
        System.out.println("Number of No Authorities: "+ no_countA);
        pw_yes.close();
        pw_no.close();
    }
    
    public void getHubness(ArrayList<DoubleValues> hubness,  String[] users_list_yes, String[] users_list_no, IndexSearcher searcher) throws FileNotFoundException, IOException{
        PrintWriter pw_yes;
        PrintWriter pw_no;
        List<String> yes_supporters_list = Arrays.asList(users_list_yes);
        List<String> no_supporters_list = Arrays.asList(users_list_no);
     
        HashMap<String, Double> hubnessID = new HashMap<>();
        pw_yes = new PrintWriter(output_data_directory + "yes_hubness.txt");  
        pw_no = new PrintWriter(output_data_directory + "no_hubness.txt");  
        String hub;
        int yes_countH = 0;
        int no_countH = 0;
        String[] hub_data;
        for (int i = 0; i < 1000; i++) {
            hub = mapper.getNode(hubness.get(i).index);
//            String hubby = String.valueOf(hubness.get(i).index);
            hubnessID.put( hub, hubness.get(i).value);
            hub_data = getUserNameScreenname(hub, searcher);
            if (yes_supporters_list.contains(hub)){
                yes_countH++;
                pw_yes.println(hub_data[0] + " " + hub_data[1] + " " + hub + " " + hubness.get(i).value);
            }
            else{
                pw_no.println(hub_data[0] + " " + hub_data[1] + " " + hub + " " + hubness.get(i).value);
                no_countH++;
            }
        }
        System.out.println("Number of Yes Hubness: "+ yes_countH);
        System.out.println("Number of No Hubness: "+ no_countH);
        pw_yes.close();
        pw_no.close();
    }
    
    public HashMap<String, Double> normalizeAuthoritiesValue(ArrayList<DoubleValues> authorities){
        Double max = authorities.get(0).value;
        HashMap<String, Double> authoritiesID = new HashMap<>();
        for (int i=0; i<authorities.size();i++){
            String authority = mapper.getNode(authorities.get(i).index);
            double score_normalized = authorities.get(i).value/max;
//            String auth = String.valueOf(authorities.get(i).index);
            authoritiesID.put( authority, score_normalized);
            
        }
        return(authoritiesID);
    }
    
    public void computeTop500( String[] users_list_yes, String[] users_list_no, IndexSearcher searcher, HashMap<String, Double> normalized_scores_yes, HashMap<String, Double> normalized_scores_no,ArrayList<DoubleValues> authorities) throws IOException{
        PrintWriter pw_yes = new PrintWriter(output_data_directory + "top500_centralYES.txt");
        PrintWriter pw_no = new PrintWriter(output_data_directory + "top500_centralNO.txt");
        
        int yes_count= 0;
        int no_count = 0;
        
        String[] authority_data;
        List<String> yes_supporters_list = Arrays.asList(users_list_yes);
        
        List<String> no_supporters_list = Arrays.asList(users_list_no);
        
        HashMap<String, Double> final_scores_yes = new HashMap<>();
        HashMap<String, Double> final_scores_no = new HashMap<>();
        
//        Double max_auth = authorities.get(0).value;
//        Double max_hub = hubness.get(0).value;
        String authority;
        double final_score;
        
        System.out.println("1. Let's firt obtain the scores for all the authorities...");
        for (int i=0; i<authorities.size();i++){
            System.out.println(i);
            authority = mapper.getNode(authorities.get(i).index);
            
            double auth_score_normalized = authorities.get(i).value;////max_auth;
            
//            double hub_score_normalized = hubness.indexOf(authorities.get(i).index)/max_hub;
//            System.out.println("hub_score "+ hubness.indexOf(authorities.get(i).index) + " MAX HUB "+ max_hub);
            if (yes_supporters_list.contains(authority)){
//                System.out.println("Nuovi score: "+ normalized_scores_yes.get(authority) +" "+ auth_score_normalized +" " + hub_score_normalized);
                final_score = 0.5 * normalized_scores_yes.get(authority) + 0.5*auth_score_normalized ;
//                System.out.println("totale: "+ final_score);
                final_scores_yes.put(authority, final_score);
//                System.out.println("========================");
            }
            else{
//               System.out.println(i);
               final_score = 0.5 *normalized_scores_no.get(authority) + 0.5* auth_score_normalized ;
               final_scores_no.put(authority, final_score);
            }
        }
        
        System.out.println("2. Now we are sorting the scores...");
        List<Entry<String, Double>> yes_score_sorted = new ArrayList<>(final_scores_yes.entrySet());
        Collections.sort( yes_score_sorted , new Comparator<Map.Entry<String, Double>>() { 
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        
        
       List<Entry<String, Double>> no_score_sorted = new ArrayList<>(final_scores_no.entrySet());
        Collections.sort(no_score_sorted, new Comparator<Map.Entry<String, Double>>() { 
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        
        System.out.println("3. Saving yes file");
        for (int i=0; i<500; i++){
            authority = yes_score_sorted.get(i).getKey();
            authority_data = getUserNameScreenname(authority, searcher);
            pw_yes.println(authority_data[0] + " " +authority_data[1] + " " + authority + " " + yes_score_sorted.get(i).getValue());          
        }
        pw_yes.close();
        
        System.out.println("4. Saving no file");
        for (int i=0; i<500; i++){
            authority = no_score_sorted.get(i).getKey();
            authority_data = getUserNameScreenname(authority, searcher);
            pw_no.println(authority_data[0] + " " +authority_data[1] + " " + authority + " " + no_score_sorted.get(i).getValue());          
        }
        pw_no.close();
    }
    
    public void getHITSandTop500(WeightedDirectedGraph largest_component_graph, String[] users_list_yes, String[] users_list_no, IndexSearcher searcher) throws FileNotFoundException, IOException{
        
        System.out.println("Computing HITS...");
        //HITS
        ArrayList<ArrayList<DoubleValues>> hits = HubnessAuthority.compute(largest_component_graph, 0.00001, runner); //hits
        
        //Authorities
        ArrayList<DoubleValues> authorities = hits.get(0);
        ArrayList<DoubleValues> hubness = hits.get(1);
        
        System.out.println("Computing HUBNESS and AUHORITIES...");
        getHubness(hubness, users_list_yes, users_list_no, searcher);
        getAuthorities(authorities, users_list_yes, users_list_no, searcher);
        
        System.out.println("Getting normalized Scores...");
        HashMap<String, HashMap<String, Double>> scores_yesno = NormalizedYesNoScores.computeUsersScores( Arrays.asList(users_list_yes), authorities);
        HashMap<String, Double> normalized_scores_yes = scores_yesno.get("yes");
        HashMap<String, Double> normalized_scores_no = scores_yesno.get("no");
        
        System.out.println("Start saving top 500...");
        computeTop500(users_list_yes, users_list_no, searcher, normalized_scores_yes, normalized_scores_no, authorities);
        
        
        
       
    }
    
    public static WeightedDirectedGraph getGraph() throws FileNotFoundException, IOException {
        WeightedDirectedGraph g = new WeightedDirectedGraph(1000000);
        FileInputStream fstream = new FileInputStream(graphFilename);
        GZIPInputStream gzStream = new GZIPInputStream(fstream);
        InputStreamReader inputStreamReader = new InputStreamReader(gzStream, "UTF-8");
        BufferedReader br = new BufferedReader(inputStreamReader);

        String edge;

        while ((edge = br.readLine()) != null) {
            String node1 = edge.split("\t")[0];
            String node2 = edge.split("\t")[1];
            double weight = Double.parseDouble(edge.split("\t")[2]);
            g.add(mapper.getId(node1), mapper.getId(node2), weight);
        }
        return g;
    }

    
//    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
    public WeightedDirectedGraph saveLccHITSandTop500() throws FileNotFoundException, IOException, InterruptedException{
        
        IndexReader index_reader = DirectoryReader.open(FSDirectory.open(new File(TweetIndex.tweets_index_directory)));
        IndexSearcher searcher = new IndexSearcher(index_reader); 
        System.out.println("Loading graph...");
        //subgraph of the graph provided, with only the ids of the user finded before
//        WeightedDirectedGraph graph = new WeightedDirectedGraph(1000000);
        WeightedDirectedGraph graph = getGraph();
//        GraphReader.readGraphLong2IntRemap(graph, graphFilename, mapLongToInt, false);
        System.out.println("Creating the subgraph...");
        WeightedDirectedGraph subgraph = SubGraph.extract(graph, nodes, runner);
        System.out.println("Computing LCC...");
        Set<Integer> largest_component = getLargestCC(subgraph);
        int[] lcc = largest_component.stream().mapToInt(i-> i).toArray();
        WeightedDirectedGraph largest_component_graph = SubGraph.extract(graph, lcc, runner);
        getHITSandTop500(largest_component_graph, users_list_yes, users_list_no, searcher);      
        
        return(graph);

     }
}
