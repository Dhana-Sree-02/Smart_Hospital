SMART HOSPITAL - Full Advanced DSA Curriculum (CO1 to CO6)
=============================================================

This upgraded version of the Smart Hospital Information System includes
the complete Advanced DSA curriculum implemented from scratch:

- CO1: Naive String Matching, Knuth-Morris-Pratt (KMP), Z-Algorithm, Rabin-Karp
- CO2: Suffix Array (Prefix Doubling O(n log^2 n)), Binary Search Substring Matcher,
       Kasai's LCP Array, Longest Repeated Substring Analyzer, Suffix Automaton
- CO3: Wagner-Fischer Levenshtein, Damerau-Levenshtein, Weighted Edit Distance,
       Needleman-Wunsch (Global Alignment with Traceback),
       Smith-Waterman (Local Alignment with Traceback),
       Bitmask DP Diagnostic Test Selection (Set Cover) vs Brute-Force
- CO4: Edmonds-Karp Max Flow, Dinic's Algorithm, Max-Flow Min-Cut Bottleneck Theorem,
       Maximum Bipartite Matching for Emergency Patient-to-Bed Allocation
- CO5: 2-Approximation for Metric TSP (Ambulance Route Optimization via Prim MST + DFS),
       Greedy Set Cover (Hospital Defibrillator Station Placement),
       2-Approximation Vertex Cover (Corridor Surveillance Coverage),
       Exact Brute-Force TSP Benchmark (V <= 12)
- CO6: Randomized Skip List (Dynamic Emergency Patient Triage Priority Queue),
       Karger's Randomized Min-Cut (Hospital Infrastructure Reliability),
       Monte Carlo Clinical Queue & Wait-Time Simulation

Engine Architecture:
- Strict zero `java.util.*` imports in all algorithm engines (co1..co6).
- All internal buffers and queues use primitive arrays, matrices, and node pointers.

Automated Test Suite:
- 79 passing unit tests covering all CO1-CO6 algorithms and hospital rules.
- Run via Eclipse or `java --module-path bin -m Smart_Hospital1/smarthospital.test.SystemTestSuite`.

Eclipse Setup:
1. Open Eclipse -> File -> Import -> Existing Projects into Workspace.
2. Select this root directory containing .project and .classpath.
3. Run src/smarthospital/Main.java as Java Application.
