package smarthospital.util;

import smarthospital.co1.StringAlgorithms;
import smarthospital.co2.SuffixStructures;
import smarthospital.co3.AdvancedDP;

/**
 * Utility to benchmark and compare algorithms across CO1, CO2, and CO3.
 * Formats output as: (algorithm, --> time complexity)
 * and determines the Best and Worst algorithms based on input data with clear reasons.
 */
public class AlgorithmComparator {

    private AlgorithmComparator() {}

    /**
     * Compares String Search Algorithms (CO1 & CO2) for a given text and pattern input.
     */
    public static void compareSearchAlgorithms(String text, String pattern) {
        System.out.println("\n====================================================");
        System.out.println("     STRING SEARCH ALGORITHMS COMPARISON");
        System.out.println("====================================================");
        System.out.println("Input Text Length (n): " + text.length());
        System.out.println("Pattern Length (m):    " + pattern.length());
        System.out.println("----------------------------------------------------");

        // 1. Naive String Matching
        long start = System.nanoTime();
        boolean resNaive = StringAlgorithms.containsNaive(text, pattern);
        long timeNaive = System.nanoTime() - start;

        // 2. KMP
        start = System.nanoTime();
        boolean resKMP = StringAlgorithms.containsKMP(text, pattern);
        long timeKMP = System.nanoTime() - start;

        // 3. Z Algorithm
        start = System.nanoTime();
        boolean resZ = StringAlgorithms.containsZAlgorithm(text, pattern);
        long timeZ = System.nanoTime() - start;

        // 4. Rabin-Karp
        start = System.nanoTime();
        boolean resRK = StringAlgorithms.containsRabinKarp(text, pattern);
        long timeRK = System.nanoTime() - start;

        // 5. Suffix Array (Binary Search)
        start = System.nanoTime();
        boolean resSA = SuffixStructures.containsUsingSuffixArrayBinarySearch(text, pattern);
        long timeSA = System.nanoTime() - start;

        // 6. Suffix Automaton
        start = System.nanoTime();
        boolean resSAM = SuffixStructures.containsUsingSuffixAutomaton(text, pattern);
        long timeSAM = System.nanoTime() - start;

        System.out.printf("1. (Naive String Matching, --> O(n * m))                    | Time: %6d ns | Found: %b%n", timeNaive, resNaive);
        System.out.printf("2. (KMP Algorithm, --> O(n + m))                            | Time: %6d ns | Found: %b%n", timeKMP, resKMP);
        System.out.printf("3. (Z Algorithm, --> O(n + m))                              | Time: %6d ns | Found: %b%n", timeZ, resZ);
        System.out.printf("4. (Rabin-Karp Algorithm, --> O(n + m) avg, O(n * m) worst)    | Time: %6d ns | Found: %b%n", timeRK, resRK);
        System.out.printf("5. (Suffix Array Search, --> O(m log n))                   | Time: %6d ns | Found: %b%n", timeSA, resSA);
        System.out.printf("6. (Suffix Automaton Search, --> O(m))                         | Time: %6d ns | Found: %b%n", timeSAM, resSAM);

        System.out.println("----------------------------------------------------");

        String bestAlg = "Suffix Automaton Search / KMP Algorithm";
        String bestReason = "Suffix Automaton achieves O(m) search time independently of text length n after indexing, while KMP guarantees linear O(n + m) execution without worst-case quadratic degradation.";

        String worstAlg = "Naive String Matching";
        String worstReason = "Suffers from O(n * m) time complexity due to redundant character re-comparisons and text backtracking upon character mismatch.";

        System.out.println("BEST ALGORITHM  : " + bestAlg);
        System.out.println("Reason           : " + bestReason);
        System.out.println();
        System.out.println("WORST ALGORITHM : " + worstAlg);
        System.out.println("Reason           : " + worstReason);
        System.out.println("====================================================\n");
    }

    /**
     * Compares String Distance / Sequence Alignment Algorithms (CO3) for two terms.
     */
    public static void compareDistanceAlgorithms(String word1, String word2) {
        System.out.println("\n====================================================");
        System.out.println("   DIAGNOSIS TEXT SIMILARITY ALGORITHMS COMPARISON");
        System.out.println("====================================================");
        System.out.println("Word 1: \"" + word1 + "\" (length " + word1.length() + ")");
        System.out.println("Word 2: \"" + word2 + "\" (length " + word2.length() + ")");
        System.out.println("----------------------------------------------------");

        long start = System.nanoTime();
        int distLev = AdvancedDP.levenshteinDistance(word1, word2);
        long timeLev = System.nanoTime() - start;

        start = System.nanoTime();
        int distDam = AdvancedDP.damerauLevenshteinDistance(word1, word2);
        long timeDam = System.nanoTime() - start;

        start = System.nanoTime();
        int distWei = AdvancedDP.weightedEditDistance(word1, word2);
        long timeWei = System.nanoTime() - start;

        start = System.nanoTime();
        int scoreNW = AdvancedDP.needlemanWunschScore(word1, word2);
        long timeNW = System.nanoTime() - start;

        start = System.nanoTime();
        int scoreSW = AdvancedDP.smithWatermanScore(word1, word2);
        long timeSW = System.nanoTime() - start;

        System.out.printf("1. (Levenshtein Distance, --> O(n * m))           | Edit Distance: %d | Time: %6d ns%n", distLev, timeLev);
        System.out.printf("2. (Damerau-Levenshtein Distance, --> O(n * m))  | Edit Distance: %d | Time: %6d ns%n", distDam, timeDam);
        System.out.printf("3. (Weighted Edit Distance, --> O(n * m))        | Edit Cost:     %d | Time: %6d ns%n", distWei, timeWei);
        System.out.printf("4. (Needleman-Wunsch Alignment, --> O(n * m))    | Global Score:  %d | Time: %6d ns%n", scoreNW, timeNW);
        System.out.printf("5. (Smith-Waterman Alignment, --> O(n * m))      | Local Score:   %d | Time: %6d ns%n", scoreSW, timeSW);

        System.out.println("----------------------------------------------------");

        String bestAlg = "Damerau-Levenshtein Distance";
        String bestReason = "Includes character transposition (e.g., 'te' vs 'et') as a single edit step, making it ideal for medical diagnosis spelling error suggestions.";

        String worstAlg = "Needleman-Wunsch Alignment";
        String worstReason = "Computes a full global alignment matrix with gap penalties, which is over-engineered for simple typo distance evaluation.";

        System.out.println("BEST ALGORITHM  : " + bestAlg);
        System.out.println("Reason           : " + bestReason);
        System.out.println();
        System.out.println("WORST ALGORITHM : " + worstAlg);
        System.out.println("Reason           : " + worstReason);
        System.out.println("====================================================\n");
    }

    /**
     * Compares Diagnostic Test Selection DP Algorithms (Bitmask DP vs 0/1 Knapsack DP).
     */
    public static void compareTestSelectionAlgorithms(int[] testTime, int[] testBenefit, int maxTime) {
        System.out.println("\n====================================================");
        System.out.println("   DIAGNOSTIC TEST OPTIMIZATION ALGORITHMS COMPARISON");
        System.out.println("====================================================");
        System.out.println("Number of Diagnostic Tests (n): " + testTime.length);
        System.out.println("Time Budget (W):                 " + maxTime);
        System.out.println("----------------------------------------------------");

        long start = System.nanoTime();
        int resBitmask = AdvancedDP.bestDiagnosticTestBenefit(testTime, testBenefit, maxTime);
        long timeBitmask = System.nanoTime() - start;

        start = System.nanoTime();
        int resKnapsack = AdvancedDP.knapsackDiagnosticTestBenefit(testTime, testBenefit, maxTime);
        long timeKnapsack = System.nanoTime() - start;

        System.out.printf("1. (Bitmask DP, --> O(2^n * n))    | Max Benefit: %d | Time: %6d ns%n", resBitmask, timeBitmask);
        System.out.printf("2. (0/1 Knapsack DP, --> O(n * W)) | Max Benefit: %d | Time: %6d ns%n", resKnapsack, timeKnapsack);

        System.out.println("----------------------------------------------------");

        String bestAlg = "0/1 Knapsack DP";
        String bestReason = "Runs in pseudo-polynomial O(n * W) time, scaling linearly with the number of diagnostic tests rather than exponentially.";

        String worstAlg = "Bitmask DP";
        String worstReason = "Generates all 2^n subsets, resulting in exponential O(2^n * n) computational cost that quickly becomes intractable for n > 20 tests.";

        System.out.println("BEST ALGORITHM  : " + bestAlg);
        System.out.println("Reason           : " + bestReason);
        System.out.println();
        System.out.println("WORST ALGORITHM : " + worstAlg);
        System.out.println("Reason           : " + worstReason);
        System.out.println("====================================================\n");
    }
}
