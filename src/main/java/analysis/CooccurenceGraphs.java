/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;
import static analysis.SAXanalysisAndKMeans.cluster_directory;
import static indexing.TweetIndex.output_data_directory;
import it.stilo.g.algo.CoreDecomposition;
import it.stilo.g.algo.SubGraph;
import it.stilo.g.structures.Core;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import static org.apache.lucene.util.Version.LUCENE_41;



/**
 *
 * @author Roberta
 */
public class CooccurenceGraphs {   
    
    public static final String cooccurence_graph_directory = output_data_directory + "cooccurence_graphs/";
    public static int runner = (int) (Runtime.getRuntime().availableProcessors());
         
    public static int countCoOccurrence (String t1, String t2, IndexReader index_reader) throws ParseException, IOException{
        IndexSearcher searcher = new IndexSearcher(index_reader);
        Analyzer analyzer = new ItalianAnalyzer(Version.LUCENE_41);
        QueryParser parser = new QueryParser(LUCENE_41, "text", analyzer);
        Query q1 = parser.parse(t1);
        Query q2 = parser.parse(t2);
        BooleanQuery query = new BooleanQuery();
        query.add(q1, BooleanClause.Occur.MUST);
        query.add(q2, BooleanClause.Occur.MUST);
        TotalHitCountCollector collector = new TotalHitCountCollector();
        searcher.search(query, collector);
        int cooccurence = collector.getTotalHits();
        return (cooccurence);     
    }  
    
    public static int countOccurence (String term, IndexReader index_reader) throws ParseException, IOException {
        IndexSearcher searcher = new IndexSearcher(index_reader);
        Analyzer analyzer = new ItalianAnalyzer(Version.LUCENE_41);
        Query query = new TermQuery(new Term("text", term));
        TotalHitCountCollector collector = new TotalHitCountCollector();
        searcher.search(query, collector);
        int occurence = collector.getTotalHits();//frequence of the term
        return (occurence);      
    }
    
    
    public static int lessFrequentTermFrequency (String term1, String term2,IndexReader index_reader) throws ParseException, IOException{
        int frequency_term1 = countOccurence(term1, index_reader);
        int frequency_term2 = countOccurence(term2, index_reader);    
        if (frequency_term1 >= frequency_term2){
            return (frequency_term2);
        }
        else{
            return (frequency_term1);
        }       
    }
    
    public static int computeLessFreqTermThreshold(String term1, String term2, double threshold, IndexReader index_reader) throws ParseException, IOException{
        int freq = lessFrequentTermFrequency( term1, term2,  index_reader);
        return(int)(freq*threshold);        
    }
       
    
    public static double normalizeCooccurence(int weight, String term1, String term2,IndexReader index_reader  ) throws IOException, ParseException{
        int frequency_term1 = countOccurence(term1, index_reader);
        int frequency_term2 = countOccurence(term2, index_reader);
        double new_weight = weight/(frequency_term1+frequency_term2);
        return(new_weight);
    }
    
    public static boolean checkThresholdClauses(int coOccurence, double weight, String term1, String term2, double thresholdLFT,double thresholdTotal, IndexReader index_reader )throws ParseException, IOException{
        //if the co-occurence number is at least equal to the 30% (thresholdLFT = 0.3) of the freq of the less frequent term
        // OR if the weight (double) is at least equal to 0.1 (thresholdTotal = 0.1)that means that this word appear togheter 1 times over 10
        int less_freq_term_threshold = computeLessFreqTermThreshold(term1, term2, thresholdLFT, index_reader);
        int min_num_cooccurence = (int) (less_freq_term_threshold*thresholdLFT);
        boolean clause_respected = coOccurence!=0 & (coOccurence>= min_num_cooccurence | weight>=thresholdTotal);
        return(clause_respected);
        
    }
    
  
    public static WeightedUndirectedGraph createCoOccurrenceGraphCluster (PrintWriter pw, String[] cluster_list, String index_dir, double thresholdLFT, double thresholdTotal) throws IOException, ParseException{       
        WeightedUndirectedGraph graph = new WeightedUndirectedGraph(cluster_list.length); 
        File directory = new File(index_dir);
        IndexReader index_reader = DirectoryReader.open(FSDirectory.open(directory));
//        ArrayList<String> nodes1 = new ArrayList<>();
//        ArrayList<String> nodes2 = new ArrayList<>();
//        ArrayList<Integer> weights = new  ArrayList<>(); 
        System.out.println("Starting term analysis for the cluster");
        for (int i = 0; i < cluster_list.length-1; i++){
            String term1 = cluster_list[i];
            for (int j = i + 1; j < cluster_list.length; j++){
                String term2 = cluster_list[j];
                int cooccurence = countCoOccurrence (term1, term2, index_reader);
                double normalized_cooccurence = normalizeCooccurence(cooccurence, term1, term2, index_reader);  
                boolean clauses_respected = checkThresholdClauses(cooccurence, normalized_cooccurence, term1, term2, thresholdLFT, thresholdTotal, index_reader);
                if (clauses_respected){                
                    graph.add(i,j, cooccurence);
                    pw.println(term1 + " "+ term2 + " " + cooccurence);
                }              
            }    
        }
        System.out.println("done, let's save the graph");
        System.out.println(" ");
        System.out.println(" ");
        return(graph);//serve?
    }
    
    
        
    public static void extractANDsaveCCandKCore (PrintWriter cc_pw, PrintWriter kcore_pw, String[] cluster_terms, WeightedUndirectedGraph graph) throws InterruptedException, IOException {       
        int[] nodes_list = new int[graph.size];
        for (int i = 0; i < graph.size; i++) {
            nodes_list[i] = i;
        }
        
        Set<Set<Integer>> connected_components = it.stilo.g.algo.ConnectedComponents.rootedConnectedComponents(graph, nodes_list, runner);   
        System.out.println("Connecte components:         " + connected_components);
        for (Set<Integer> component : connected_components) {   
            String[] cc_nodes = new String[component.size()];
            Integer[] cc_array = component.toArray(new Integer[component.size()]);
            for (int node_id =0; node_id< component.size(); node_id++) {                
                cc_nodes[node_id] = cluster_terms[node_id];
            }   
            cc_pw.println(Arrays.toString(cc_nodes));
            
            WeightedUndirectedGraph graphComp = SubGraph.extract(graph, component.stream().mapToInt(i->i).toArray(), runner); 
            Core kcore = CoreDecomposition.getInnerMostCore(graphComp, 2);                
            System.out.println("K CORE");
            System.out.println("k: " + kcore.minDegree); //il numero k e' il numero di degree minore in questa subgraph
//            System.out.println("seq   " + Arrays.toString(kcore.seq) );
            String[] kcore_nodes = new String[kcore.seq.length];
            for (int node_id =0; node_id< kcore.seq.length; node_id++) {                
                kcore_nodes[node_id] = cluster_terms[kcore.seq[node_id]];
            }   
            kcore_pw.println(Arrays.toString(kcore_nodes));
        }       

    }
    
    /*
    I took as input the file with the Cluster ID and the list of term
    and for each cluster i create a cooccurence graph this will be done for the yes 
    and the no goup. In input is needed also the index dir (that will be different for the
    yes and the no side. 
    */
    public static void createCoOccurenceGraphsCCandKCORE(String index_dir, double thresholdLFT, double thresholdTotal, String cluster_name_file,String cooccurence_filename, int k, String cc_filename, String kcore_filename) throws IOException, ParseException, InterruptedException{
        PrintWriter cc_pw = new PrintWriter(new FileWriter(cooccurence_graph_directory + cc_filename));
        PrintWriter kcore_pw = new PrintWriter(new FileWriter(cooccurence_graph_directory + kcore_filename));
        PrintWriter cooccurence_pw = new PrintWriter(new FileWriter(cooccurence_graph_directory + cooccurence_filename));
        BufferedReader br = new BufferedReader(new FileReader(cluster_name_file)); 
        int cluster_id = 0;
        String new_row;
//        String filename =  ;        
        while((new_row = br.readLine())!=null){
            cooccurence_pw.println("Cluster ID: " +cluster_id);
            kcore_pw.println("Cluster ID: " +cluster_id);
            cc_pw.println("Cluster ID: " +cluster_id);
            System.out.println("Computing co-occurence graph for cluster "+cluster_id);
//            String filename = cooccurence_graph_directory + cooccurence_filename + cluster_id + ".txt";
            
            String just_terms = new_row.replace(new_row.split(", ")[0].split(" ")[0]+ " ", "");
            String[] cluster_list = just_terms.substring(1, just_terms.length()-1 ).split(", ");
            WeightedUndirectedGraph graph =createCoOccurrenceGraphCluster(cooccurence_pw,cluster_list, index_dir, thresholdLFT, thresholdTotal);
            extractANDsaveCCandKCore(cc_pw, kcore_pw, cluster_list, graph);
            
            cluster_id++;
            cooccurence_pw.println("------------------------------------");
            cc_pw.println("------------------------------------");
            kcore_pw.println("------------------------------------");          
        }         
        cooccurence_pw.close();
        kcore_pw.close();
        cc_pw.close();
    }
       public static void getCoOccurenceGraphCCandKCore(int number_no_cluster, int number_yes_cluster,double thresholdLFT, double thresholdTotal) throws IOException, ParseException, InterruptedException{
//        double thresholdLFT = 0.3;
//        double thresholdTotal = 0.1;
//        int k = 13;
//                
        System.out.println("====================================================");
        System.out.println("YES SIDE CO-OCCURENCE GRAPHS COMPUTATION");
        System.out.println(" ");
        String yes_cluster_filename = cluster_directory + "yesClusterListGroup.txt";
        String yes_kcore_filename = "yes_side/yesKCore.txt";
        String yes_cc_filename = "yes_side/yesCC.txt";
        String semi_filename_yes = "yes_side/yes_cooccurence_graph_cluster.txt";
        createCoOccurenceGraphsCCandKCORE(output_data_directory+ "yes_politicians_index",thresholdLFT, thresholdTotal,yes_cluster_filename,semi_filename_yes ,  number_yes_cluster, yes_cc_filename, yes_kcore_filename);
        
        System.out.println("====================================================");
        System.out.println("NO SIDE CO-OCCURENCE GRAPHS COMPUTATION");
        String no_cluster_filename = cluster_directory + "noClusterListGroup.txt";
        String no_kcore_filename =  "no_side/noKCore.txt";
        String no_cc_filename = "no_side/noCC.txt";
        String semi_filename_no = "no_side/no_cooccurence_graph_cluster.txt";
        createCoOccurenceGraphsCCandKCORE(output_data_directory+ "no_politicians_index", thresholdLFT, thresholdTotal, no_cluster_filename,semi_filename_no, number_no_cluster, no_cc_filename, no_kcore_filename);
        
    }

}
