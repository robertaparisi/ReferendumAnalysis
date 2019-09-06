package indexing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Roberta
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;



import static org.apache.lucene.util.Version.LUCENE_41;
import twitter4j.HashtagEntity;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;


public class TweetIndex {
    
    public static final String  stream_directory = "src/main/resources/data/stream/";
    public static final String  stopwords_filepath = "src/main/resources/data/stopwords_list.txt";
    public static final String  sourcenames_directory = "src/main/resources/data/sourcenames/";
    public static final String  output_data_directory= "src/main/resources/outputData/";
    public static final String  tweets_index_directory = output_data_directory + "tweets_index/";
//    public static final String  stopwords_list = "src/main/resources/data/stopwords.txt";
    public static final File[] stream_days_subdir = new File(stream_directory).listFiles((File file) -> file.isDirectory());
    private static Object GzipReader;
    
    public static CharArraySet getItalianStopwordsList() throws IOException{  
        FileInputStream input_stream = new FileInputStream(stopwords_filepath);
        InputStreamReader input_reader = new InputStreamReader(input_stream);
        BufferedReader br = new BufferedReader(input_reader);

        String sw;
//        ArrayList<String> stopwords = new ArrayList();
        CharArraySet stopwords = CharArraySet.copy(Version.LUCENE_41, ItalianAnalyzer.getDefaultStopSet());
//        
        while ((sw = br.readLine()) != null) {
            stopwords.add(sw);
        }

//        STOPWORDS.addAll(stopwords);
        return (stopwords);
    }
    
    public static BufferedReader getBufferedReaderGzipFile(String filename) throws IOException {
        // open the input (compressed) file.
        FileInputStream stream_file = new FileInputStream(filename);
        // open the gziped file to decompress.
        GZIPInputStream gzipstream_file = new GZIPInputStream(stream_file);
        Reader file_reader = new InputStreamReader(gzipstream_file, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(file_reader);
        //returning the buffered reader for the file
        return br;
        }
    
    public static IndexWriter createNewIndex(String sub_directory) throws IOException{
        File indexFile = new File(sub_directory);
        Directory index = new SimpleFSDirectory(indexFile);
        
        Analyzer italian_analyzer = new ItalianAnalyzer(Version.LUCENE_41, getItalianStopwordsList());
        Analyzer simple_analyzer = new SimpleAnalyzer(Version.LUCENE_41);
        Analyzer tokenization = new WhitespaceAnalyzer(Version.LUCENE_41); // a tutti gli altri applichiamo la semplice  tokenizazzione
        
        
        Map<String, Analyzer> field_analyzer = new HashMap<>(); 
        field_analyzer.put("text", italian_analyzer); // per il testo del tweet usiamo l'italian perche' stemma le parole e toglie stopwords
        field_analyzer.put("name", simple_analyzer); //il name non non lo voglio stemmato
    
        PerFieldAnalyzerWrapper field_anal_wrapper = new PerFieldAnalyzerWrapper(tokenization, field_analyzer);
        
        IndexWriterConfig cfg = new IndexWriterConfig(LUCENE_41, field_anal_wrapper);
        
        return (new IndexWriter(index, cfg));
    } 


    public static org.apache.lucene.document.Document createNewDoc(Long date, Long id, String screenname, String name, String text, String hashtags, String mentions) {       
        org.apache.lucene.document.Document doc;
        doc = new org.apache.lucene.document.Document();
        doc.add(new LongField("date", date, Field.Store.YES));
        doc.add(new LongField("id",id, Field.Store.YES)); 
        doc.add(new StringField("screenname", screenname, Field.Store.YES)); 
        doc.add(new TextField("name",name, Field.Store.YES)); 
        doc.add(new TextField("text",text, Field.Store.YES)); 
        doc.add(new TextField("hashtags", hashtags.toLowerCase(), Field.Store.YES));
        doc.add(new TextField("mentions", mentions, Field.Store.YES));
//        doc.add(new LongField("num_followers", num_followers, Field.Store.YES));  
        return doc;
}

    
    public static void createIndexAllTweets() throws IOException, ParseException, java.text.ParseException, JSONException, TwitterException {
        JSONObject json;
        BufferedReader br;
        String tweet;
        org.apache.lucene.document.Document document;
        
        
        IndexWriter index_writer = createNewIndex(tweets_index_directory);     
        System.out.println("Number of folder: " + stream_directory.length());
        for (File subDirectory : stream_days_subdir) {
            System.out.println("Reading file in the folder " +  subDirectory.getName());
            File[] tweet_files = new File(subDirectory.getAbsolutePath()).listFiles((File file) -> file.isFile());
            System.out.println("Number of file to read in this folder: " + tweet_files.length);
            
            //creating an index for each file in each subdirectories 
            for (File file : tweet_files) {
                //using the getBufferedReaderGzipFile function to unzip and read the file
                br = getBufferedReaderGzipFile(file.getPath());
                StatusWrapper sw;
                //each line in the file is a tweet, so let's add it to the index
                //field to store: date, screen name, name, user id, number of followers and text 
                while ((tweet = br.readLine()) != null) {
                            sw = new StatusWrapper();
                            sw.load(tweet);
                            Long tweet_time = sw.getTime();
//                            SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMYYYYHHmmss");
//                            Date tweet_date = (Date) dateFormatter.parseObject(tweet_time + "");
//                            System.out.println("Date_time: "+ tweet_date);
                            Long user_id = sw.getStatus().getUser().getId();
                            String screen_name = sw.getStatus().getUser().getScreenName();
                            String name = sw.getStatus().getUser().getName().toLowerCase();
//                            Long num_followers = (long) sw.getStatus().getUser().getFollowersCount();
                            String tweet_text = sw.getStatus().getText();
                            UserMentionEntity[] user_mentions = sw.getStatus().getUserMentionEntities();
                            String mentions = "";
                            for (UserMentionEntity m : user_mentions) {
                                mentions += m.getText()+ " ";
                            }                                
                            HashtagEntity[] hashtag_entities = sw.getStatus().getHashtagEntities();
//                            ArrayList<String> hashtags_list = new ArrayList<>();
                            String hashtags = "";
                            for (HashtagEntity h : hashtag_entities) {
//                                hashtags_list.add(h.getText());
                                hashtags += h.getText()+ " ";
                            }
                   
                            document = createNewDoc(tweet_time, user_id, screen_name, name, tweet_text, hashtags,  mentions);
                            index_writer.addDocument(document);
                }
            }
        }

        index_writer.commit(); 
        index_writer.close();
    }
    
  
  
}
