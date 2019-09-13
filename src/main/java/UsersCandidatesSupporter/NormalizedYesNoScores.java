/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UsersCandidatesSupporter;

import static indexing.ClassifyPoliticiansYesNo.no_tags;
import static indexing.ClassifyPoliticiansYesNo.yes_tags;
import indexing.TweetIndex;
import static indexing.TweetIndex.output_data_directory;
import static indexing.TweetIndex.sourcenames_directory;
import it.stilo.g.structures.DoubleValues;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

/**
 *
 * @author Roberta
 */
public class NormalizedYesNoScores {
    
    public static int computeYesPoints(int num_yes_tag, int num_yes_mention, int num_no_tag, int num_no_mention){
        int score = 0;
        if (num_no_mention==0 | num_no_tag==0){
            score = score+15;
        }
        if (num_yes_mention> num_no_mention*1.35 & num_yes_tag > num_no_tag*1.35 ){
            score = score +10;
        }
        score = score+ num_yes_tag + num_yes_mention;
        return(score);
        
    }
    
    public static int computeNoPoints(int num_yes_tag, int num_yes_mention, int num_no_tag, int num_no_mention){
        int score = 0;
        if (num_yes_mention==0 | num_yes_tag==0){
            score = score+15;
        }
        if (num_no_mention> num_yes_mention*1.35 & num_no_tag > num_yes_tag*1.35){
            score = score +10;
        }
        score = score+ num_no_tag + num_no_mention;
        return(score);
        
    }
    
    public static int getUserPoints(String user, List<String> yes_list, int num_yes_tag, int num_yes_mention, int num_no_tag, int num_no_mention){
        int score = 0;
        if (yes_list.contains(user)){
            score = computeYesPoints(num_yes_tag, num_yes_mention, num_no_tag, num_no_mention);
        } else{
            score = computeNoPoints(num_yes_tag, num_yes_mention, num_no_tag, num_no_mention);
        }
        return(score);
    }
    public static int getMaxScore(HashMap<String, Integer> scores){
        int max =0;
        int score;
        for (String user: scores.keySet()){
            score = scores.get(user);
            if (max < score){
                max = score;
            }
                        
        }
        return (max);
    }
    
    public static String[] getUserNameScreenname(String user_id, IndexSearcher searcher) throws IOException{
        BytesRef ref = new BytesRef();
        NumericUtils.longToPrefixCoded(Long.parseLong(user_id), 0, ref);
        TermQuery q = new TermQuery(new Term("id", ref));
        TopDocs top = searcher.search(q, 1);
        ScoreDoc[] hits = top.scoreDocs;
        Document doc = searcher.doc(hits[0].doc);
        String name = doc.get("name");
        String screenname = doc.get("screenname");
        String[] results = {name, screenname};
        return(results);
        
    }
    public static HashMap<String, Double> saveNormalizedScores(HashMap<String, Integer> scores, int max_score, String filename, IndexSearcher searcher ) throws IOException{
        HashMap<String, Double> new_scores = new HashMap<>();
        PrintWriter pw = new PrintWriter(filename);
        String[] user_data = new String[2];
        for(String user: scores.keySet()){
            user_data = getUserNameScreenname(user, searcher);
            double normalized_score = (double)scores.get(user)/max_score;
            new_scores.put(user, normalized_score);
            pw.println(user_data[0]+" "+ user_data[1]+" "+ user+ " "+ normalized_score);
        }
        pw.close();
        return(new_scores);
    }    
    public static HashMap<String, HashMap<String, Double>> computeUsersScores( List<String> yes_list, ArrayList<DoubleValues> authorities ) throws IOException{
         
        IndexReader index_reader = DirectoryReader.open(FSDirectory.open(new File(TweetIndex.tweets_index_directory)));
        IndexSearcher searcher = new IndexSearcher(index_reader); 
        
        String filename_yes = sourcenames_directory +"yes_politicians.txt";
        BufferedReader br_yes = new BufferedReader(new FileReader(filename_yes)); 
        String[] yes_politicians = br_yes.readLine().split(",");
        
        String filename_no = sourcenames_directory + "no_politicians.txt";
        BufferedReader br_no = new BufferedReader(new FileReader(filename_no)); 
        String[] no_politicians = br_no.readLine().split(",");  
        
        String authority;
        TotalHitCountCollector collector; 
        HashMap<String, Integer> scores_yes = new HashMap<>();
        HashMap<String, Integer> scores_no = new HashMap<>();
        
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
     
        
        for(int i =0; i<authorities.size(); i++){
            authority = GraphAnalysisUsers.mapper.getNode(authorities.get(i).index);
            BytesRef ref = new BytesRef();
            NumericUtils.longToPrefixCoded(Long.parseLong(authority), 0, ref);
            TermQuery q = new TermQuery(new Term("id", ref));
            
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
            
            
            if (yes_list.contains(authority)){
                int score = computeYesPoints(no_tag_count, no_pol_mention_count, no_tag_count, no_pol_mention_count);
                scores_yes.put(authority,score) ;
            } else{
                int score = computeNoPoints(no_tag_count, no_pol_mention_count, no_tag_count, no_pol_mention_count);
                scores_yes.put(authority, score);
            }         
        }
        int max_yes = getMaxScore(scores_yes);
        int max_no = getMaxScore(scores_no);
        String fileoutput_no = output_data_directory + "normalizedScoresNo.txt";
        
        String fileoutput_yes = output_data_directory + "normalizedScoresYes.txt";
        
        HashMap<String, Double> new_scores_no = saveNormalizedScores(scores_no, max_no, fileoutput_no, searcher);
        HashMap<String, Double> new_scores_yes = saveNormalizedScores(scores_yes, max_yes, fileoutput_yes, searcher);
        HashMap<String, HashMap<String, Double>> collect_scores = new HashMap<>();
        collect_scores.put("no", new_scores_no);
        collect_scores.put("yes", new_scores_yes);
        return(collect_scores);
    }
    
    

}
