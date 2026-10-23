package algorithms;

import graph.Graph;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Benchmark {

    public static Graph generate(int n, int m, String type) {
        Graph g = new Graph(n, type.equals("directed") || type.equals("DAG"));
        Random rnd = new Random(42);

        int edgesAdded = 0;

        if (type.equals("undirected")) {
            int[] perm = new int[n];
            for (int i = 0; i < n; i++) perm[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int temp = perm[i]; perm[i] = perm[j]; perm[j] = temp;
            }
            for (int i = 1; i < n; i++) {
                g.addEdge(perm[rnd.nextInt(i)], perm[i], 1 + rnd.nextInt(1000));
                edgesAdded++;
            }
        }

        int[] rank = new int[n];
        if (type.equals("DAG")) {
            for (int i = 0; i < n; i++) rank[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int temp = rank[i]; rank[i] = rank[j]; rank[j] = temp;
            }
        }

        while (edgesAdded < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (a == b) {
                b = rnd.nextInt(n);
            }

            if (type.equals("DAG") && rank[a] > rank[b]) {
                int temp = a; a = b; b = temp;
            }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
            edgesAdded++;
        }
        g.m = m;
        return g;
    }

    public static void main(String[] args) throws IOException {
        int[] sizes = {1000, 3162, 10000, 31623, 100000};
        int[] densityMults = {4, 20};

        PrintWriter csv = new PrintWriter(new FileWriter("results.csv"));
        csv.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");

        for (int n : sizes) {
            for (int mult : densityMults) {
                int m = n * mult;

                // MST: Kruskal и Prim
                Graph undir = generate(n, m, "undirected");
                runBenchmark(csv, "Kruskal", "undirected", n, m, () -> {
                    MST.MSTResult res = MST.kruskal(undir);
                    return new long[]{res.dsuOps, res.edgeScans, res.comparisons, 0, res.totalWeight};
                });
                runBenchmark(csv, "Prim", "undirected", n, m, () -> {
                    MST.MSTResult res = MST.prim(undir);
                    return new long[]{0, res.edgeScans, res.comparisons, 0, res.totalWeight};
                });

                // SCC: Kosaraju
                Graph dir = generate(n, m, "directed");
                runBenchmark(csv, "Kosaraju", "directed", n, m, () -> {
                    DirectedAlgos.SCCResult res = DirectedAlgos.kosaraju(dir);
                    return new long[]{0, res.edgeScans, 0, res.maxDepth, res.components};
                });

                // Topo: Kahn и DAG Paths
                Graph dag = generate(n, m, "DAG");
                runBenchmark(csv, "Kahn", "DAG", n, m, () -> {
                    DirectedAlgos.TopoResult res = DirectedAlgos.kahn(dag);
                    return new long[]{0, res.edgeScans, 0, 0, res.order.size()};
                });

                DirectedAlgos.TopoResult topo = DirectedAlgos.kahn(dag);
                runBenchmark(csv, "DagShortestPath", "DAG", n, m, () -> {
                    DAGPaths.DAGResult res = DAGPaths.shortestPaths(dag, topo.order);
                    return new long[]{0, res.edgeScans, 0, 0, res.sumDistances};
                });
            }
        }
        csv.close();
        System.out.println("Benchmark finished. Check results.csv");
    }

    interface AlgoRunner {
        long[] run();
    }

    static void runBenchmark(PrintWriter csv, String algo, String graphType, int n, int m, AlgoRunner runner) {
        long[] times = new long[5];
        long[] metrics = null;

        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            metrics = runner.run();
            times[i] = System.currentTimeMillis() - start;
        }
        Arrays.sort(times);
        long medianTime = times[2];

        csv.printf("%s,%s,%d,%d,%d,%d,%d,%d,%d,%d\n",
                algo, graphType, n, m, medianTime,
                metrics[0], metrics[1], metrics[2], metrics[3], metrics[4]);
    }
}