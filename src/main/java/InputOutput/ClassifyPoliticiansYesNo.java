/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InputOutput;

import static InputOutput.TweetIndex.createNewIndex;
import static InputOutput.TweetIndex.createNewDoc;
import static InputOutput.TweetIndex.createNewIndex;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import static InputOutput.TweetIndex.output_data_directory;
import static InputOutput.TweetIndex.sourcenames_directory;
import static InputOutput.TweetIndex.tweets_index_directory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Roberta
 */
public class ClassifyPoliticiansYesNo {
    private static Object names;
    
    
    public static Map<String, List<String>> classifyPoliticians(Directory dir, String[] politicians) throws IOException  {
        IndexReader index_reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(index_reader);
        
        Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_41);
        QueryParser qparser = new QueryParser(Version.LUCENE_41, "hashtags", analyzer);
        
        String[] yes_tags = {"bastaunsi", "si", "iodicosi", "iovotosi"};
        String[] no_tags = {"iodicono", "iovotono", "no", "bastaunno"};
        
        BooleanQuery yes = new BooleanQuery();
                for (String tag : yes_tags) {
            Query query_term = new TermQuery(new Term("hashtags", tag));
            yes.add(query_term, BooleanClause.Occur.SHOULD);
        }
        
        BooleanQuery no = new BooleanQuery();
        for (String tag : no_tags) {
            Query query_term = new TermQuery(new Term("hashtags", tag));
            no.add(query_term, BooleanClause.Occur.SHOULD);
        }        
        
        TotalHitCountCollector collector;
               
        Query q;
        List<String> yes_politicians = new ArrayList<>();
        List<String> no_politicians = new ArrayList<>();
        Map<String, List<String>> yes_no_hashmap  = new HashMap<>();
        
        for (String politician: politicians) {
         
            q = new TermQuery(new Term("screenname", politician));
            
            BooleanQuery qyes = new BooleanQuery();
            qyes.add(yes, BooleanClause.Occur.MUST);
            qyes.add(q, BooleanClause.Occur.MUST);
            
            
            BooleanQuery qno = new BooleanQuery();
            qno.add(no, BooleanClause.Occur.MUST);
            qno.add(q, BooleanClause.Occur.MUST);

            collector = new TotalHitCountCollector();
            searcher.search(qyes, collector);
            int yes_tag_count = collector.getTotalHits();
            
            collector = new TotalHitCountCollector();
            searcher.search(qno, collector);
            int no_tag_count = collector.getTotalHits();

            if (yes_tag_count != 0 || no_tag_count != 0) {
                if (yes_tag_count > no_tag_count) {
                    yes_politicians.add(politician);
                    System.out.println(politician);
                    System.out.println("YES: " + yes_tag_count + " - " + no_tag_count);
                } else {
                    no_politicians.add(politician);
                    System.out.println(politician);
                    System.out.println("NO: " + yes_tag_count + " - " + no_tag_count);
                }
                
//                if they never used a tag between the yes or no tag that we have they are not going to be stored
            }
        }
        
        try (PrintWriter writer = new PrintWriter(sourcenames_directory + "yes_politicians.txt", "UTF-8")) {
            writer.print(String.join(",", yes_politicians));
        }
             
        try (PrintWriter writer = new PrintWriter(sourcenames_directory + "no_politicians.txt", "UTF-8")) {
            writer.print(String.join(",", no_politicians));
        }
           
        
        
        yes_no_hashmap.put("yes", yes_politicians);
        yes_no_hashmap.put("no", no_politicians);
        return (yes_no_hashmap);
    }
    
    public static void createIndexYesNoPolitician(Directory dir, List<String> politicians, int top_n_results, String file_name) throws IOException  {
//          private static void index(Directory dir, ArrayList<String> screenNames, String fileName, boolean stemming) throws IOException {
        IndexReader index_reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(index_reader);
        
        BooleanQuery screen_names = new BooleanQuery();
        //String q = "";
        for (String p: politicians) {
            Query q = new TermQuery(new Term("screenname", p));
            screen_names.add(q, BooleanClause.Occur.SHOULD);
        }
        
        
        BooleanQuery query = new BooleanQuery();
        query.add(screen_names, BooleanClause.Occur.MUST);

        TopDocs top_documents = searcher.search(query, top_n_results);
        ScoreDoc[] hits = top_documents.scoreDocs;

        org.apache.lucene.document.Document tweet_entry;
        org.apache.lucene.document.Document doc;
        
        IndexWriter idx= createNewIndex(file_name);

        for (ScoreDoc entry : hits) {
            tweet_entry = searcher.doc(entry.doc);
            System.out.println(tweet_entry.get("date"));
            Long tweet_date = Long.parseLong(tweet_entry.get("date"));
            Long user_id = Long.parseLong(tweet_entry.get("id"));
            String screen_name = tweet_entry.get("screenname");
            String name = tweet_entry.get("name");
            String tweet_text = tweet_entry.get("text");
            String hashtags = tweet_entry.get("hashtags");
            String mentions = tweet_entry.get("mentions");
          


            doc =createNewDoc(tweet_date, user_id, screen_name, name, tweet_text, hashtags, mentions);
            idx.addDocument(doc);
        }
        System.out.println("Number of document in the index: " + idx.maxDoc());
        idx.close();

         
    }
    
    public static void main (String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
      
        FileInputStream fstream = new FileInputStream(sourcenames_directory + "politicians.txt");
        InputStreamReader file_reader = new InputStreamReader(fstream, "UTF-8");
        BufferedReader br = new BufferedReader(file_reader); 
        String[] politicians = br.readLine().split(",");
        
        Directory tweets_idx_dir = new SimpleFSDirectory(new File(tweets_index_directory));
        Map<String, List<String>> yes_no_hashmap = classifyPoliticians(tweets_idx_dir , politicians);
        
         
        System.out.println("Total numbers of politicians: "+ politicians.length +" which "+yes_no_hashmap.get("yes").size()+
                "support the referendum (voting YES), other "+ yes_no_hashmap.get("no")+" will not support the referendum(voting NO)");

        String file_yes = "yes_politicians_index";
        String file_no = "no_politicians_index";
        int top_n_results = 100000000;
        
        System.out.println("Creating the index for the Yes side");
        createIndexYesNoPolitician(tweets_idx_dir, yes_no_hashmap.get("yes"), top_n_results, output_data_directory + file_yes);
        System.out.println("Creating the index for the No side");
        createIndexYesNoPolitician(tweets_idx_dir, yes_no_hashmap.get("no"), top_n_results, output_data_directory + file_no);      
 
    }
    
}
