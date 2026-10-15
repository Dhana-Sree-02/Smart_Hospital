# Smart Hospital Information System (Full DSA Curriculum: CO1 to CO6)

An Eclipse-compatible Java hospital information system integrating real-world hospital workflows (patient registration, appointment scheduling with doctor slot conflict detection, and medical records management) with foundational and advanced algorithms across the entire advanced DSA curriculum:

- **CO1: String Matching Algorithms** (Naive, KMP, Z-Algorithm, Rabin-Karp)
- **CO2: Suffix Structures & Indexing** (Suffix Array, Binary Search, Kasai LCP, Repeated-Prefix Analysis, Suffix Automaton)
- **CO3: Advanced Dynamic Programming** (Wagner-Fischer, Damerau-Levenshtein, Weighted Edit Distance, Needleman-Wunsch, Smith-Waterman, Bitmask DP)
- **CO4: Network Flow & Bipartite Matching** (Edmonds-Karp, Dinic's Algorithm, Max-Flow Min-Cut Theorem, Maximum Patient-to-Bed Matching)
- **CO5: NP-Completeness & Approximation Algorithms** (2-Approximation Metric TSP Ambulance Routing, Greedy Set Cover Facility Placement, 2-Approximation Vertex Cover Corridor Surveillance, Exact Brute-Force Benchmark)
- **CO6: Randomized Algorithms & Data Structures** (Randomized Triage Skip List, Karger's Min-Cut Network Reliability, Monte Carlo Clinical Queue Simulation)

> **EDUCATIONAL NOTICE / VIVA NOTE:**  
> All patient records, diagnoses, and medical notes in this project are strictly fictional. Algorithm outputs (such as diagnosis text edit distances, DNA sequence alignments, flow allocations, and route approximations) are educational demonstrations for computer science coursework and are **not clinically validated** or intended for medical decision-making.

---

## 1. Project Architecture & Package Structure

The project follows a clean layered enterprise architecture:  
`ConsoleUI` $\rightarrow$ `Services` $\rightarrow$ `Algorithm Engines (CO1 - CO6)` $\rightarrow$ `Repositories` $\rightarrow$ `Models`

```
Smart_Hospital/
│
├── .classpath                          # Eclipse JRE container & source folder definition
├── .project                            # Eclipse project metadata
├── README.md                           # Comprehensive documentation & viva guide
├── README.txt                          # Quick Eclipse import notes
│
├── data/                               # Persistent CSV storage directory
│   ├── patients.csv                    # Fictional patient demographics & histories
│   ├── appointments.csv                # Booked doctor-patient appointment slots
│   └── medical_records.csv             # Clinical diagnoses & treatment notes
│
└── src/
    ├── module-info.java                # Java 17+ module descriptor (module Smart_Hospital1)
    │
    └── smarthospital/
        ├── Main.java                   # Application bootstrap & CLI argument handler
        │
        ├── model/                      # Domain entities
        │   ├── Patient.java            # Patient demographic & baseline medical profile
        │   ├── Appointment.java        # Appointment slot, doctor, and status
        │   └── MedicalRecord.java      # Clinical consultation notes & diagnosis
        │
        ├── repository/                 # Data-access layer backed by CSV files
        │   ├── PatientRepository.java
        │   ├── AppointmentRepository.java
        │   └── MedicalRecordRepository.java
        │
        ├── service/                    # Business rules & algorithm orchestration
        │   ├── PatientService.java     # Patient lookup & CO1/CO3 typo diagnosis matching
        │   ├── AppointmentService.java # Slot validation & conflict detection
        │   └── MedicalSearchService.java # CO1 search & CO2 Suffix Array corpus index
        │
        ├── co1/                        # CO1 Engine (Primitive arrays, no java.util.*)
        │   └── StringAlgorithms.java   # Naive, KMP, Z-Algorithm, Rabin-Karp
        │
        ├── co2/                        # CO2 Engine (Primitive arrays, no java.util.*)
        │   └── SuffixStructures.java   # Suffix Array, Binary Search, Kasai LCP, SAM
        │
        ├── co3/                        # CO3 Engine (Primitive arrays, no java.util.*)
        │   └── AdvancedDP.java         # Levenshtein, Damerau, Weighted, NW, SW, Bitmask DP
        │
        ├── co4/                        # CO4 Engine (Primitive arrays, no java.util.*)
        │   └── NetworkFlow.java        # Edmonds-Karp, Dinic, Min-Cut, Bipartite Matching
        │
        ├── co5/                        # CO5 Engine (Primitive arrays, no java.util.*)
        │   └── ApproximationAlgorithms.java # 2-Approx Metric TSP, Set Cover, Vertex Cover
        │
        ├── co6/                        # CO6 Engine (Primitive arrays, no java.util.*)
        │   └── RandomizedAlgorithms.java    # Skip List, Karger's Min-Cut, Monte Carlo Queue
        │
        ├── ui/
        │   └── ConsoleUI.java          # 13-option hierarchical console menu & interactive runner
        │
        ├── util/
        │   ├── AlgorithmComparator.java# Benchmarking, cross-checking & Big-O analyzer
        │   ├── CsvStorage.java         # CSV file reader/writer with quoting support
        │   └── Ids.java                # Auto-incrementing identifier generator
        │
        └── test/
            └── SystemTestSuite.java    # Automated verification test suite (79 unit tests)
```

---

## 2. Course Outcomes (CO1 - CO6) Implementation Map

| Course Outcome | Algorithm | Hospital Feature Integration | Theoretical Time | Space |
| :--- | :--- | :--- | :--- | :--- |
| **CO1** | **Naive String Matching** | Clinical notes search & cross-verification baseline | $O(n \cdot m)$ | $O(1)$ |
| **CO1** | **Knuth-Morris-Pratt (KMP)** | Fast patient search across name, ID, diagnosis, and history | $O(n + m)$ | $O(m)$ |
| **CO1** | **Z-Algorithm** | Medical notes keyword scanning via Z-array prefix box | $O(n + m)$ | $O(n + m)$ |
| **CO1** | **Rabin-Karp** | Polynomial rolling hash with character collision verification | $O(n + m)$ avg, $O(n \cdot m)$ worst | $O(1)$ |
| **CO2** | **Suffix Array (Prefix Doubling)** | In-place manual merge-sort ranking of all clinical suffixes | $O(n \log^2 n)$ | $O(n)$ |
| **CO2** | **Binary Search on Suffix Array** | Multi-query substring search over indexed medical corpus | $O(m \log n)$ per query | $O(1)$ |
| **CO2** | **Kasai's LCP Array** | Longest Common Prefix computation without redundant character rescans | $O(n)$ | $O(n)$ |
| **CO2** | **Repeated-Prefix Analysis** | Identifies repeated clinical phrases and lexical patterns across records | $O(n)$ | $O(1)$ |
| **CO2** | **Suffix Automaton (SAM)** | Minimal DAWG structure for rapid $O(m)$ substring existence checks | $O(n \cdot \Sigma)$ build, $O(m)$ query | $O(n \cdot \Sigma)$ |
| **CO3** | **Wagner-Fischer Levenshtein** | Typo tolerance in medical term entry | $O(n \cdot m)$ | $O(n \cdot m)$ |
| **CO3** | **Damerau-Levenshtein** | Detects adjacent character transposition typos (e.g. `diabtes` $\leftrightarrow$ `diabetes`) | $O(n \cdot m)$ | $O(n \cdot m)$ |
| **CO3** | **Weighted Edit Distance** | Configurable insertion, deletion, and replacement penalty weights | $O(n \cdot m)$ | $O(n \cdot m)$ |
| **CO3** | **Needleman-Wunsch** | Educational global DNA sequence alignment with complete traceback | $O(n \cdot m)$ | $O(n \cdot m)$ |
| **CO3** | **Smith-Waterman** | Educational local genomic alignment finding optimal conserved motifs | $O(n \cdot m)$ | $O(n \cdot m)$ |
| **CO3** | **Bitmask Dynamic Programming** | Minimum-cost diagnostic test selection under condition-coverage constraints | $O(2^M \cdot K)$ | $O(2^M)$ |
| **CO4** | **Edmonds-Karp Max Flow** | Hospital transfer pipeline throughput analysis using BFS paths | $O(V \cdot E^2)$ | $O(V^2)$ |
| **CO4** | **Dinic's Algorithm** | High-performance layered graph blocking flow for hospital distribution | $O(V^2 \cdot E)$ | $O(V^2)$ |
| **CO4** | **Max-Flow Min-Cut Theorem** | Identifies critical single-point-of-failure bottlenecks in oxygen/supply pipes | $O(V^2 \cdot E)$ | $O(V^2)$ |
| **CO4** | **Maximum Bipartite Matching** | Optimal assignment of emergency patients to eligible specialized hospital beds | $O(E \sqrt{V})$ | $O(V^2)$ |
| **CO5** | **2-Approx Metric TSP** | Ambulance dispatch optimization visiting satellite clinics via MST + DFS | $O(V^2)$ | $O(V^2)$ |
| **CO5** | **Greedy Set Cover** | Strategic defibrillator and crash-cart station placement ($H_n \le \ln n + 1$) | $O(M \cdot N)$ | $O(M)$ |
| **CO5** | **2-Approx Vertex Cover** | Hospital corridor intersection surveillance coverage via maximal matching | $O(V^2)$ | $O(V)$ |
| **CO5** | **Exact TSP Benchmark** | Brute-force exhaustive solver ($V \le 12$) to empirically verify $\le 2 \times OPT$ | $O(V!)$ | $O(V)$ |
| **CO6** | **Randomized Skip List** | Dynamic emergency patient triage priority queue with coin-flip balancing | Average $O(\log n)$ | $O(n)$ |
| **CO6** | **Karger's Randomized Min-Cut** | Hospital power/oxygen grid structural vulnerability via random edge contractions | $O(\text{trials} \cdot V)$ | $O(V)$ |
| **CO6** | **Monte Carlo Queue Simulation** | Stochastic estimation of ER patient waiting times and overcapacity risk | $O(\text{days})$ | $O(1)$ |

---

## 3. Strict Algorithm Engine Constraints Satisfied

In strict accordance with DSA engine requirements:
- **Zero `java.util.*` collections inside `smarthospital.co1`, `co2`, `co3`, `co4`, `co5`, and `co6`**.
- All internal queues, buffers, and match tracking use **primitive `int[]`, `char[]`, `int[][]`, `boolean[]` arrays** and custom node pointers.
- Suffix Array construction uses an **explicit manual Merge Sort on rank pairs** rather than `Arrays.sort`.
- Flow networks use primitive circular queues and DFS work pointers.
- Skip List uses custom multi-level nodes with a fast linear feedback pseudo-random generator without `java.util.Random`.
- Karger's algorithm uses a manual Disjoint-Set Union (DSU) structure with path compression.

---

## 4. How to Import and Run the Project

### In Eclipse IDE:
1. Open Eclipse.
2. Select **File $\rightarrow$ Import... $\rightarrow$ General $\rightarrow$ Existing Projects into Workspace**.
3. Choose the root folder containing `.project` and `.classpath`.
4. Click **Finish**.
5. Right-click [`src/smarthospital/Main.java`](file:///d:/Java/New%20folder/Smart_Hospital1/src/smarthospital/Main.java) $\rightarrow$ **Run As $\rightarrow$ Java Application**.

### From Command Line (Windows PowerShell / Terminal):
```powershell
# 1. Compile all Java source files
javac -d bin $(Get-ChildItem -Path src -Filter *.java -Recurse).FullName

# 2. Run the Interactive Smart Hospital Console Application
java --module-path bin -m Smart_Hospital1/smarthospital.Main

# 3. Or run the Automated Verification Test Suite directly
java --module-path bin -m Smart_Hospital1/smarthospital.test.SystemTestSuite
```

---

## 5. Console Menu Navigation (13 Options)

```
====================================================
         SMART HOSPITAL MANAGEMENT SYSTEM
  (Full DSA Curriculum: CO1 to CO6 Implementation)  
====================================================
 1. Patient Management
    ├── 1. Register New Patient
    ├── 2. View All Patients
    ├── 3. Search Patients (by ID, Name, Diagnosis, History via KMP)
    └── 4. View Patient Details & Medical History
 2. Appointment Management
    ├── 1. Schedule New Appointment (Doctor Slot Conflict Rejection)
    ├── 2. View All Appointments
    └── 3. View Appointments by Patient ID
 3. Medical Records
    ├── 1. View All Medical Records
    ├── 2. View Medical Records by Patient ID
    └── 3. Add New Medical Record (Persisted to CSV)
 4. Medical Text Search (CO1 & CO2)
    ├── 1. Search Records using CO1 (Naive / KMP / Z / Rabin-Karp)
    ├── 2. Multi-Query Substring Search on Suffix Array Corpus Index
    ├── 3. Analyze Repeated Clinical Phrases using Kasai LCP Array
    └── 4. Compare CO1 Search vs CO2 Suffix Array on Medical Corpus
 5. Diagnosis Similarity (CO3 Edit Distance)
    ├── 1. Typo-Tolerant Diagnosis Term Ranking (Levenshtein & Damerau)
    └── 2. Detailed 3-Way Edit Distance Comparison on Two Diagnosis Strings
 6. Biological Sequence Analysis (CO3 Alignment)
    ├── 1. Needleman-Wunsch Global Alignment (Scores & Traceback)
    ├── 2. Smith-Waterman Local Alignment (Scores & Traceback)
    └── 3. Custom DNA Sequence Comparison
 7. Diagnostic Test Selection Demo (CO3 Bitmask DP)
    ├── 1. Standard Diagnostic Test Panel Selection (Bitmask DP)
    ├── 2. Compare Bitmask DP vs Brute-Force Verification
    └── 3. Custom Diagnostic Test Battery & Condition Constraints
 8. Hospital Bed & Resource Allocation (CO4 Network Flow)
    ├── 1. Emergency Patient-to-Bed Matching (Maximum Bipartite Matching)
    ├── 2. Hospital Oxygen / Transfer Pipeline Max Flow & Bottleneck Min-Cut
    └── 3. Benchmark Edmonds-Karp vs Dinic's Algorithm
 9. Ambulance Routing & Facility Coverage (CO5 Approximation)
    ├── 1. Ambulance Route Optimization (2-Approximation Metric TSP)
    ├── 2. Compare 2-Approx TSP vs Exact Brute-Force Solver
    ├── 3. Hospital Defibrillator / Station Placement (Greedy Set Cover)
    └── 4. Corridor Surveillance Placement (2-Approximation Vertex Cover)
10. Emergency Triage & Queue Simulation (CO6 Randomized Algorithms)
    ├── 1. Dynamic Patient Triage Priority Queue (Randomized Skip List Demo)
    ├── 2. Hospital Network Vulnerability Assessment (Karger's Min-Cut)
    └── 3. Monte Carlo Clinical Queue & Wait-Time Simulation
11. DSA Algorithm Center (Benchmarks & Verification CO1-CO6)
    ├── 1. CO1: Benchmark String Matching (Naive, KMP, Z, Rabin-Karp)
    ├── 2. CO2: Inspect Suffix Array, Kasai LCP Array & Suffix Automaton
    ├── 3. CO3: Dynamic Programming Suite (Edit Distance & Alignments)
    ├── 4. CO4: Network Flow Comparison (Edmonds-Karp vs Dinic)
    ├── 5. CO5: Approximation vs Exact TSP Comparison
    ├── 6. CO6: Skip List Triage Throughput Benchmark
    └── 7. Run Automated Comprehensive Test Suite (CO1 - CO6 Verification)
12. Reports
    ├── 1. Patient Demographics & Diagnosis Summary
    ├── 2. Appointment Schedule & Doctor Department Load Summary
    └── 3. Medical Records & Corpus Index Summary
 0. Exit
```

---

## 6. Verification & Test Suite Results

The project includes an automated test runner ([`SystemTestSuite.java`](file:///d:/Java/New%20folder/Smart_Hospital1/src/smarthospital/test/SystemTestSuite.java)) covering **79 independent test cases** across all modules:

- **CO1 String Matching Tests (22 tests):** Exact match counts, identical index cross-check across Naive/KMP/Z/Rabin-Karp, overlapping matches (`AAAA` with `AA`), empty/long patterns, case-insensitivity.
- **CO2 Suffix Structures Tests (8 tests):** Standard `banana` SA and Kasai LCP validation, binary search substring retrieval, clinical corpus search, LRS extraction (`s diabetes `), Suffix Automaton containment.
- **CO3 Dynamic Programming Tests (16 tests):** Levenshtein identity/empty/insertion/deletion/substitution, Damerau transposition validation (`daibetes` vs `diabetes`), Weighted edit distance, Needleman-Wunsch & Smith-Waterman traceback, Bitmask DP matched against brute force.
- **CO4 Network Flow Tests (6 tests):** Edmonds-Karp and Dinic max flow equivalence on 6-node network (max flow = 14), min-cut edge extraction, 4-patient to 4-bed maximum bipartite matching.
- **CO5 Approximation Algorithms Tests (7 tests):** 2-Approximation Metric TSP tour validity, start/end depot return, cost bound $\le 2 \times OPT$ vs exact solver, Greedy Set Cover covering all zones, 2-Approximation Vertex Cover covering all corridor edges.
- **CO6 Randomized Algorithms Tests (13 tests):** Triage Skip List insert/search/poll order (priority 1 $\rightarrow$ 2 $\rightarrow$ 3 $\rightarrow$ 5), Karger's Min-Cut identifying bridge edge, Monte Carlo queue simulation convergence.
- **Hospital Business Rule Tests (7 tests):** Patient registration/lookup, doctor slot conflict rejection, non-existent patient rejection, medical record persistence and retrieval.

**Test Summary:**
```
================================================================================
TEST SUITE EXECUTION SUMMARY:
Tests Passed: 79
Tests Failed: 0
Result:       ALL TESTS PASSED SUCCESSFULLY!
================================================================================
```
