package smarthospital.co5;

/**
 * CO5 - NP-Completeness and Approximation Algorithms Engine.
 * 
 * Implemented from scratch using primitive arrays and explicit loops without java.util.*:
 * 1. 2-Approximation for Metric Traveling Salesperson Problem (TSP)
 *    - Hospital Ambulance Emergency Dispatch & Clinical Supply Route Optimization
 *    - Prim's MST + Eulerian DFS shortcutting (Cost <= 2 * OPT)
 * 2. Greedy Approximation for Set Cover (H_n-Approximation)
 *    - Automated Hospital Emergency Equipment & Defibrillator Location Coverage
 * 3. 2-Approximation for Vertex Cover
 *    - Hospital Corridor & Wing Interconnection Surveillance Monitoring
 * 4. Exact Brute-Force TSP Solver for Small N (N <= 12) to empirically verify approximation ratio
 * 
 * Educational Notes:
 * - Metric TSP is NP-hard. When triangle inequality holds, the MST-doubling algorithm
 *   guarantees a solution at most twice the optimal tour cost in polynomial O(V^2) time.
 * - Set Cover is NP-hard; the greedy algorithm achieves an H_n = ln(n) + 1 approximation ratio.
 * - These models teach students how to address computationally intractable hospital operations.
 */
public final class ApproximationAlgorithms {

    private ApproximationAlgorithms() {
        // Prevent instantiation
    }

    // =========================================================================
    // RESULT STRUCTURES
    // =========================================================================

    /**
     * Encapsulates the result of an Ambulance TSP tour calculation.
     */
    public static class TspResult {
        public final int[] tour;
        public final int totalCost;
        public final int mstCost;
        public final boolean isMetric;

        public TspResult(int[] tour, int totalCost, int mstCost, boolean isMetric) {
            this.tour = tour;
            this.totalCost = totalCost;
            this.mstCost = mstCost;
            this.isMetric = isMetric;
        }
    }

    /**
     * Encapsulates the result of Greedy Set Cover.
     */
    public static class SetCoverResult {
        public final int[] selectedSets;
        public final int totalSelected;
        public final int coveredElementsCount;

        public SetCoverResult(int[] selectedSets, int totalSelected, int coveredElementsCount) {
            this.selectedSets = selectedSets;
            this.totalSelected = totalSelected;
            this.coveredElementsCount = coveredElementsCount;
        }
    }

    /**
     * Encapsulates the result of 2-Approximation Vertex Cover.
     */
    public static class VertexCoverResult {
        public final int[] coverVertices;
        public final int coverSize;

        public VertexCoverResult(int[] coverVertices, int coverSize) {
            this.coverVertices = coverVertices;
            this.coverSize = coverSize;
        }
    }

    // =========================================================================
    // 1. 2-APPROXIMATION FOR METRIC TSP (AMBULANCE DISPATCH)
    // =========================================================================

    /**
     * Computes a 2-approximation for Metric TSP on a complete distance matrix.
     * 
     * Core Idea:
     * 1. Constructs a Minimum Spanning Tree (MST) of all hospital depots using Prim's algorithm.
     * 2. The cost of this MST is strictly less than OPT (since removing any edge from OPT gives a spanning tree).
     * 3. Performs a Preorder Depth-First Search (DFS) walk of the MST.
     * 4. Shortcuts repeated vertices directly, which by Triangle Inequality does not increase cost.
     * 5. Returns to origin, guaranteeing: Total Tour Cost <= 2 * Weight(MST) < 2 * OPT.
     * 
     * Time Complexity:  O(V^2)
     * Space Complexity: O(V^2) for graph adjacency
     * 
     * @param vertices Total number of hospital depots/stops (V).
     * @param dist     Symmetric distance matrix [V][V] satisfying triangle inequality.
     * @return TspResult containing the tour sequence and total distance.
     */
    public static TspResult solveMetricTsp2Approx(int vertices, int[][] dist) {
        if (vertices <= 0 || dist == null) {
            return new TspResult(new int[0], 0, 0, false);
        }
        if (vertices == 1) {
            return new TspResult(new int[]{0, 0}, 0, 0, true);
        }

        // 1. Build Minimum Spanning Tree (MST) using Prim's algorithm
        int[] parent = new int[vertices];
        int[] key = new int[vertices];
        boolean[] inMst = new boolean[vertices];

        for (int i = 0; i < vertices; i++) {
            key[i] = Integer.MAX_VALUE;
            parent[i] = -1;
            inMst[i] = false;
        }

        key[0] = 0;
        int mstTotalWeight = 0;

        for (int count = 0; count < vertices; count++) {
            // Find vertex with minimum key value not yet included in MST
            int u = -1;
            int minKey = Integer.MAX_VALUE;
            for (int v = 0; v < vertices; v++) {
                if (!inMst[v] && key[v] < minKey) {
                    minKey = key[v];
                    u = v;
                }
            }

            if (u == -1) break;

            inMst[u] = true;
            mstTotalWeight += key[u];

            for (int v = 0; v < vertices; v++) {
                if (!inMst[v] && dist[u][v] > 0 && dist[u][v] < key[v]) {
                    key[v] = dist[u][v];
                    parent[v] = u;
                }
            }
        }

        // 2. Build MST Adjacency list as primitive 2D matrix
        int[][] mstAdj = new int[vertices][vertices];
        for (int v = 1; v < vertices; v++) {
            int p = parent[v];
            if (p != -1) {
                mstAdj[p][v] = 1;
                mstAdj[v][p] = 1;
            }
        }

        // 3. Preorder DFS Traversal with shortcutting
        int[] tourOrder = new int[vertices + 1];
        int[] tourIdx = new int[]{0};
        boolean[] visited = new boolean[vertices];

        dfsPreorder(0, vertices, mstAdj, visited, tourOrder, tourIdx);

        // Return to start depot
        tourOrder[vertices] = 0;

        // 4. Calculate total tour distance
        int totalDistance = 0;
        for (int i = 0; i < vertices; i++) {
            int from = tourOrder[i];
            int to = tourOrder[i + 1];
            totalDistance += dist[from][to];
        }

        return new TspResult(tourOrder, totalDistance, mstTotalWeight, true);
    }

    private static void dfsPreorder(int u, int vertices, int[][] mstAdj, boolean[] visited, int[] tourOrder, int[] tourIdx) {
        visited[u] = true;
        tourOrder[tourIdx[0]++] = u;

        for (int v = 0; v < vertices; v++) {
            if (mstAdj[u][v] == 1 && !visited[v]) {
                dfsPreorder(v, vertices, mstAdj, visited, tourOrder, tourIdx);
            }
        }
    }

    // =========================================================================
    // 2. EXACT BRUTE-FORCE TSP (FOR SMALL N <= 12)
    // =========================================================================

    /**
     * Exact exhaustive TSP solver for small N to compare approximation quality.
     */
    public static int solveExactTspSmallN(int vertices, int[][] dist) {
        if (vertices <= 0 || dist == null) return 0;
        if (vertices == 1) return 0;

        int[] p = new int[vertices - 1];
        for (int i = 0; i < vertices - 1; i++) {
            p[i] = i + 1;
        }

        int[] bestCost = new int[]{Integer.MAX_VALUE};
        permuteTsp(p, 0, vertices, dist, bestCost);
        return bestCost[0];
    }

    private static void permuteTsp(int[] p, int k, int vertices, int[][] dist, int[] bestCost) {
        if (k == p.length) {
            int cost = dist[0][p[0]];
            for (int i = 0; i < p.length - 1; i++) {
                cost += dist[p[i]][p[i + 1]];
            }
            cost += dist[p[p.length - 1]][0];
            if (cost < bestCost[0]) {
                bestCost[0] = cost;
            }
            return;
        }

        for (int i = k; i < p.length; i++) {
            int temp = p[k];
            p[k] = p[i];
            p[i] = temp;

            permuteTsp(p, k + 1, vertices, dist, bestCost);

            temp = p[k];
            p[k] = p[i];
            p[i] = temp;
        }
    }

    // =========================================================================
    // 3. GREEDY SET COVER APPROXIMATION (HOSPITAL DEFIBRILLATOR COVERAGE)
    // =========================================================================

    /**
     * Greedy Set Cover approximation for Hospital Facility / Defibrillator placement.
     * 
     * Core Idea:
     * Iteratively selects the facility/station that covers the largest number
     * of currently uncovered hospital zones.
     * 
     * Approximation Ratio: H_n <= ln(n) + 1
     * 
     * @param totalElements Total hospital zones needing coverage (0 to totalElements - 1).
     * @param setMasks      Array where setMasks[i] is the bitmask of zones covered by station i.
     * @return SetCoverResult with chosen stations.
     */
    public static SetCoverResult solveGreedySetCover(int totalElements, int[] setMasks) {
        if (totalElements <= 0 || setMasks == null || setMasks.length == 0) {
            return new SetCoverResult(new int[0], 0, 0);
        }

        int targetMask = (1 << totalElements) - 1;
        int currentCovered = 0;

        int numSets = setMasks.length;
        boolean[] usedSets = new boolean[numSets];

        int[] chosen = new int[numSets];
        int chosenCount = 0;

        while (currentCovered != targetMask) {
            int bestSet = -1;
            int maxNewCovered = 0;

            for (int i = 0; i < numSets; i++) {
                if (!usedSets[i]) {
                    // Count how many new bits this set covers
                    int newBits = setMasks[i] & (~currentCovered);
                    int count = countBits(newBits);
                    if (count > maxNewCovered) {
                        maxNewCovered = count;
                        bestSet = i;
                    }
                }
            }

            if (bestSet == -1 || maxNewCovered == 0) {
                break; // Cannot cover remaining zones
            }

            usedSets[bestSet] = true;
            chosen[chosenCount++] = bestSet;
            currentCovered |= setMasks[bestSet];
        }

        int[] result = new int[chosenCount];
        for (int i = 0; i < chosenCount; i++) {
            result[i] = chosen[i];
        }

        return new SetCoverResult(result, chosenCount, countBits(currentCovered));
    }

    // =========================================================================
    // 4. 2-APPROXIMATION FOR VERTEX COVER
    // =========================================================================

    /**
     * 2-Approximation for Vertex Cover using Maximal Matching.
     * 
     * Core Idea:
     * Iteratively picks an uncovered edge (u, v), adds both u and v to the cover,
     * and deletes all edges incident to u or v.
     * Since any valid vertex cover must pick at least one vertex from each disjoint edge,
     * taking both vertices guarantees size <= 2 * OPT.
     * 
     * @param vertices Total corridor junctions (V).
     * @param edges    Adjacency matrix [V][V] representing corridor connections.
     * @return VertexCoverResult with the chosen monitoring nodes.
     */
    public static VertexCoverResult solveVertexCover2Approx(int vertices, int[][] edges) {
        if (vertices <= 0 || edges == null) {
            return new VertexCoverResult(new int[0], 0);
        }

        int[][] remaining = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                remaining[i][j] = edges[i][j];
            }
        }

        boolean[] inCover = new boolean[vertices];
        int coverCount = 0;

        for (int u = 0; u < vertices; u++) {
            for (int v = u + 1; v < vertices; v++) {
                if (remaining[u][v] == 1) {
                    // Edge (u, v) is not covered: add both endpoints
                    inCover[u] = true;
                    inCover[v] = true;

                    // Remove all incident edges to u and v
                    for (int k = 0; k < vertices; k++) {
                        remaining[u][k] = 0;
                        remaining[k][u] = 0;
                        remaining[v][k] = 0;
                        remaining[k][v] = 0;
                    }
                }
            }
        }

        for (int i = 0; i < vertices; i++) {
            if (inCover[i]) {
                coverCount++;
            }
        }

        int[] result = new int[coverCount];
        int idx = 0;
        for (int i = 0; i < vertices; i++) {
            if (inCover[i]) {
                result[idx++] = i;
            }
        }

        return new VertexCoverResult(result, coverCount);
    }

    private static int countBits(int n) {
        int count = 0;
        while (n > 0) {
            count += (n & 1);
            n >>= 1;
        }
        return count;
    }
}
