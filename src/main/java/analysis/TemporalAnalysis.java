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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.sax.SAXProcessor;
import net.seninp.jmotif.sax.alphabet.NormalAlphabet;
import net.seninp.jmotif.sax.datastructure.SAXRecords;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.FieldCacheRangeFilter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author Roberta
 */
public class TemporalAnalysis {
    
  
    private final IndexReader index_reader;
    private final int top_n_results = 10000000;
   
    TemporalAnalysis(String index_path) throws IOException {
        Directory directory = new SimpleFSDirectory(new File(index_path));
        this.index_reader = DirectoryReader.open(directory);
        }    
          
    public static ArrayList<Long> setGrain (Long grain) throws ParseException{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date start_date_formatted = formatter.parse("2016/11/26 00:00:00");
        Date end_date_formatted = formatter.parse("2016/12/08 00:00:00");
        long starting_time = start_date_formatted.getTime();
        long ending_time = end_date_formatted.getTime();
        ArrayList<Long> time_interval = new ArrayList<>();
        for (long d = starting_time; d < ending_time + grain; d += grain) {
            time_interval.add(d);
        }
        return(time_interval);
    }    
        
    public ArrayList<Double> frequencies_value(ArrayList<Long> time_interval) throws ParseException, IOException, org.apache.lucene.queryparser.classic.ParseException {
        IndexSearcher searcher = new IndexSearcher(this.index_reader);
        ArrayList<Double> frequencies = new ArrayList<>();
        for (int n = 1; n < time_interval.size(); n++) {
            Query query = NumericRangeQuery.newLongRange("date", time_interval.get(n-1), time_interval.get(n), true, false);
            TopDocs top_documents = searcher.search(query, top_n_results);
            frequencies.add(Double.valueOf(top_documents.totalHits));
        }
        return (frequencies);
    }
    
    public TimeSeriesCollection mergeCoordinates(TimeSeriesCollection collection, ArrayList<Double> frequencies, String label, ArrayList<Long> time_interval){
        TimeSeries series = new TimeSeries(label);
        for (int interval = 0; interval < time_interval.size()-1; interval ++){
            Hour h = new Hour(new Date(time_interval.get(interval)));
            series.add(h, frequencies.get(interval));
        }
        collection.addSeries(series);
        return(collection);
    }
    
    
    ////////////// second part////////////////////////////////////////////

    public static ArrayList<String>orderedTermFrequencies(IndexReader index_reader, long min_term_frequency) throws IOException { 
        //NavigableMap<Integer, ArrayList<String>> 
        //i am going to use treemap, since they keep the order if the freequencies, in order to speed up the rest of the work        
        NavigableMap<Integer, ArrayList<String>> terms_freq = new TreeMap<>(Collections.reverseOrder());
        Fields fields = MultiFields.getFields(index_reader);
        TermsEnum terms_enum = fields.terms("text").iterator(null);
        System.out.println("Number of terms in the index: " + fields.terms("text").size());
        BytesRef bytes_ref;
        while ((bytes_ref = terms_enum.next()) != null) {
            String term = bytes_ref.utf8ToString(); 
            int freq = terms_enum.docFreq();//index_reader.docFreq(new Term("text", bytes_ref));
            ArrayList<String> array_of_terms = new ArrayList<>();
            array_of_terms.add(bytes_ref.utf8ToString());          
            if (terms_freq.containsKey(freq) == true) {// if already exist words with that frequency
                array_of_terms.addAll(terms_freq.get(freq));
            }            
            terms_freq.put(freq, array_of_terms);
        }        
        ArrayList<String> oterms = new ArrayList<>();
        for (Integer f: terms_freq.keySet()) {
            if (f>= min_term_frequency) {
                //System.out.println(terms.get(freq) + ": " + freq);
                for (String t : terms_freq.get(f)) {
                    // terms containing at least one alphabet character
                    if (t.matches(".*[a-z]+.*") && t.length() >= 2) {
                        oterms.add(t);
                    }
                   
                }
            }
        }
        System.out.println("Terms that have minimum lenght 2, at least 1 alphabetic character and that appear at least " + min_term_frequency +" times: " + oterms.size());
    return(oterms);
    }
    
    
    public static int peak_query(String term, long start, long end, IndexReader index_reader) throws IOException {
        IndexSearcher searcher = new IndexSearcher(index_reader);
        Query query = new TermQuery(new Term("text", term));
        TotalHitCountCollector collector = new TotalHitCountCollector();
        FieldCacheRangeFilter<Long> dateFilter = FieldCacheRangeFilter.newLongRange("date", start, end, true, true);
        searcher.search(query, dateFilter, collector);
        return(collector.getTotalHits());
    }
    

    public static String buildSaxString(ArrayList<Integer> timeSeries, int alphabetSize, double threshold) throws SAXException {
        double[] time_series_array = timeSeries.stream().mapToDouble(Integer::doubleValue).toArray();
        NormalAlphabet na = new NormalAlphabet();
        SAXProcessor processor = new SAXProcessor();
        SAXRecords sax_records = processor.ts2saxByChunking(time_series_array, time_series_array.length, na.getCuts(alphabetSize), threshold);
        String sax_string = sax_records.getSAXString("");
        return (sax_string);
    }   
           
    
    public static Map<String, String> sax_analyzer(String regex_match, double threshold, ArrayList<Long> time_interval, IndexReader index_reader, long min_term_frequency) throws IOException, SAXException {
        ArrayList<String> terms_freq = orderedTermFrequencies(index_reader, min_term_frequency);
        Map<String, String> top1000term = new HashMap<>();
        for (String term : terms_freq) { // per ogni parola con quella frequenza   .get(freq){ 
            ArrayList<Integer> sax_values_array = new ArrayList<>();
            String sax_string;
            int sax_values;
        // la parola deve essere lunga almeno 2 caratteri e deve avere almeno una lettera
            for (int p = 1; p <  time_interval.size(); p++) {
                sax_values = peak_query(term, time_interval.get(p- 1), time_interval.get(p), index_reader);
                sax_values_array.add(sax_values);                   
            }
            sax_string = buildSaxString(sax_values_array, 2, threshold);
            if (sax_string.matches(regex_match)){     
                top1000term.put(term, sax_string);
                }              
             if (top1000term.size()==1000){ //se abbiamo raggiunto i 1000 termini ci blocchiamo
                break;
            }
        }
        return(top1000term);
    }
               
    public static Map<String, String> compute_sax_analysis(String index_path,int alphabet_size, String regex_match, double threshold, long grain, long min_term_frequency) throws IOException, ParseException, SAXException{
        Directory directory = new SimpleFSDirectory(new File(index_path));
        IndexReader index_reader = DirectoryReader.open(directory);
        TemporalAnalysis temporal_anal = new TemporalAnalysis(index_path);
        ArrayList<Long> time_interval = setGrain(grain);
        Map<String, String> sax = sax_analyzer(regex_match, threshold, time_interval, index_reader, min_term_frequency);
        return (sax);
    }    
       

    
       
   
}

    
   
        
    

