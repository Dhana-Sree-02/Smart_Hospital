package smarthospital.co4;

/**
 * CO4 - Network Flow and Matching Algorithms Engine.
 * 
 * Implemented from scratch using primitive arrays and explicit loops without java.util.*:
 * 1. Edmonds-Karp Algorithm (BFS-based Ford-Fulkerson) - O(V * E^2)
 * 2. Dinic's Algorithm (Level Graph + Blocking Flow via DFS) - O(V^2 * E)
 * 3. Max-Flow Min-Cut Theorem: Identifies bottleneck cut edges and reachable S-T partitions
 * 4. Maximum Bipartite Matching: Hospital resource/bed allocation to emergency patients
 * 
 * Educational Notes:
 * - A flow network is a directed graph where each edge has a capacity and receives a flow.
 * - The Max-Flow Min-Cut Theorem states that the maximum flow passing from source to sink
 *   equals the total capacity of the minimum cut separating source and sink.
 * - In the hospital context, this models emergency bed assignment, ambulance transfer
 *   pipelines, and medical staff scheduling capacity.
 */
public final class NetworkFlow {

    private NetworkFlow() {
        // Prevent instantiation
    }

    // =========================================================================
    // RESULT STRUCTURES
    // =========================================================================

    /**
     * Encapsulates the result of a Maximum Flow calculation.
     */
    public static class MaxFlowResult {
        public final int maxFlow;
        public final int[][] flow;
        public final boolean[] sourcePartition;
        public final CutEdge[] minCutEdges;

        public MaxFlowResult(int maxFlow, int[][] flow, boolean[] sourcePartition, CutEdge[] minCutEdges) {
            this.maxFlow = maxFlow;
            this.flow = flow;
            this.sourcePartition = sourcePartition;
            this.minCutEdges = minCutEdges;
        }
    }

    /**
     * Represents a bottleneck edge in the minimum cut.
     */
    public static class CutEdge {
        public final int u;
        public final int v;
        public final int capacity;

        public CutEdge(int u, int v, int capacity) {
            this.u = u;
            this.v = v;
            this.capacity = capacity;
        }

        @Override
        public String toString() {
            return String.format("(%d -> %d, capacity: %d)", u, v, capacity);
        }
    }

    /**
     * Encapsulates the result of Maximum Bipartite Matching.
     */
    public static class BipartiteMatchingResult {
        public final int matchCount;
        public final int[] patientToBed; // patientToBed[i] is bed assigned to patient i, or -1

        public BipartiteMatchingResult(int matchCount, int[] patientToBed) {
            this.matchCount = matchCount;
            this.patientToBed = patientToBed;
        }
    }

    // =========================================================================
    // 1. EDMONDS-KARP ALGORITHM (O(V * E^2))
    // =========================================================================

    /**
     * Computes Maximum Flow from source to sink using the Edmonds-Karp algorithm.
     * 
     * Core Idea:
     * Augments flow along the shortest augmenting path (fewest edges) using Breadth-First Search (BFS).
     * Guaranteed to terminate in O(V * E^2) operations without risk of infinite augmentation
     * on irrational capacities.
     * 
     * Time Complexity:  O(V * E^2)
     * Space Complexity: O(V^2) for adjacency capacity and flow matrices
     * 
     * @param vertices Total number of vertices in the graph (0 to vertices - 1).
     * @param source   Source vertex index.
     * @param sink     Sink vertex index.
     * @param capacity Directed edge capacities: capacity[u][v] is capacity of edge u -> v.
     * @return MaxFlowResult containing total flow and the minimum cut.
     */
    public static MaxFlowResult edmondsKarp(int vertices, int source, int sink, int[][] capacity) {
        if (vertices <= 0 || source < 0 || sink < 0 || source >= vertices || sink >= vertices) {
            return new MaxFlowResult(0, new int[0][0], new boolean[0], new CutEdge[0]);
        }

        int[][] flow = new int[vertices][vertices];
        int[] parent = new int[vertices];
        int totalFlow = 0;

        while (bfsAugmentingPath(vertices, source, sink, capacity, flow, parent)) {
            // Find bottleneck capacity along the BFS path
            int pathFlow = Integer.MAX_VALUE;
            int curr = sink;

            while (curr != source) {
                int prev = parent[curr];
                int residual = capacity[prev][curr] - flow[prev][curr];
                if (residual < pathFlow) {
                    pathFlow = residual;
                }
                curr = prev;
            }

            // Augment flow and subtract from reverse residual edges
            curr = sink;
            while (curr != source) {
                int prev = parent[curr];
                flow[prev][curr] += pathFlow;
                flow[curr][prev] -= pathFlow;
                curr = prev;
            }

            totalFlow += pathFlow;
        }

        // Identify Min-Cut: reachable nodes in the residual graph from source
        boolean[] reachable = findResidualReachable(vertices, source, capacity, flow);
        CutEdge[] minCut = extractMinCutEdges(vertices, reachable, capacity);

        return new MaxFlowResult(totalFlow, flow, reachable, minCut);
    }

    /**
     * BFS helper for Edmonds-Karp using a primitive circular queue (no java.util.Queue).
     */
    private static boolean bfsAugmentingPath(int vertices, int source, int sink, int[][] capacity, int[][] flow, int[] parent) {
        boolean[] visited = new boolean[vertices];
        int[] queue = new int[vertices];
        int head = 0;
        int tail = 0;

        for (int i = 0; i < vertices; i++) {
            parent[i] = -1;
        }

        queue[tail++] = source;
        visited[source] = true;

        while (head < tail) {
            int u = queue[head++];

            for (int v = 0; v < vertices; v++) {
                int residual = capacity[u][v] - flow[u][v];
                if (!visited[v] && residual > 0) {
                    parent[v] = u;
                    visited[v] = true;
                    if (v == sink) {
                        return true;
                    }
                    queue[tail++] = v;
                }
            }
        }
        return visited[sink];
    }

    // =========================================================================
    // 2. DINIC'S ALGORITHM (O(V^2 * E))
    // =========================================================================

    /**
     * Computes Maximum Flow from source to sink using Dinic's Algorithm.
     * 
     * Core Idea:
     * 1. Constructs a layered / level graph using BFS where level[v] = distance from source.
     * 2. Pushes blocking flow along admissible edges (level[v] == level[u] + 1) using DFS.
     * 3. Maintains work pointers (ptr[u]) to prevent re-scanning dead-end edges.
     * 
     * Time Complexity:  O(V^2 * E) general, O(E * sqrt(V)) for unit networks / bipartite matching.
     * Space Complexity: O(V^2) for capacity and flow matrices.
     */
    public static MaxFlowResult dinic(int vertices, int source, int sink, int[][] capacity) {
        if (vertices <= 0 || source < 0 || sink < 0 || source >= vertices || sink >= vertices) {
            return new MaxFlowResult(0, new int[0][0], new boolean[0], new CutEdge[0]);
        }

        int[][] flow = new int[vertices][vertices];
        int[] level = new int[vertices];
        int[] ptr = new int[vertices];
        int totalFlow = 0;

        while (dinicBfs(vertices, source, sink, capacity, flow, level)) {
            for (int i = 0; i < vertices; i++) {
                ptr[i] = 0;
            }
            while (true) {
                int pushed = dinicDfs(source, sink, Integer.MAX_VALUE, vertices, capacity, flow, level, ptr);
                if (pushed <= 0) {
                    break;
                }
                totalFlow += pushed;
            }
        }

        boolean[] reachable = findResidualReachable(vertices, source, capacity, flow);
        CutEdge[] minCut = extractMinCutEdges(vertices, reachable, capacity);

        return new MaxFlowResult(totalFlow, flow, reachable, minCut);
    }

    private static boolean dinicBfs(int vertices, int source, int sink, int[][] capacity, int[][] flow, int[] level) {
        for (int i = 0; i < vertices; i++) {
            level[i] = -1;
        }

        int[] queue = new int[vertices];
        int head = 0;
        int tail = 0;

        level[source] = 0;
        queue[tail++] = source;

        while (head < tail) {
            int u = queue[head++];
            for (int v = 0; v < vertices; v++) {
                int residual = capacity[u][v] - flow[u][v];
                if (residual > 0 && level[v] == -1) {
                    level[v] = level[u] + 1;
                    queue[tail++] = v;
                }
            }
        }
        return level[sink] != -1;
    }

    private static int dinicDfs(int u, int sink, int pushed, int vertices, int[][] capacity, int[][] flow, int[] level, int[] ptr) {
        if (pushed == 0 || u == sink) {
            return pushed;
        }

        for (int v = ptr[u]; v < vertices; v++) {
            ptr[u] = v;
            int residual = capacity[u][v] - flow[u][v];

            if (level[v] == level[u] + 1 && residual > 0) {
                int nextPushed = pushed < residual ? pushed : residual;
                int tr = dinicDfs(v, sink, nextPushed, vertices, capacity, flow, level, ptr);
                if (tr > 0) {
                    flow[u][v] += tr;
                    flow[v][u] -= tr;
                    return tr;
                }
            }
        }
        return 0;
    }

    // =========================================================================
    // 3. MAXIMUM BIPARTITE MATCHING (HOSPITAL BED / DOCTOR ALLOCATION)
    // =========================================================================

    /**
     * Solves Maximum Bipartite Matching for Hospital Bed / Resource Allocation.
     * 
     * Problem Statement:
     * Given P patients and B hospital beds/resources, where eligibleMatrix[p][b] is true
     * if bed b is medically suitable for patient p's diagnosis/triage level.
     * Finds the maximum number of simultaneous non-conflicting assignments.
     * 
     * Reduction to Network Flow:
     * - Source (0) connected to each patient p (1 .. P) with capacity 1.
     * - Each patient p connected to eligible bed b (P + 1 .. P + B) with capacity 1.
     * - Each bed b connected to Sink (P + B + 1) with capacity 1.
     * 
     * @param patientCount Number of patients (P).
     * @param bedCount     Number of hospital beds (B).
     * @param eligible     Boolean matrix [P][B]: true if patient p can occupy bed b.
     * @return BipartiteMatchingResult with match count and assignment array.
     */
    public static BipartiteMatchingResult solveBipartiteMatching(int patientCount, int bedCount, boolean[][] eligible) {
        if (patientCount <= 0 || bedCount <= 0 || eligible == null) {
            return new BipartiteMatchingResult(0, new int[0]);
        }

        int totalVertices = patientCount + bedCount + 2;
        int source = 0;
        int sink = totalVertices - 1;
        int[][] capacity = new int[totalVertices][totalVertices];

        // 1. Source -> Patients (capacity 1)
        for (int p = 0; p < patientCount; p++) {
            int patientNode = 1 + p;
            capacity[source][patientNode] = 1;
        }

        // 2. Patients -> Eligible Beds (capacity 1)
        for (int p = 0; p < patientCount; p++) {
            int patientNode = 1 + p;
            for (int b = 0; b < bedCount; b++) {
                if (eligible[p] != null && b < eligible[p].length && eligible[p][b]) {
                    int bedNode = 1 + patientCount + b;
                    capacity[patientNode][bedNode] = 1;
                }
            }
        }

        // 3. Beds -> Sink (capacity 1)
        for (int b = 0; b < bedCount; b++) {
            int bedNode = 1 + patientCount + b;
            capacity[bedNode][sink] = 1;
        }

        MaxFlowResult flowRes = dinic(totalVertices, source, sink, capacity);

        int[] patientToBed = new int[patientCount];
        for (int p = 0; p < patientCount; p++) {
            patientToBed[p] = -1;
            int patientNode = 1 + p;
            for (int b = 0; b < bedCount; b++) {
                int bedNode = 1 + patientCount + b;
                if (flowRes.flow[patientNode][bedNode] == 1) {
                    patientToBed[p] = b;
                    break;
                }
            }
        }

        return new BipartiteMatchingResult(flowRes.maxFlow, patientToBed);
    }

    // =========================================================================
    // 4. MIN-CUT ANALYSIS HELPERS
    // =========================================================================

    private static boolean[] findResidualReachable(int vertices, int source, int[][] capacity, int[][] flow) {
        boolean[] reachable = new boolean[vertices];
        int[] queue = new int[vertices];
        int head = 0;
        int tail = 0;

        reachable[source] = true;
        queue[tail++] = source;

        while (head < tail) {
            int u = queue[head++];
            for (int v = 0; v < vertices; v++) {
                if (!reachable[v] && (capacity[u][v] - flow[u][v] > 0)) {
                    reachable[v] = true;
                    queue[tail++] = v;
                }
            }
        }
        return reachable;
    }

    private static CutEdge[] extractMinCutEdges(int vertices, boolean[] reachable, int[][] capacity) {
        // First pass: count cut edges
        int count = 0;
        for (int u = 0; u < vertices; u++) {
            if (reachable[u]) {
                for (int v = 0; v < vertices; v++) {
                    if (!reachable[v] && capacity[u][v] > 0) {
                        count++;
                    }
                }
            }
        }

        CutEdge[] cutEdges = new CutEdge[count];
        int idx = 0;
        for (int u = 0; u < vertices; u++) {
            if (reachable[u]) {
                for (int v = 0; v < vertices; v++) {
                    if (!reachable[v] && capacity[u][v] > 0) {
                        cutEdges[idx++] = new CutEdge(u, v, capacity[u][v]);
                    }
                }
            }
        }
        return cutEdges;
    }
}
