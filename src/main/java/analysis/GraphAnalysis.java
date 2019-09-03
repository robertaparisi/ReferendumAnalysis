/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;


import com.google.common.util.concurrent.AtomicDouble;
import it.stilo.g.algo.CoreDecomposition;
import it.stilo.g.algo.GraphInfo;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.util.Iterator;
import java.util.Set;
import it.stilo.g.algo.SubGraph;
import it.stilo.g.algo.SubGraphByEdgesWeight;
import it.stilo.g.algo.UnionDisjoint;
import it.stilo.g.structures.Core;
import it.stilo.g.structures.WeightedUndirectedGraph;
import it.stilo.g.util.NodesMapper;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import static org.apache.commons.math3.stat.inference.TestUtils.g;
import org.apache.lucene.queryparser.classic.ParseException;
/**
 *
 * @author Roberta
 */
public class GraphAnalysis {
    public static int runner = (int) (Runtime.getRuntime().availableProcessors());
    
    public static WeightedUndirectedGraph getAndSaveCCandKcore(kmeans_cluster,  sax_no, WeightedUndirectedGraph graph, String cc_filename, String kcore_filename) throws InterruptedException, IOException {
        FileWriter cc_file = new FileWriter(cc_filename);
        PrintWriter cc_print = new PrintWriter(cc_file);
        FileWriter kcore_file = new FileWriter(kcore_filename);
        PrintWriter kcore_print = new PrintWriter(kcore_file);
        
        int[] nodes_list = new int[graph.size];
        for (int i = 0; i < graph.size; i++) {
            nodes_list[i] = i;
        }
        
        Set<Set<Integer>> connected_components = it.stilo.g.algo.ConnectedComponents.rootedConnectedComponents(graph, nodes_list, runner);       
        for (Set<Integer> component : connected_components) {  
                
            cc_print.println();
                WeightedUndirectedGraph graphComp = SubGraph.extract(graph, component.stream().mapToInt(i->i).toArray(), runner); 
                Core kcore = CoreDecomposition.getInnerMostCore(graphComp, 2);                  
                
                System.out.println("K CORE");
                System.out.println("k: " + c.minDegree); //il numero k e' il numero di degree minore in questa subgraph
                for (int node : c.seq) {
                     System.out.print(kmYES.get(clust).get(node) + " ");
                     w.add(kmYES.get(clust).get(node) + " "); //salvo i risultati sul file
                }
                 
                 w.add("\r\n");
                 System.out.println();
            
        }       
        int[] nodes_subgraph =  largest_component.stream().mapToInt(i->i).toArray();
        WeightedUndirectedGraph sub_graph = it.stilo.g.algo.SubGraph.extract(graph, nodes_subgraph, runner);
        return (sub_graph);
    }
    
    
    private static WeightedUndirectedGraph kCore(WeightedUndirectedGraph graph) throws InterruptedException {
        // calculates the kcore and returns a graph. Now its not working who knows why
        WeightedUndirectedGraph graph_copy = UnionDisjoint.copy(graph, runner);
        Core kcore = CoreDecomposition.getInnerMostCore(graph_copy, runner);
        System.out.println("Kcore - Min. degree : " + kcore.minDegree + " V : " + kcore.seq.length);
        System.out.println("Seq: " + kcore.seq);        
        WeightedUndirectedGraph sub_graph = SubGraph.extract(graph_copy, kcore.seq, runner);
        return (sub_graph);
    }
    
    public static void extractANDsaveCCandKCore (int k, ){
        
        
    }
    
    public static void extractKCoreAndConnectedComponent(double threshold) throws IOException, ParseException, Exception {

        // do the same analysis for the yes-group and no-group
        String[] prefixYesNo = {"yes", "no"};
        for (String prefix : prefixYesNo) {

            // Get the number of clusters
            int c = getNumberClusters(RESOURCES_LOCATION + prefix + "_graph.txt");

            // Get the number of nodes inside each cluster
            List<Integer> numberNodes = getNumberNodes(RESOURCES_LOCATION + prefix + "_graph.txt", c);

            PrintWriter pw_cc = new PrintWriter(new FileWriter(RESOURCES_LOCATION + prefix + "_largestcc.txt")); //open the file where the largest connected component will be written to
            PrintWriter pw_kcore = new PrintWriter(new FileWriter(RESOURCES_LOCATION + prefix + "_kcore.txt")); //open the file where the kcore will be written to

            // create the array of graphs
            WeightedUndirectedGraph[] gArray = new WeightedUndirectedGraph[c];
            for (int i = 0; i < c; i++) {
                System.out.println();
                System.out.println("Cluster " + i);

                gArray[i] = new WeightedUndirectedGraph(numberNodes.get(i) + 1);

                // Put the nodes,
                NodesMapper<String> mapper = new NodesMapper<String>();
                gArray[i] = addNodesGraph(gArray[i], i, RESOURCES_LOCATION + prefix + "_graph.txt", mapper);

                //normalize the weights
                gArray[i] = normalizeGraph(gArray[i]);

                AtomicDouble[] info = GraphInfo.getGraphInfo(gArray[i], 1);
                System.out.println("Nodes:" + info[0]);
                System.out.println("Edges:" + info[1]);
                System.out.println("Density:" + info[2]);

                // extract remove the edges with w<t
                gArray[i] = SubGraphByEdgesWeight.extract(gArray[i], threshold, 1);

                // get the largest CC and save to a file
                WeightedUndirectedGraph largestCC = getLargestCC(gArray[i]);
                saveGraphToFile(pw_cc, mapper, largestCC.in, i);

                // Get the inner core and save to a file
                WeightedUndirectedGraph kcore = kcore(gArray[i]);
                saveGraphToFile(pw_kcore, mapper, kcore.in, i);
            }

            pw_cc.close();
            pw_kcore.close();
        }
    }

}
    
    
