/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UsersCandidatesSupporter;

import static analysis.CooccurenceGraphs.cooccurence_graph_directory;
import static indexing.ClassifyPoliticiansYesNo.no_tags;
import static indexing.ClassifyPoliticiansYesNo.yes_tags;
import indexing.TweetIndex;
import static indexing.TweetIndex.sourcenames_directory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import static org.apache.commons.math3.analysis.FunctionUtils.collector;
import static org.apache.commons.math3.analysis.FunctionUtils.collector;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import static org.apache.lucene.util.Version.LUCENE_41;
import sun.print.PrinterJobWrapper;

/**
 *
 * @author Roberta
 */
public class UserYesNoSupporter {
    public static Set<String> getUserID() throws FileNotFoundException, IOException{      
        Set<String> user_id = new HashSet<>();        
        FileInputStream fstream = new FileInputStream("src/main/resources/data/Official_SBN-ITA-2016-Net.gz");
        GZIPInputStream gzStream = new GZIPInputStream(fstream);
        InputStreamReader input_stream_reader = new InputStreamReader(gzStream, "UTF-8");
        BufferedReader br = new BufferedReader(input_stream_reader);        
        String edge;
        while ((edge = br.readLine()) != null) {
            user_id.add(edge.split("\t")[0]);
            user_id.add(edge.split("\t")[1]);
        }
        return (user_id);
    }
    
    

 
    public static Map<String, List<String>> classifyUsers(String[] yes_politicians, String[] no_politicians) throws IOException, org.apache.lucene.queryparser.classic.ParseException  {
                
        IndexReader index_reader = DirectoryReader.open(FSDirectory.open(new File(TweetIndex.tweets_index_directory)));
        IndexSearcher searcher = new IndexSearcher(index_reader); 
               
        BooleanQuery yes_tag = new BooleanQuery();
        for (String tag : yes_tags) {
            Query query_term = new TermQuery(new Term("hashtags", tag));

            yes_tag.add(query_term, BooleanClause.Occur.SHOULD);
        }
        BooleanQuery no_tag = new BooleanQuery();
        for (String tag : no_tags) {
            Query query_term = new TermQuery(new Term("hashtags", tag));
            no_tag.add(query_term, BooleanClause.Occur.SHOULD);
        }   
        
        BooleanQuery yes_politician_mentioned = new BooleanQuery();
        for (String p : yes_politicians) {
            Query query_term = new TermQuery(new Term("mentions", p));

            yes_tag.add(query_term, BooleanClause.Occur.SHOULD);
        }
        
        BooleanQuery no_politician_mentioned = new BooleanQuery();
        for (String p : no_politicians) {
            Query query_term = new TermQuery(new Term("mentions", p));
            no_politician_mentioned.add(query_term, BooleanClause.Occur.SHOULD);
        }   
        
        List<String> yes_supporters = new ArrayList<>();
        List<String> no_supporters = new ArrayList<>();
        Map<String, List<String>> yes_no_hashmap  = new HashMap<>();
        Query q;
        Set<String> user_id = getUserID();
        TotalHitCountCollector collector; 
        System.out.println("Total number of user: " + user_id.size());
        
//        BooleanQuery q_yes_tweets = new BooleanQuery();
        int total_yes_tweets = 0;
        int total_no_tweets = 0;
        
        int count_user = 0;
        
        
        for (String user : user_id) {   
            count_user++;
            System.out.println("User id: " + count_user);
            BytesRef ref = new BytesRef();
            NumericUtils.longToPrefixCoded(Long.parseLong(user), 0, ref);
            q = new TermQuery(new Term("id", ref));
           
            BooleanQuery q_yes = new BooleanQuery();            
            q_yes.add(yes_tag, BooleanClause.Occur.MUST);
            q_yes.add(q, BooleanClause.Occur.MUST);
            
            
            collector = new TotalHitCountCollector();
            searcher.search(q_yes, collector);
            int yes_tag_count = collector.getTotalHits();

                        
            BooleanQuery q_no = new BooleanQuery();
            q_no.add(no_tag, BooleanClause.Occur.MUST);
            q_no.add(q, BooleanClause.Occur.MUST);
            
            collector = new TotalHitCountCollector();
            searcher.search(q_no, collector);
            int no_tag_count = collector.getTotalHits();
            
                       
            BooleanQuery q_yes_pol_mention = new BooleanQuery();
            q_yes_pol_mention.add(yes_politician_mentioned, BooleanClause.Occur.MUST);
            q_yes_pol_mention.add(q, BooleanClause.Occur.MUST);
            
            collector = new TotalHitCountCollector();
            searcher.search(q_yes_pol_mention, collector);
            int yes_pol_mention_count = collector.getTotalHits();
            
            
            BooleanQuery q_no_pol_mention = new BooleanQuery();
            q_no_pol_mention.add(no_politician_mentioned, BooleanClause.Occur.MUST);
            q_no_pol_mention.add(q, BooleanClause.Occur.MUST);
            
            collector = new TotalHitCountCollector();
            searcher.search(q_no_pol_mention, collector);
            int no_pol_mention_count = collector.getTotalHits();
            
                       
            if (yes_tag_count != 0 || no_tag_count != 0 || no_pol_mention_count!=0 || yes_pol_mention_count!=0) {
                if ((yes_tag_count > no_tag_count & yes_pol_mention_count > no_pol_mention_count) |
                        (yes_tag_count + yes_pol_mention_count > no_tag_count+ no_pol_mention_count)) {
                    yes_supporters.add(user);

                    TopDocs top_documents = searcher.search(q, Integer.MAX_VALUE);
                    ScoreDoc[] hits = top_documents.scoreDocs;
                    total_yes_tweets += hits.length; //add new number of tweets
                } 
                else if ((yes_tag_count < no_tag_count & yes_pol_mention_count < no_pol_mention_count) |
                        (yes_tag_count + yes_pol_mention_count < no_tag_count+ no_pol_mention_count)){
                    no_supporters.add(user);
                    
                    TopDocs top_documents = searcher.search(q, Integer.MAX_VALUE);
                    ScoreDoc[] hits = top_documents.scoreDocs;
                    total_no_tweets += hits.length;//add new number of tweets
                }
            }
            
        }    
        System.out.println("Number of tweets for no supporters "+ total_no_tweets);
        System.out.println("Number of tweets for yes supporters "+ total_yes_tweets);
        
        yes_no_hashmap.put("yes", yes_supporters);
        yes_no_hashmap.put("no", no_supporters);
            
            
        return(yes_no_hashmap);           
    }
    
    
    
    
    public static void createUserClassification() throws FileNotFoundException, IOException, ParseException{
        
        String filename_yes = "src/main/resources/data/yes_politicians.txt";
        BufferedReader br_yes = new BufferedReader(new FileReader(filename_yes)); 
        String[] yes_politicians = br_yes.readLine().split(",");
        
        String filename_no = "src/main/resources/data/no_politicians.txt";
        BufferedReader br_no = new BufferedReader(new FileReader(filename_no)); 
        String[] no_politicians = br_no.readLine().split(",");  
        
        Map<String, List<String>> supporters = classifyUsers(yes_politicians, no_politicians);
        System.out.println("Done with classification, let's save the two list");
        PrintWriter user_yes_pw = new PrintWriter(new FileWriter("src/main/resources/data/yes_user.txt"));
        user_yes_pw.println(supporters.get("yes"));
        user_yes_pw.close();
        PrintWriter user_no_pw = new PrintWriter(new FileWriter("src/main/resources/data/no_user.txt"));
        user_no_pw.println(supporters.get("no"));
        user_no_pw.close();
        
        System.out.println("YES supportes:      " + supporters.get("yes").size() );
        System.out.println("NO supportes:      " + supporters.get("no").size() );
    }

} 
            
    
       

