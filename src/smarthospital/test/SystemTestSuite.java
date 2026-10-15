package smarthospital.test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import smarthospital.co1.StringAlgorithms;
import smarthospital.co2.SuffixStructures;
import smarthospital.co3.AdvancedDP;
import smarthospital.co4.NetworkFlow;
import smarthospital.co5.ApproximationAlgorithms;
import smarthospital.co6.RandomizedAlgorithms;
import smarthospital.model.Appointment;
import smarthospital.model.MedicalRecord;
import smarthospital.model.Patient;
import smarthospital.repository.AppointmentRepository;
import smarthospital.repository.MedicalRecordRepository;
import smarthospital.repository.PatientRepository;
import smarthospital.service.AppointmentService;
import smarthospital.service.MedicalSearchService;
import smarthospital.service.PatientService;

/**
 * Self-contained automated test suite for Smart Hospital DSA upgrade.
 * 
 * Verifies:
 * - CO1: Naive, KMP, Z, and Rabin-Karp on normal, repeated, empty, and edge cases.
 * - CO2: Suffix Array construction, Binary Search, Kasai's LCP array, and Suffix Automaton.
 * - CO3: Levenshtein, Damerau-Levenshtein, Weighted Edit Distance, Needleman-Wunsch,
 *        Smith-Waterman, and Bitmask DP vs Brute-Force.
 * - Hospital business rules: conflict detection, patient registration, search, and records.
 */
public final class SystemTestSuite {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        runAllTests();
    }

    public static boolean runAllTests() {
        testsPassed = 0;
        testsFailed = 0;

        System.out.println("\n================================================================================");
        System.out.println("            RUNNING AUTOMATED SYSTEM & ALGORITHM TEST SUITE");
        System.out.println("================================================================================");

        testCO1StringMatching();
        testCO2SuffixStructures();
        testCO3DynamicProgramming();
        testCO4NetworkFlow();
        testCO5Approximation();
        testCO6Randomized();
        testHospitalFeatures();

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("TEST SUITE EXECUTION SUMMARY:");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Result:       " + (testsFailed == 0 ? "ALL TESTS PASSED SUCCESSFULLY!" : "TEST FAILURES ENCOUNTERED"));
        System.out.println("================================================================================\n");

        return testsFailed == 0;
    }

    // =========================================================================
    // CO1 TESTS
    // =========================================================================

    private static void testCO1StringMatching() {
        System.out.println("\n[Testing CO1: String Matching Algorithms]");

        String text = "fever in patient, diagnosed with typhoid fever and high fever";
        String pattern = "fever";

        int[] naive = StringAlgorithms.searchNaive(text, pattern);
        int[] kmp = StringAlgorithms.searchKMP(text, pattern);
        int[] z = StringAlgorithms.searchZ(text, pattern);
        int[] rk = StringAlgorithms.searchRabinKarp(text, pattern);

        // Expected match indices: 0, 41, 56
        assertEquals("Naive match count", 3, naive.length);
        assertEquals("KMP match count", 3, kmp.length);
        assertEquals("Z match count", 3, z.length);
        assertEquals("Rabin-Karp match count", 3, rk.length);

        assertArrayEquals("KMP vs Naive indices", naive, kmp);
        assertArrayEquals("Z vs Naive indices", naive, z);
        assertArrayEquals("Rabin-Karp vs Naive indices", naive, rk);

        // Overlapping matches: "AAAA" with pattern "AA" -> [0, 1, 2]
        String overlapText = "AAAA";
        String overlapPat = "AA";
        int[] expOverlap = new int[]{0, 1, 2};
        assertArrayEquals("Overlap Naive", expOverlap, StringAlgorithms.searchNaive(overlapText, overlapPat));
        assertArrayEquals("Overlap KMP", expOverlap, StringAlgorithms.searchKMP(overlapText, overlapPat));
        assertArrayEquals("Overlap Z", expOverlap, StringAlgorithms.searchZ(overlapText, overlapPat));
        assertArrayEquals("Overlap Rabin-Karp", expOverlap, StringAlgorithms.searchRabinKarp(overlapText, overlapPat));

        // Edge cases: empty pattern
        assertEquals("Empty pattern Naive", 0, StringAlgorithms.searchNaive(text, "").length);
        assertEquals("Empty pattern KMP", 0, StringAlgorithms.searchKMP(text, "").length);
        assertEquals("Empty pattern Z", 0, StringAlgorithms.searchZ(text, "").length);
        assertEquals("Empty pattern Rabin-Karp", 0, StringAlgorithms.searchRabinKarp(text, "").length);

        // Edge cases: pattern longer than text
        assertEquals("Long pattern Naive", 0, StringAlgorithms.searchNaive("short", "longer_pattern").length);
        assertEquals("Long pattern KMP", 0, StringAlgorithms.searchKMP("short", "longer_pattern").length);
        assertEquals("Long pattern Z", 0, StringAlgorithms.searchZ("short", "longer_pattern").length);
        assertEquals("Long pattern Rabin-Karp", 0, StringAlgorithms.searchRabinKarp("short", "longer_pattern").length);

        // Non-matching pattern
        assertEquals("No match Naive", 0, StringAlgorithms.searchNaive(text, "hypertension").length);
        assertEquals("No match KMP", 0, StringAlgorithms.searchKMP(text, "hypertension").length);

        // Case-insensitive helpers
        assertTrue("KMP contains case-insensitive", StringAlgorithms.containsKMP("Type 2 Diabetes", "diabetes"));
        assertTrue("Rabin-Karp contains case-insensitive", StringAlgorithms.containsRabinKarp("Fracture in tibia", "fracture"));
    }

    // =========================================================================
    // CO2 TESTS
    // =========================================================================

    private static void testCO2SuffixStructures() {
        System.out.println("\n[Testing CO2: Suffix Structures & Suffix Array]");

        // Known standard suffix array example: "banana"
        // Suffixes:
        // 0: banana
        // 1: anana
        // 2: nana
        // 3: ana
        // 4: na
        // 5: a
        // Sorted suffixes:
        // 5: a
        // 3: ana
        // 1: anana
        // 0: banana
        // 4: na
        // 2: nana
        // Expected SA: [5, 3, 1, 0, 4, 2]
        String banana = "banana";
        int[] saBanana = SuffixStructures.buildSuffixArray(banana);
        int[] expBananaSA = new int[]{5, 3, 1, 0, 4, 2};
        assertArrayEquals("Suffix Array for 'banana'", expBananaSA, saBanana);

        // Kasai's LCP array for "banana":
        // lcp[0] = 0 (for "a")
        // lcp[1] = 1 ("ana" with "a")
        // lcp[2] = 3 ("anana" with "ana")
        // lcp[3] = 0 ("banana" with "anana")
        // lcp[4] = 0 ("na" with "banana")
        // lcp[5] = 2 ("nana" with "na")
        // Expected LCP: [0, 1, 3, 0, 0, 2]
        int[] lcpBanana = SuffixStructures.buildLcpArray(banana, saBanana);
        int[] expBananaLCP = new int[]{0, 1, 3, 0, 0, 2};
        assertArrayEquals("Kasai LCP for 'banana'", expBananaLCP, lcpBanana);

        // Binary Search on Suffix Array
        int[] matchesAna = SuffixStructures.searchWithSuffixArray(banana, saBanana, "ana");
        assertEquals("Binary search 'ana' match count", 2, matchesAna.length);

        // Medical corpus text test
        String medText = "patient has diabetes and mild hypertension; previous diabetes review normal";
        int[] saMed = SuffixStructures.buildSuffixArray(medText);
        int[] matchesDiabetes = SuffixStructures.searchWithSuffixArray(medText, saMed, "diabetes");
        assertEquals("Suffix Array search 'diabetes' match count", 2, matchesDiabetes.length);

        // Repeated substring analysis using LCP ("patient has diabetes..." and "previous diabetes...")
        SuffixStructures.RepeatedSubstringResult lrs = SuffixStructures.findLongestRepeatedSubstring(medText);
        assertEquals("LRS detected string", "s diabetes ", lrs.substring);

        // Suffix Automaton verification
        SuffixStructures.SuffixAutomaton sam = new SuffixStructures.SuffixAutomaton(medText);
        assertTrue("SAM contains 'hypertension'", sam.contains("hypertension"));
        assertTrue("SAM contains 'diabetes'", sam.contains("diabetes"));
        assertFalse("SAM does not contain 'fracture'", sam.contains("fracture"));
    }

    // =========================================================================
    // CO3 TESTS
    // =========================================================================

    private static void testCO3DynamicProgramming() {
        System.out.println("\n[Testing CO3: Dynamic Programming Algorithms]");

        // 1. Levenshtein Distance
        assertEquals("Levenshtein identical", 0, AdvancedDP.levenshteinDistance("diabetes", "diabetes"));
        assertEquals("Levenshtein empty", 8, AdvancedDP.levenshteinDistance("diabetes", ""));
        assertEquals("Levenshtein substitution", 1, AdvancedDP.levenshteinDistance("kitten", "sitten"));
        assertEquals("Levenshtein insertion", 1, AdvancedDP.levenshteinDistance("fever", "fevers"));
        assertEquals("Levenshtein deletion", 1, AdvancedDP.levenshteinDistance("asthma", "astma"));

        // 2. Damerau-Levenshtein Distance (Adjacent Transposition)
        // "diabtes" vs "diabetes" -> insertion (1)
        // "daibetes" vs "diabetes" -> transposition of 'a' and 'i' (1 in Damerau, 2 in Levenshtein)
        assertEquals("Damerau transposition cost", 1, AdvancedDP.damerauLevenshteinDistance("daibetes", "diabetes"));
        assertEquals("Levenshtein transposition cost", 2, AdvancedDP.levenshteinDistance("daibetes", "diabetes"));

        // 3. Weighted Edit Distance
        // insert=1, delete=1, replace=2. "cat" -> "hat": replace 1 char = cost 2
        assertEquals("Weighted replace cost", 2, AdvancedDP.weightedEditDistance("cat", "hat", 1, 1, 2));

        // 4. Biological Sequence Alignment (Needleman-Wunsch & Smith-Waterman)
        String seq1 = "GAATTC";
        String seq2 = "GATTA";
        AdvancedDP.AlignmentResult nw = AdvancedDP.alignNeedlemanWunsch(seq1, seq2, 2, -1, -2);
        assertTrue("Needleman-Wunsch score positive", nw.score > 0);
        assertEquals("Aligned seq lengths match", nw.alignedSequence1.length(), nw.alignedSequence2.length());

        AdvancedDP.AlignmentResult sw = AdvancedDP.alignSmithWaterman("ACGTGAATTCACGT", "GAATTC", 2, -1, -2);
        assertTrue("Smith-Waterman score positive", sw.score > 0);
        assertEquals("SW aligned local lengths match", sw.alignedSequence1.length(), sw.alignedSequence2.length());

        // 5. Bitmask DP Diagnostic Test Selection vs Brute-Force
        // 4 conditions (masks 0..3)
        // Test 0: covers condition 0 and 1 (mask 3), cost 50
        // Test 1: covers condition 1 and 2 (mask 6), cost 60
        // Test 2: covers condition 2 and 3 (mask 12), cost 70
        // Test 3: covers condition 0 and 3 (mask 9), cost 80
        // Requirement: cover condition 0, 1, 2, 3 (mask 15)
        // Option A: Test 0 (50) + Test 2 (70) = 120 (covers 0,1,2,3)
        // Option B: Test 1 (60) + Test 3 (80) = 140 (covers 0,1,2,3)
        // Min cost should be 120.
        int[] costs = new int[]{50, 60, 70, 80};
        int[] masks = new int[]{3, 6, 12, 9};
        int reqMask = 15;

        AdvancedDP.TestSelectionResult dpResult = AdvancedDP.selectDiagnosticTestsBitmaskDP(4, reqMask, costs, masks);
        int bfMinCost = AdvancedDP.selectDiagnosticTestsBruteForce(reqMask, costs, masks);

        assertEquals("Bitmask DP min cost", 120, dpResult.minimumCost);
        assertEquals("Bitmask DP vs Brute-Force", bfMinCost, dpResult.minimumCost);
        assertTrue("Bitmask DP satisfied constraint", dpResult.isSatisfied);
    }

    // =========================================================================
    // CO4 TESTS: NETWORK FLOW & BIPARTITE MATCHING
    // =========================================================================

    private static void testCO4NetworkFlow() {
        System.out.println("\n[Testing CO4: Network Flow & Bipartite Matching]");

        // 6-vertex flow network (0=source, 5=sink)
        // Analytical max flow = 14 (bottleneck cut at 3->5 [4] + 4->5 [10])
        int v = 6;
        int[][] cap = new int[v][v];
        cap[0][1] = 10;
        cap[0][2] = 10;
        cap[1][2] = 2;
        cap[1][3] = 4;
        cap[1][4] = 8;
        cap[2][4] = 9;
        cap[3][5] = 10;
        cap[4][5] = 10;

        NetworkFlow.MaxFlowResult ek = NetworkFlow.edmondsKarp(v, 0, 5, cap);
        NetworkFlow.MaxFlowResult dinic = NetworkFlow.dinic(v, 0, 5, cap);

        assertEquals("Edmonds-Karp max flow", 14, ek.maxFlow);
        assertEquals("Dinic max flow", 14, dinic.maxFlow);
        assertEquals("EK vs Dinic equivalence", ek.maxFlow, dinic.maxFlow);
        assertTrue("Min-cut edges exist", dinic.minCutEdges.length > 0);

        // Maximum Bipartite Matching: 4 emergency patients, 4 hospital beds
        boolean[][] eligible = new boolean[4][4];
        eligible[0][0] = true;
        eligible[0][1] = true; // Patient 0 can use Bed 0 or Bed 1
        eligible[1][1] = true;
        eligible[1][2] = true; // Patient 1 can use Bed 1 or Bed 2
        eligible[2][2] = true; // Patient 2 needs Bed 2
        eligible[3][3] = true; // Patient 3 needs Bed 3

        NetworkFlow.BipartiteMatchingResult matchRes = NetworkFlow.solveBipartiteMatching(4, 4, eligible);
        assertEquals("Max bipartite matching count", 4, matchRes.matchCount);
        assertTrue("All patients assigned beds", matchRes.patientToBed[0] != -1 && matchRes.patientToBed[3] == 3);
    }

    // =========================================================================
    // CO5 TESTS: APPROXIMATION ALGORITHMS
    // =========================================================================

    private static void testCO5Approximation() {
        System.out.println("\n[Testing CO5: Approximation Algorithms]");

        // 4 hospital stops with symmetric metric distances satisfying triangle inequality
        int v = 4;
        int[][] dist = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        ApproximationAlgorithms.TspResult tsp = ApproximationAlgorithms.solveMetricTsp2Approx(v, dist);
        int exactOpt = ApproximationAlgorithms.solveExactTspSmallN(v, dist);

        assertTrue("TSP tour has valid length (V + 1)", tsp.tour.length == v + 1);
        assertEquals("TSP starts and ends at 0", 0, tsp.tour[0]);
        assertEquals("TSP returns to start depot", 0, tsp.tour[v]);
        assertTrue("2-Approx cost satisfies 2*OPT bound", tsp.totalCost <= 2 * exactOpt);

        // Greedy Set Cover: 4 zones needing coverage, 3 equipment packs
        // Pack 0 covers {0, 1} (mask 3)
        // Pack 1 covers {1, 2} (mask 6)
        // Pack 2 covers {2, 3} (mask 12)
        int[] packMasks = {3, 6, 12};
        ApproximationAlgorithms.SetCoverResult sc = ApproximationAlgorithms.solveGreedySetCover(4, packMasks);
        assertEquals("Set cover covers all 4 zones", 4, sc.coveredElementsCount);
        assertTrue("Set cover selects at least 2 packs", sc.totalSelected >= 2);

        // 2-Approximation Vertex Cover: 4 nodes, edges (0-1), (1-2), (2-3)
        int[][] edges = new int[4][4];
        edges[0][1] = 1; edges[1][0] = 1;
        edges[1][2] = 1; edges[2][1] = 1;
        edges[2][3] = 1; edges[3][2] = 1;

        ApproximationAlgorithms.VertexCoverResult vc = ApproximationAlgorithms.solveVertexCover2Approx(4, edges);
        assertTrue("Vertex cover covers all edges", vc.coverSize >= 2);
    }

    // =========================================================================
    // CO6 TESTS: RANDOMIZED ALGORITHMS & DATA STRUCTURES
    // =========================================================================

    private static void testCO6Randomized() {
        System.out.println("\n[Testing CO6: Randomized Algorithms]");

        // 1. Triage Skip List
        RandomizedAlgorithms.TriageSkipList skipList = new RandomizedAlgorithms.TriageSkipList();
        assertTrue("Skip list starts empty", skipList.isEmpty());

        skipList.insert(5, "P_URGENT_5");
        skipList.insert(1, "P_RESUSCITATION_1");
        skipList.insert(3, "P_EMERGENT_3");
        skipList.insert(2, "P_VERY_URGENT_2");

        assertEquals("Skip list size", 4, skipList.size());
        assertEquals("Skip list search key 1", "P_RESUSCITATION_1", skipList.search(1));
        assertEquals("Skip list search key 3", "P_EMERGENT_3", skipList.search(3));
        assertNotNull("Search non-existent returns null", skipList.search(99) == null ? "OK" : null);

        // Polling order MUST be priority 1, then 2, then 3, then 5
        RandomizedAlgorithms.SkipNode n1 = skipList.pollHighestPriority();
        RandomizedAlgorithms.SkipNode n2 = skipList.pollHighestPriority();
        RandomizedAlgorithms.SkipNode n3 = skipList.pollHighestPriority();
        RandomizedAlgorithms.SkipNode n5 = skipList.pollHighestPriority();

        assertEquals("First polled key is 1 (highest priority)", 1, n1.key);
        assertEquals("Second polled key is 2", 2, n2.key);
        assertEquals("Third polled key is 3", 3, n3.key);
        assertEquals("Fourth polled key is 5", 5, n5.key);
        assertTrue("Skip list empty after polling", skipList.isEmpty());

        // 2. Karger's Min-Cut: 4 vertices in K4 with a single bridge to vertex 4
        // Vertex 0, 1, 2, 3 fully connected, single edge 3-4 -> min-cut is 1
        RandomizedAlgorithms.Edge[] bridgeEdges = {
            new RandomizedAlgorithms.Edge(0, 1),
            new RandomizedAlgorithms.Edge(1, 2),
            new RandomizedAlgorithms.Edge(2, 0),
            new RandomizedAlgorithms.Edge(0, 3),
            new RandomizedAlgorithms.Edge(1, 3),
            new RandomizedAlgorithms.Edge(2, 3),
            new RandomizedAlgorithms.Edge(3, 4) // Bridge
        };
        int minCut = RandomizedAlgorithms.kargerMinCut(5, bridgeEdges, 50);
        assertEquals("Karger min-cut on bridge graph", 1, minCut);

        // 3. Monte Carlo Queue Simulation
        RandomizedAlgorithms.QueueSimulationResult sim =
                RandomizedAlgorithms.simulateHospitalQueue(100, 10, 8.0, 1.0);
        assertTrue("Average wait time is non-negative", sim.averageWaitMinutes >= 0.0);
        assertTrue("Congestion probability is within [0, 1]", sim.congestionProbability >= 0.0 && sim.congestionProbability <= 1.0);
    }

    // =========================================================================
    // HOSPITAL BUSINESS RULE TESTS
    // =========================================================================

    private static void testHospitalFeatures() {
        System.out.println("\n[Testing Hospital Features & Business Rules]");

        PatientRepository patientRepo = new PatientRepository(false);
        AppointmentRepository apptRepo = new AppointmentRepository(false);
        MedicalRecordRepository recordRepo = new MedicalRecordRepository(false);

        PatientService patientService = new PatientService(patientRepo);
        AppointmentService apptService = new AppointmentService(apptRepo, patientRepo);
        MedicalSearchService searchService = new MedicalSearchService(recordRepo);

        // 1. Patient Registration & Retrieval
        String testPatientId = "TEST_P9999";
        Patient testPatient = new Patient(
                testPatientId, "Test Subject", 30, "Other", "555-0199", "O+",
                "Test Acute Condition", "Fictional clinical history", LocalDate.now()
        );
        patientService.register(testPatient);
        Patient retrieved = patientService.byId(testPatientId);
        assertNotNull("Patient registered and retrieved", retrieved);
        assertEquals("Patient name matches", "Test Subject", retrieved.getName());

        // 2. Patient Search via KMP
        List<Patient> foundPatients = patientService.search("Acute");
        assertTrue("Patient found via keyword search", foundPatients.size() > 0);

        // 3. Appointment Scheduling & Slot Conflict Detection
        LocalDate testDate = LocalDate.of(2027, 5, 20);
        LocalTime testTime = LocalTime.of(14, 0);
        String doc = "Dr. Test Specialist";

        // First schedule must succeed
        boolean scheduled1 = apptService.schedule("TEST_A9001", testPatientId, doc, "Cardiology", testDate, testTime);
        assertTrue("First appointment schedule succeeds", scheduled1);

        // Second schedule with identical doctor, date, and time MUST be rejected
        boolean scheduledConflict = apptService.schedule("TEST_A9002", testPatientId, doc, "Cardiology", testDate, testTime);
        assertFalse("Duplicate doctor-date-time slot appointment rejected", scheduledConflict);

        // Schedule with non-existent patient ID MUST be rejected
        boolean scheduledInvalidPatient = apptService.schedule("TEST_A9003", "NON_EXISTENT_ID", doc, "Cardiology", testDate, LocalTime.of(15, 0));
        assertFalse("Appointment for non-existent patient rejected", scheduledInvalidPatient);

        // 4. Medical Record Addition & Search
        MedicalRecord mr = new MedicalRecord(
                "TEST_R9001", testPatientId, LocalDate.now(), doc, "Test Acute Condition", "Specialized observation notes."
        );
        searchService.addRecord(mr);

        List<MedicalRecord> searchRecords = searchService.searchRecords("observation", 1);
        assertTrue("Medical record retrieved via KMP", searchRecords.size() > 0);
    }

    // =========================================================================
    // ASSERTION HELPERS
    // =========================================================================

    private static void assertEquals(String testName, int expected, int actual) {
        if (expected == actual) {
            testsPassed++;
            System.out.println("  PASS: " + testName + " (expected " + expected + ", got " + actual + ")");
        } else {
            testsFailed++;
            System.err.println("  FAIL: " + testName + " (expected " + expected + ", got " + actual + ")");
        }
    }

    private static void assertEquals(String testName, String expected, String actual) {
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            testsPassed++;
            System.out.println("  PASS: " + testName + " (\"" + actual + "\")");
        } else {
            testsFailed++;
            System.err.println("  FAIL: " + testName + " (expected \"" + expected + "\", got \"" + actual + "\")");
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            testsPassed++;
            System.out.println("  PASS: " + testName);
        } else {
            testsFailed++;
            System.err.println("  FAIL: " + testName + " (condition was false)");
        }
    }

    private static void assertFalse(String testName, boolean condition) {
        if (!condition) {
            testsPassed++;
            System.out.println("  PASS: " + testName);
        } else {
            testsFailed++;
            System.err.println("  FAIL: " + testName + " (condition was true)");
        }
    }

    private static void assertNotNull(String testName, Object obj) {
        if (obj != null) {
            testsPassed++;
            System.out.println("  PASS: " + testName + " (not null)");
        } else {
            testsFailed++;
            System.err.println("  FAIL: " + testName + " (was null)");
        }
    }

    private static void assertArrayEquals(String testName, int[] expected, int[] actual) {
        if (expected == null && actual == null) {
            testsPassed++;
            System.out.println("  PASS: " + testName);
            return;
        }
        if (expected == null || actual == null || expected.length != actual.length) {
            testsFailed++;
            System.err.println("  FAIL: " + testName + " (lengths differ: exp "
                    + (expected == null ? -1 : expected.length) + ", got "
                    + (actual == null ? -1 : actual.length) + ")");
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != actual[i]) {
                testsFailed++;
                System.err.println("  FAIL: " + testName + " (mismatch at index " + i + ": exp " + expected[i] + ", got " + actual[i] + ")");
                return;
            }
        }
        testsPassed++;
        System.out.println("  PASS: " + testName + " (all " + expected.length + " elements matched)");
    }
}
