package smarthospital.co2;

/**
 * CO2 - Suffix Structures Engine.
 * 
 * Implemented from scratch using primitive arrays and explicit loops without java.util.*:
 * 1. Suffix Array Construction via Prefix Doubling (O(n log^2 n))
 * 2. Binary Search on Suffix Array for Substring Search (O(m log n))
 * 3. Kasai's Algorithm for Longest Common Prefix (LCP) Array (O(n))
 * 4. Repeated-Prefix / Longest Repeated Substring Analysis using LCP
 * 5. Linear Suffix Automaton (SAM) for fast O(m) substring verification
 * 
 * Educational Notes:
 * - A Suffix Array contains sorted starting positions of all suffixes of a text.
 * - Suffix-array construction here uses genuine Prefix Doubling with an in-place manual merge sort.
 * - This implementation achieves O(n log^2 n) time and O(n) space without calling library sort functions.
 * - Suffix searching uses true binary search (lower and upper bounds) to retrieve all occurrences in O(m log n).
 * - LCP array analyzes lexical character repetition; it does not perform semantic medical analysis.
 */
public final class SuffixStructures {

    private SuffixStructures() {
        // Prevent instantiation
    }

    // =========================================================================
    // 1. SUFFIX ARRAY CONSTRUCTION (PREFIX DOUBLING - O(n log^2 n))
    // =========================================================================

    /**
     * Builds the Suffix Array using the Prefix Doubling technique.
     * 
     * Core Idea:
     * Sorts suffixes based on prefixes of doubling lengths: 1, 2, 4, 8, ...
     * In step k, suffix i is identified by the pair (rank[i], rank[i + k]).
     * Sorting is performed using an explicit manual Merge Sort over primitive arrays.
     * 
     * Time Complexity:  O(n log^2 n)
     * Space Complexity: O(n) auxiliary
     * 
     * @param text The input text string.
     * @return Suffix Array: sa[i] is the starting index of the i-th lexicographically smallest suffix.
     */
    public static int[] buildSuffixArray(String text) {
        if (text == null) {
            return new int[0];
        }
        int n = text.length();
        if (n == 0) {
            return new int[0];
        }
        if (n == 1) {
            return new int[]{0};
        }

        int[] sa = new int[n];
        int[] rank = new int[n];
        int[] temp = new int[n];

        // Step 0: Initial ranking based on single character code points
        for (int i = 0; i < n; i++) {
            sa[i] = i;
            rank[i] = (int) text.charAt(i);
        }

        // Iterative prefix doubling: k = 1, 2, 4, 8, ...
        for (int k = 1; k < n; k *= 2) {
            mergeSortSuffixes(sa, rank, temp, 0, n - 1, k, n);

            int[] newRank = new int[n];
            newRank[sa[0]] = 0;

            for (int i = 1; i < n; i++) {
                int prev = sa[i - 1];
                int curr = sa[i];

                boolean isSameFirstRank = (rank[curr] == rank[prev]);
                int currSecondRank = (curr + k < n) ? rank[curr + k] : -1;
                int prevSecondRank = (prev + k < n) ? rank[prev + k] : -1;
                boolean isSameSecondRank = (currSecondRank == prevSecondRank);

                if (isSameFirstRank && isSameSecondRank) {
                    newRank[curr] = newRank[prev];
                } else {
                    newRank[curr] = newRank[prev] + 1;
                }
            }

            rank = newRank;

            // Early exit optimization: all suffixes have distinct ranks
            if (rank[sa[n - 1]] == n - 1) {
                break;
            }
        }

        return sa;
    }

    /**
     * Manual Merge Sort on suffix indices based on rank pairs (rank[i], rank[i + k]).
     */
    private static void mergeSortSuffixes(int[] sa, int[] rank, int[] temp, int left, int right, int k, int n) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSortSuffixes(sa, rank, temp, left, mid, k, n);
        mergeSortSuffixes(sa, rank, temp, mid + 1, right, k, n);

        // Merge two sorted halves
        int i = left;
        int j = mid + 1;
        int t = left;

        while (i <= mid && j <= right) {
            int cmp = compareSuffixRanks(sa[i], sa[j], rank, k, n);
            if (cmp <= 0) {
                temp[t++] = sa[i++];
            } else {
                temp[t++] = sa[j++];
            }
        }
        while (i <= mid) {
            temp[t++] = sa[i++];
        }
        while (j <= right) {
            temp[t++] = sa[j++];
        }
        for (int index = left; index <= right; index++) {
            sa[index] = temp[index];
        }
    }

    /**
     * Compares two suffixes a and b based on their 2k-length rank pairs.
     */
    private static int compareSuffixRanks(int a, int b, int[] rank, int k, int n) {
        if (rank[a] != rank[b]) {
            return rank[a] - rank[b];
        }
        int rankA2 = (a + k < n) ? rank[a + k] : -1;
        int rankB2 = (b + k < n) ? rank[b + k] : -1;
        return rankA2 - rankB2;
    }

    // =========================================================================
    // 2. BINARY-SEARCH SUBSTRING MATCHING (O(m log n))
    // =========================================================================

    /**
     * Compares the prefix of suffix starting at suffixStart in text with pattern.
     * 
     * @return < 0 if suffix prefix < pattern,
     *         0 if suffix starts with pattern,
     *         > 0 if suffix prefix > pattern.
     */
    public static int comparePrefix(String text, int suffixStart, String pattern) {
        int textLength = text.length();
        int patternLength = pattern.length();

        for (int j = 0; j < patternLength; j++) {
            if (suffixStart + j >= textLength) {
                return -1; // Suffix ended earlier, so it is smaller
            }
            char cText = text.charAt(suffixStart + j);
            char cPat = pattern.charAt(j);
            if (cText != cPat) {
                return (int) cText - (int) cPat;
            }
        }
        return 0; // Exact prefix match
    }

    /**
     * Finds all start positions of pattern in text using Binary Search over the Suffix Array.
     * 
     * Core Idea:
     * In a sorted Suffix Array, all suffixes having pattern as a prefix reside in a
     * contiguous block [firstIndex, lastIndex].
     * Uses binary search to find the lower bound (firstIndex) and upper bound (lastIndex).
     * 
     * Time Complexity:  O(m * log n)
     * Space Complexity: O(1) auxiliary (excluding result array)
     * 
     * @param text        The input text string.
     * @param suffixArray The precomputed Suffix Array of text.
     * @param pattern     The pattern string to locate.
     * @return Array of all matching starting indices in text.
     */
    public static int[] searchWithSuffixArray(String text, int[] suffixArray, String pattern) {
        if (text == null || suffixArray == null || pattern == null) {
            return new int[0];
        }
        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n || suffixArray.length != n) {
            return new int[0];
        }

        // 1. Find lower bound (first index where suffix starts with pattern)
        int low = 0;
        int high = n - 1;
        int firstMatch = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = comparePrefix(text, suffixArray[mid], pattern);
            if (cmp >= 0) {
                if (cmp == 0) {
                    firstMatch = mid;
                }
                high = mid - 1; // Seek leftmost match
            } else {
                low = mid + 1;
            }
        }

        if (firstMatch == -1) {
            return new int[0];
        }

        // 2. Find upper bound (last index where suffix starts with pattern)
        low = firstMatch;
        high = n - 1;
        int lastMatch = firstMatch;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = comparePrefix(text, suffixArray[mid], pattern);
            if (cmp <= 0) {
                if (cmp == 0) {
                    lastMatch = mid;
                }
                low = mid + 1; // Seek rightmost match
            } else {
                high = mid - 1;
            }
        }

        int count = lastMatch - firstMatch + 1;
        int[] positions = new int[count];
        for (int i = 0; i < count; i++) {
            positions[i] = suffixArray[firstMatch + i];
        }
        return positions;
    }

    /**
     * Checks if text contains pattern using Suffix Array binary search.
     */
    public static boolean containsUsingSuffixArrayBinarySearch(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (text == null || pattern.length() > text.length()) {
            return false;
        }
        int[] sa = buildSuffixArray(text);
        int[] matches = searchWithSuffixArray(text, sa, pattern);
        return matches.length > 0;
    }

    // =========================================================================
    // 3. KASAI'S LCP ARRAY ALGORITHM (O(n))
    // =========================================================================

    /**
     * Computes the Longest Common Prefix (LCP) array using Kasai's Algorithm.
     * 
     * Core Idea:
     * lcp[i] = length of the longest common prefix between suffixArray[i] and suffixArray[i - 1].
     * Kasai proved that when moving from suffix i to suffix i + 1 in text order,
     * the LCP decreases by at most 1 (commonLength >= previousLength - 1).
     * This avoids re-scanning characters from scratch, achieving strictly O(n) execution.
     * 
     * Time Complexity:  O(n)
     * Space Complexity: O(n) for the rank and LCP arrays
     * 
     * @param text        The input text string.
     * @param suffixArray The precomputed Suffix Array.
     * @return The precomputed LCP array.
     */
    public static int[] buildLcpArray(String text, int[] suffixArray) {
        if (text == null || suffixArray == null) {
            return new int[0];
        }
        int n = text.length();
        if (n == 0 || suffixArray.length != n) {
            return new int[0];
        }

        int[] rank = new int[n];
        int[] lcp = new int[n];

        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }

        int commonLength = 0;

        for (int suffixStart = 0; suffixStart < n; suffixStart++) {
            int currentRank = rank[suffixStart];
            if (currentRank == 0) {
                lcp[0] = 0;
                continue;
            }

            int previousSuffixStart = suffixArray[currentRank - 1];

            while (suffixStart + commonLength < n
                    && previousSuffixStart + commonLength < n
                    && text.charAt(suffixStart + commonLength) == text.charAt(previousSuffixStart + commonLength)) {
                commonLength++;
            }

            lcp[currentRank] = commonLength;

            if (commonLength > 0) {
                commonLength--;
            }
        }

        return lcp;
    }

    // =========================================================================
    // 4. REPEATED-PREFIX ANALYSIS USING LCP
    // =========================================================================

    /**
     * Result of repeated-prefix analysis.
     */
    public static class RepeatedSubstringResult {
        public final String substring;
        public final int length;
        public final int firstPosition;
        public final int secondPosition;

        public RepeatedSubstringResult(String substring, int length, int firstPos, int secondPos) {
            this.substring = substring;
            this.length = length;
            this.firstPosition = firstPos;
            this.secondPosition = secondPos;
        }

        @Override
        public String toString() {
            if (length == 0) {
                return "No repeated substring found.";
            }
            return String.format("Longest Repeated Substring: \"%s\" (length %d) at indices %d and %d",
                    substring, length, firstPosition, secondPosition);
        }
    }

    /**
     * Finds the Longest Repeated Substring (LRS) in text using the Suffix Array and LCP Array.
     * 
     * Educational Note:
     * This demonstrates lexical character repetition using LCP.
     * It does not perform semantic medical analysis or clinical reasoning.
     */
    public static RepeatedSubstringResult findLongestRepeatedSubstring(String text) {
        if (text == null || text.length() < 2) {
            return new RepeatedSubstringResult("", 0, -1, -1);
        }

        int[] sa = buildSuffixArray(text);
        int[] lcp = buildLcpArray(text, sa);

        int maxLcp = 0;
        int maxIndex = -1;

        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > maxLcp) {
                maxLcp = lcp[i];
                maxIndex = i;
            }
        }

        if (maxLcp == 0 || maxIndex == -1) {
            return new RepeatedSubstringResult("", 0, -1, -1);
        }

        int pos1 = sa[maxIndex - 1];
        int pos2 = sa[maxIndex];
        String repeated = text.substring(pos2, pos2 + maxLcp);

        return new RepeatedSubstringResult(repeated, maxLcp, Math.min(pos1, pos2), Math.max(pos1, pos2));
    }

    // =========================================================================
    // 5. SUFFIX AUTOMATON (SAM)
    // =========================================================================

    /**
     * Deterministic Suffix Automaton (SAM) for O(m) substring verification.
     * Implemented cleanly using primitive arrays without external collections.
     */
    public static class SuffixAutomaton {
        private static final int ALPHABET_SIZE = 128;

        private final int[] length;
        private final int[] link;
        private final int[][] next;
        private int stateCount;
        private int lastState;

        public SuffixAutomaton(String text) {
            int maxStates = Math.max(2, 2 * text.length());
            length = new int[maxStates];
            link = new int[maxStates];
            next = new int[maxStates][ALPHABET_SIZE];

            for (int i = 0; i < maxStates; i++) {
                link[i] = -1;
                for (int c = 0; c < ALPHABET_SIZE; c++) {
                    next[i][c] = -1;
                }
            }

            length[0] = 0;
            link[0] = -1;
            stateCount = 1;
            lastState = 0;

            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                extend(ch);
            }
        }

        private void extend(char ch) {
            int c = (int) ch;
            if (c >= ALPHABET_SIZE) {
                return;
            }

            int curr = stateCount++;
            length[curr] = length[lastState] + 1;

            int p = lastState;
            while (p != -1 && next[p][c] == -1) {
                next[p][c] = curr;
                p = link[p];
            }

            if (p == -1) {
                link[curr] = 0;
            } else {
                int q = next[p][c];
                if (length[p] + 1 == length[q]) {
                    link[curr] = q;
                } else {
                    int clone = stateCount++;
                    length[clone] = length[p] + 1;
                    link[clone] = link[q];

                    for (int charIdx = 0; charIdx < ALPHABET_SIZE; charIdx++) {
                        next[clone][charIdx] = next[q][charIdx];
                    }

                    while (p != -1 && next[p][c] == q) {
                        next[p][c] = clone;
                        p = link[p];
                    }

                    link[q] = clone;
                    link[curr] = clone;
                }
            }
            lastState = curr;
        }

        /**
         * Checks if pattern is a substring of the indexed text in O(m) time.
         */
        public boolean contains(String pattern) {
            if (pattern == null) {
                return false;
            }
            int curr = 0;
            for (int i = 0; i < pattern.length(); i++) {
                int c = (int) pattern.charAt(i);
                if (c >= ALPHABET_SIZE || next[curr][c] == -1) {
                    return false;
                }
                curr = next[curr][c];
            }
            return true;
        }

        public int getStateCount() {
            return stateCount;
        }
    }

    /**
     * Checks if text contains pattern using Suffix Automaton.
     */
    public static boolean containsUsingSuffixAutomaton(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (text == null || pattern.length() > text.length()) {
            return false;
        }
        SuffixAutomaton automaton = new SuffixAutomaton(text);
        return automaton.contains(pattern);
    }
}
