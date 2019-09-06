/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import static analysis.SAXanalysisAndKMeans.cluster_directory;
import static indexing.TweetIndex.output_data_directory;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author Roberta
 */
public class CooccurenceGraphAnalysis {
    

        
    public static void main(String[] args) throws IOException, ParseException{
        double thresholdLFT = 0.3;
        double thresholdTotal = 0.1;
        int k = 13;
                
        System.out.println("====================================================");
        System.out.println("YES SIDE CO-OCCURENCE GRAPHS COMPUTATION");
        System.out.println(" ");
        String yes_cluster_filename = cluster_directory + "yesClusterListGroup.txt";
        String semi_filename_yes = "yes_side/yes_cooccurence_graph_cluster";
        CooccurenceGraphs.createCoOccurenceGraphs(output_data_directory+ "yes_politicians_index",thresholdLFT, thresholdTotal,yes_cluster_filename,semi_filename_yes , k);
        
        System.out.println("====================================================");
        System.out.println("NO SIDE CO-OCCURENCE GRAPHS COMPUTATION");
        String no_cluster_filename = cluster_directory + "noClusterListGroup.txt";
        String semi_filename_no = "no_side/no_cooccurence_graph_cluster";
        CooccurenceGraphs.createCoOccurenceGraphs(output_data_directory+ "no_politicians_index", thresholdLFT, thresholdTotal, no_cluster_filename,semi_filename_no, k);
        
    }
}
 
    

