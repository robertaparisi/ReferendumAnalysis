
import SpreadOfInfluence.MainLPA;
import static SpreadOfInfluence.MainLPA.getLPA;
import UsersCandidatesSupporter.GraphAnalysisUsers;
import analysis.CooccurenceGraphs;
import analysis.SAXanalysisAndKMeans;
import analysis.TimeSeriesPlot;
import it.stilo.g.structures.WeightedDirectedGraph;
import java.io.IOException;
import java.text.ParseException;
import twitter4j.JSONException;
import twitter4j.TwitterException;

public class main {
    public static void main(String[] args) throws IOException, ParseException, JSONException, TwitterException, org.apache.lucene.queryparser.classic.ParseException, Exception {
//        Boolean recalculate_general_index = true;
        Boolean recalculate_general_index = false; //PAY ATTENTION, HEAVY TIME CONSUMING!!!! if you already have the tweet_index folder don't use this as true
//        Boolean recalculate_yesno_index = true;  
        Boolean recalculate_yesno_index = false; // medium time consuming  - not a big deal
        
        
        indexing.MainIndex.run_main_index(recalculate_general_index,recalculate_yesno_index);
        
         
//        Boolean create_temporal_yesno_plot = true;
        Boolean create_temporal_yesno_plot = false; //FAST!
        if (create_temporal_yesno_plot){
            TimeSeriesPlot.create_timeseries_plot();
        }
        
        //create SAX string and clusterize them for each of the two group (Y, N)
        int alphabet_size = 2; //is the number of letter to consider, basically if it's a is under the median, otherwise it's more   
        int number_yes_cluster = 13; // n. of yes clusters
        int number_no_cluster = 13; // n. of no clusters
//        Boolean computeSAXstringAndKmeans = true;
        Boolean computeSAXstringAndKmeans = false; // fast enough (less than 1 minutes)
        if (computeSAXstringAndKmeans){
            SAXanalysisAndKMeans.saxStringAndKMeansComputation(alphabet_size, number_no_cluster, number_yes_cluster);
        }
        double thresholdLFT = 0.3;
        double thresholdTotal = 0.1;
//        boolean cooccurenceGraphsComputation = true;
        boolean cooccurenceGraphsComputation = false; // medium time consuming - not a big deal
        if (cooccurenceGraphsComputation){
            CooccurenceGraphs.getCoOccurenceGraphCCandKCore(number_no_cluster, number_yes_cluster, thresholdLFT, thresholdTotal);
        }
        
//        Boolean classify_user = true;
        Boolean classify_user = false; //HEAVY TIME CONSUMING (12 to 14 hours)
        if (classify_user){
            UsersCandidatesSupporter.UserYesNoSupporter.createUserClassification();
        }
        
//        Boolean extractLCCandHITS = true;
        Boolean extractLCCandHITS = false; //medium time consuming  - not a big deal
        
        Boolean computeSpreadOfInfluence = true;
//        Boolean computeSpreadOfInfluence = false;
        if (extractLCCandHITS){
            GraphAnalysisUsers analysis4graph = new GraphAnalysisUsers();
            WeightedDirectedGraph graph = analysis4graph.saveLccHITSandTop500();
            if (computeSpreadOfInfluence){
                MainLPA.getLPA(graph);
            }
        }
        else{
            WeightedDirectedGraph graph = GraphAnalysisUsers.getGraph();        
            MainLPA.getLPA(graph);
        }

       
        
     }
    
}
