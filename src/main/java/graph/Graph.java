package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Graph {

    public static class Edge {
        public int to;
        public int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public int n;
    public int m;
    public boolean isDirected;
    public List<List<Edge>> adj;

    public Graph(int n, boolean isDirected) {
        this.n = n;
        this.isDirected = isDirected;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int weight) {
        adj.get(u).add(new Edge(v, weight));
        if (!isDirected && u != v) {
            adj.get(v).add(new Edge(u, weight));
        }
    }

    public static Graph fromFile(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));
        if (!scanner.hasNext()) return null;

        int n = scanner.nextInt();
        int m = scanner.nextInt();
        String type = scanner.next();
        boolean isDirected = type.equals("directed");

        Graph graph = new Graph(n, isDirected);
        graph.m = m;

        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            int w = scanner.nextInt();
            graph.addEdge(u, v, w);
        }

        scanner.close();
    }
}