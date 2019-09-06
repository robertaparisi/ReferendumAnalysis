package indexing;


import static indexing.ClassifyPoliticiansYesNo.classifyPoliticians;
import static indexing.ClassifyPoliticiansYesNo.createIndexYesNoPolitician;
import static indexing.TweetIndex.createIndexAllTweets;
import static indexing.TweetIndex.output_data_directory;
import static indexing.TweetIndex.sourcenames_directory;
import static indexing.TweetIndex.tweets_index_directory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import twitter4j.JSONException;
import twitter4j.TwitterException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Roberta
 */
public class MainIndex {    
    
       public static void run_main_index(boolean general_index, boolean yesno_index) throws IOException, ParseException, JSONException, TwitterException, org.apache.lucene.queryparser.classic.ParseException{
            if (general_index){
                createIndexAllTweets();

            }
            if (yesno_index){
                FileInputStream fstream = new FileInputStream(sourcenames_directory + "politicians.txt");
                InputStreamReader file_reader = new InputStreamReader(fstream, "UTF-8");
                BufferedReader br = new BufferedReader(file_reader); 
                String[] politicians = br.readLine().split(",");

                Directory tweets_idx_dir = new SimpleFSDirectory(new File(tweets_index_directory));
                Map<String, List<String>> yes_no_hashmap = classifyPoliticians(tweets_idx_dir , politicians);


                System.out.println("Total numbers of politicians: "+ politicians.length + " which "+yes_no_hashmap.get("yes").size()+
                        " support the referendum (voting YES), other "+ yes_no_hashmap.get("no").size()+" will not support the referendum(voting NO)");

                String file_yes = "yes_politicians_index";
                String file_no = "no_politicians_index";
                int top_n_results = 100000000;

                System.out.println("Creating the index for the Yes side");
                createIndexYesNoPolitician(tweets_idx_dir, yes_no_hashmap.get("yes"), top_n_results, output_data_directory + file_yes);

                System.out.println("Creating the index for the No side");
                createIndexYesNoPolitician(tweets_idx_dir, yes_no_hashmap.get("no"), top_n_results, output_data_directory + file_no);      

            }

    }
    
}
