package smarthospital.co1;

/**
 * CO1 - String Matching Algorithms Engine.
 * 
 * Implemented from scratch using primitive arrays and explicit loops without java.util.*:
 * 1. Naive String Matching (O(n * m))
 * 2. Knuth-Morris-Pratt Algorithm (O(n + m)) with Failure / LPS Function
 * 3. Z-Algorithm (O(n + m)) with Z-Box and Z-Array Construction
 * 4. Rabin-Karp Algorithm (O(n + m) average, O(n * m) worst) with Polynomial Rolling Hash
 * 
 * Features:
 * - Returns all matching start indices as primitive int[].
 * - Handles empty patterns, patterns longer than text, and overlapping matches.
 * - Exact character-by-character verification on Rabin-Karp hash matches to avoid collisions.
 * - Detailed educational documentation and step-by-step logic.
 */
public final class StringAlgorithms {

    private StringAlgorithms() {
        // Prevent instantiation of static algorithm engine
    }

    // =========================================================================
    // 1. NAIVE STRING MATCHING
    // =========================================================================

    /**
     * Searches for all occurrences of pattern in text using Naive String Matching.
     * 
     * Core Idea:
     * Slides a window of length m across text from index 0 to (n - m).
     * At each start position, tests character equality until a mismatch or full match occurs.
     * 
     * Time Complexity:  O((n - m + 1) * m) -> O(n * m)
     * Space Complexity: O(1) auxiliary (excluding result array)
     * 
     * @param text    The input text string.
     * @param pattern The pattern string to locate.
     * @return Array of all 0-based starting indices where pattern occurs.
     */
    public static int[] searchNaive(String text, String pattern) {
        if (text == null || pattern == null) {
            return new int[0];
        }
        int textLength = text.length();
        int patternLength = pattern.length();

        if (patternLength == 0 || patternLength > textLength) {
            return new int[0];
        }

        int capacity = 8;
        int[] matches = new int[capacity];
        int matchCount = 0;

        int maxStart = textLength - patternLength;
        for (int start = 0; start <= maxStart; start++) {
            int j = 0;
            while (j < patternLength && text.charAt(start + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == patternLength) {
                if (matchCount == matches.length) {
                    matches = expandArray(matches);
                }
                matches[matchCount++] = start;
            }
        }

        return trimArray(matches, matchCount);
    }

    /**
     * Checks if text contains pattern using Naive search.
     */
    public static boolean containsNaive(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (text == null || pattern.length() > text.length()) {
            return false;
        }
        int textLength = text.length();
        int patternLength = pattern.length();
        int maxStart = textLength - patternLength;

        for (int start = 0; start <= maxStart; start++) {
            int j = 0;
            while (j < patternLength && text.charAt(start + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == patternLength) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // 2. KNUTH-MORRIS-PRATT (KMP) ALGORITHM
    // =========================================================================

    /**
     * Computes the Longest Proper Prefix which is also a Suffix (LPS) array.
     * 
     * Core Idea:
     * lps[i] stores the length of the longest proper prefix of pattern[0..i]
     * that is identical to a suffix of pattern[0..i].
     * 
     * Time Complexity:  O(m)
     * Space Complexity: O(m)
     * 
     * @param pattern The pattern string.
     * @return The precomputed LPS array.
     */
    public static int[] computeLpsArray(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }
        int patternLength = pattern.length();
        int[] lps = new int[patternLength];
        lps[0] = 0;

        int lengthOfPreviousLps = 0;
        int i = 1;

        while (i < patternLength) {
            if (pattern.charAt(i) == pattern.charAt(lengthOfPreviousLps)) {
                lengthOfPreviousLps++;
                lps[i] = lengthOfPreviousLps;
                i++;
            } else {
                if (lengthOfPreviousLps != 0) {
                    lengthOfPreviousLps = lps[lengthOfPreviousLps - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Searches for all occurrences of pattern in text using KMP.
     * 
     * Core Idea:
     * Never backtracks the text index i. Upon mismatch, uses lps[j - 1] to resume
     * comparison at the next viable character of pattern, skipping already matched prefixes.
     * 
     * Time Complexity:  O(n + m)
     * Space Complexity: O(m) for the LPS table
     * 
     * @param text    The input text string.
     * @param pattern The pattern string to locate.
     * @return Array of all 0-based starting indices where pattern occurs.
     */
    public static int[] searchKMP(String text, String pattern) {
        if (text == null || pattern == null) {
            return new int[0];
        }
        int textLength = text.length();
        int patternLength = pattern.length();

        if (patternLength == 0 || patternLength > textLength) {
            return new int[0];
        }

        int[] lps = computeLpsArray(pattern);
        int[] matches = new int[8];
        int matchCount = 0;

        int textIndex = 0;
        int patternIndex = 0;

        while (textIndex < textLength) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;

                if (patternIndex == patternLength) {
                    if (matchCount == matches.length) {
                        matches = expandArray(matches);
                    }
                    matches[matchCount++] = textIndex - patternLength;
                    patternIndex = lps[patternIndex - 1];
                }
            } else {
                if (patternIndex != 0) {
                    patternIndex = lps[patternIndex - 1];
                } else {
                    textIndex++;
                }
            }
        }

        return trimArray(matches, matchCount);
    }

    /**
     * Case-insensitive check if text contains pattern using KMP.
     */
    public static boolean containsKMP(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (text == null || pattern.length() > text.length()) {
            return false;
        }
        String lowerText = toLower(text);
        String lowerPattern = toLower(pattern);
        int[] results = searchKMP(lowerText, lowerPattern);
        return results.length > 0;
    }

    // =========================================================================
    // 3. Z-ALGORITHM
    // =========================================================================

    /**
     * Builds the Z-array for a string.
     * 
     * Core Idea:
     * Z[i] is the length of the longest substring starting from value[i] that is
     * also a prefix of value. Maintains a [left, right] interval (Z-box) representing
     * the rightmost segment matching a prefix to achieve linear execution.
     * 
     * Time Complexity:  O(length)
     * Space Complexity: O(length)
     * 
     * @param value Concatenated string.
     * @return Precomputed Z-array.
     */
    public static int[] buildZArray(String value) {
        if (value == null || value.isEmpty()) {
            return new int[0];
        }
        int length = value.length();
        int[] z = new int[length];
        z[0] = length;

        int left = 0;
        int right = 0;

        for (int i = 1; i < length; i++) {
            if (i <= right) {
                int k = i - left;
                int remainingInBox = right - i + 1;
                if (z[k] < remainingInBox) {
                    z[i] = z[k];
                } else {
                    left = i;
                    while (right < length && value.charAt(right) == value.charAt(right - left)) {
                        right++;
                    }
                    z[i] = right - left;
                    right--;
                }
            } else {
                left = i;
                right = i;
                while (right < length && value.charAt(right) == value.charAt(right - left)) {
                    right++;
                }
                z[i] = right - left;
                right--;
            }
        }
        return z;
    }

    /**
     * Searches for all occurrences of pattern in text using the Z-Algorithm.
     * 
     * Core Idea:
     * Creates combined string: pattern + '\0' + text (using null char delimiter).
     * Where Z[i] == patternLength in text portion, a full match occurs.
     * 
     * Time Complexity:  O(n + m)
     * Space Complexity: O(n + m)
     * 
     * @param text    The input text string.
     * @param pattern The pattern string to locate.
     * @return Array of all 0-based starting indices where pattern occurs.
     */
    public static int[] searchZ(String text, String pattern) {
        if (text == null || pattern == null) {
            return new int[0];
        }
        int textLength = text.length();
        int patternLength = pattern.length();

        if (patternLength == 0 || patternLength > textLength) {
            return new int[0];
        }

        // Form concatenated string with non-colliding delimiter
        StringBuilder combined = new StringBuilder(patternLength + 1 + textLength);
        combined.append(pattern);
        combined.append('\0');
        combined.append(text);

        String combinedStr = combined.toString();
        int[] z = buildZArray(combinedStr);

        int[] matches = new int[8];
        int matchCount = 0;

        int offset = patternLength + 1;
        int totalLength = combinedStr.length();

        for (int i = offset; i < totalLength; i++) {
            if (z[i] >= patternLength) {
                if (matchCount == matches.length) {
                    matches = expandArray(matches);
                }
                matches[matchCount++] = i - offset;
            }
        }

        return trimArray(matches, matchCount);
    }

    /**
     * Case-insensitive check if text contains pattern using Z-Algorithm.
     */
    public static boolean containsZAlgorithm(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (text == null || pattern.length() > text.length()) {
            return false;
        }
        String lowerText = toLower(text);
        String lowerPattern = toLower(pattern);
        int[] results = searchZ(lowerText, lowerPattern);
        return results.length > 0;
    }

    // =========================================================================
    // 4. RABIN-KARP ALGORITHM (POLYNOMIAL ROLLING HASH)
    // =========================================================================

    /**
     * Searches for all occurrences of pattern in text using Rabin-Karp.
     * 
     * Core Idea:
     * Treats string windows as numbers in base B modulo Q.
     * Computes the hash of the next window in O(1) time by subtracting the leading
     * character and adding the trailing character (rolling hash).
     * When windowHash == patternHash, verifies every character to eliminate false positives
     * caused by hash collisions.
     * 
     * Time Complexity:  O(n + m) average, O(n * m) worst case
     * Space Complexity: O(1) auxiliary
     * 
     * @param text    The input text string.
     * @param pattern The pattern string to locate.
     * @return Array of all 0-based starting indices where pattern occurs.
     */
    public static int[] searchRabinKarp(String text, String pattern) {
        if (text == null || pattern == null) {
            return new int[0];
        }
        int textLength = text.length();
        int patternLength = pattern.length();

        if (patternLength == 0 || patternLength > textLength) {
            return new int[0];
        }

        final long base = 256L;
        final long prime = 1_000_000_007L;

        // Compute base^(m - 1) % prime
        long highestPower = 1L;
        for (int i = 0; i < patternLength - 1; i++) {
            highestPower = (highestPower * base) % prime;
        }

        long patternHash = 0L;
        long windowHash = 0L;

        // Initial hash values for pattern and first window
        for (int i = 0; i < patternLength; i++) {
            patternHash = (patternHash * base + (int) pattern.charAt(i)) % prime;
            windowHash = (windowHash * base + (int) text.charAt(i)) % prime;
        }

        int[] matches = new int[8];
        int matchCount = 0;

        int maxStart = textLength - patternLength;
        for (int start = 0; start <= maxStart; start++) {
            // Hash match candidate: verify character-by-character to eliminate collisions
            if (patternHash == windowHash) {
                boolean identical = true;
                for (int j = 0; j < patternLength; j++) {
                    if (text.charAt(start + j) != pattern.charAt(j)) {
                        identical = false;
                        break;
                    }
                }
                if (identical) {
                    if (matchCount == matches.length) {
                        matches = expandArray(matches);
                    }
                    matches[matchCount++] = start;
                }
            }

            // Slide window: compute hash of next window in O(1)
            if (start < maxStart) {
                long leadingCharVal = ((long) text.charAt(start) * highestPower) % prime;
                windowHash = (windowHash - leadingCharVal + prime) % prime;
                windowHash = (windowHash * base + (long) text.charAt(start + patternLength)) % prime;
            }
        }

        return trimArray(matches, matchCount);
    }

    /**
     * Case-insensitive check if text contains pattern using Rabin-Karp.
     */
    public static boolean containsRabinKarp(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return true;
        }
        if (text == null || pattern.length() > text.length()) {
            return false;
        }
        String lowerText = toLower(text);
        String lowerPattern = toLower(pattern);
        int[] results = searchRabinKarp(lowerText, lowerPattern);
        return results.length > 0;
    }

    // =========================================================================
    // HELPER METHODS (PRIMITIVE UTILITIES - NO java.util.*)
    // =========================================================================

    /**
     * Expands an int array to double its current length.
     */
    private static int[] expandArray(int[] original) {
        int newLength = original.length * 2;
        int[] expanded = new int[newLength];
        for (int i = 0; i < original.length; i++) {
            expanded[i] = original[i];
        }
        return expanded;
    }

    /**
     * Trims an int array down to the exact number of used elements.
     */
    private static int[] trimArray(int[] original, int count) {
        int[] trimmed = new int[count];
        for (int i = 0; i < count; i++) {
            trimmed[i] = original[i];
        }
        return trimmed;
    }

    /**
     * Converts a string to lowercase manually without locale side-effects.
     */
    public static String toLower(String input) {
        if (input == null) {
            return null;
        }
        char[] chars = new char[input.length()];
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                chars[i] = (char) (c + ('a' - 'A'));
            } else {
                chars[i] = c;
            }
        }
        return new String(chars);
    }
}
