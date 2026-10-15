package smarthospital.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import smarthospital.model.Appointment;
import smarthospital.model.MedicalRecord;
import smarthospital.model.Patient;
import smarthospital.service.AppointmentService;
import smarthospital.service.MedicalSearchService;
import smarthospital.service.PatientService;
import smarthospital.util.AlgorithmComparator;
import smarthospital.util.Ids;
import smarthospital.co1.StringAlgorithms;
import smarthospital.co2.SuffixStructures;
import smarthospital.co3.AdvancedDP;
import smarthospital.co4.NetworkFlow;
import smarthospital.co5.ApproximationAlgorithms;
import smarthospital.co6.RandomizedAlgorithms;
import smarthospital.test.SystemTestSuite;

/**
 * Hierarchical console user interface for the Smart Hospital System.
 * Connects hospital workflows with CO1 through CO6 algorithm implementations.
 */
public class ConsoleUI {

    private final Scanner in = new Scanner(System.in);
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final MedicalSearchService recordService;

    public ConsoleUI(PatientService p, AppointmentService a, MedicalSearchService r) {
        this.patientService = p;
        this.appointmentService = a;
        this.recordService = r;
    }

    public void start() {
        while (true) {
            displayMainMenu();
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1":
                    patientManagementMenu();
                    break;
                case "2":
                    appointmentManagementMenu();
                    break;
                case "3":
                    medicalRecordsMenu();
                    break;
                case "4":
                    medicalTextSearchMenu();
                    break;
                case "5":
                    diagnosisSimilarityMenu();
                    break;
                case "6":
                    biologicalSequenceMenu();
                    break;
                case "7":
                    diagnosticTestSelectionMenu();
                    break;
                case "8":
                    hospitalResourceAllocationMenu();
                    break;
                case "9":
                    ambulanceRoutingMenu();
                    break;
                case "10":
                    emergencyTriageQueueMenu();
                    break;
                case "11":
                    dsaAlgorithmCenterMenu();
                    break;
                case "12":
                    reportsMenu();
                    break;
                case "0":
                    System.out.println("\n[INFO] Thank you for using Smart Hospital Information System. Session closed.");
                    return;
                default:
                    System.out.println("[WARN] Invalid option. Please select a valid number from 0 to 12.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n====================================================");
        System.out.println("         SMART HOSPITAL MANAGEMENT SYSTEM");
        System.out.println("  (Full DSA Curriculum: CO1 to CO6 Implementation)  ");
        System.out.println("====================================================");
        System.out.println(" 1. Patient Management");
        System.out.println(" 2. Appointment Management");
        System.out.println(" 3. Medical Records");
        System.out.println(" 4. Medical Text Search (CO1 & CO2)");
        System.out.println(" 5. Diagnosis Similarity (CO3 Edit Distance)");
        System.out.println(" 6. Biological Sequence Analysis (CO3 Alignment)");
        System.out.println(" 7. Diagnostic Test Selection Demo (CO3 Bitmask DP)");
        System.out.println(" 8. Hospital Bed & Resource Allocation (CO4 Network Flow)");
        System.out.println(" 9. Ambulance Routing & Facility Coverage (CO5 Approximation)");
        System.out.println("10. Emergency Triage & Queue Simulation (CO6 Randomized Algorithms)");
        System.out.println("11. DSA Algorithm Center (Benchmarks & Verification CO1-CO6)");
        System.out.println("12. Reports");
        System.out.println(" 0. Exit");
        System.out.print("Choose an option (0-12): ");
    }

    // =========================================================================
    // 1. PATIENT MANAGEMENT
    // =========================================================================

    private void patientManagementMenu() {
        while (true) {
            System.out.println("\n--- PATIENT MANAGEMENT ---");
            System.out.println("1. Register New Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Search Patients (by ID, Name, Diagnosis, or Medical History)");
            System.out.println("4. View Patient Details & Medical History");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    registerPatient();
                    break;
                case "2":
                    listPatients();
                    break;
                case "3":
                    searchPatients();
                    break;
                case "4":
                    viewPatientDetails();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void registerPatient() {
        System.out.println("\n--- REGISTER PATIENT ---");
        String name = ask("Full Name: ");
        int age = askInt("Age: ");
        String gender = ask("Gender (Male/Female/Other): ");
        String phone = ask("Phone Number: ");
        String blood = ask("Blood Group: ");
        String diagnosis = ask("Diagnosis Label: ");
        String history = ask("Medical History / Clinical Background: ");

        Patient p = new Patient(Ids.patient(), name, age, gender, phone, blood, diagnosis, history, LocalDate.now());
        patientService.register(p);
        System.out.println("[SUCCESS] Patient registered successfully! Assigned Patient ID: " + p.getId());
    }

    private void listPatients() {
        System.out.println("\n--- REGISTERED PATIENTS ---");
        List<Patient> all = patientService.all();
        if (all.isEmpty()) {
            System.out.println("No patients currently registered.");
            return;
        }
        System.out.printf("%-6s %-18s %-4s %-8s %-12s %-8s %-20s%n", "ID", "NAME", "AGE", "GENDER", "PHONE", "BLOOD", "DIAGNOSIS");
        System.out.println("--------------------------------------------------------------------------------");
        for (Patient p : all) {
            System.out.println(p);
        }
    }

    private void searchPatients() {
        String keyword = ask("Enter search keyword (searches ID, name, diagnosis, history): ");
        List<Patient> results = patientService.search(keyword);

        System.out.println("\nSearch Results (powered by CO1 Knuth-Morris-Pratt Algorithm):");
        if (results.isEmpty()) {
            System.out.println("No matching patients found for keyword \"" + keyword + "\".");
        } else {
            System.out.printf("%-6s %-18s %-4s %-8s %-12s %-8s %-20s%n", "ID", "NAME", "AGE", "GENDER", "PHONE", "BLOOD", "DIAGNOSIS");
            System.out.println("--------------------------------------------------------------------------------");
            for (Patient p : results) {
                System.out.println(p);
            }
        }
    }

    private void viewPatientDetails() {
        String pid = ask("Enter Patient ID: ");
        Patient p = patientService.byId(pid);
        if (p == null) {
            System.out.println("[ERROR] Patient ID not found.");
            return;
        }
        System.out.println("\n====================================================");
        System.out.println("               PATIENT RECORD DOSSIER");
        System.out.println("====================================================");
        System.out.println("Patient ID:         " + p.getId());
        System.out.println("Full Name:          " + p.getName());
        System.out.println("Age / Gender:       " + p.getAge() + " years / " + p.getGender());
        System.out.println("Phone Number:       " + p.getPhone());
        System.out.println("Blood Group:        " + p.getBloodGroup());
        System.out.println("Registered Date:    " + p.getRegisteredDate());
        System.out.println("Primary Diagnosis:  " + p.getDiagnosis());
        System.out.println("Clinical History:   " + p.getHistory());

        List<MedicalRecord> records = recordService.byPatient(pid);
        System.out.println("\nLinked Medical Records (" + records.size() + " entries):");
        if (records.isEmpty()) {
            System.out.println("  No medical records filed yet.");
        } else {
            for (MedicalRecord r : records) {
                System.out.println("  - [" + r.getId() + "] " + r.getDate() + " (" + r.getDoctor() + "): " + r.getDiagnosis() + " - " + r.getNotes());
            }
        }

        List<Appointment> appts = appointmentService.byPatient(pid);
        System.out.println("\nLinked Appointments (" + appts.size() + " entries):");
        if (appts.isEmpty()) {
            System.out.println("  No scheduled appointments.");
        } else {
            for (Appointment a : appts) {
                System.out.println("  - [" + a.getId() + "] " + a.getDate() + " at " + a.getTime() + " with " + a.getDoctor() + " (" + a.getDepartment() + ") [" + a.getStatus() + "]");
            }
        }
        System.out.println("====================================================");
    }

    // =========================================================================
    // 2. APPOINTMENT MANAGEMENT
    // =========================================================================

    private void appointmentManagementMenu() {
        while (true) {
            System.out.println("\n--- APPOINTMENT MANAGEMENT ---");
            System.out.println("1. Schedule New Appointment");
            System.out.println("2. View All Appointments");
            System.out.println("3. View Appointments by Patient ID");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    scheduleAppointment();
                    break;
                case "2":
                    listAppointments();
                    break;
                case "3":
                    listAppointmentsByPatient();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void scheduleAppointment() {
        System.out.println("\n--- SCHEDULE APPOINTMENT ---");
        String pid = ask("Patient ID: ");
        if (patientService.byId(pid) == null) {
            System.out.println("[ERROR] Patient ID " + pid + " does not exist. Please register the patient first.");
            return;
        }

        String doctor = ask("Attending Doctor: ");
        String dept = ask("Department (e.g. Cardiology, Orthopedics): ");
        LocalDate date = askDate("Appointment Date (yyyy-mm-dd): ");
        LocalTime time = askTime("Appointment Time (HH:mm): ");

        boolean scheduled = appointmentService.schedule(Ids.appointment(), pid, doctor, dept, date, time);
        if (scheduled) {
            System.out.println("[SUCCESS] Appointment confirmed and scheduled successfully.");
        } else {
            System.out.println("[REJECTED] Conflict detected! Doctor " + doctor + " already has a scheduled appointment on " + date + " at " + time + ".");
        }
    }

    private void listAppointments() {
        System.out.println("\n--- ALL APPOINTMENTS ---");
        List<Appointment> all = appointmentService.all();
        if (all.isEmpty()) {
            System.out.println("No appointments scheduled.");
            return;
        }
        System.out.printf("%-6s %-6s %-18s %-16s %-12s %-8s %-10s%n", "APPT", "PATIENT", "DOCTOR", "DEPARTMENT", "DATE", "TIME", "STATUS");
        System.out.println("--------------------------------------------------------------------------------");
        for (Appointment a : all) {
            System.out.println(a);
        }
    }

    private void listAppointmentsByPatient() {
        String pid = ask("Enter Patient ID: ");
        List<Appointment> list = appointmentService.byPatient(pid);
        System.out.println("\nAppointments for Patient " + pid + ":");
        if (list.isEmpty()) {
            System.out.println("No appointments found for this patient.");
            return;
        }
        for (Appointment a : list) {
            System.out.println(a);
        }
    }

    // =========================================================================
    // 3. MEDICAL RECORDS
    // =========================================================================

    private void medicalRecordsMenu() {
        while (true) {
            System.out.println("\n--- MEDICAL RECORDS ---");
            System.out.println("1. View All Medical Records");
            System.out.println("2. View Medical Records by Patient ID");
            System.out.println("3. Add New Medical Record");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    listMedicalRecords();
                    break;
                case "2":
                    listRecordsByPatient();
                    break;
                case "3":
                    addMedicalRecord();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void listMedicalRecords() {
        System.out.println("\n--- ALL MEDICAL RECORDS ---");
        List<MedicalRecord> all = recordService.all();
        if (all.isEmpty()) {
            System.out.println("No medical records available.");
            return;
        }
        for (MedicalRecord r : all) {
            System.out.println(r);
        }
    }

    private void listRecordsByPatient() {
        String pid = ask("Enter Patient ID: ");
        List<MedicalRecord> records = recordService.byPatient(pid);
        if (records.isEmpty()) {
            System.out.println("No medical records found for Patient " + pid);
            return;
        }
        System.out.println("\nMedical Records for Patient " + pid + ":");
        for (MedicalRecord r : records) {
            System.out.println(r);
        }
    }

    private void addMedicalRecord() {
        System.out.println("\n--- ADD MEDICAL RECORD ---");
        String pid = ask("Patient ID: ");
        if (patientService.byId(pid) == null) {
            System.out.println("[ERROR] Patient ID " + pid + " does not exist.");
            return;
        }
        String doctor = ask("Attending Doctor: ");
        String diagnosis = ask("Diagnosis: ");
        String notes = ask("Clinical Notes: ");

        MedicalRecord record = new MedicalRecord(Ids.record(), pid, LocalDate.now(), doctor, diagnosis, notes);
        recordService.addRecord(record);
        System.out.println("[SUCCESS] Medical record saved and persisted to data/medical_records.csv! ID: " + record.getId());
    }

    // =========================================================================
    // 4. MEDICAL TEXT SEARCH (CO1 & CO2)
    // =========================================================================

    private void medicalTextSearchMenu() {
        while (true) {
            System.out.println("\n--- MEDICAL TEXT SEARCH (CO1 & CO2) ---");
            System.out.println("1. Search Clinical Records using CO1 Algorithms (Naive / KMP / Z / Rabin-Karp)");
            System.out.println("2. Multi-Query Substring Search on Suffix Array Corpus Index (CO2)");
            System.out.println("3. Analyze Repeated Clinical Phrases using Kasai LCP Array (CO2)");
            System.out.println("4. Compare CO1 Search vs CO2 Suffix Array on Medical Corpus");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    searchRecordsCO1();
                    break;
                case "2":
                    suffixArrayCorpusSearch();
                    break;
                case "3":
                    analyzeLcpRepeatedPhrases();
                    break;
                case "4":
                    compareCO1andCO2Corpus();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void searchRecordsCO1() {
        System.out.println("\nSelect CO1 Algorithm for Search:");
        System.out.println("1. Knuth-Morris-Pratt (KMP) [O(n + m)]");
        System.out.println("2. Rabin-Karp Polynomial Rolling Hash [O(n + m) avg]");
        System.out.println("3. Z-Algorithm [O(n + m)]");
        System.out.println("4. Naive String Matching [O(n * m)]");
        int alg = askInt("Choose algorithm (1-4): ");
        String query = ask("Enter medical keyword to locate (e.g. diabetes, fracture, hypertension, fever): ");

        long start = System.nanoTime();
        List<MedicalRecord> matched = recordService.searchRecords(query, alg);
        long duration = System.nanoTime() - start;

        System.out.println("\nMatches Found: " + matched.size() + " record(s) [Execution Time: " + duration + " ns]");
        if (matched.isEmpty()) {
            System.out.println("No records matched the query \"" + query + "\".");
        } else {
            for (MedicalRecord r : matched) {
                System.out.println("  - [" + r.getId() + " | Patient: " + r.getPatientId() + "] " + r.getDiagnosis() + " => Notes: " + r.getNotes());
            }
        }
    }

    private void suffixArrayCorpusSearch() {
        System.out.println("\nIndexing combined clinical records corpus with Suffix Array...");
        long start = System.nanoTime();
        MedicalSearchService.CorpusIndex index = recordService.buildCorpusIndex();
        long indexTime = System.nanoTime() - start;
        System.out.println("[INDEX BUILT] Corpus size: " + index.getCombinedText().length() + " chars in " + indexTime + " ns.");
        System.out.println("You can now enter multiple substring queries against this single pre-built index!");

        while (true) {
            String query = ask("\nEnter substring query (or 'back' to return): ");
            if (query.equalsIgnoreCase("back") || query.isEmpty()) {
                break;
            }

            long queryStart = System.nanoTime();
            int[] positions = index.queryPositions(query);
            Set<String> matchedIds = index.queryMatchingRecordIds(query);
            long queryDuration = System.nanoTime() - queryStart;

            System.out.println("Binary Search Result: " + positions.length + " match occurrence(s) in " + queryDuration + " ns.");
            if (positions.length > 0) {
                System.out.print("Corpus Offsets: ");
                int show = Math.min(6, positions.length);
                for (int i = 0; i < show; i++) {
                    System.out.print(positions[i] + (i < show - 1 ? ", " : ""));
                }
                if (positions.length > 6) System.out.print("... (" + positions.length + " total)");
                System.out.println();
                System.out.println("Associated Medical Record IDs: " + matchedIds);
            } else {
                System.out.println("Substring \"" + query + "\" not found in the indexed corpus.");
            }
        }
    }

    private void analyzeLcpRepeatedPhrases() {
        System.out.println("\n--- REPEATED CLINICAL PHRASE ANALYSIS (CO2 KASAI LCP) ---");
        MedicalSearchService.CorpusIndex index = recordService.buildCorpusIndex();
        SuffixStructures.RepeatedSubstringResult result = index.analyzeRepeatedPhrases();

        System.out.println("Corpus Text Length: " + index.getCombinedText().length() + " characters.");
        System.out.println(result);
        System.out.println("\nEDUCATIONAL NOTE: LCP finds the longest repeating character subsequence across records.");
        System.out.println("It measures lexical repetition and does not perform semantic or diagnostic medical analysis.");
    }

    private void compareCO1andCO2Corpus() {
        MedicalSearchService.CorpusIndex index = recordService.buildCorpusIndex();
        String corpus = index.getCombinedText();
        String pattern = ask("Enter search pattern to benchmark against the clinical corpus: ");
        AlgorithmComparator.compareSearchAlgorithms(corpus, pattern);
    }

    // =========================================================================
    // 5. DIAGNOSIS SIMILARITY (CO3 EDIT DISTANCE)
    // =========================================================================

    private void diagnosisSimilarityMenu() {
        while (true) {
            System.out.println("\n--- DIAGNOSIS SIMILARITY (CO3 DYNAMIC PROGRAMMING) ---");
            System.out.println("1. Typo-Tolerant Diagnosis Term Ranking (Levenshtein & Damerau-Levenshtein)");
            System.out.println("2. Detailed 3-Way Edit Distance Comparison on Two Diagnosis Strings");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    queryDiagnosisSimilarity();
                    break;
                case "2":
                    compareTwoDiagnosisTerms();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void queryDiagnosisSimilarity() {
        String query = ask("Enter entered/misspelled diagnosis term (e.g. 'diabtes', 'hypertention'): ");
        List<String> suggestions = patientService.similarDiagnoses(query);

        System.out.println("\n--- RANKED DIAGNOSIS CANDIDATES ---");
        if (suggestions.isEmpty()) {
            System.out.println("No matching candidate diagnoses found within edit distance threshold.");
        } else {
            System.out.printf("%-26s | %-12s | %-14s | %-14s%n", "Diagnosis Label", "Damerau", "Levenshtein", "Weighted Cost");
            System.out.println("--------------------------------------------------------------------------------");
            for (String s : suggestions) {
                System.out.println(s);
            }
        }
        System.out.println("\nDISCLAIMER: String edit distance is an educational text-entry assistance tool.");
        System.out.println("The closest lexical match is strictly NOT a clinical diagnosis or medical recommendation.");
    }

    private void compareTwoDiagnosisTerms() {
        String term1 = ask("Enter first diagnosis string: ");
        String term2 = ask("Enter second diagnosis string: ");
        AlgorithmComparator.compareDistanceAlgorithms(term1, term2);
    }

    // =========================================================================
    // 6. BIOLOGICAL SEQUENCE ANALYSIS (CO3 ALIGNMENT)
    // =========================================================================

    private void biologicalSequenceMenu() {
        while (true) {
            System.out.println("\n--- BIOLOGICAL SEQUENCE ALIGNMENT (CO3 DYNAMIC PROGRAMMING) ---");
            System.out.println("1. Needleman-Wunsch Global Alignment (Scores & Traceback)");
            System.out.println("2. Smith-Waterman Local Alignment (Scores & Traceback)");
            System.out.println("3. Custom DNA Sequence Comparison");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    runGlobalAlignmentDemo();
                    break;
                case "2":
                    runLocalAlignmentDemo();
                    break;
                case "3":
                    runCustomSequenceAlignment();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void runGlobalAlignmentDemo() {
        System.out.println("\n[DEMO] Needleman-Wunsch Global Alignment:");
        String s1 = "GCATGCU";
        String s2 = "GATTACA";
        System.out.println("Sequence 1: " + s1);
        System.out.println("Sequence 2: " + s2);
        System.out.println("Scoring: Match=+2, Mismatch=-1, Gap=-2\n");

        AdvancedDP.AlignmentResult res = AdvancedDP.alignNeedlemanWunsch(s1, s2);
        System.out.println(res);
        System.out.println("\nDISCLAIMER: Educational biological sequence module for teaching DP. Not clinically validated.");
    }

    private void runLocalAlignmentDemo() {
        System.out.println("\n[DEMO] Smith-Waterman Local Alignment:");
        String s1 = "ACACACTA";
        String s2 = "AGCACACA";
        System.out.println("Sequence 1: " + s1);
        System.out.println("Sequence 2: " + s2);
        System.out.println("Scoring: Match=+2, Mismatch=-1, Gap=-2\n");

        AdvancedDP.AlignmentResult res = AdvancedDP.alignSmithWaterman(s1, s2);
        System.out.println(res);
        System.out.println("\nDISCLAIMER: Educational biological sequence module for teaching DP. Not clinically validated.");
    }

    private void runCustomSequenceAlignment() {
        String s1 = ask("Enter DNA Sequence 1: ");
        String s2 = ask("Enter DNA Sequence 2: ");
        int match = askInt("Match score (e.g. 2): ");
        int mismatch = askInt("Mismatch penalty (e.g. -1): ");
        int gap = askInt("Gap penalty (e.g. -2): ");

        System.out.println("\n--- GLOBAL ALIGNMENT ---");
        System.out.println(AdvancedDP.alignNeedlemanWunsch(s1, s2, match, mismatch, gap));

        System.out.println("\n--- LOCAL ALIGNMENT ---");
        System.out.println(AdvancedDP.alignSmithWaterman(s1, s2, match, mismatch, gap));

        System.out.println("DISCLAIMER: Educational sequence alignment demonstration only.");
    }

    // =========================================================================
    // 7. DIAGNOSTIC TEST SELECTION DEMO (CO3 BITMASK DP)
    // =========================================================================

    private void diagnosticTestSelectionMenu() {
        while (true) {
            System.out.println("\n--- DIAGNOSTIC TEST SELECTION DEMO (CO3 BITMASK DP) ---");
            System.out.println("1. Run Standard Diagnostic Panel Selection (Bitmask DP)");
            System.out.println("2. Compare Bitmask DP vs Brute-Force Verification");
            System.out.println("3. Custom Diagnostic Test Battery & Condition Constraints");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    runStandardTestSelectionDemo();
                    break;
                case "2":
                    compareBitmaskAndBruteForce();
                    break;
                case "3":
                    customTestSelectionInput();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void runStandardTestSelectionDemo() {
        System.out.println("\n================================================================================");
        System.out.println("        STANDARD DIAGNOSTIC TEST PANEL OPTIMIZATION (BITMASK DP)");
        System.out.println("================================================================================");
        System.out.println("Fictional Diagnostic Condition Criteria (M = 5 markers):");
        System.out.println("  Bit 0 (1): Fever / Infection Marker");
        System.out.println("  Bit 1 (2): Glycemic Profile / Blood Glucose");
        System.out.println("  Bit 2 (4): Blood Pressure / Cardiovascular Stress");
        System.out.println("  Bit 3 (8): Musculoskeletal / Bone Trauma");
        System.out.println("  Bit 4 (16): Pulmonary / Respiratory Distress");

        String[] testNames = {
            "Complete Blood Count (CBC)",
            "Comprehensive Metabolic Panel",
            "Cardiac Lipid & Pressure Panel",
            "Orthopedic Digital X-Ray",
            "Chest X-Ray & Spirometry",
            "Emergency Multi-Screen Panel"
        };
        int[] costs = {40, 75, 90, 65, 110, 160};
        int[] masks = {1, 3, 4, 8, 24, 22};

        System.out.println("\nAvailable Fictional Test Battery (K = " + testNames.length + " tests):");
        for (int i = 0; i < testNames.length; i++) {
            System.out.printf("  [%d] %-30s | Cost: $%3d | Coverage Mask: %2d (binary: %5s)%n",
                    i, testNames[i], costs[i], masks[i], Integer.toBinaryString(masks[i]));
        }

        int reqMask = (1 << 0) | (1 << 1) | (1 << 4);
        System.out.println("\nPatient Requirement Constraints: Fever + Glycemic + Respiratory (Mask: " + reqMask + ", binary: " + Integer.toBinaryString(reqMask) + ")");

        AdvancedDP.TestSelectionResult res = AdvancedDP.selectDiagnosticTestsBitmaskDP(5, reqMask, costs, masks);

        System.out.println("\n--- OPTIMAL SELECTION (BITMASK DP) ---");
        System.out.println("Minimum Total Cost:      $" + res.minimumCost);
        System.out.println("Achieved Coverage Mask:  " + res.achievedCoverageMask + " (binary: " + Integer.toBinaryString(res.achievedCoverageMask) + ")");
        System.out.println("Constraints Satisfied:   " + (res.isSatisfied ? "YES [ALL REQUIRED CRITERIA COVERED]" : "NO"));
        System.out.println("Selected Diagnostic Tests:");
        for (int idx : res.selectedTestIndices) {
            System.out.printf("  * %-30s ($%d)%n", testNames[idx], costs[idx]);
        }

        System.out.println("\nEDUCATIONAL DISCLAIMER: This problem demonstrates Set Cover optimization using Bitmask DP.");
        System.out.println("It is a pedagogical exercise and does not represent or optimize real clinical diagnostic protocols.");
        System.out.println("================================================================================\n");
    }

    private void compareBitmaskAndBruteForce() {
        int[] costs = {40, 75, 90, 65, 110, 160};
        int[] masks = {1, 3, 4, 8, 24, 22};
        int reqMask = (1 << 0) | (1 << 1) | (1 << 4);
        AlgorithmComparator.compareTestSelectionAlgorithms(5, reqMask, costs, masks);
    }

    private void customTestSelectionInput() {
        int m = askInt("Enter number of diagnostic conditions (M <= 10): ");
        if (m <= 0 || m > 12) {
            System.out.println("[ERROR] M must be between 1 and 12.");
            return;
        }
        int k = askInt("Enter number of available diagnostic tests (K <= 20): ");
        if (k <= 0 || k > 25) {
            System.out.println("[ERROR] K must be between 1 and 25.");
            return;
        }

        int[] costs = new int[k];
        int[] masks = new int[k];

        for (int i = 0; i < k; i++) {
            System.out.println("\nTest #" + (i + 1) + ":");
            costs[i] = askInt("  Test cost ($): ");
            masks[i] = askInt("  Condition bitmask covered (integer, 1 to " + ((1 << m) - 1) + "): ");
        }

        int reqMask = askInt("\nEnter required condition target bitmask (1 to " + ((1 << m) - 1) + "): ");
        AlgorithmComparator.compareTestSelectionAlgorithms(m, reqMask, costs, masks);
    }

    // =========================================================================
    // 8. HOSPITAL RESOURCE & BED ALLOCATION (CO4 NETWORK FLOW)
    // =========================================================================

    private void hospitalResourceAllocationMenu() {
        while (true) {
            System.out.println("\n--- HOSPITAL RESOURCE ALLOCATION (CO4 NETWORK FLOW) ---");
            System.out.println("1. Emergency Patient-to-Bed Matching (Maximum Bipartite Matching)");
            System.out.println("2. Hospital Oxygen / Transfer Pipeline Max Flow & Bottleneck Min-Cut");
            System.out.println("3. Benchmark Edmonds-Karp vs Dinic's Algorithm");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    runBipartiteMatchingDemo();
                    break;
                case "2":
                    runPipelineFlowDemo();
                    break;
                case "3":
                    benchmarkNetworkFlow();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void runBipartiteMatchingDemo() {
        System.out.println("\n================================================================================");
        System.out.println("    EMERGENCY PATIENT-TO-BED ALLOCATION (MAXIMUM BIPARTITE MATCHING)");
        System.out.println("================================================================================");
        System.out.println("Scenario: 5 incoming emergency patients with varying clinical needs.");
        System.out.println("Available Resources: 5 Specialized Beds (ICU, Trauma, Cardiac, General-1, General-2).");

        String[] patients = {"P1 (Acute Cardiac)", "P2 (Severe Trauma)", "P3 (Respiratory Failure)", "P4 (Minor Fracture)", "P5 (Routine Fever)"};
        String[] beds = {"Bed 0: ICU Suite", "Bed 1: Trauma Bay", "Bed 2: Cardiac Unit", "Bed 3: General Bed A", "Bed 4: General Bed B"};

        boolean[][] eligible = new boolean[5][5];
        eligible[0][2] = true; eligible[0][0] = true; // P1 can use Cardiac or ICU
        eligible[1][1] = true; eligible[1][0] = true; // P2 can use Trauma or ICU
        eligible[2][0] = true;                         // P3 requires ICU Suite
        eligible[3][1] = true; eligible[3][3] = true; eligible[3][4] = true; // P4 can use Trauma or General
        eligible[4][3] = true; eligible[4][4] = true; // P5 can use General beds

        NetworkFlow.BipartiteMatchingResult res = NetworkFlow.solveBipartiteMatching(5, 5, eligible);

        System.out.println("\nOptimal Simultaneous Assignments (Max Flow = " + res.matchCount + " of 5):");
        for (int p = 0; p < 5; p++) {
            int bed = res.patientToBed[p];
            if (bed != -1) {
                System.out.printf("  [ASSIGNED] %-24s ---> %s%n", patients[p], beds[bed]);
            } else {
                System.out.printf("  [WAITLIST] %-24s ---> No available suitable bed%n", patients[p]);
            }
        }
        System.out.println("================================================================================\n");
    }

    private void runPipelineFlowDemo() {
        System.out.println("\n--- HOSPITAL OXYGEN / TRANSFER PIPELINE BOTTLENECK ANALYSIS ---");
        int v = 6;
        int[][] cap = new int[v][v];
        cap[0][1] = 16; cap[0][2] = 13;
        cap[1][2] = 10; cap[1][3] = 12;
        cap[2][1] = 4;  cap[2][4] = 14;
        cap[3][2] = 9;  cap[3][5] = 20;
        cap[4][3] = 7;  cap[4][5] = 4;

        NetworkFlow.MaxFlowResult res = NetworkFlow.dinic(v, 0, 5, cap);

        System.out.println("Main Supply Station (Node 0) to Ward Distribution Sink (Node 5)");
        System.out.println("Maximum Sustainable Oxygen Throughput: " + res.maxFlow + " Liters/min");
        System.out.println("\nCritical Bottleneck Supply Lines (Minimum Cut Edges):");
        for (NetworkFlow.CutEdge edge : res.minCutEdges) {
            System.out.println("  * " + edge);
        }
    }

    private void benchmarkNetworkFlow() {
        int v = 6;
        int[][] cap = new int[v][v];
        cap[0][1] = 16; cap[0][2] = 13;
        cap[1][2] = 10; cap[1][3] = 12;
        cap[2][1] = 4;  cap[2][4] = 14;
        cap[3][2] = 9;  cap[3][5] = 20;
        cap[4][3] = 7;  cap[4][5] = 4;
        AlgorithmComparator.compareNetworkFlowAlgorithms(v, 0, 5, cap);
    }

    // =========================================================================
    // 9. AMBULANCE ROUTING & FACILITY COVERAGE (CO5 APPROXIMATION)
    // =========================================================================

    private void ambulanceRoutingMenu() {
        while (true) {
            System.out.println("\n--- AMBULANCE ROUTING & FACILITY COVERAGE (CO5 APPROXIMATION) ---");
            System.out.println("1. Ambulance Route Optimization (2-Approximation Metric TSP)");
            System.out.println("2. Compare 2-Approx TSP vs Exact Brute-Force Solver");
            System.out.println("3. Hospital Defibrillator / Station Placement (Greedy Set Cover)");
            System.out.println("4. Corridor Surveillance Placement (2-Approximation Vertex Cover)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    runAmbulanceTspDemo();
                    break;
                case "2":
                    compareTspApproximation();
                    break;
                case "3":
                    runDefibrillatorSetCoverDemo();
                    break;
                case "4":
                    runCorridorVertexCoverDemo();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void runAmbulanceTspDemo() {
        System.out.println("\n================================================================================");
        System.out.println("       AMBULANCE EMERGENCY ROUTE OPTIMIZATION (METRIC TSP 2-APPROX)");
        System.out.println("================================================================================");
        System.out.println("Scenario: Central Hospital dispatching emergency vehicle across 5 satellite clinics.");

        String[] stops = {
            "Central Hospital Base", "East Outpatient Clinic", "North Trauma Center",
            "South Dialysis Wing", "West Pediatric Annex"
        };
        int[][] dist = {
            {0, 12, 10, 19, 8},
            {12, 0, 15, 24, 18},
            {10, 15, 0, 14, 11},
            {19, 24, 14, 0, 16},
            {8, 18, 11, 16, 0}
        };

        ApproximationAlgorithms.TspResult res = ApproximationAlgorithms.solveMetricTsp2Approx(5, dist);

        System.out.println("\nComputed 2-Approximation Tour (via Prim MST + Preorder DFS):");
        System.out.println("Total Round-Trip Travel Distance: " + res.totalCost + " km");
        System.out.println("Minimum Spanning Tree Lower Bound: " + res.mstCost + " km (OPT > " + res.mstCost + ")");
        System.out.print("Dispatch Route: ");
        for (int i = 0; i < res.tour.length; i++) {
            System.out.print(stops[res.tour[i]] + (i < res.tour.length - 1 ? " -> " : ""));
        }
        System.out.println("\n================================================================================\n");
    }

    private void compareTspApproximation() {
        int[][] dist = {
            {0, 12, 10, 19, 8},
            {12, 0, 15, 24, 18},
            {10, 15, 0, 14, 11},
            {19, 24, 14, 0, 16},
            {8, 18, 11, 16, 0}
        };
        AlgorithmComparator.compareTspAlgorithms(5, dist);
    }

    private void runDefibrillatorSetCoverDemo() {
        System.out.println("\n--- HOSPITAL DEFIBRILLATOR STATION PLACEMENT (GREEDY SET COVER) ---");
        System.out.println("Requirement: Cover all 6 hospital emergency zones with minimum equipment stations.");

        // Station masks covering combinations of 6 zones (bits 0..5)
        int[] stationMasks = {
            (1 << 0) | (1 << 1),                 // Station 0 covers Zones 0, 1
            (1 << 1) | (1 << 2) | (1 << 3),       // Station 1 covers Zones 1, 2, 3
            (1 << 3) | (1 << 4),                 // Station 2 covers Zones 3, 4
            (1 << 4) | (1 << 5),                 // Station 3 covers Zones 4, 5
            (1 << 0) | (1 << 5)                  // Station 4 covers Zones 0, 5
        };

        ApproximationAlgorithms.SetCoverResult res = ApproximationAlgorithms.solveGreedySetCover(6, stationMasks);

        System.out.println("Selected Station Locations: " + res.totalSelected + " stations required.");
        System.out.print("Chosen Station Indices: [");
        for (int i = 0; i < res.selectedSets.length; i++) {
            System.out.print(res.selectedSets[i] + (i < res.selectedSets.length - 1 ? ", " : ""));
        }
        System.out.println("]");
        System.out.println("Total Hospital Zones Monitored: " + res.coveredElementsCount + " / 6");
    }

    private void runCorridorVertexCoverDemo() {
        System.out.println("\n--- CORRIDOR SURVEILLANCE PLACEMENT (2-APPROX VERTEX COVER) ---");
        int v = 5;
        int[][] edges = new int[v][v];
        edges[0][1] = 1; edges[1][0] = 1;
        edges[1][2] = 1; edges[2][1] = 1;
        edges[2][3] = 1; edges[3][2] = 1;
        edges[3][4] = 1; edges[4][3] = 1;
        edges[0][4] = 1; edges[4][0] = 1; // 5-cycle corridor ring

        ApproximationAlgorithms.VertexCoverResult res = ApproximationAlgorithms.solveVertexCover2Approx(v, edges);

        System.out.println("Corridor Ring Junctions: 5 nodes");
        System.out.println("Selected Camera Hub Locations: " + res.coverSize + " nodes.");
        System.out.print("Selected Camera Nodes: [");
        for (int i = 0; i < res.coverVertices.length; i++) {
            System.out.print(res.coverVertices[i] + (i < res.coverVertices.length - 1 ? ", " : ""));
        }
        System.out.println("]");
        System.out.println("Guarantee: Size <= 2 * OPT via Maximal Matching reduction.");
    }

    // =========================================================================
    // 10. EMERGENCY TRIAGE & QUEUE SIMULATION (CO6 RANDOMIZED ALGORITHMS)
    // =========================================================================

    private void emergencyTriageQueueMenu() {
        while (true) {
            System.out.println("\n--- EMERGENCY TRIAGE & QUEUE SIMULATION (CO6 RANDOMIZED ALGORITHMS) ---");
            System.out.println("1. Dynamic Patient Triage Priority Queue (Randomized Skip List Demo)");
            System.out.println("2. Hospital Network Vulnerability Assessment (Karger's Min-Cut)");
            System.out.println("3. Monte Carlo Clinical Queue & Wait-Time Simulation");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    runSkipListTriageDemo();
                    break;
                case "2":
                    runKargerMinCutDemo();
                    break;
                case "3":
                    runMonteCarloQueueDemo();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void runSkipListTriageDemo() {
        System.out.println("\n================================================================================");
        System.out.println("    EMERGENCY PATIENT TRIAGE PRIORITY QUEUE (RANDOMIZED SKIP LIST)");
        System.out.println("================================================================================");
        System.out.println("Triage Levels: 1 = Resuscitation | 2 = Emergent | 3 = Urgent | 4 = Less Urgent");

        RandomizedAlgorithms.TriageSkipList queue = new RandomizedAlgorithms.TriageSkipList();

        queue.insert(3, "P1004 - Severe Migraine");
        queue.insert(1, "P1008 - Cardiac Arrest");
        queue.insert(4, "P1002 - Arm Fracture");
        queue.insert(2, "P1005 - Severe Asthma Attack");
        queue.insert(1, "P1009 - Multiple Trauma");

        System.out.println("\nInserted 5 emergency patients into the probabilistic Skip List.");
        System.out.println("Active Triage Queue Size: " + queue.size());

        System.out.println("\nDispatching Patients by Clinical Urgency (Smallest Priority Key First):");
        while (!queue.isEmpty()) {
            RandomizedAlgorithms.SkipNode node = queue.pollHighestPriority();
            System.out.printf("  [DISPATCHED] Priority Key: %d | Patient: %s%n", node.key, node.value);
        }
        System.out.println("================================================================================\n");
    }

    private void runKargerMinCutDemo() {
        System.out.println("\n--- HOSPITAL NETWORK RELIABILITY (KARGER'S RANDOMIZED MIN-CUT) ---");
        RandomizedAlgorithms.Edge[] edges = {
            new RandomizedAlgorithms.Edge(0, 1),
            new RandomizedAlgorithms.Edge(1, 2),
            new RandomizedAlgorithms.Edge(2, 0),
            new RandomizedAlgorithms.Edge(0, 3),
            new RandomizedAlgorithms.Edge(1, 3),
            new RandomizedAlgorithms.Edge(3, 4), // Critical bottleneck connection
            new RandomizedAlgorithms.Edge(4, 5),
            new RandomizedAlgorithms.Edge(5, 6),
            new RandomizedAlgorithms.Edge(6, 4)
        };

        int minCut = RandomizedAlgorithms.kargerMinCut(7, edges, 100);
        System.out.println("Hospital Complex Power Grid: 7 Substations and 9 Power Lines");
        System.out.println("Estimated Global Minimum Cut (Karger 100 trials): " + minCut + " line(s).");
        System.out.println("Insight: A min-cut of " + minCut + " indicates a critical single-point-of-failure line.");
    }

    private void runMonteCarloQueueDemo() {
        System.out.println("\n--- MONTE CARLO CLINICAL QUEUE & WAIT-TIME SIMULATION ---");
        int days = askInt("Enter number of simulated hospital days (e.g. 365): ");
        int beds = askInt("Enter active emergency beds (e.g. 12): ");
        double arrivalRate = 10.0;
        double serviceRate = 1.0;

        RandomizedAlgorithms.QueueSimulationResult sim =
                RandomizedAlgorithms.simulateHospitalQueue(days, beds, arrivalRate, serviceRate);

        System.out.println("\n--- SIMULATION RESULTS (" + days + " DAYS) ---");
        System.out.printf("Average Patient Wait Time:   %.1f minutes%n", sim.averageWaitMinutes);
        System.out.printf("Peak Daily Wait Time:        %.1f minutes%n", sim.maxWaitMinutes);
        System.out.printf("Overcapacity Congestion Risk: %.1f%%%n", sim.congestionProbability * 100.0);
    }

    // =========================================================================
    // 11. DSA ALGORITHM CENTER (CO1 TO CO6)
    // =========================================================================

    private void dsaAlgorithmCenterMenu() {
        while (true) {
            System.out.println("\n--- DSA ALGORITHM CENTER & BENCHMARKING (CO1 - CO6) ---");
            System.out.println("1. CO1: Benchmark String Matching (Naive, KMP, Z, Rabin-Karp)");
            System.out.println("2. CO2: Inspect Suffix Array, Kasai LCP Array & Suffix Automaton");
            System.out.println("3. CO3: Dynamic Programming Suite (Edit Distance & Alignments)");
            System.out.println("4. CO4: Network Flow Comparison (Edmonds-Karp vs Dinic)");
            System.out.println("5. CO5: Approximation vs Exact TSP Comparison");
            System.out.println("6. CO6: Skip List Triage Throughput Benchmark");
            System.out.println("7. Run Automated Comprehensive Test Suite (CO1 - CO6 Verification)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    customStringBenchmark();
                    break;
                case "2":
                    inspectSuffixStructures();
                    break;
                case "3":
                    customDistanceBenchmark();
                    break;
                case "4":
                    benchmarkNetworkFlow();
                    break;
                case "5":
                    compareTspApproximation();
                    break;
                case "6":
                    AlgorithmComparator.compareQueueStructures(1000);
                    break;
                case "7":
                    SystemTestSuite.runAllTests();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void customStringBenchmark() {
        String text = ask("Enter target text: ");
        String pattern = ask("Enter search pattern: ");
        AlgorithmComparator.compareSearchAlgorithms(text, pattern);
    }

    private void inspectSuffixStructures() {
        String word = ask("Enter string to analyze with Suffix Structures (e.g. 'banana', 'diabetes'): ");
        if (word.isEmpty()) {
            System.out.println("[WARN] String cannot be empty.");
            return;
        }

        int[] sa = SuffixStructures.buildSuffixArray(word);
        int[] lcp = SuffixStructures.buildLcpArray(word, sa);

        System.out.println("\n====================================================");
        System.out.println("       SUFFIX ARRAY & KASAI LCP TABLE");
        System.out.println("====================================================");
        System.out.println("Input Text: \"" + word + "\" (Length: " + word.length() + ")");
        System.out.printf("%-4s | %-6s | %-6s | %s%n", "Rank", "SA[i]", "LCP[i]", "Suffix String");
        System.out.println("----------------------------------------------------");
        for (int i = 0; i < sa.length; i++) {
            System.out.printf("%-4d | %-6d | %-6d | %s%n", i, sa[i], lcp[i], word.substring(sa[i]));
        }
        System.out.println("----------------------------------------------------");

        SuffixStructures.RepeatedSubstringResult lrs = SuffixStructures.findLongestRepeatedSubstring(word);
        System.out.println(lrs);
        System.out.println("====================================================\n");
    }

    private void customDistanceBenchmark() {
        String w1 = ask("Enter first string: ");
        String w2 = ask("Enter second string: ");
        AlgorithmComparator.compareDistanceAlgorithms(w1, w2);
    }

    // =========================================================================
    // 12. REPORTS
    // =========================================================================

    private void reportsMenu() {
        while (true) {
            System.out.println("\n--- HOSPITAL SUMMARY REPORTS ---");
            System.out.println("1. Patient Demographics & Diagnosis Summary");
            System.out.println("2. Appointment Schedule & Doctor Department Load Summary");
            System.out.println("3. Medical Records & Corpus Index Summary");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose: ");
            String c = in.nextLine().trim();
            switch (c) {
                case "1":
                    reportPatients();
                    break;
                case "2":
                    reportAppointments();
                    break;
                case "3":
                    reportRecords();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    private void reportPatients() {
        List<Patient> list = patientService.all();
        System.out.println("\n====================================================");
        System.out.println("           PATIENT DEMOGRAPHICS REPORT");
        System.out.println("====================================================");
        System.out.println("Total Registered Patients: " + list.size());

        Map<String, Integer> diagnosisCounts = new LinkedHashMap<>();
        Map<String, Integer> genderCounts = new LinkedHashMap<>();

        int totalAge = 0;
        for (Patient p : list) {
            totalAge += p.getAge();
            diagnosisCounts.put(p.getDiagnosis(), diagnosisCounts.getOrDefault(p.getDiagnosis(), 0) + 1);
            genderCounts.put(p.getGender(), genderCounts.getOrDefault(p.getGender(), 0) + 1);
        }

        double avgAge = list.isEmpty() ? 0 : (double) totalAge / list.size();
        System.out.printf("Average Patient Age:       %.1f years%n", avgAge);

        System.out.println("\nGender Breakdown:");
        for (Map.Entry<String, Integer> entry : genderCounts.entrySet()) {
            System.out.printf("  - %-12s: %d%n", entry.getKey(), entry.getValue());
        }

        System.out.println("\nDiagnosis Distribution:");
        for (Map.Entry<String, Integer> entry : diagnosisCounts.entrySet()) {
            System.out.printf("  - %-22s: %d patient(s)%n", entry.getKey(), entry.getValue());
        }
        System.out.println("====================================================\n");
    }

    private void reportAppointments() {
        List<Appointment> list = appointmentService.all();
        System.out.println("\n====================================================");
        System.out.println("         APPOINTMENT SCHEDULE & LOAD REPORT");
        System.out.println("====================================================");
        System.out.println("Total Booked Appointments: " + list.size());

        Map<String, Integer> deptCounts = new LinkedHashMap<>();
        Map<String, Integer> doctorCounts = new LinkedHashMap<>();

        for (Appointment a : list) {
            deptCounts.put(a.getDepartment(), deptCounts.getOrDefault(a.getDepartment(), 0) + 1);
            doctorCounts.put(a.getDoctor(), doctorCounts.getOrDefault(a.getDoctor(), 0) + 1);
        }

        System.out.println("\nDepartment Load:");
        for (Map.Entry<String, Integer> e : deptCounts.entrySet()) {
            System.out.printf("  - %-20s: %d appointment(s)%n", e.getKey(), e.getValue());
        }

        System.out.println("\nDoctor Patient Schedule:");
        for (Map.Entry<String, Integer> e : doctorCounts.entrySet()) {
            System.out.printf("  - %-20s: %d appointment(s)%n", e.getKey(), e.getValue());
        }
        System.out.println("====================================================\n");
    }

    private void reportRecords() {
        List<MedicalRecord> list = recordService.all();
        System.out.println("\n====================================================");
        System.out.println("          MEDICAL RECORDS & CORPUS REPORT");
        System.out.println("====================================================");
        System.out.println("Total Clinical Record Entries: " + list.size());

        int totalNotesChars = 0;
        for (MedicalRecord r : list) {
            totalNotesChars += (r.getDiagnosis() + " " + r.getNotes()).length();
        }

        System.out.println("Total Combined Corpus Characters: " + totalNotesChars);
        System.out.printf("Average Characters per Record:    %.1f%n", list.isEmpty() ? 0 : (double) totalNotesChars / list.size());
        System.out.println("Persisted Storage Backing:        data/medical_records.csv");
        System.out.println("Index Support:                    Suffix Array + Kasai LCP + Suffix Automaton");
        System.out.println("====================================================\n");
    }

    // =========================================================================
    // INPUT HELPERS WITH ROBUST EXCEPTION HANDLING
    // =========================================================================

    private String ask(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private int askInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = in.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("[WARN] Invalid integer. Please enter a numeric value.");
            }
        }
    }

    private LocalDate askDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = in.nextLine().trim();
                return LocalDate.parse(line);
            } catch (Exception e) {
                System.out.println("[WARN] Invalid date. Please use standard ISO format: yyyy-mm-dd (e.g. 2026-09-27).");
            }
        }
    }

    private LocalTime askTime(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = in.nextLine().trim();
                return LocalTime.parse(line);
            } catch (Exception e) {
                System.out.println("[WARN] Invalid time. Please use standard format: HH:mm (e.g. 10:30).");
            }
        }
    }
}
