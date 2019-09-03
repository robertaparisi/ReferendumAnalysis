/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analysis;


import it.stilo.g.algo.CoreDecomposition;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.util.Iterator;
import java.util.Set;
import it.stilo.g.algo.SubGraph;
import it.stilo.g.algo.UnionDisjoint;
import it.stilo.g.structures.Core;
import it.stilo.g.structures.WeightedUndirectedGraph;
import java.util.HashSet;
import static org.apache.commons.math3.stat.inference.TestUtils.g;
/**
 *
 * @author Roberta
 */
public class GraphAnalysis {
    public static int runner = (int) (Runtime.getRuntime().availableProcessors());
    
    public static WeightedUndirectedGraph getLargestCC(WeightedUndirectedGraph graph) throws InterruptedException {
        int[] nodes_list = new int[graph.size];
        for (int i = 0; i < graph.size; i++) {
            nodes_list[i] = i;
        }
        Set<Set<Integer>> components = it.stilo.g.algo.ConnectedComponents.rootedConnectedComponents(graph, nodes_list, runner);       
        Set<Integer> largest_component = new HashSet<>();
        int m = 0;
        for (Set<Integer> cc : components) {
            if (cc.size() > m) {
                largest_component = cc;
                m = cc.size();
            }
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
    
    

}
    
    
