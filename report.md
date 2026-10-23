# Assignment 3 - Graphs Report

## Complexity Table
| Algorithm | Time Complexity | Extra Space | Justification |
| :--- | :--- | :--- | :--- |
| **DSU** | $O(\alpha(n))$ | $O(V)$ | Path compression and union by rank reduce tree height to almost constant $\alpha(n)$. Arrays `parent` and `rank` take $O(V)$. |
| **Kruskal** | $O(E \log E)$ | $O(V + E)$ | Sorting edges takes $O(E \log E)$. DSU operations take $O(E \alpha(V))$. Total time is dominated by sorting. |
| **Prim** | $O(E \log V)$ | $O(V)$ | PriorityQueue operations take $O(\log V)$ per edge relaxation. Space is for the PQ and `visited` array. |
| **Kosaraju** | $O(V + E)$ | $O(V)$ | Two DFS passes. Each vertex and edge is visited a constant number of times. Iterative stack and `visited` take $O(V)$. |
| **Kahn** | $O(V + E)$ | $O(V)$ | Computes in-degrees and processes a queue. Each edge is removed exactly once. |
| **DAG Paths** | $O(V + E)$ | $O(V)$ | Single pass over vertices in topological order, relaxing each outgoing edge exactly once. |

## Counters vs Formulas
* **Kosaraju:** The ratio `edgeScans / (2 * m)` equals exactly 1.0 across all graph sizes, confirming $O(V+E)$ work because each edge is scanned twice (once in the forward graph, once in the reversed).
* **Kahn & DAG Paths:** The ratio `edgeScans / m` is exactly 1.0, proving that every edge is relaxed/scanned exactly once.
* **Kruskal:** The ratio `dsuOps / m` decreases slightly as density increases, confirming that the algorithm efficiently skips cycles and doesn't process every edge heavily. The main cost is edge sorting.

## Correctness Arguments
**Cut Property (MST):**
The cut property states that for any cut of a graph, the lightest edge crossing the cut must belong to the Minimum Spanning Tree. Kruskal uses this by sorting all edges and adding the lightest safe edge (where the cut separates two different DSU components). Prim uses this by growing a single cut around the visited vertices and greedily picking the lightest outgoing edge from the Priority Queue. Both approaches guarantee a globally minimum weight.

**Induction on Topological Order (DAG Paths):**
In a Directed Acyclic Graph, all dependencies flow strictly forward. If vertices are processed in topological order, by the time we reach vertex $v$, all possible paths leading to $v$ have already been fully relaxed. Therefore, the distance to $v$ is finalized. A single pass is sufficient, and negative weights are perfectly safe because a DAG cannot contain negative cycles (or any cycles at all), making infinite distance reduction impossible.

## Which Algorithm When
| Scenario | Recommended Algorithm | Reason |
| :--- | :--- | :--- |
| **Dense Graph MST** | Prim | $O(E \log V)$ performs better than Kruskal's $O(E \log E)$ when $E$ is close to $V^2$. |
| **Sparse Graph MST** | Kruskal | Very fast on edge lists. $O(E \log E)$ is efficient for small $E$. |
| **Cycle Detection** | Kahn (Topological Sort) | Early termination or missing vertices directly indicate the presence of a cycle. |
| **Shortest Paths (DAG)** | Relaxation in Topo Order | $O(V+E)$ is strictly faster than Dijkstra $O(E \log V)$ and supports negative weights. |

## Slope Check
On log-log axes, the slope of `edgeScans` vs `n` for Kosaraju and Kahn is exactly 1.00, confirming linear work. Kruskal is slightly steeper due to the $E \log E$ sorting factor. 

## Time Measurements
The `time_ms` measurements show general expected trends (dense graphs take longer), but small graphs (e.g., n=1000) have median times near 0-5 ms, mostly representing JVM warmup noise. Operation counters (like `edgeScans`) are hardware-independent and provide the definitive proof of complexity.