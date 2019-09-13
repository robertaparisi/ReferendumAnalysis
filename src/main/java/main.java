
import UsersCandidatesSupporter.GraphAnalysisUsers;
import static UsersCandidatesSupporter.GraphAnalysisUsers.runner;
import static UsersCandidatesSupporter.GraphAnalysisUsers.users_list_no;
import static UsersCandidatesSupporter.GraphAnalysisUsers.users_list_yes;
import analysis.CooccurenceGraphs;
import analysis.SAXanalysisAndKMeans;
import analysis.TimeSeriesPlot;
import it.stilo.g.algo.SubGraph;
import it.stilo.g.structures.WeightedDirectedGraph;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;
import twitter4j.JSONException;
import twitter4j.TwitterException;

public class main {
    public static void main(String[] args) throws IOException, ParseException, JSONException, TwitterException, org.apache.lucene.queryparser.classic.ParseException, Exception {
//        Boolean recalculate_general_index = true;
        Boolean recalculate_general_index = false;
//        Boolean recalculate_yesno_index = true;  
        Boolean recalculate_yesno_index = false;
        
        
        indexing.MainIndex.run_main_index(recalculate_general_index,recalculate_yesno_index);
        
         
//        Boolean create_temporal_yesno_plot = true;
        Boolean create_temporal_yesno_plot = false;
        if (create_temporal_yesno_plot){
            TimeSeriesPlot.create_timeseries_plot();
        }
        
        //create SAX string and clusterize them for each of the two group (Y, N)
        int alphabet_size = 2; //is the number of letter to consider, basically if it's a is under the median, otherwise it's more   
        int number_yes_cluster = 13; // n. of yes clusters
        int number_no_cluster = 13; // n. of no clusters
//        Boolean computeSAXstringAndKmeans = true;
        Boolean computeSAXstringAndKmeans = false;
        if (computeSAXstringAndKmeans){
            SAXanalysisAndKMeans.saxStringAndKMeansComputation(alphabet_size, number_no_cluster, number_yes_cluster);
        }
        double thresholdLFT = 0.3;
        double thresholdTotal = 0.1;
//        boolean cooccurenceGraphsComputation = true;
        boolean cooccurenceGraphsComputation = false;
        if (cooccurenceGraphsComputation){
            CooccurenceGraphs.getCoOccurenceGraphCCandKCore(number_no_cluster, number_yes_cluster, thresholdLFT, thresholdTotal);
        }
        
//        Boolean classify_user = true;
        Boolean classify_user = false;
        if (classify_user){
            UsersCandidatesSupporter.UserYesNoSupporter.createUserClassification();
        }
        
        Boolean extractLCCandHITS = true;
//        Boolean extractLCCandHITS = false;
//        Boolean extractTop500central = true;
//        Boolean extractTop500central = false;
        if (extractLCCandHITS){
            GraphAnalysisUsers analysis4graph = new GraphAnalysisUsers();
            WeightedDirectedGraph lcc_graph = analysis4graph.saveLccHITSandTop500();
        }

       
        
     }
    
}
