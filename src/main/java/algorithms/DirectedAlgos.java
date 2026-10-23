package algorithms;

import graph.Graph;
import graph.Graph.Edge;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class DirectedAlgos {

    public static class SCCResult {
        public int components = 0;
        public long edgeScans = 0;
        public int maxDepth = 0;
    }

    public static class TopoResult {
        public List<Integer> order = new ArrayList<>();
        public boolean hasCycle = false;
        public long edgeScans = 0;
    }

    public static SCCResult kosaraju(Graph g) {
        SCCResult res = new SCCResult();
        int n = g.n;
        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>(n);

        int[] edgeIndex = new int[n];
        int[] stack = new int[n];
        int top = -1;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                stack[++top] = i;
                visited[i] = true;
                res.maxDepth = Math.max(res.maxDepth, top + 1);

                while (top >= 0) {
                    int u = stack[top];
                    List<Edge> edges = g.adj.get(u);
                    if (edgeIndex[u] < edges.size()) {
                        res.edgeScans++;
                        int v = edges.get(edgeIndex[u]++).to;
                        if (!visited[v]) {
                            visited[v] = true;
                            stack[++top] = v;
                            res.maxDepth = Math.max(res.maxDepth, top + 1);
                        }
                    } else {
                        finishOrder.add(u);
                        top--;
                    }
                }
            }
        }

        Graph revG = new Graph(n, true);
        for (int u = 0; u < n; u++) {
            for (Edge e : g.adj.get(u)) {
                revG.addEdge(e.to, u, e.weight);
            }
        }

        Arrays.fill(visited, false);
        Arrays.fill(edgeIndex, 0);

        for (int i = n - 1; i >= 0; i--) {
            int startNode = finishOrder.get(i);
            if (!visited[startNode]) {
                res.components++;

                stack[++top] = startNode;
                visited[startNode] = true;
                res.maxDepth = Math.max(res.maxDepth, top + 1);

                while (top >= 0) {
                    int u = stack[top];
                    List<Edge> edges = revG.adj.get(u);
                    if (edgeIndex[u] < edges.size()) {
                        res.edgeScans++;
                        int v = edges.get(edgeIndex[u]++).to;
                        if (!visited[v]) {
                            visited[v] = true;
                            stack[++top] = v;
                            res.maxDepth = Math.max(res.maxDepth, top + 1);
                        }
                    } else {
                        top--;
                    }
                }
            }
        }
        return res;
    }

    public static TopoResult kahn(Graph g) {
        TopoResult res = new TopoResult();
        int n = g.n;
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Edge e : g.adj.get(u)) {
                inDegree[e.to]++;
            }
        }

        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) q.add(i);
        }

        while (!q.isEmpty()) {
            int u = q.poll();
            res.order.add(u);

            for (Edge e : g.adj.get(u)) {
                res.edgeScans++;
                if (--inDegree[e.to] == 0) {
                    q.add(e.to);
                }
            }
        }

        if (res.order.size() < n) {
            res.hasCycle = true;
        }
        return res;
    }
}