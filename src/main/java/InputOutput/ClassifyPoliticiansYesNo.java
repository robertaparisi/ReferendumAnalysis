/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InputOutput;

import static InputOutput.TweetIndex.sourcenames_directory;
import static InputOutput.TweetIndex.tweets_index_directory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Roberta
 */
public class ClassifyPoliticiansYesNo {
    
    
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
        yes_no_hashmap.put("yes", yes_politicians);
        yes_no_hashmap.put("no", no_politicians);
        return (ans);
    }
    
    
    public static void main (String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
      
        FileInputStream fstream = new FileInputStream(sourcenames_directory + "politicians.txt");
        InputStreamReader file_reader = new InputStreamReader(fstream, "UTF-8");
        BufferedReader br = new BufferedReader(file_reader); 
        String[] politicians = br.readLine().split(",");
        Directory tweets_idx_dir = new SimpleFSDirectory(new File(tweets_index_directory));
        classifyPoliticians(tweets_idx_dir , politicians);
               
        
    }
    
}
