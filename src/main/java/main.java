
import analysis.TimeSeriesPlot;
import java.io.IOException;
import java.text.ParseException;
import twitter4j.JSONException;
import twitter4j.TwitterException;

public class main {
    public static void main(String[] args) throws IOException, ParseException, JSONException, TwitterException, org.apache.lucene.queryparser.classic.ParseException {
//        Boolean recalculate_general_index = true;
        Boolean recalculate_general_index = false;
        Boolean recalculate_yesno_index = true;  
//        Boolean recalculate_yesno_index = false;
        
        
        indexing.MainIndex.run_main_index(recalculate_general_index,recalculate_yesno_index);
        
         
        Boolean create_temporal_yesno_plot = true;
//        Boolean create_temporal_yesno_plot = false;
        if (create_temporal_yesno_plot){
            TimeSeriesPlot.create_timeseries_plot();
            
        }
      
         
         
     }
    
}
