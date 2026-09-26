package smarthospital.co3;
import java.util.Arrays;
/**
* CO3 - Advanced Dynamic Programming.
*
* Algorithms included:
* 1. Levenshtein Edit Distance
* 2. Damerau-Levenshtein Distance
* 3. Weighted Edit Distance
* 4. Needleman-Wunsch Global Alignment
* 5. Smith-Waterman Local Alignment
* 6. Bitmask DP for selecting diagnostic tests
*/
public class AdvancedDP {
    private AdvancedDP() {
    }
    /**
    * Levenshtein distance.
    *
    * Operations:
    * - Insert
    * - Delete
    * - Replace
    *
    * dp[i][j] = minimum edits needed to convert
    * first i characters of word1 into first j characters of word2.
    */
    public static int levenshteinDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 0; i <= word1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= word2.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                int replaceCost = word1.charAt(i - 1) == word2.charAt(j - 1)
                ? 0 : 1;
                dp[i][j] = Math.min(
                Math.min(
                dp[i - 1][j] + 1, // Delete
                dp[i][j - 1] + 1), // Insert
                dp[i - 1][j - 1] + replaceCost);
                // Replace / Match
            }
        }
        return dp[word1.length()][word2.length()];
    }
    /**
    * Damerau-Levenshtein distance.
    *
    * Adds transposition of two adjacent characters.
    */
    public static int damerauLevenshteinDistance(String word1, String word2) {
        int rows = word1.length() + 1;
        int columns = word2.length() + 1;
        int[][] dp = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j < columns; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < columns; j++) {
                int replaceCost = word1.charAt(i - 1) == word2.charAt(j - 1)
                ? 0 : 1;
                dp[i][j] = Math.min(
                Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                dp[i - 1][j - 1] + replaceCost);
                if (i > 1 && j > 1
                && word1.charAt(i - 1) == word2.charAt(j - 2)
                && word1.charAt(i - 2) == word2.charAt(j - 1)) {
                    dp[i][j] = Math.min(
                    dp[i][j],
                    dp[i - 2][j - 2] + 1);
                }
            }
        }
        return dp[word1.length()][word2.length()];
    }
    /**
    * Weighted Edit Distance.
    *
    * Here:
    * insertion = 1
    * deletion  = 1
    * replacement = 2
    *
    * Change these constants when the hospital use case requires
    * different costs.
    */
    public static int weightedEditDistance(String word1, String word2) {
        final int INSERT_COST = 1;
        final int DELETE_COST = 1;
        final int REPLACE_COST = 2;
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 0; i <= word1.length(); i++) {
            dp[i][0] = i * DELETE_COST;
        }
        for (int j = 0; j <= word2.length(); j++) {
            dp[0][j] = j * INSERT_COST;
        }
        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                int replaceCost = word1.charAt(i - 1) == word2.charAt(j - 1)
                ? 0 : REPLACE_COST;
                dp[i][j] = Math.min(
                Math.min(
                dp[i - 1][j] + DELETE_COST,
                dp[i][j - 1] + INSERT_COST),
                dp[i - 1][j - 1] + replaceCost);
            }
        }
        return dp[word1.length()][word2.length()];
    }
    /**
    * Needleman-Wunsch: global sequence alignment.
    *
    * Every character participates in the alignment.
    */
    public static int needlemanWunschScore(String first, String second) {
        final int MATCH = 2;
        final int MISMATCH = -1;
        final int GAP = -2;
        int[][] dp = new int[first.length() + 1][second.length() + 1];
        for (int i = 1; i <= first.length(); i++) {
            dp[i][0] = i * GAP;
        }
        for (int j = 1; j <= second.length(); j++) {
            dp[0][j] = j * GAP;
        }
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                int diagonal = dp[i - 1][j - 1]
                + (first.charAt(i - 1) == second.charAt(j - 1)
                ? MATCH : MISMATCH);
                int delete = dp[i - 1][j] + GAP;
                int insert = dp[i][j - 1] + GAP;
                dp[i][j] = Math.max(diagonal, Math.max(delete, insert));
            }
        }
        return dp[first.length()][second.length()];
    }
    /**
    * Smith-Waterman: local sequence alignment.
    *
    * Unlike Needleman-Wunsch, negative scores are replaced by zero,
    * allowing the best matching region to start anywhere.
    */
    public static int smithWatermanScore(String first, String second) {
        final int MATCH = 2;
        final int MISMATCH = -1;
        final int GAP = -2;
        int[][] dp = new int[first.length() + 1][second.length() + 1];
        int bestScore = 0;
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                int diagonal = dp[i - 1][j - 1]
                + (first.charAt(i - 1) == second.charAt(j - 1)
                ? MATCH : MISMATCH);
                int delete = dp[i - 1][j] + GAP;
                int insert = dp[i][j - 1] + GAP;
                dp[i][j] = Math.max(
                0,
                Math.max(diagonal, Math.max(delete, insert)));
                bestScore = Math.max(bestScore, dp[i][j]);
            }
        }
        return bestScore;
    }
    /**
    * Bitmask DP example:
    * Select a subset of diagnostic tests without exceeding a time limit.
    *
    * testTime[i] = time required by test i
    * testBenefit[i] = usefulness score of test i
    * maxTime = total available time
    */
    public static int bestDiagnosticTestBenefit(
    int[] testTime, int[] testBenefit, int maxTime) {
        int numberOfTests = testTime.length;
        int numberOfSubsets = 1 << numberOfTests;
        int bestBenefit = 0;
        for (int mask = 0; mask < numberOfSubsets; mask++) {
            int totalTime = 0;
            int totalBenefit = 0;
            for (int test = 0; test < numberOfTests; test++) {
                // If the test bit is 1, this test is selected.
                if ((mask & (1 << test)) != 0) {
                    totalTime += testTime[test];
                    totalBenefit += testBenefit[test];
                }
            }
            if (totalTime <= maxTime) {
                bestBenefit = Math.max(bestBenefit, totalBenefit);
            }
        }
        return bestBenefit;
    }
}
