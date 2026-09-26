package smarthospital.co1;
/**
* CO1 - String Matching Algorithms.
*
* Algorithms included:
* 1. Naive String Matching
* 2. KMP
* 3. Z Algorithm
* 4. Rabin-Karp
*
* These methods search for a small pattern inside a larger text.
*/
public class StringAlgorithms {
    private StringAlgorithms() {
    }
    /**
    * Naive String Matching
    *
    * Idea:
    * Compare the pattern with the text at every possible starting position.
    *
    * Time complexity: O(n * m)
    * n = text length, m = pattern length
    */
    public static void naiveSearch(String text, String pattern) {
        System.out.println("\n--- Naive String Matching ---");
        if (pattern.isEmpty()) {
            System.out.println("Pattern is empty.");
            return;
        }
        boolean found = false;
        for (int start = 0; start <= text.length() - pattern.length(); start++) {
            int j = 0;
            // Compare pattern characters with text characters.
            while (j < pattern.length()
            && text.charAt(start + j) == pattern.charAt(j)) {
                j++;
            }
            if (j == pattern.length()) {
                System.out.println("Pattern found at index: " + start);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Pattern not found.");
        }
    }
    /**
    * KMP - Knuth-Morris-Pratt
    *
    * The LPS array stores the longest proper prefix which is also a suffix.
    * This prevents unnecessary re-comparisons.
    *
    * Time complexity: O(n + m)
    */
    public static void kmpSearch(String text, String pattern) {
        System.out.println("\n--- KMP String Matching ---");
        if (pattern.isEmpty()) {
            System.out.println("Pattern is empty.");
            return;
        }
        int[] lps = buildLpsArray(pattern);
        boolean found = false;
        int textIndex = 0;
        int patternIndex = 0;
        while (textIndex < text.length()) {
            if (text.charAt(textIndex) == pattern.charAt(patternIndex)) {
                textIndex++;
                patternIndex++;
                if (patternIndex == pattern.length()) {
                    System.out.println("Pattern found at index: "
                    + (textIndex - patternIndex));
                    found = true;
                    patternIndex = lps[patternIndex - 1];
                }
            } else if (patternIndex > 0) {
                patternIndex = lps[patternIndex - 1];
            } else {
                textIndex++;
            }
        }
        if (!found) {
            System.out.println("Pattern not found.");
        }
    }
    /**
    * Builds the LPS (Longest Prefix Suffix) array used by KMP.
    */
    public static int[] buildLpsArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int length = 0;
        int i = 1;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }
    /**
    * Z Algorithm
    *
    * Create pattern + separator + text.
    * Z[i] tells how many characters from position i match the prefix.
    *
    * Time complexity: O(n + m)
    */
    public static void zAlgorithmSearch(String text, String pattern) {
        System.out.println("\n--- Z Algorithm ---");
        if (pattern.isEmpty()) {
            System.out.println("Pattern is empty.");
            return;
        }
        String combined = pattern + "$" + text;
        int[] z = buildZArray(combined);
        boolean found = false;
        for (int i = pattern.length() + 1; i < combined.length(); i++) {
            if (z[i] == pattern.length()) {
                int textIndex = i - pattern.length() - 1;
                System.out.println("Pattern found at index: " + textIndex);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Pattern not found.");
        }
    }
    /**
    * Builds the Z array for a string.
    */
    public static int[] buildZArray(String value) {
        int[] z = new int[value.length()];
        int left = 0;
        int right = 0;
        for (int i = 1; i < value.length(); i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }
            while (i + z[i] < value.length()
            && value.charAt(z[i]) == value.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }
        return z;
    }
    /**
    * Rabin-Karp
    *
    * Uses a rolling hash to compare the pattern with each text window.
    * A direct character comparison is done after a hash match.
    *
    * Average time: O(n + m)
    * Worst case: O(n * m)
    */
    public static void rabinKarpSearch(String text, String pattern) {
        System.out.println("\n--- Rabin-Karp String Matching ---");
        if (pattern.isEmpty()) {
            System.out.println("Pattern is empty.");
            return;
        }
        if (pattern.length() > text.length()) {
            System.out.println("Pattern not found.");
            return;
        }
        final int base = 256;
        final int prime = 101;
        int patternHash = 0;
        int windowHash = 0;
        int highestPower = 1;
        for (int i = 0; i < pattern.length() - 1; i++) {
            highestPower = (highestPower * base) % prime;
        }
        for (int i = 0; i < pattern.length(); i++) {
            patternHash = (base * patternHash + pattern.charAt(i)) % prime;
            windowHash = (base * windowHash + text.charAt(i)) % prime;
        }
        boolean found = false;
        for (int start = 0; start <= text.length() - pattern.length(); start++) {
            // Hash match is only a candidate; verify characters to avoid collisions.
            if (patternHash == windowHash
            && text.regionMatches(start, pattern, 0, pattern.length())) {
                System.out.println("Pattern found at index: " + start);
                found = true;
            }
            if (start < text.length() - pattern.length()) {
                windowHash = (base * (windowHash
                - text.charAt(start) * highestPower)
                + text.charAt(start + pattern.length())) % prime;
                if (windowHash < 0) {
                    windowHash += prime;
                }
            }
        }
        if (!found) {
            System.out.println("Pattern not found.");
        }
    }
    public static boolean containsKMP(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return true;
        if (text == null) return false;
        text = text.toLowerCase();
        pattern = pattern.toLowerCase();
        int[] lps = buildLpsArray(pattern);
        int i=0,j=0;
        while(i<text.length()) {
            if(text.charAt(i)==pattern.charAt(j)) {
                i++;
                j++;
                if(j==pattern.length())return true;
            }
            else if(j>0) j=lps[j-1];
            else i++;
        }
        return false;
    }
    public static boolean containsRabinKarp(String text, String pattern) {
        if(pattern==null || pattern.isEmpty()) return true;
        if(text==null || pattern.length()>text.length()) return false;
        text=text.toLowerCase();
        pattern=pattern.toLowerCase();
        final int base=256, prime=101;
        int ph=0,wh=0,power=1;
        for(int i=0; i<pattern.length()-1; i++) power=power*base%prime;
        for(int i=0; i<pattern.length(); i++) {
            ph=(ph*base+pattern.charAt(i))%prime;
            wh=(wh*base+text.charAt(i))%prime;
        }
        for(int start=0; start<=text.length()-pattern.length(); start++) {
            if(ph==wh && text.regionMatches(start,pattern,0,pattern.length())) return true;
            if(start<text.length()-pattern.length()) {
                wh=(base*(wh-text.charAt(start)*power)+text.charAt(start+pattern.length()))%prime;
                if(wh<0)wh+=prime;
            }
        }
        return false;
    }
}
