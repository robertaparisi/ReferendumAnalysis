
import analysis.SAXanalysisAndKMeans;
import analysis.TimeSeriesPlot;
import java.io.IOException;
import java.text.ParseException;
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
        SAXanalysisAndKMeans.saxStringAndKMeansComputation(alphabet_size, number_no_cluster, number_yes_cluster);
      
         
         
     }
    
}
