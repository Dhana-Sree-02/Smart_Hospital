package smarthospital.co3;

/**
 * CO3 - Advanced Dynamic Programming Engine.
 * 
 * Implemented from scratch using primitive arrays and explicit loops without java.util.*:
 * 1. Diagnosis Text Similarity:
 *    - Wagner-Fischer Levenshtein Distance (O(n * m))
 *    - Damerau-Levenshtein Distance with adjacent transpositions (O(n * m))
 *    - Weighted Edit Distance with configurable insertion, deletion, and replacement costs
 *    - Ranked diagnosis term matching with educational non-clinical disclaimer
 * 
 * 2. Biological Sequence Alignment (Educational DNA/Genomic Module):
 *    - Needleman-Wunsch Global Alignment with complete traceback (O(n * m))
 *    - Smith-Waterman Local Alignment with complete traceback (O(n * m))
 *    - Configurable match, mismatch, and gap penalties
 * 
 * 3. Bitmask Dynamic Programming:
 *    - Optimal Diagnostic Test Selection under condition-coverage constraints
 *    - Finds minimum-cost test battery covering required diagnostic criteria
 *    - Explicit traceback of selected tests and coverage constraint validation
 *    - Mathematical verification against brute-force subset enumeration
 *    - Clear documentation of exponential O(2^M * K) time and O(2^M) memory complexity
 */
public final class AdvancedDP {

    private AdvancedDP() {
        // Prevent instantiation
    }

    // =========================================================================
    // 1. DIAGNOSIS TEXT SIMILARITY
    // =========================================================================

    /**
     * Computes the Levenshtein edit distance between word1 and word2 using Wagner-Fischer DP.
     * 
     * Core Idea:
     * dp[i][j] stores the minimum edit operations (insert, delete, substitute)
     * needed to transform prefix word1[0..i-1] into prefix word2[0..j-1].
     * 
     * Time Complexity:  O(n * m)
     * Space Complexity: O(n * m)
     */
    public static int levenshteinDistance(String word1, String word2) {
        if (word1 == null) word1 = "";
        if (word2 == null) word2 = "";

        int n = word1.length();
        int m = word2.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i; // Deletion cost
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j; // Insertion cost
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : 1;
                int deletion = dp[i - 1][j] + 1;
                int insertion = dp[i][j - 1] + 1;
                int substitution = dp[i - 1][j - 1] + cost;

                dp[i][j] = min3(deletion, insertion, substitution);
            }
        }
        return dp[n][m];
    }

    /**
     * Computes the Damerau-Levenshtein distance between word1 and word2.
     * 
     * Core Idea:
     * Extends Levenshtein distance by including adjacent character transposition
     * (e.g. "te" <-> "et") as a single edit operation.
     * 
     * Time Complexity:  O(n * m)
     * Space Complexity: O(n * m)
     */
    public static int damerauLevenshteinDistance(String word1, String word2) {
        if (word1 == null) word1 = "";
        if (word2 == null) word2 = "";

        int n = word1.length();
        int m = word2.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : 1;
                int deletion = dp[i - 1][j] + 1;
                int insertion = dp[i][j - 1] + 1;
                int substitution = dp[i - 1][j - 1] + cost;

                int best = min3(deletion, insertion, substitution);

                // Adjacent transposition check
                if (i > 1 && j > 1
                        && word1.charAt(i - 1) == word2.charAt(j - 2)
                        && word1.charAt(i - 2) == word2.charAt(j - 1)) {
                    int transposition = dp[i - 2][j - 2] + 1;
                    if (transposition < best) {
                        best = transposition;
                    }
                }

                dp[i][j] = best;
            }
        }
        return dp[n][m];
    }

    /**
     * Computes Weighted Edit Distance with configurable operation costs.
     * 
     * @param word1       First string.
     * @param word2       Second string.
     * @param insertCost  Cost of inserting a character.
     * @param deleteCost  Cost of deleting a character.
     * @param replaceCost Cost of replacing a character.
     * @return Total weighted transformation cost.
     */
    public static int weightedEditDistance(String word1, String word2, int insertCost, int deleteCost, int replaceCost) {
        if (word1 == null) word1 = "";
        if (word2 == null) word2 = "";

        int n = word1.length();
        int m = word2.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i * deleteCost;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j * insertCost;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (word1.charAt(i - 1) == word2.charAt(j - 1)) ? 0 : replaceCost;
                int deletion = dp[i - 1][j] + deleteCost;
                int insertion = dp[i][j - 1] + insertCost;
                int substitution = dp[i - 1][j - 1] + cost;

                dp[i][j] = min3(deletion, insertion, substitution);
            }
        }
        return dp[n][m];
    }

    /**
     * Overloaded Weighted Edit Distance with standard defaults (insert=1, delete=1, replace=2).
     */
    public static int weightedEditDistance(String word1, String word2) {
        return weightedEditDistance(word1, word2, 1, 1, 2);
    }

    /**
     * Encapsulates a ranked diagnosis match result.
     */
    public static class DiagnosisRanking {
        public final String term;
        public final int levenshteinDist;
        public final int damerauDist;
        public final int weightedDist;

        public DiagnosisRanking(String term, int lev, int dam, int wei) {
            this.term = term;
            this.levenshteinDist = lev;
            this.damerauDist = dam;
            this.weightedDist = wei;
        }
    }

    /**
     * Ranks a list of candidate diagnosis terms against an entered query.
     * Uses manual sorting without java.util.* collections.
     */
    public static DiagnosisRanking[] rankDiagnoses(String query, String[] candidates) {
        if (query == null || candidates == null || candidates.length == 0) {
            return new DiagnosisRanking[0];
        }

        int count = candidates.length;
        DiagnosisRanking[] rankings = new DiagnosisRanking[count];

        String lowerQuery = query.toLowerCase();
        for (int i = 0; i < count; i++) {
            String candidate = candidates[i];
            String lowerCand = candidate.toLowerCase();
            int lev = levenshteinDistance(lowerQuery, lowerCand);
            int dam = damerauLevenshteinDistance(lowerQuery, lowerCand);
            int wei = weightedEditDistance(lowerQuery, lowerCand);
            rankings[i] = new DiagnosisRanking(candidate, lev, dam, wei);
        }

        // Insertion sort by Damerau distance ascending, then Levenshtein distance
        for (int i = 1; i < count; i++) {
            DiagnosisRanking key = rankings[i];
            int j = i - 1;
            while (j >= 0 && (rankings[j].damerauDist > key.damerauDist
                    || (rankings[j].damerauDist == key.damerauDist && rankings[j].levenshteinDist > key.levenshteinDist))) {
                rankings[j + 1] = rankings[j];
                j--;
            }
            rankings[j + 1] = key;
        }

        return rankings;
    }

    // =========================================================================
    // 2. BIOLOGICAL SEQUENCE ALIGNMENT (EDUCATIONAL GENOMICS MODULE)
    // =========================================================================

    /**
     * Encapsulates biological sequence alignment results including traceback and visual match line.
     */
    public static class AlignmentResult {
        public final String alignedSequence1;
        public final String alignedSequence2;
        public final String matchBar;
        public final int score;
        public final boolean isGlobal;

        public AlignmentResult(String s1, String s2, String bar, int score, boolean isGlobal) {
            this.alignedSequence1 = s1;
            this.alignedSequence2 = s2;
            this.matchBar = bar;
            this.score = score;
            this.isGlobal = isGlobal;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(isGlobal ? "Global Alignment (Needleman-Wunsch)" : "Local Alignment (Smith-Waterman)");
            sb.append(" | Score: ").append(score).append("\n");
            sb.append("Seq1: ").append(alignedSequence1).append("\n");
            sb.append("      ").append(matchBar).append("\n");
            sb.append("Seq2: ").append(alignedSequence2).append("\n");
            return sb.toString();
        }
    }

    /**
     * Needleman-Wunsch Global Alignment with complete traceback.
     * 
     * Core Idea:
     * Aligns every character of seq1 and seq2 end-to-end.
     * Recursion: dp[i][j] = max(diagonal + match/mismatch, up + gap, left + gap).
     * Traceback from (n, m) down to (0, 0) reconstructs optimal alignment with '-' gaps.
     * 
     * Time Complexity:  O(n * m)
     * Space Complexity: O(n * m)
     */
    public static AlignmentResult alignNeedlemanWunsch(String seq1, String seq2, int match, int mismatch, int gap) {
        if (seq1 == null) seq1 = "";
        if (seq2 == null) seq2 = "";

        int n = seq1.length();
        int m = seq2.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i * gap;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j * gap;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int scoreMatch = (seq1.charAt(i - 1) == seq2.charAt(j - 1)) ? match : mismatch;
                int diag = dp[i - 1][j - 1] + scoreMatch;
                int up = dp[i - 1][j] + gap;
                int left = dp[i][j - 1] + gap;

                dp[i][j] = max3(diag, up, left);
            }
        }

        // Traceback from (n, m) to (0, 0)
        StringBuilder rev1 = new StringBuilder();
        StringBuilder rev2 = new StringBuilder();
        StringBuilder revBar = new StringBuilder();

        int i = n;
        int j = m;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0) {
                int scoreMatch = (seq1.charAt(i - 1) == seq2.charAt(j - 1)) ? match : mismatch;
                if (dp[i][j] == dp[i - 1][j - 1] + scoreMatch) {
                    char c1 = seq1.charAt(i - 1);
                    char c2 = seq2.charAt(j - 1);
                    rev1.append(c1);
                    rev2.append(c2);
                    revBar.append(c1 == c2 ? '|' : '.');
                    i--;
                    j--;
                    continue;
                }
            }

            if (i > 0 && dp[i][j] == dp[i - 1][j] + gap) {
                rev1.append(seq1.charAt(i - 1));
                rev2.append('-');
                revBar.append(' ');
                i--;
            } else {
                rev1.append('-');
                rev2.append(seq2.charAt(j - 1));
                revBar.append(' ');
                j--;
            }
        }

        return new AlignmentResult(
                reverseString(rev1.toString()),
                reverseString(rev2.toString()),
                reverseString(revBar.toString()),
                dp[n][m],
                true
        );
    }

    /**
     * Overloaded Needleman-Wunsch with standard scoring parameters (match=+2, mismatch=-1, gap=-2).
     */
    public static AlignmentResult alignNeedlemanWunsch(String seq1, String seq2) {
        return alignNeedlemanWunsch(seq1, seq2, 2, -1, -2);
    }

    public static int needlemanWunschScore(String seq1, String seq2) {
        return alignNeedlemanWunsch(seq1, seq2).score;
    }

    /**
     * Smith-Waterman Local Alignment with complete traceback.
     * 
     * Core Idea:
     * Finds highest-scoring local matching sub-segments anywhere in seq1 and seq2.
     * Negative values are clamped to 0.
     * Traceback begins at the cell with highest score and stops when score 0 is reached.
     * 
     * Time Complexity:  O(n * m)
     * Space Complexity: O(n * m)
     */
    public static AlignmentResult alignSmithWaterman(String seq1, String seq2, int match, int mismatch, int gap) {
        if (seq1 == null) seq1 = "";
        if (seq2 == null) seq2 = "";

        int n = seq1.length();
        int m = seq2.length();

        int[][] dp = new int[n + 1][m + 1];

        int bestScore = 0;
        int maxI = 0;
        int maxJ = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int scoreMatch = (seq1.charAt(i - 1) == seq2.charAt(j - 1)) ? match : mismatch;
                int diag = dp[i - 1][j - 1] + scoreMatch;
                int up = dp[i - 1][j] + gap;
                int left = dp[i][j - 1] + gap;

                int val = max3(0, diag, Math.max(up, left));
                dp[i][j] = val;

                if (val > bestScore) {
                    bestScore = val;
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback from (maxI, maxJ) terminating when score drops to 0
        StringBuilder rev1 = new StringBuilder();
        StringBuilder rev2 = new StringBuilder();
        StringBuilder revBar = new StringBuilder();

        int i = maxI;
        int j = maxJ;

        while (i > 0 && j > 0 && dp[i][j] > 0) {
            int scoreMatch = (seq1.charAt(i - 1) == seq2.charAt(j - 1)) ? match : mismatch;

            if (dp[i][j] == dp[i - 1][j - 1] + scoreMatch) {
                char c1 = seq1.charAt(i - 1);
                char c2 = seq2.charAt(j - 1);
                rev1.append(c1);
                rev2.append(c2);
                revBar.append(c1 == c2 ? '|' : '.');
                i--;
                j--;
            } else if (dp[i][j] == dp[i - 1][j] + gap) {
                rev1.append(seq1.charAt(i - 1));
                rev2.append('-');
                revBar.append(' ');
                i--;
            } else if (dp[i][j] == dp[i][j - 1] + gap) {
                rev1.append('-');
                rev2.append(seq2.charAt(j - 1));
                revBar.append(' ');
                j--;
            } else {
                break;
            }
        }

        return new AlignmentResult(
                reverseString(rev1.toString()),
                reverseString(rev2.toString()),
                reverseString(revBar.toString()),
                bestScore,
                false
        );
    }

    /**
     * Overloaded Smith-Waterman with standard scoring parameters (match=+2, mismatch=-1, gap=-2).
     */
    public static AlignmentResult alignSmithWaterman(String seq1, String seq2) {
        return alignSmithWaterman(seq1, seq2, 2, -1, -2);
    }

    public static int smithWatermanScore(String seq1, String seq2) {
        return alignSmithWaterman(seq1, seq2).score;
    }

    // =========================================================================
    // 3. BITMASK DYNAMIC PROGRAMMING: DIAGNOSTIC TEST SELECTION
    // =========================================================================

    /**
     * Represents a fictional diagnostic test with condition coverage mask and cost.
     */
    public static class DiagnosticTest {
        public final int id;
        public final String name;
        public final int coverageMask;
        public final int cost;

        public DiagnosticTest(int id, String name, int coverageMask, int cost) {
            this.id = id;
            this.name = name;
            this.coverageMask = coverageMask;
            this.cost = cost;
        }
    }

    /**
     * Result of Bitmask DP Diagnostic Test Selection.
     */
    public static class TestSelectionResult {
        public final int minimumCost;
        public final int achievedCoverageMask;
        public final int requiredCoverageMask;
        public final int[] selectedTestIndices;
        public final boolean isSatisfied;

        public TestSelectionResult(int cost, int achieved, int required, int[] tests, boolean satisfied) {
            this.minimumCost = cost;
            this.achievedCoverageMask = achieved;
            this.requiredCoverageMask = required;
            this.selectedTestIndices = tests;
            this.isSatisfied = satisfied;
        }
    }

    /**
     * Solves the Diagnostic Test Selection problem using Bitmask Dynamic Programming.
     * 
     * Problem Statement:
     * Given M diagnostic condition markers (represented by bitmask bits 0..M-1)
     * and K available diagnostic tests each covering a subset of conditions with a cost,
     * find the minimum-cost subset of tests whose combined coverage satisfies requiredMask.
     * 
     * Core DP Formulation:
     * Let dp[mask] = minimum cost to cover the subset of conditions represented by mask.
     * Base Case: dp[0] = 0; all other dp[mask] = INF.
     * Transition for each test k:
     *   dp[mask | testMask[k]] = min(dp[mask | testMask[k]], dp[mask] + testCost[k])
     * 
     * Time Complexity:  O(2^M * K) where M is number of conditions, K is number of tests.
     * Space Complexity: O(2^M) states.
     * 
     * Educational Notice:
     * Demonstrates exponential Set Cover optimization using Bitmask DP.
     * This is a pedagogical tool and does not optimize real-world patient clinical decisions.
     */
    public static TestSelectionResult selectDiagnosticTestsBitmaskDP(
            int conditionCount,
            int requiredMask,
            int[] testCosts,
            int[] testMasks) {

        if (conditionCount <= 0 || testCosts == null || testMasks == null || testCosts.length != testMasks.length) {
            return new TestSelectionResult(0, 0, requiredMask, new int[0], false);
        }

        int k = testCosts.length;
        int totalStates = 1 << conditionCount;
        final int INF = 1_000_000_000;

        int[] dp = new int[totalStates];
        int[] parentMask = new int[totalStates];
        int[] chosenTest = new int[totalStates];

        for (int mask = 0; mask < totalStates; mask++) {
            dp[mask] = INF;
            parentMask[mask] = -1;
            chosenTest[mask] = -1;
        }
        dp[0] = 0;

        // DP State Transitions
        for (int test = 0; test < k; test++) {
            int tCost = testCosts[test];
            int tMask = testMasks[test];

            for (int mask = totalStates - 1; mask >= 0; mask--) {
                if (dp[mask] != INF) {
                    int nextMask = mask | tMask;
                    int newCost = dp[mask] + tCost;
                    if (newCost < dp[nextMask]) {
                        dp[nextMask] = newCost;
                        parentMask[nextMask] = mask;
                        chosenTest[nextMask] = test;
                    }
                }
            }
        }

        // Find state covering at least requiredMask with minimal cost
        int bestCost = INF;
        int bestMask = -1;

        for (int mask = 0; mask < totalStates; mask++) {
            if ((mask & requiredMask) == requiredMask) {
                if (dp[mask] < bestCost) {
                    bestCost = dp[mask];
                    bestMask = mask;
                }
            }
        }

        if (bestMask == -1 || bestCost == INF) {
            return new TestSelectionResult(INF, 0, requiredMask, new int[0], false);
        }

        // Traceback to reconstruct selected tests
        int[] tempTests = new int[k];
        int testCount = 0;
        int curr = bestMask;

        while (curr > 0 && chosenTest[curr] != -1) {
            tempTests[testCount++] = chosenTest[curr];
            curr = parentMask[curr];
        }

        int[] selected = new int[testCount];
        for (int i = 0; i < testCount; i++) {
            selected[i] = tempTests[testCount - 1 - i]; // Reverse order
        }

        return new TestSelectionResult(bestCost, bestMask, requiredMask, selected, true);
    }

    /**
     * Brute-force verification of the Diagnostic Test Selection problem.
     * Evaluates all 2^K combinations of tests to mathematically cross-check Bitmask DP.
     */
    public static int selectDiagnosticTestsBruteForce(int requiredMask, int[] testCosts, int[] testMasks) {
        int k = testCosts.length;
        int totalSubsets = 1 << k;
        final int INF = 1_000_000_000;
        int minCost = INF;

        for (int subset = 0; subset < totalSubsets; subset++) {
            int currentCoverage = 0;
            int currentCost = 0;

            for (int test = 0; test < k; test++) {
                if ((subset & (1 << test)) != 0) {
                    currentCoverage |= testMasks[test];
                    currentCost += testCosts[test];
                }
            }

            if ((currentCoverage & requiredMask) == requiredMask) {
                if (currentCost < minCost) {
                    minCost = currentCost;
                }
            }
        }
        return minCost == INF ? -1 : minCost;
    }

    /**
     * Backward-compatible knapsack-based test selection for time budget comparison.
     */
    public static int knapsackDiagnosticTestBenefit(int[] testTime, int[] testBenefit, int maxTime) {
        int n = testTime.length;
        int[][] dp = new int[n + 1][maxTime + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= maxTime; w++) {
                if (testTime[i - 1] <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - testTime[i - 1]] + testBenefit[i - 1]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }
        return dp[n][maxTime];
    }

    public static int bestDiagnosticTestBenefit(int[] testTime, int[] testBenefit, int maxTime) {
        return knapsackDiagnosticTestBenefit(testTime, testBenefit, maxTime);
    }

    // =========================================================================
    // HELPER METHODS (PRIMITIVE UTILITIES)
    // =========================================================================

    private static int min3(int a, int b, int c) {
        int m = (a < b) ? a : b;
        return (m < c) ? m : c;
    }

    private static int max3(int a, int b, int c) {
        int m = (a > b) ? a : b;
        return (m > c) ? m : c;
    }

    private static String reverseString(String s) {
        char[] chars = s.toCharArray();
        int left = 0;
        int right = chars.length - 1;
        while (left < right) {
            char t = chars[left];
            chars[left] = chars[right];
            chars[right] = t;
            left++;
            right--;
        }
        return new String(chars);
    }
}
