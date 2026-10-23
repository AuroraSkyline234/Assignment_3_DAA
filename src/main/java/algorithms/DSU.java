package algorithms;

public class DSU {
    private int[] parent;
    private int[] rank;
    private int componentsCount;

    public long findOps = 0;
    public long unionOps = 0;

    public DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        componentsCount = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int i) {
        findOps++;
        if (parent[i] == i) {
            return i;
        }
        return parent[i] = find(parent[i]);
    }

    public boolean union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);

        if (rootI != rootJ) {
            unionOps++;

            if (rank[rootI] < rank[rootJ]) {
                parent[rootI] = rootJ;
            } else if (rank[rootI] > rank[rootJ]) {
                parent[rootJ] = rootI;
            } else {
                parent[rootJ] = rootI;
                rank[rootI]++;
            }
            componentsCount--;
            return true;
        }
        return false;
    }

    public int components() {
        return componentsCount;
    }
}