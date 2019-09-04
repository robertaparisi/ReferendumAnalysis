/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.floor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Roberta
 */
public class KMeans {
    
    private final HashMap<String, Integer> termSAXclusterid; // Sax String and its cluster id
    private final int k;
    private final Object[] terms_list;
    private final Map<String, String> termsSAXstring;
    private final Object [] saxStrings;
    private final int saxStringsSize;
    private final Random rand = new Random(25023);



    public KMeans(int k, Map<String, String> termsSAXstring) {
        this.termSAXclusterid = new HashMap<>();
        this.k = k;
        this.termsSAXstring = termsSAXstring;
        this.terms_list =  termsSAXstring.keySet().toArray() ;
        this.saxStrings = termsSAXstring.values().toArray();        
        this.saxStringsSize = termsSAXstring.get(termsSAXstring.keySet().stream().findFirst().get()).length();
    }

    private String [] getKRandomCentroids() {
        String[] centroids = new String[this.k];
        for (int i = 0; i < this.k; i++) {
            centroids[i] = this.saxStrings[rand.nextInt(this.saxStrings.length)].toString();
        }
        return centroids;
    }   
    
    /*
    if we do not have a centroid yet, we are going to create it or, better said, we are going to take one random Sax String
    from the list that contain all the possible sax string and use it as random
    */
    private String getRandomCentroid() {
        String centroid  = this.saxStrings[rand.nextInt(this.saxStrings.length)].toString();
        return centroid;
    }   
    
    /*
    Here we are going to create the centroid, assing a random one if it's empty, otherwise we are going
    to compute the average character for each character position, considering all the element in the cluster list
    */
     
    private String getClusterCentroid(LinkedHashSet<String> cluster) {        
        ArrayList<String> clusterList = new ArrayList<>(cluster); 
        String centroid = "";         
        if (cluster.isEmpty()) {
            centroid = getRandomCentroid();
        }
        else{
            for (int i = 0; i < this.saxStringsSize; i++) {
                int chars = 0;
                for (String element : clusterList) {
                    chars += (int) element.charAt(i);
                }
                centroid += (char) (int)(chars/cluster.size());
            } 
        }   
        return centroid;
    }
    
    

    /*
    Computing the centroids for all the k clusters
    */
    private String[] computeCentroids(ArrayList<LinkedHashSet<String>> clusters) {
        String[] centroids = new String[this.k];
        for (int i = 0; i < this.k; i++) {
            centroids[i] = getClusterCentroid(clusters.get(i));
        }
        return centroids;
    }
    
    

    /*
    We need to check if the sax string is in the right cluster, so we check at the entire clusters list, 
    if the Sax String is not assigned to the same  cluster we remove it from the old cluster and put it into the new
    cluster, updating also the termSAXclusterid, so that the list sax string, cluster id is also updated.
    
    Why  I used a LinkedHashSet???
    
    A LinkedHashSet is an ordered version of HashSet that maintains a doubly-linked List across all elements. 
    When the iteration order is needed to be maintained this class is used. 
     */
    private void updateCluster(ArrayList<LinkedHashSet<String>> clusters, String saxString, int newCluster) {
        if (this.termSAXclusterid.containsKey(saxString)) {
            int oldCluster = this.termSAXclusterid.get(saxString);
            if (oldCluster != newCluster) {
                LinkedHashSet<String> oldClusterUpdated = clusters.get(oldCluster);
                oldClusterUpdated.remove(saxString);                
            }
        }
        LinkedHashSet<String> newClusterUpdated = clusters.get(newCluster);  
        newClusterUpdated.add(saxString);
        this.termSAXclusterid.put(saxString, newCluster); 
    }
   
      
    public static Map<Integer, ArrayList<String>> getClusterMap(HashMap<String, Integer> termSAXclusterid, Map<String, String> termSAXstrings) {
        Map<Integer, ArrayList<String>> cluster_term = new HashMap<>();
        String[] sax_strings = termSAXclusterid.keySet().toArray(new String[termSAXclusterid.size()]);
        String[] terms = termSAXstrings.keySet().toArray(new String[termSAXstrings.size()]);
        for (int i = 0; i < termSAXclusterid.size(); i++) {
            int cluster =  termSAXclusterid.get(sax_strings[i]);
            ArrayList<String> t = new ArrayList<>();
            t.add(terms[i]);
            if (cluster_term.containsKey(cluster)) {
                t.addAll(cluster_term.get(cluster));
            }
            cluster_term.put(cluster, t);
        }
        return (cluster_term);
    }
    
//    public Map<Integer, ArrayList<String>> bestKMeans() {
//        
//        int num_iter = 25;
//        Map<Integer, ArrayList<String>> cluster_term = new HashMap<>();
//        List<Double> error = new ArrayList<>();
//        ArrayList<ArrayList<Double>> centroids = new ArrayList<>();
//
//        double distOld = Integer.MAX_VALUE;
//        double distNew;
//        for(int iter=0; iter< num_iter; iter++) {
//            Map<Integer, ArrayList<String>> clusters = computeKMeans();
//            distNew = clusterDist;
//            if(distOld>distNew) {
//                System.out.println("best score:"+distNew);
//                fclusters = clusters;
//                distOld = distNew;
//                centroids = clusterCentroids;
//            }
//        }
//        scores = cScores;
//        clusterCentroids = centroids;
//    return(fclusters);
//}
  
    /*
    this function that return an hashmap that contains for each SAX string the id of the cluster to whom 
    it belong to. The way in which this function work is storing the error and updating it after each iteration, 
    adding the new error that results from the new assignment of one sax string to a cluster. In particular, the amount that we will
    add is the minimum distance between the clusters and the sax string. 
     */
    public HashMap<String, Integer> computeKMeans() throws Exception {
        int list_size = saxStrings.length;
        int old_error = 0;
        int new_error = 0; 
        String[] centroids;
        // clusters is an auxiliary structure that allows indexed access
        // instantiate the empty clusters (sets) inside the list of clusters
        ArrayList<LinkedHashSet<String>> clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            clusters.add(new LinkedHashSet<>());
        }
        centroids = getKRandomCentroids();
        System.out.println("Centroidi STEP 0: "+ Arrays.toString(centroids));
        
        do {
            old_error = new_error;
            new_error = 0;
            for (int i = 0; i < list_size; i++) {
                int min_distance = Integer.MAX_VALUE;
                String sax_string = (String) this.saxStrings[i];
                int distance;
                int new_cluster = 0;
                for (int num_cluster = 0; num_cluster < k; num_cluster++) {
                    distance = StringUtils.getLevenshteinDistance(sax_string,  centroids[num_cluster]);
//                    System.out.println(sax_string + " distanza di: " +distance + " per il cluster "+ num_cluster + "che e' = "+ centroids[num_cluster]);
                    if (distance < min_distance) {
                        min_distance = distance;
                        new_cluster = num_cluster;
                    }
                }
//                System.out.println("==============================");
                new_error += min_distance; 
                updateCluster(clusters, sax_string, new_cluster);
            }
            centroids = computeCentroids(clusters); // calculate the centroids for the next iteration
        } while (old_error != new_error);
        System.out.println("error: "+ new_error);
        return termSAXclusterid;
    }
    
    
    public static void saveCluster(HashMap<String, Integer> sax_string_cluster, Map<String, String> termSAXstrings,String group_cluster_filename,  String term_cluster_filename) throws IOException {
        PrintWriter tc_print = new PrintWriter(new FileWriter(term_cluster_filename));
        PrintWriter gc_print = new PrintWriter(new FileWriter(group_cluster_filename));
        String[] term = termSAXstrings.keySet().toArray(new String[termSAXstrings.size()]);
        String[] sax_string = sax_string_cluster.keySet().toArray(new String[sax_string_cluster.size()]);
        Map<Integer, ArrayList<String>> cluster_group = getClusterMap(sax_string_cluster, termSAXstrings);
        String[] cluster_group_key = cluster_group.keySet().toArray(new String[cluster_group.size()]);
        for (int i = 0; i < sax_string_cluster.size() ; i++) {
            tc_print.println( term[i] + " "+ sax_string[i] + " " + sax_string_cluster.get(sax_string[i]));            
        }
        for (int i = 0; i<= cluster_group.size(); i++){
            gc_print.println(cluster_group_key[i] + " " + cluster_group.get((int)cluster_group_key[i].chars());
        }
        cluster_group_key[i].chars()

        pw.close();
    } 
 
   
}
