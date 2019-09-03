/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import static org.apache.lucene.util.Version.LUCENE_41;



/**
 *
 * @author Roberta
 */
public class CooccurenceGraph {    
    public static void writeCoocurrenceGraph(ArrayList<String> nodes1, ArrayList<String> nodes2, ArrayList<Integer> weights, String file_name)throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(file_name));
        for (int i = 0; i < nodes1.size() ; i++) {
            pw.println( nodes1.get(i) + " "+ nodes2.get(i) + " " + weights.get(i));
        }
        pw.close();
    }     
    public static int countCoOccurrence (String t1, String t2, IndexReader index_reader) throws ParseException, IOException{
        IndexSearcher searcher = new IndexSearcher(index_reader);
        Analyzer analyzer = new ItalianAnalyzer(Version.LUCENE_41);
        QueryParser parser = new QueryParser(LUCENE_41, "term", analyzer);
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
    
    public static WeightedUndirectedGraph createCoOccurrenceGraphCluster (String[] cluster_list, String dir, int threshold, String file_name) throws IOException, ParseException{       
        WeightedUndirectedGraph graph = new WeightedUndirectedGraph(cluster_list.length); 
        File directory = new File(dir);
        IndexReader index_reader = DirectoryReader.open(FSDirectory.open(directory));
        ArrayList<String> nodes1 = new ArrayList<>();
        ArrayList<String> nodes2 = new ArrayList<>();
        ArrayList<Integer> weights = new  ArrayList<>();        
        for (int i = 0; i < cluster_list.length; i++){
            String term1 = cluster_list[i];
            for (int j = i + 1; i < cluster_list.length + 1; i++){
                String term2 = cluster_list[i];
                int weight = countCoOccurrence (term1, term2, index_reader);
                if (weight >= threshold){
                    graph.add(i,j, weight);
                    nodes1.add(term1);
                    nodes2.add(term2);
                    weights.add(weight);
                }              
            }    
        }
        writeCoocurrenceGraph(nodes1, nodes2, weights, file_name);
        return(graph);
    }
}
