package algorithms;

import graph.Graph;
import graph.Graph.Edge;
import java.util.Arrays;
import java.util.List;

public class DAGPaths {

    public static class DAGResult {
        public long sumDistances = 0;
        public long edgeScans = 0;
        public int[] parent;
        public long[] dist;
    }

    public static DAGResult shortestPaths(Graph g, List<Integer> topoOrder) {
        DAGResult res = new DAGResult();
        int n = g.n;
        res.dist = new long[n];
        res.parent = new int[n];

        Arrays.fill(res.dist, Long.MAX_VALUE / 2);
        Arrays.fill(res.parent, -1);

        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Edge e : g.adj.get(u)) {
                inDegree[e.to]++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                res.dist[i] = 0;
            }
        }

        for (int u : topoOrder) {
            if (res.dist[u] == Long.MAX_VALUE / 2) continue;

            for (Edge e : g.adj.get(u)) {
                res.edgeScans++;
                if (res.dist[u] + e.weight < res.dist[e.to]) {
                    res.dist[e.to] = res.dist[u] + e.weight;
                    res.parent[e.to] = u;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            if (res.dist[i] != Long.MAX_VALUE / 2) {
                res.sumDistances += res.dist[i];
            }
        }

        return res;
    }
}