package algorithms;

import graph.Graph;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTests {

    @Test
    void testMSTAgreement() {
        Random rnd = new Random(123);
        for (int k = 0; k < 100; k++) {
            int n = 50;
            Graph g = new Graph(n, false);
            for (int i = 1; i < n; i++) {
                g.addEdge(rnd.nextInt(i), i, rnd.nextInt(100) + 1);
            }
            for (int i = 0; i < 200; i++) {
                g.addEdge(rnd.nextInt(n), rnd.nextInt(n), rnd.nextInt(100) + 1);
            }
            assertEquals(MST.kruskal(g).totalWeight, MST.prim(g).totalWeight, "Kruskal and Prim weights must match");
        }
    }

    @Test
    void testStackOverflowSafeSCC() {
        int n = 100000;
        Graph g = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) {
            g.addEdge(i, i + 1, 1);
        }
        DirectedAlgos.SCCResult res = DirectedAlgos.kosaraju(g);
        assertEquals(n, res.components, "Path graph of 100_000 nodes should have 100_000 SCCs without throwing StackOverflowError");
    }

    @Test
    void testTopoSortAndCycle() {
        Graph gDAG = new Graph(4, true);
        gDAG.addEdge(0, 1, 1);
        gDAG.addEdge(1, 2, 1);
        gDAG.addEdge(0, 2, 1);
        gDAG.addEdge(2, 3, 1);
        DirectedAlgos.TopoResult res1 = DirectedAlgos.kahn(gDAG);
        assertFalse(res1.hasCycle, "DAG should not have a cycle");
        assertEquals(4, res1.order.size(), "DAG topo order should contain all vertices");

        Graph gCycle = new Graph(3, true);
        gCycle.addEdge(0, 1, 1);
        gCycle.addEdge(1, 2, 1);
        gCycle.addEdge(2, 0, 1);
        DirectedAlgos.TopoResult res2 = DirectedAlgos.kahn(gCycle);
        assertTrue(res2.hasCycle, "Cycle should be detected");
    }
}