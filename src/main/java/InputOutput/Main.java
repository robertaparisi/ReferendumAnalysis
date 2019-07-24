package InputOutput;


import static InputOutput.GetPoliticiansFile.decompress;
import static InputOutput.TweetIndex.createIndexAllTweets;
import java.io.IOException;
import java.text.ParseException;
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
public class Main {    
    
       public static void main(String[] args) throws IOException, ParseException, JSONException, TwitterException{
//        System.out.println("here1");
//        String results = "indices";
//        new File(results).mkdir();
//        System.out.println("here");

        createIndexAllTweets();

    }
    
}
