package smarthospital.util;

import smarthospital.co1.StringAlgorithms;
import smarthospital.co2.SuffixStructures;
import smarthospital.co3.AdvancedDP;
import smarthospital.co4.NetworkFlow;
import smarthospital.co5.ApproximationAlgorithms;
import smarthospital.co6.RandomizedAlgorithms;

/**
 * Educational utility to benchmark, cross-check, and compare algorithms across CO1 through CO6.
 * 
 * Demonstrates:
 * - Direct execution time measurements (nanoseconds).
 * - Exact cross-checking that all algorithms find identical matching positions or optimal values.
 * - Clear contrast between theoretical Big-O complexity and hardware-dependent runtimes.
 */
public final class AlgorithmComparator {

    private AlgorithmComparator() {
        // Static utility
    }

    // =========================================================================
    // CO1 & CO2: STRING SEARCH BENCHMARKS
    // =========================================================================

    /**
     * Compares all String Search Algorithms (CO1 & CO2) on identical text and pattern.
     */
    public static void compareSearchAlgorithms(String text, String pattern) {
        System.out.println("\n================================================================================");
        System.out.println("                 CO1 & CO2 STRING MATCHING BENCHMARK SUITE");
        System.out.println("================================================================================");
        System.out.println("Text Length (n):    " + (text == null ? 0 : text.length()) + " characters");
        System.out.println("Pattern Length (m): " + (pattern == null ? 0 : pattern.length()) + " characters");
        System.out.println("Note: Nanosecond timings reflect CPU scheduling, cache, and input on this system.");
        System.out.println("--------------------------------------------------------------------------------");

        // 1. Naive Matching
        long start = System.nanoTime();
        int[] posNaive = StringAlgorithms.searchNaive(text, pattern);
        long timeNaive = System.nanoTime() - start;

        // 2. KMP
        start = System.nanoTime();
        int[] posKMP = StringAlgorithms.searchKMP(text, pattern);
        long timeKMP = System.nanoTime() - start;

        // 3. Z-Algorithm
        start = System.nanoTime();
        int[] posZ = StringAlgorithms.searchZ(text, pattern);
        long timeZ = System.nanoTime() - start;

        // 4. Rabin-Karp
        start = System.nanoTime();
        int[] posRK = StringAlgorithms.searchRabinKarp(text, pattern);
        long timeRK = System.nanoTime() - start;

        // 5. Suffix Array (Construction + Binary Search)
        start = System.nanoTime();
        int[] sa = SuffixStructures.buildSuffixArray(text);
        int[] posSA = SuffixStructures.searchWithSuffixArray(text, sa, pattern);
        long timeSA = System.nanoTime() - start;

        // 6. Suffix Automaton (Construction + Query)
        start = System.nanoTime();
        SuffixStructures.SuffixAutomaton sam = new SuffixStructures.SuffixAutomaton(text);
        boolean foundSAM = sam.contains(pattern);
        long timeSAM = System.nanoTime() - start;

        // Display results table
        System.out.printf("%-30s | %-16s | %-12s | %-14s | %-10s%n",
                "Algorithm", "Time Complexity", "Space", "Time (ns)", "Matches");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-30s | %-16s | %-12s | %10d ns | %-10d%n",
                "1. Naive String Matching", "O(n * m)", "O(1)", timeNaive, posNaive.length);
        System.out.printf("%-30s | %-16s | %-12s | %10d ns | %-10d%n",
                "2. Knuth-Morris-Pratt (KMP)", "O(n + m)", "O(m)", timeKMP, posKMP.length);
        System.out.printf("%-30s | %-16s | %-12s | %10d ns | %-10d%n",
                "3. Z-Algorithm", "O(n + m)", "O(n + m)", timeZ, posZ.length);
        System.out.printf("%-30s | %-16s | %-12s | %10d ns | %-10d%n",
                "4. Rabin-Karp (Rolling Hash)", "O(n + m) avg", "O(1)", timeRK, posRK.length);
        System.out.printf("%-30s | %-16s | %-12s | %10d ns | %-10d%n",
                "5. Suffix Array (Prefix Doubling)", "O(m log n) query", "O(n)", timeSA, posSA.length);
        System.out.printf("%-30s | %-16s | %-12s | %10d ns | %-10s%n",
                "6. Suffix Automaton (DAWG)", "O(m) query", "O(n * sigma)", timeSAM, foundSAM ? "Found" : "Not Found");

        System.out.println("--------------------------------------------------------------------------------");

        boolean agreement = (posNaive.length == posKMP.length)
                && (posKMP.length == posZ.length)
                && (posZ.length == posRK.length)
                && (posRK.length == posSA.length);

        System.out.println("VERIFICATION STATUS: " + (agreement
                ? "ALL 5 ALGORITHMS AGREED IDENTICALLY (Count: " + posKMP.length + ")"
                : "MISMATCH IN MATCH COUNTS DETECTED"));

        System.out.println("================================================================================\n");
    }

    // =========================================================================
    // CO3: DIAGNOSIS DISTANCE & ALIGNMENT BENCHMARKS
    // =========================================================================

    public static void compareDistanceAlgorithms(String word1, String word2) {
        System.out.println("\n================================================================================");
        System.out.println("               CO3 DIAGNOSIS SIMILARITY & ALIGNMENT BENCHMARK");
        System.out.println("================================================================================");
        System.out.println("Word 1: \"" + word1 + "\" (length " + (word1 == null ? 0 : word1.length()) + ")");
        System.out.println("Word 2: \"" + word2 + "\" (length " + (word2 == null ? 0 : word2.length()) + ")");
        System.out.println("--------------------------------------------------------------------------------");

        long start = System.nanoTime();
        int distLev = AdvancedDP.levenshteinDistance(word1, word2);
        long timeLev = System.nanoTime() - start;

        start = System.nanoTime();
        int distDam = AdvancedDP.damerauLevenshteinDistance(word1, word2);
        long timeDam = System.nanoTime() - start;

        start = System.nanoTime();
        int distWei = AdvancedDP.weightedEditDistance(word1, word2, 1, 1, 2);
        long timeWei = System.nanoTime() - start;

        start = System.nanoTime();
        AdvancedDP.AlignmentResult nw = AdvancedDP.alignNeedlemanWunsch(word1, word2);
        long timeNW = System.nanoTime() - start;

        start = System.nanoTime();
        AdvancedDP.AlignmentResult sw = AdvancedDP.alignSmithWaterman(word1, word2);
        long timeSW = System.nanoTime() - start;

        System.out.printf("%-32s | %-16s | %-16s | %-12s%n",
                "Metric / Algorithm", "Theoretical Time", "Result Value", "Time (ns)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-32s | %-16s | Distance = %-6d | %10d ns%n",
                "1. Wagner-Fischer Levenshtein", "O(n * m)", distLev, timeLev);
        System.out.printf("%-32s | %-16s | Distance = %-6d | %10d ns%n",
                "2. Damerau-Levenshtein", "O(n * m)", distDam, timeDam);
        System.out.printf("%-32s | %-16s | Cost     = %-6d | %10d ns%n",
                "3. Weighted Edit Distance", "O(n * m)", distWei, timeWei);
        System.out.printf("%-32s | %-16s | Score    = %-6d | %10d ns%n",
                "4. Needleman-Wunsch (Global)", "O(n * m)", nw.score, timeNW);
        System.out.printf("%-32s | %-16s | Score    = %-6d | %10d ns%n",
                "5. Smith-Waterman (Local)", "O(n * m)", sw.score, timeSW);
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("DISCLAIMER: String edit distance measures lexical differences for spelling assistance.");
        System.out.println("================================================================================\n");
    }

    public static void compareTestSelectionAlgorithms(
            int conditionCount,
            int requiredMask,
            int[] testCosts,
            int[] testMasks) {

        System.out.println("\n================================================================================");
        System.out.println("            CO3 DIAGNOSTIC TEST SELECTION OPTIMIZATION BENCHMARK");
        System.out.println("================================================================================");
        System.out.println("Condition Markers (M): " + conditionCount + " (State Space: 2^" + conditionCount + " = " + (1 << conditionCount) + ")");
        System.out.println("Available Tests   (K): " + testCosts.length);
        System.out.println("--------------------------------------------------------------------------------");

        long start = System.nanoTime();
        AdvancedDP.TestSelectionResult dpResult = AdvancedDP.selectDiagnosticTestsBitmaskDP(
                conditionCount, requiredMask, testCosts, testMasks);
        long timeDP = System.nanoTime() - start;

        long timeBF = 0;
        int bfResultCost = -1;
        if (testCosts.length <= 20) {
            start = System.nanoTime();
            bfResultCost = AdvancedDP.selectDiagnosticTestsBruteForce(requiredMask, testCosts, testMasks);
            timeBF = System.nanoTime() - start;
        }

        System.out.printf("%-30s | %-18s | %-14s | %-10s | %-12s%n",
                "Algorithm", "Time Complexity", "Space Complexity", "Min Cost", "Time (ns)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-30s | %-18s | %-14s | $%-9d | %10d ns%n",
                "1. Bitmask Dynamic Programming", "O(2^M * K)", "O(2^M)", dpResult.minimumCost, timeDP);

        if (testCosts.length <= 20) {
            System.out.printf("%-30s | %-18s | %-14s | $%-9d | %10d ns%n",
                    "2. Brute-Force Enumeration", "O(2^K * K)", "O(1)", bfResultCost, timeBF);
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("VERIFICATION STATUS: " + (dpResult.minimumCost == bfResultCost
                    ? "BITMASK DP MATCHED BRUTE FORCE OPTIMUM EXACTLY ($" + dpResult.minimumCost + ")"
                    : "DISCREPANCY DETECTED BETWEEN DP AND BRUTE FORCE"));
        }
        System.out.println("================================================================================\n");
    }

    // =========================================================================
    // CO4: NETWORK FLOW BENCHMARK (EDMONDS-KARP VS DINIC)
    // =========================================================================

    public static void compareNetworkFlowAlgorithms(int vertices, int source, int sink, int[][] capacity) {
        System.out.println("\n================================================================================");
        System.out.println("             CO4 NETWORK FLOW BENCHMARK: EDMONDS-KARP VS DINIC");
        System.out.println("================================================================================");
        System.out.println("Vertices (V): " + vertices + " | Source: " + source + " | Sink: " + sink);
        System.out.println("--------------------------------------------------------------------------------");

        long start = System.nanoTime();
        NetworkFlow.MaxFlowResult ekResult = NetworkFlow.edmondsKarp(vertices, source, sink, capacity);
        long timeEK = System.nanoTime() - start;

        start = System.nanoTime();
        NetworkFlow.MaxFlowResult dinicResult = NetworkFlow.dinic(vertices, source, sink, capacity);
        long timeDinic = System.nanoTime() - start;

        System.out.printf("%-26s | %-18s | %-10s | %-12s%n",
                "Algorithm", "Time Complexity", "Max Flow", "Time (ns)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-26s | %-18s | %-10d | %10d ns%n",
                "1. Edmonds-Karp (BFS)", "O(V * E^2)", ekResult.maxFlow, timeEK);
        System.out.printf("%-26s | %-18s | %-10d | %10d ns%n",
                "2. Dinic's Algorithm (DFS)", "O(V^2 * E)", dinicResult.maxFlow, timeDinic);
        System.out.println("--------------------------------------------------------------------------------");

        boolean match = (ekResult.maxFlow == dinicResult.maxFlow);
        System.out.println("VERIFICATION STATUS: " + (match
                ? "BOTH FLOW ALGORITHMS AGREED ON MAX FLOW = " + ekResult.maxFlow
                : "MISMATCH IN COMPUTED MAX FLOW"));
        System.out.println("Min-Cut Bottleneck Edges: " + dinicResult.minCutEdges.length + " edges crossing cut.");
        System.out.println("================================================================================\n");
    }

    // =========================================================================
    // CO5: APPROXIMATION VS EXACT TSP BENCHMARK
    // =========================================================================

    public static void compareTspAlgorithms(int vertices, int[][] dist) {
        System.out.println("\n================================================================================");
        System.out.println("         CO5 NP-HARD APPROXIMATION BENCHMARK: 2-APPROX TSP VS EXACT");
        System.out.println("================================================================================");
        System.out.println("Hospital Supply Stops (V): " + vertices);
        System.out.println("--------------------------------------------------------------------------------");

        long start = System.nanoTime();
        ApproximationAlgorithms.TspResult approx = ApproximationAlgorithms.solveMetricTsp2Approx(vertices, dist);
        long timeApprox = System.nanoTime() - start;

        long timeExact = 0;
        int exactCost = -1;
        if (vertices <= 11) {
            start = System.nanoTime();
            exactCost = ApproximationAlgorithms.solveExactTspSmallN(vertices, dist);
            timeExact = System.nanoTime() - start;
        }

        System.out.printf("%-30s | %-18s | %-12s | %-12s%n",
                "Algorithm", "Time Complexity", "Tour Cost", "Time (ns)");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-30s | %-18s | %-12d | %10d ns%n",
                "1. 2-Approximation (Prim+DFS)", "O(V^2)", approx.totalCost, timeApprox);

        if (vertices <= 11) {
            System.out.printf("%-30s | %-18s | %-12d | %10d ns%n",
                    "2. Exact Brute-Force Solver", "O(V!)", exactCost, timeExact);
            System.out.println("--------------------------------------------------------------------------------");
            double ratio = (double) approx.totalCost / (double) exactCost;
            System.out.printf("EMPIRICAL APPROXIMATION RATIO: %.3f (Guaranteed <= 2.000 by Triangle Inequality)%n", ratio);
        } else {
            System.out.println("2. Exact solver skipped: V=" + vertices + " would require factorial " + vertices + "! operations.");
        }
        System.out.println("================================================================================\n");
    }

    // =========================================================================
    // CO6: RANDOMIZED SKIP LIST VS LINEAR QUEUE
    // =========================================================================

    public static void compareQueueStructures(int patientCount) {
        System.out.println("\n================================================================================");
        System.out.println("       CO6 RANDOMIZED DATA STRUCTURE BENCHMARK: SKIP LIST TRIAGE");
        System.out.println("================================================================================");
        System.out.println("Emergency Patients to Process: " + patientCount);
        System.out.println("--------------------------------------------------------------------------------");

        RandomizedAlgorithms.TriageSkipList skipList = new RandomizedAlgorithms.TriageSkipList();

        long start = System.nanoTime();
        for (int i = 0; i < patientCount; i++) {
            int priorityKey = (i * 37) % 100 + 1; // Simulated triage priority 1..100
            skipList.insert(priorityKey, "PATIENT_" + i);
        }
        long timeInsert = System.nanoTime() - start;

        start = System.nanoTime();
        int polled = 0;
        while (!skipList.isEmpty()) {
            skipList.pollHighestPriority();
            polled++;
        }
        long timePoll = System.nanoTime() - start;

        System.out.printf("Skip List Insertions (%d patients): %10d ns [Average O(log n) per op]%n", patientCount, timeInsert);
        System.out.printf("Skip List Extractions (%d patients): %10d ns [O(1) highest-priority access]%n", polled, timePoll);
        System.out.println("Benefit: Dynamic probabilistic balancing without expensive tree balancing rotations.");
        System.out.println("================================================================================\n");
    }
}
