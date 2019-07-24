package InputOutput;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Roberta
 */


import InputOutput.StatusWrapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import twitter4j.HashtagEntity;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;


public class TweetIndex {
    
    public static final String  stream_directory = "src/main/resources/data/stream/";
    public static final String  sourcenames_directory = "src/main/resources/data/sourcenames/";
    public static final String  output_data_directory= "src/main/resources/outputData/";
    public static final String  tweets_index_directory = output_data_directory + "tweets_index/";
    public static final File[] stream_days_subdir = new File(stream_directory).listFiles((File file) -> file.isDirectory());
    private static Object GzipReader;
    
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
        ItalianAnalyzer analyzer = new ItalianAnalyzer(Version.LUCENE_41);
        IndexWriterConfig cfg = new IndexWriterConfig(Version.LUCENE_41, analyzer);
        File indexFile = new File("src/main/resources/outputData" + sub_directory);
        FSDirectory index = FSDirectory.open(indexFile);
        return new IndexWriter(index, cfg);
    } 


    public static org.apache.lucene.document.Document createNewDoc(Long date, Long id, String screenname, String name, String text, String hashtags, String mentions) {       
        org.apache.lucene.document.Document doc;
        doc = new org.apache.lucene.document.Document();
        doc.add(new LongField("date", date, Field.Store.YES));
        doc.add(new LongField("id",id, Field.Store.YES)); 
        doc.add(new StringField("screenname", screenname, Field.Store.YES)); 
        doc.add(new StringField("name",name, Field.Store.YES)); 
        doc.add(new StringField("text",text, Field.Store.YES)); 
        doc.add(new StringField("hashtags", hashtags, Field.Store.YES));
        doc.add(new StringField("mentions", mentions, Field.Store.YES));
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
                System.out.println("Reading " + file.getName());
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
