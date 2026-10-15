package smarthospital.co6;

/**
 * CO6 - Randomized Algorithms and Data Structures Engine.
 * 
 * Implemented from scratch using primitive arrays and custom nodes without java.util.*:
 * 1. Randomized Skip List (Emergency Patient Triage Priority Queue)
 *    - Probabilistic balancing via coin-flip levels (p = 0.5)
 *    - Average O(log n) search, insertion, and deletion without AVL/Red-Black tree rotations
 * 2. Karger's Randomized Minimum Cut Algorithm
 *    - Hospital Infrastructure / Oxygen & Power Network Reliability Assessment
 *    - Contraction algorithm using Disjoint Set Union (DSU) with path compression
 * 3. Monte Carlo Clinical Queue Simulation
 *    - Probabilistic patient wait-time and bed occupancy estimation
 * 
 * Educational Notes:
 * - A Skip List uses randomization to maintain balanced search layers, achieving logarithmic
 *   expected search and update times with simple linked-list pointer updates.
 * - Karger's algorithm demonstrates a Monte Carlo randomized algorithm whose success probability
 *   is amplified by repeating independent contraction trials.
 */
public final class RandomizedAlgorithms {

    private RandomizedAlgorithms() {
        // Prevent instantiation
    }

    // =========================================================================
    // 1. RANDOMIZED SKIP LIST (EMERGENCY TRIAGE QUEUE)
    // =========================================================================

    /**
     * Node in the multi-level Skip List.
     */
    public static class SkipNode {
        public final int key;        // Triage priority score (smaller = higher clinical urgency)
        public final String value;   // Patient identifier / clinical notes
        public final SkipNode[] forward;

        public SkipNode(int key, String value, int level) {
            this.key = key;
            this.value = value;
            this.forward = new SkipNode[level + 1];
        }
    }

    /**
     * Probabilistic Skip List supporting average O(log n) operations.
     */
    public static class TriageSkipList {
        private static final int MAX_LEVEL = 16;
        private static final double PROBABILITY = 0.5;

        private final SkipNode header;
        private int currentLevel;
        private int size;
        private long randomSeed;

        public TriageSkipList() {
            this.header = new SkipNode(Integer.MIN_VALUE, "HEADER", MAX_LEVEL);
            this.currentLevel = 0;
            this.size = 0;
            this.randomSeed = 88172645463325252L;
        }

        /**
         * Fast pseudo-random generator without java.util.Random.
         */
        private double nextRandomDouble() {
            randomSeed ^= (randomSeed << 21);
            randomSeed ^= (randomSeed >>> 35);
            randomSeed ^= (randomSeed << 4);
            long positive = randomSeed & 0x7FFFFFFFFFFFFFFFL;
            return (double) positive / (double) Long.MAX_VALUE;
        }

        private int randomLevel() {
            int level = 0;
            while (level < MAX_LEVEL && nextRandomDouble() < PROBABILITY) {
                level++;
            }
            return level;
        }

        /**
         * Inserts a patient into the triage queue ordered by priority key in average O(log n).
         */
        public void insert(int key, String value) {
            SkipNode[] update = new SkipNode[MAX_LEVEL + 1];
            SkipNode curr = header;

            for (int i = currentLevel; i >= 0; i--) {
                while (curr.forward[i] != null && curr.forward[i].key < key) {
                    curr = curr.forward[i];
                }
                update[i] = curr;
            }

            int newLevel = randomLevel();
            if (newLevel > currentLevel) {
                for (int i = currentLevel + 1; i <= newLevel; i++) {
                    update[i] = header;
                }
                currentLevel = newLevel;
            }

            SkipNode newNode = new SkipNode(key, value, newLevel);
            for (int i = 0; i <= newLevel; i++) {
                newNode.forward[i] = update[i].forward[i];
                update[i].forward[i] = newNode;
            }
            size++;
        }

        /**
         * Searches for a patient by triage priority in average O(log n).
         */
        public String search(int key) {
            SkipNode curr = header;
            for (int i = currentLevel; i >= 0; i--) {
                while (curr.forward[i] != null && curr.forward[i].key < key) {
                    curr = curr.forward[i];
                }
            }
            curr = curr.forward[0];
            if (curr != null && curr.key == key) {
                return curr.value;
            }
            return null;
        }

        /**
         * Removes the patient with the highest clinical urgency (smallest key).
         */
        public SkipNode pollHighestPriority() {
            SkipNode first = header.forward[0];
            if (first == null) {
                return null;
            }

            for (int i = 0; i <= currentLevel; i++) {
                if (header.forward[i] == first) {
                    header.forward[i] = first.forward[i];
                }
            }

            while (currentLevel > 0 && header.forward[currentLevel] == null) {
                currentLevel--;
            }
            size--;
            return first;
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }
    }

    // =========================================================================
    // 2. KARGER'S RANDOMIZED MIN-CUT ALGORITHM
    // =========================================================================

    public static class Edge {
        public final int u;
        public final int v;

        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    /**
     * Finds the global Minimum Cut of an undirected network using Karger's Contraction Algorithm.
     * 
     * Core Idea:
     * Repeatedly contracts random edges until only two super-vertices remain.
     * The number of edges between the two remaining super-vertices is a cut.
     * Repeating this process across trials finds the true global minimum cut with high probability.
     * 
     * Time Complexity:  O(trials * (V + E * alpha(V)))
     * 
     * @param vertices Total hospital facilities/nodes.
     * @param edges    List of connections (oxygen pipes / power lines).
     * @param trials   Number of independent contraction trials.
     * @return Minimum cut size found.
     */
    public static int kargerMinCut(int vertices, Edge[] edges, int trials) {
        if (vertices <= 1 || edges == null || edges.length == 0) {
            return 0;
        }

        int minCut = Integer.MAX_VALUE;
        long seed = 123456789L;

        for (int t = 0; t < trials; t++) {
            // Disjoint Set Union (DSU) with path compression
            int[] parent = new int[vertices];
            for (int i = 0; i < vertices; i++) {
                parent[i] = i;
            }

            int superVertices = vertices;

            while (superVertices > 2) {
                // Pick a random edge
                seed ^= (seed << 21);
                seed ^= (seed >>> 35);
                seed ^= (seed << 4);
                int randomEdgeIdx = (int) ((seed & 0x7FFFFFFF) % edges.length);

                Edge e = edges[randomEdgeIdx];
                int rootU = findRoot(parent, e.u);
                int rootV = findRoot(parent, e.v);

                if (rootU != rootV) {
                    parent[rootU] = rootV;
                    superVertices--;
                }
            }

            // Count cut edges crossing the two remaining components
            int cutEdges = 0;
            for (Edge e : edges) {
                int rootU = findRoot(parent, e.u);
                int rootV = findRoot(parent, e.v);
                if (rootU != rootV) {
                    cutEdges++;
                }
            }

            if (cutEdges < minCut) {
                minCut = cutEdges;
            }
        }

        return minCut;
    }

    private static int findRoot(int[] parent, int i) {
        if (parent[i] == i) {
            return i;
        }
        parent[i] = findRoot(parent, parent[i]); // Path compression
        return parent[i];
    }

    // =========================================================================
    // 3. MONTE CARLO CLINICAL QUEUE SIMULATION
    // =========================================================================

    public static class QueueSimulationResult {
        public final double averageWaitMinutes;
        public final double maxWaitMinutes;
        public final double congestionProbability;

        public QueueSimulationResult(double avg, double max, double congestionProb) {
            this.averageWaitMinutes = avg;
            this.maxWaitMinutes = max;
            this.congestionProbability = congestionProb;
        }

        @Override
        public String toString() {
            return String.format("Monte Carlo Queue Stats: Avg Wait: %.1f mins | Max Wait: %.1f mins | Congestion: %.1f%%",
                    averageWaitMinutes, maxWaitMinutes, congestionProbability * 100.0);
        }
    }

    /**
     * Simulates emergency room arrival and service queues over multiple stochastic days.
     */
    public static QueueSimulationResult simulateHospitalQueue(
            int days, int beds, double arrivalRatePerHour, double serviceRatePerHour) {

        long seed = 987654321L;
        double totalWait = 0.0;
        double maxObservedWait = 0.0;
        int congestedDays = 0;

        for (int d = 0; d < days; d++) {
            // Pseudo-random daily patient load
            seed ^= (seed << 21);
            seed ^= (seed >>> 35);
            seed ^= (seed << 4);
            double dailyFactor = 0.8 + (((seed & 0x7FFFFFFF) % 1000) / 1000.0) * 0.4;

            double expectedArrivals = 24.0 * arrivalRatePerHour * dailyFactor;
            double serviceCapacity = 24.0 * beds * serviceRatePerHour;

            double wait;
            if (expectedArrivals > serviceCapacity) {
                congestedDays++;
                wait = (expectedArrivals - serviceCapacity) * 45.0 + 30.0;
            } else {
                wait = (expectedArrivals / serviceCapacity) * 20.0 + 5.0;
            }

            totalWait += wait;
            if (wait > maxObservedWait) {
                maxObservedWait = wait;
            }
        }

        double avgWait = totalWait / days;
        double congestionProb = (double) congestedDays / days;

        return new QueueSimulationResult(avgWait, maxObservedWait, congestionProb);
    }
}
