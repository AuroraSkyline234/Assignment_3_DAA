package algorithms;

import graph.Graph;
import graph.Graph.Edge;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MST {

    public static class MSTResult {
        public long totalWeight = 0;
        public int components = 0;
        public long edgeScans = 0;
        public long comparisons = 0;
        public long dsuOps = 0;
    }

    static class KruskalEdge {
        int u, v, weight;
        KruskalEdge(int u, int v, int w) {
            this.u = u; this.v = v; this.weight = w;
        }
    }

    public static MSTResult kruskal(Graph g) {
        MSTResult res = new MSTResult();
        List<KruskalEdge> edges = new ArrayList<>();

        for (int u = 0; u < g.n; u++) {
            for (Edge e : g.adj.get(u)) {
                if (u <= e.to) {
                    edges.add(new KruskalEdge(u, e.to, e.weight));
                }
            }
        }

        edges.sort((e1, e2) -> {
            res.comparisons++;
            return Integer.compare(e1.weight, e2.weight);
        });

        DSU dsu = new DSU(g.n);
        for (KruskalEdge edge : edges) {
            res.edgeScans++;
            if (dsu.find(edge.u) != dsu.find(edge.v)) {
                dsu.union(edge.u, edge.v);
                res.totalWeight += edge.weight;
            }
        }

        res.components = dsu.components();
        res.dsuOps = dsu.findOps + dsu.unionOps;
        return res;
    }

    static class PrimNode {
        int to, weight;
        PrimNode(int to, int weight) {
            this.to = to; this.weight = weight;
        }
    }

    public static MSTResult prim(Graph g) {
        MSTResult res = new MSTResult();
        boolean[] visited = new boolean[g.n];
        PriorityQueue<PrimNode> pq = new PriorityQueue<>((n1, n2) -> {
            res.comparisons++;
            return Integer.compare(n1.weight, n2.weight);
        });

        for (int start = 0; start < g.n; start++) {
            if (!visited[start]) {
                res.components++;
                pq.add(new PrimNode(start, 0));

                while (!pq.isEmpty()) {
                    PrimNode current = pq.poll();
                    int u = current.to;

                    if (visited[u]) continue;
                    visited[u] = true;
                    res.totalWeight += current.weight;

                    for (Edge e : g.adj.get(u)) {
                        res.edgeScans++;
                        if (!visited[e.to]) {
                            pq.add(new PrimNode(e.to, e.weight));
                        }
                    }
                }
            }
        }
        return res;
    }
}