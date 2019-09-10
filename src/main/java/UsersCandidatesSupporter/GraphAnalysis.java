/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UsersCandidatesSupporter;

import static analysis.GraphAnalysis.runner;
import static com.google.common.collect.Iterables.all;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import it.stilo.g.algo.ConnectedComponents;
import it.stilo.g.algo.HubnessAuthority;
import it.stilo.g.algo.SubGraph;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Roberta
 */

/*
Using the provided Graph and the library G (see slides to obtain it) first select the subgraph
induced by users S(M) then find the largest connected component CC and compute HITS on this
subgraph. Then, find the 1000 highest ranked (Authority) users. Who are they? Can be divided in
YES and NO supporters? Propose some metrics.
*/
public class GraphAnalysis {
    private static Object max_set;
    
    public static void largestCC(){
        
    }
        private static WeightedUndirectedGraph getLargestCC(WeightedUndirectedGraph graph) throws InterruptedException {
        // this get the largest component of the graph and returns a graph too
        //System.out.println(Arrays.deepToString(g.weights));
        int[] nodes_list = new int[graph.size];
        for (int i = 0; i < graph.size; i++) {
             nodes_list[i] = i;
        }

        Set<Set<Integer>> connected_components = ConnectedComponents.rootedConnectedComponents(graph, nodes_list, runner);

         
        int largest_component_size = 0;
        Set<Integer> largest_component = null;
        // get largest component
        for (Set<Integer> component : connected_components) {
            if (component.size() > largest_component_size) {
                largest_component = component;
                largest_component_size = component.size();
            }
        }

        int[] subnodes = new int[largest_component.size()];
        Iterator<Integer> iterator = largest_component.iterator();
        for (int j = 0; j < subnodes.length; j++) {
            subnodes[j] = iterator.next();
        }

        WeightedUndirectedGraph s = SubGraph.extract(graph, subnodes, runner);
        return s;
    }
    
    public static void getGraphAnalysis() throws FileNotFoundException, IOException{
        
        
        String yes_filename = "src/main/resources/data/yes_user.txt";
        String no_filename = "src/main/resources/data/no_user.txt";
        
        

        BufferedReader br;
        br = new BufferedReader(new FileReader(yes_filename)); 
        String users_yes = br.readLine();//.split(",");
        String[] users_list_yes = users_yes.substring(1, users_yes.length()-1).split(", ");
        
        br = new BufferedReader(new FileReader(no_filename)); 
        String users_no = br.readLine();//.split(",");
        String[] users_list_no = users_no.substring(1, users_yes.length()-1).split(", ");
        

        
    }
    
}
