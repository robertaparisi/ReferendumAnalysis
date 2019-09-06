/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;
import static indexing.TweetIndex.output_data_directory;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
    public static void writeCoocurrenceGraph(ArrayList<String> nodes1, ArrayList<String> nodes2, ArrayList<Integer> weights, String file_name)throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file_name))) {
            for (int i = 0; i < nodes1.size() ; i++) {
                pw.println( nodes1.get(i) + " "+ nodes2.get(i) + " " + weights.get(i));
            }
        }
    }      
         
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
        boolean clause_respected = coOccurence>= min_num_cooccurence | weight>=thresholdTotal;
        return(clause_respected);
        
    }
    
  
    public static WeightedUndirectedGraph createCoOccurrenceGraphCluster (String[] cluster_list, String index_dir, double thresholdLFT, double thresholdTotal,  String file_name) throws IOException, ParseException{       
        WeightedUndirectedGraph graph = new WeightedUndirectedGraph(cluster_list.length); 
        File directory = new File(index_dir);
        IndexReader index_reader = DirectoryReader.open(FSDirectory.open(directory));
        ArrayList<String> nodes1 = new ArrayList<>();
        ArrayList<String> nodes2 = new ArrayList<>();
        ArrayList<Integer> weights = new  ArrayList<>(); 
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
                    nodes1.add(term1);
                    nodes2.add(term2);
                    weights.add(cooccurence);
                }              
            }    
        }
        System.out.println("done, let's save the graph");
        System.out.println(" ");
        System.out.println(" ");
        writeCoocurrenceGraph(nodes1, nodes2, weights, file_name);
        return(graph);//serve?
    }
    
    
    /*
    I took as input the file with the Cluster ID and the list of term
    and for each cluster i create a cooccurence graph this will be done for the yes 
    and the no goup. In input is needed also the index dir (that will be different for the
    yes and the no side. 
    */
    public static void createCoOccurenceGraphs(String index_dir, double thresholdLFT, double thresholdTotal, String cluster_name_file,String cooccurence_filename, int k) throws IOException, ParseException{
        BufferedReader br = new BufferedReader(new FileReader(cluster_name_file)); 
        int cluster_id = 0;
        String new_row;
        while((new_row = br.readLine())!=null){
            System.out.println("Computing co-occurence graph for cluster "+cluster_id);
            String filename = cooccurence_graph_directory + cooccurence_filename + cluster_id + ".txt";
            String just_terms = new_row.replace(new_row.split(", ")[0].split(" ")[0]+ " ", "");
            String[] cluster_list = just_terms.substring(1, just_terms.length()-1 ).split(", ");
            WeightedUndirectedGraph graph =createCoOccurrenceGraphCluster(cluster_list, index_dir, thresholdLFT, thresholdTotal, filename);
            cluster_id++;
        }         
    }

}
