/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;
import indexing.TweetIndex;
import static indexing.TweetIndex.output_data_directory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Roberta
 */
public class SAXanalysis {


     public static void main(String[] args) throws IOException, Exception{

        //is the number of letter to consider, basically if it's a is under the median, otherwise it's more
        int alphabet_size = 2; 
        //for being considered as sax, they need to have 2 peaks, so that's the distribution that they need to match
        String regex_match = "a*b+a*b*a*";
        double threshold = 0.5;
        long grain = TimeUnit.HOURS.toMillis(12);
        long min_term_frequency = 4; 
        String no_politicians_dir = TweetIndex.output_data_directory + "no_politicians_index";
        String yes_politicians_dir = TweetIndex.output_data_directory + "yes_politicians_index";
        
        System.out.println("Computing sax for the no side... ");
        System.out.println("==========================================");
        System.out.println("==========================================");
        Map<String, String> sax_no = TemporalAnalysis.compute_sax_analysis(no_politicians_dir, alphabet_size, regex_match,  threshold, grain, min_term_frequency);
        System.out.println("SIZE: " + sax_no.size());
        System.out.println(sax_no.keySet());
        System.out.println("==========================================");
        System.out.println("==========================================");
        System.out.println("Computing sax for the yes side... ");
        Map<String, String> sax_yes = TemporalAnalysis.compute_sax_analysis(yes_politicians_dir, alphabet_size, regex_match, threshold, grain, min_term_frequency);
        System.out.println("SIZE: " + sax_yes.size());
        System.out.println(sax_yes.keySet());
        System.out.println("==========================================");
        System.out.println("==========================================");


        int number_yes_cluster = 10; // n. of yes clusters
        int number_no_cluster = 10; // n. of no clusters
        
        KMeans yes_kmeans = new KMeans(number_yes_cluster,  sax_yes);
        HashMap<String, Integer> yes_kmeans_cluster = yes_kmeans.computeKMeans();
        KMeans.saveCluster(yes_kmeans_cluster, sax_yes, output_data_directory + "cluster/yesClusters.txt");
        
        KMeans no_kmeans = new KMeans(number_no_cluster,  sax_no);
        HashMap<String, Integer> no_kmeans_cluster = no_kmeans.computeKMeans(); 
        KMeans.saveCluster(no_kmeans_cluster,  sax_no, output_data_directory + "cluster/noClusters.txt");

        

     }
}
//
//        int number_yes_cluster = 10; // n. of yes clusters
//        int number_no_cluster = 10; // n. of no clusters
//        int random_initizializzation = 100; // n. kmeans randomizations
//
//
//    //    System.out.println("SIZE: " + saxYES.size());
//    //    System.out.println(saxYES.keySet());
//    //    System.out.println("SIZE: " + saxNO.size());
//    //    System.out.println(saxNO.keySet());
//
//        //DefaultCategoryDataset datasetYES = new DefaultCategoryDataset();
//        //DefaultCategoryDataset datasetNO = new DefaultCategoryDataset();
//        Writer w;
//
//        // YES
//        for (String term : sax_yes.keySet()) {
//            //datasetYES = te.createDataset(datasetYES, dates, saxYES.get(term), term);
//            w = new Writer(testsYES + "/" + term + ".txt");
//            w.add(taYES.queriesTweets(term, "text", false));
//            w.close();
//        }
//
//        //Plot p_yes = new Plot("YESterms", "Number of tweets over time", datasetYES, results + "/YES-terms.png", 20);
//        //p_yes.pack();
//        //p_yes.setLocation(800, 20);
//        //p_yes.setVisible(true);
//
//        // NO
//        for (String term : saxNO.keySet()) {
//            //datasetNO = taNO.createDataset(datasetNO, dates, saxNO.get(term), term);
//            w = new Writer(testsNO + "/" + term + ".txt");
//            w.add(taNO.queriesTweets(term, "text", false));
//            w.close();
//        }
//    }
//}

   
     

