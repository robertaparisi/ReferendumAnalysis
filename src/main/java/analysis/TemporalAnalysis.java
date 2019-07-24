/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author Roberta
 */
public class TemporalAnalysis {
    
    private IndexReader index_reader;
    private ArrayList<Long> time_interval;
    private final int top_n_results = 10000000;

    TemporalAnalysis(String index_path) throws IOException {
        Directory directory = new SimpleFSDirectory(new File(index_path));
        this.index_reader = DirectoryReader.open(directory);
    }
          
    public void setGrain (Long grain) throws ParseException{
        String starting_date = "2016/11/26 12:00:00";
        String ending_date = "2016/12/07 00:00:00";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date start_date_formatted = formatter.parse(starting_date);
        Date end_date_formatted = formatter.parse(ending_date);
        long starting_time = start_date_formatted.getTime();
        long ending_time = end_date_formatted.getTime();
        this.time_interval = new ArrayList<>();
        for (long d = starting_time; d < ending_time + grain; d += grain) {
            this.time_interval.add(d);
        }
    }
    
        
    public ArrayList<Double> frequencies_value() throws ParseException, IOException, org.apache.lucene.queryparser.classic.ParseException {
        
        IndexSearcher searcher = new IndexSearcher(this.index_reader);
        ArrayList<Double> frequencies = new ArrayList<>();
        
        for (int n = 1; n < time_interval.size(); n++) {
            Query query = NumericRangeQuery.newLongRange("date", time_interval.get(n-1), time_interval.get(n), true, false);
            TopDocs top_documents = searcher.search(query, top_n_results);
            frequencies.add(Double.valueOf(top_documents.totalHits));
        }

        return (frequencies);
    }
    public TimeSeriesCollection mergeCoordinates(TimeSeriesCollection collection, ArrayList<Double> frequencies, String label){
        TimeSeries series = new TimeSeries(label);
        for (int interval = 0; interval < time_interval.size()-1; interval ++){
            Hour h = new Hour(new Date(time_interval.get(interval)));
            series.add(h, frequencies.get(interval));
        }
        collection.addSeries(series);
        return(collection);
    }

            
}
    
   
        
    
}
