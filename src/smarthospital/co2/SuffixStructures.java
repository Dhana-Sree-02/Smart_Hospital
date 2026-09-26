package smarthospital.co2;
import java.util.Arrays;
/**
* CO2 - Suffix Structures.
*
* Demonstrates:
* 1. Suffix Array
* 2. SA-IS-style interface (educational wrapper)
* 3. Kasai LCP Array
* 4. Suffix Array substring search
* 5. Suffix Automaton
*
* The code is intentionally written in a readable style for learning.
*/
public class SuffixStructures {
    private SuffixStructures() {
    }
    /**
    * Builds a suffix array by sorting all suffix strings.
    *
    * This is an easy-to-understand educational implementation.
    * Time complexity is higher than the advanced linear-time algorithms.
    */
    public static int[] buildSuffixArray(String text) {
        Integer[] suffixPositions = new Integer[text.length()];
        for (int i = 0; i < text.length(); i++) {
            suffixPositions[i] = i;
        }
        Arrays.sort(suffixPositions,
        (a, b) -> text.substring(a).compareTo(text.substring(b)));
        int[] suffixArray = new int[text.length()];
        for (int i = 0; i < text.length(); i++) {
            suffixArray[i] = suffixPositions[i];
        }
        return suffixArray;
    }
    /**
    * SA-IS entry point.
    *
    * This project keeps the method name separate so students can see where
    * the SA-IS topic belongs. The returned suffix array uses the readable
    * suffix-sorting implementation above.
    *
    * For a strict algorithm-analysis submission, replace this method with
    * a full SA-IS implementation.
    */
    public static int[] buildSuffixArrayUsingSAIS(String text) {
        return buildSuffixArray(text);
    }
    /**
    * Kasai algorithm for the LCP (Longest Common Prefix) array.
    *
    * lcp[i] = common prefix length of suffixArray[i] and suffixArray[i - 1].
    *
    * Time complexity: O(n)
    */
    public static int[] buildLcpArray(String text, int[] suffixArray) {
        int n = text.length();
        int[] rank = new int[n];
        int[] lcp = new int[n];
        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }
        int commonLength = 0;
        for (int suffixStart = 0; suffixStart < n; suffixStart++) {
            int currentRank = rank[suffixStart];
            if (currentRank == 0) {
                continue;
            }
            int previousSuffixStart = suffixArray[currentRank - 1];
            while (suffixStart + commonLength < n
            && previousSuffixStart + commonLength < n
            && text.charAt(suffixStart + commonLength)
            == text.charAt(previousSuffixStart + commonLength)) {
                commonLength++;
            }
            lcp[currentRank] = commonLength;
            if (commonLength > 0) {
                commonLength--;
            }
        }
        return lcp;
    }
    /**
    * Searches for a pattern using the suffix array.
    */
    public static void searchUsingSuffixArray(String text, String pattern) {
        System.out.println("\n--- Suffix Array Search ---");
        int[] suffixArray = buildSuffixArray(text);
        boolean found = false;
        for (int suffixStart : suffixArray) {
            if (text.startsWith(pattern, suffixStart)) {
                System.out.println("Pattern found at index: " + suffixStart);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Pattern not found.");
        }
    }
    /**
    * Simple Suffix Automaton.
    *
    * Each state represents a set of substrings.
    * This implementation is kept compact and readable.
    */
    public static class SuffixAutomaton {
        private static class State {
            int length;
            int link = -1;
            int[] next = new int[128];
            State() {
                Arrays.fill(next, -1);
            }
        }
        private State[] states;
        private int stateCount;
        private int last;
        public SuffixAutomaton(String text) {
            states = new State[Math.max(2, 2 * text.length())];
            for (int i = 0; i < states.length; i++) {
                states[i] = new State();
            }
            stateCount = 1;
            last = 0;
            for (char ch : text.toCharArray()) {
                extend(ch);
            }
        }
        private void extend(char character) {
            int current = stateCount++;
            states[current].length = states[last].length + 1;
            int previous = last;
            while (previous != -1
            && states[previous].next[character] == -1) {
                states[previous].next[character] = current;
                previous = states[previous].link;
            }
            if (previous == -1) {
                states[current].link = 0;
            } else {
                int existing = states[previous].next[character];
                if (states[previous].length + 1 == states[existing].length) {
                    states[current].link = existing;
                } else {
                    int clone = stateCount++;
                    states[clone].length = states[previous].length + 1;
                    states[clone].link = states[existing].link;
                    states[clone].next = states[existing].next.clone();
                    while (previous != -1
                    && states[previous].next[character] == existing) {
                        states[previous].next[character] = clone;
                        previous = states[previous].link;
                    }
                    states[existing].link = clone;
                    states[current].link = clone;
                }
            }
            last = current;
        }
        public boolean contains(String pattern) {
            int current = 0;
            for (char ch : pattern.toCharArray()) {
                if (ch >= 128 || states[current].next[ch] == -1) {
                    return false;
                }
                current = states[current].next[ch];
            }
            return true;
        }
    }
    public static boolean containsUsingSuffixArray(String text, String pattern) {
        if(pattern==null || pattern.isEmpty()) return true;
        int[] sa=buildSuffixArray(text);
        for(int start:sa) if(text.regionMatches(true,start,pattern,0,pattern.length())) return true;
        return false;
    }
}
