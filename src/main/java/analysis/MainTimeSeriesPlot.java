/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import InputOutput.TweetIndex;
import java.io.IOException;
import java.text.ParseException;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Roberta
 */
public class MainTimeSeriesPlot {
    public static void main (String[] args) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
//        output_data_directory
        String yes_index_path = TweetIndex.output_data_directory+ "yes_politicians_index";
        String no_index_path = TweetIndex.output_data_directory+ "no_politicians_index";
        
        long grain = 43200000; //12 hours of granularity in milliseconds
       
        TemporalAnalysis temp_anal_yes = new TemporalAnalysis(yes_index_path);
        TemporalAnalysis temp_anal_no = new TemporalAnalysis(no_index_path);
        
        temp_anal_yes.setGrain(grain);
        temp_anal_no.setGrain(grain);

        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection = temp_anal_yes.mergeCoordinates(collection, temp_anal_yes.frequencies_value(), "YES tweets");
        collection = temp_anal_no.mergeCoordinates(collection, temp_anal_no.frequencies_value(), "NO tweets");

        TimeSeriesPlot plot = new TimeSeriesPlot("tweeting_frequencies", "Tweeting Frequencies \nTime Series", collection , TweetIndex.output_data_directory + "/TimeSeriesPlotYN.jpg", 200);
        plot.pack();
        plot.setLocation(800, 20);
        plot.setVisible(true);// controlla
    }
}
