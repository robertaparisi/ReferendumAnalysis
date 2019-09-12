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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
public class GraphAnalysisNew {
    private static final NodesMapper<String>  mapper = new NodesMapper<>();
    public static int runner = (int) (Runtime.getRuntime().availableProcessors());
    
    public static WeightedDirectedGraph getGraph(String graphFilename,BufferedReader br, LongIntDict mapLongToInt  ,List<Integer> list_ids) throws FileNotFoundException, IOException {
        WeightedDirectedGraph graph = new WeightedDirectedGraph(1000000);
        FileInputStream fstream = new FileInputStream(graphFilename);
        GZIPInputStream gzStream = new GZIPInputStream(fstream);
        InputStreamReader isr = new InputStreamReader(gzStream, "UTF-8");
        br = new BufferedReader(isr);
        String edge;
        long node1;
        long node2;
        double weight;

        while ((edge = br.readLine()) != null) {
            node1 = Long.parseLong(edge.split("\t")[0]);
            node2 = Long.parseLong(edge.split("\t")[1]);
            weight = Integer.parseInt(edge.split("\t")[2]);
            
            if (list_ids.contains(node1) & list_ids.contains(node2)){
                graph.add(mapLongToInt.get(node1), mapLongToInt.get(node2), weight);
            }

        }
        return (graph);
    }
    
    private static Set<Integer> getLargestCC(WeightedDirectedGraph subgraph, int[] users_list, WeightedDirectedGraph graph) throws InterruptedException {

        System.out.println("Computing connected components...");
        Set<Set<Integer>> connected_components = ConnectedComponents.rootedConnectedComponents(subgraph, users_list, runner);

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
    
    public static String[] getUserScreennames(String user_id, IndexSearcher searcher) throws IOException{
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
    public static void getAuthorities(ArrayList<DoubleValues> authorities, String[] users_list_yes, String[] users_list_no, IndexSearcher searcher) throws FileNotFoundException, IOException{
        
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
            authoritiesID.put( authority, authorities.get(i).value);
            authority_data = getUserScreennames(authority, searcher);
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
    }
    
    public static void getHubness(ArrayList<DoubleValues> hubness,  String[] users_list_yes, String[] users_list_no, IndexSearcher searcher) throws FileNotFoundException, IOException{
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
            hubnessID.put( hub, hubness.get(i).value);
            hub_data = getUserScreennames(hub, searcher);
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
    
 
    
    public static void getHITS(WeightedDirectedGraph largest_component_graph, String[] users_list_yes, String[] users_list_no, IndexSearcher searcher) throws FileNotFoundException, IOException{
                //HITS
        ArrayList<ArrayList<DoubleValues>> hits = HubnessAuthority.compute(largest_component_graph, 0.00001, runner); //hits
        
        //Authorities
        ArrayList<DoubleValues> authorities = hits.get(0);
        ArrayList<DoubleValues> hubness = hits.get(1);
      
        getHubness(hubness, users_list_yes, users_list_no, searcher);
        getAuthorities(authorities, users_list_yes, users_list_no, searcher);
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
        
        IndexReader index_reader = DirectoryReader.open(FSDirectory.open(new File(TweetIndex.tweets_index_directory)));
        IndexSearcher searcher = new IndexSearcher(index_reader); 
        
        String yes_filename = "src/main/resources/data/yes_user.txt";
        String no_filename = "src/main/resources/data/no_user.txt";
        String graphFilename = "src/main/resources/data/Official_SBN-ITA-2016-Net.gz";
        
        //reading yes list
        BufferedReader br;
        br = new BufferedReader(new FileReader(yes_filename)); 
        String users_yes = br.readLine();//.split(",");
        String[] users_list_yes = users_yes.substring(1, users_yes.length()-1).split(", ");

        //reading no list
        br = new BufferedReader(new FileReader(no_filename)); 
        String users_no = br.readLine();//.split(",");
        String[] users_list_no = users_no.substring(1, users_yes.length()-1).split(", ");
        
        //tranforming the list of ids to int instead of long, since is the only format accepted for the graph construction
        List<String> list_ids = Arrays.asList(ObjectArrays.concat(users_list_no, users_list_yes, String.class));
        LongIntDict mapLongToInt = new LongIntDict();
        int[] nodes = new int[list_ids.size()];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = mapper.getId(list_ids.get(i));
        }

        System.out.println("Loading graph...");
        //subgraph of the graph provided, with only the ids of the user finded before
        WeightedDirectedGraph graph = new WeightedDirectedGraph(1000000);

        GraphReader.readGraphLong2IntRemap(graph, graphFilename, mapLongToInt, false);
        
        System.out.println("Creating the subgraph...");
        WeightedDirectedGraph subgraph = SubGraph.extract(graph, nodes, runner);
   
        System.out.println("Computing LCC...");
        Set<Integer> largest_component = getLargestCC(subgraph, nodes, graph);
        int[] lcc = largest_component.stream().mapToInt(i-> i).toArray();
        
        WeightedDirectedGraph largest_component_graph = SubGraph.extract(graph, lcc, runner);
        getHITS(largest_component_graph, users_list_yes, users_list_no, searcher);      
        
     }
}
