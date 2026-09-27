package smarthospital.ui;

import java.time.*;
import java.util.*;
import smarthospital.model.*;
import smarthospital.service.*;
import smarthospital.util.Ids;
import smarthospital.util.AlgorithmComparator;
import smarthospital.co3.AdvancedDP;

/** Console presentation layer. UI is kept separate from business logic. */
public class ConsoleUI {
    private final Scanner in = new Scanner(System.in);
    private final PatientService patients;
    private final AppointmentService appointments;
    private final MedicalSearchService records;

    public ConsoleUI(PatientService p, AppointmentService a, MedicalSearchService r) {
        patients = p;
        appointments = a;
        records = r;
    }

    public void start() {
        while (true) {
            menu();
            String c = in.nextLine().trim();
            switch (c) {
                case "1": register(); break;
                case "2": listPatients(); break;
                case "3": search(); break;
                case "4": details(); break;
                case "5": schedule(); break;
                case "6": listAppointments(); break;
                case "7": history(); break;
                case "8": similarity(); break;
                case "9": demo(); break;
                case "10": compareCustom(); break;
                case "0":
                    System.out.println("\nSession closed.");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void menu() {
        System.out.println("\n====================================================");
        System.out.println("             SMART HOSPITAL SYSTEM");
        System.out.println("====================================================");
        System.out.println("1. Register Patient");
        System.out.println("2. View Patients");
        System.out.println("3. Search Patient / Diagnosis");
        System.out.println("4. Patient Details");
        System.out.println("5. Schedule Appointment");
        System.out.println("6. View Appointments");
        System.out.println("7. Medical Records / Text Search");
        System.out.println("8. Diagnosis Text Similarity");
        System.out.println("9. DSA Demonstration & Benchmarks");
        System.out.println("10. Compare All Algorithms on Custom Input");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    private void register() {
        System.out.println("\n--- PATIENT REGISTRATION ---");
        String name = ask("Name: ");
        int age = askInt("Age: ");
        String gender = ask("Gender: ");
        String phone = ask("Phone: ");
        String blood = ask("Blood group: ");
        String diagnosis = ask("Diagnosis label: ");
        String history = ask("History: ");
        Patient p = new Patient(Ids.patient(), name, age, gender, phone, blood, diagnosis, history, LocalDate.now());
        patients.register(p);
        System.out.println("Registered successfully. Patient ID: " + p.getId());
    }

    private void listPatients() {
        System.out.println("\n--- PATIENTS ---");
        System.out.printf("%-6s %-18s %-4s %-8s %-12s %-8s %-20s%n", "ID", "NAME", "AGE", "GENDER", "PHONE", "BLOOD", "DIAGNOSIS");
        for (Patient p : patients.all()) System.out.println(p);
    }

    private void search() {
        String k = ask("Search keyword: ");
        List<Patient> r = patients.search(k);
        System.out.println("\nCO1/KMP results:");
        if (r.isEmpty()) System.out.println("No matches.");
        else for (Patient p : r) System.out.println(p);

        // Run full algorithm comparison benchmark on search input
        StringBuilder fullText = new StringBuilder();
        for (Patient p : patients.all()) {
            fullText.append(p.getId()).append(" ").append(p.getName()).append(" ")
                    .append(p.getDiagnosis()).append(" ").append(p.getHistory()).append(" ");
        }
        AlgorithmComparator.compareSearchAlgorithms(fullText.toString(), k);
    }

    private void details() {
        Patient p = patients.byId(ask("Patient ID: "));
        if (p == null) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.println("\nID: " + p.getId() + "\nName: " + p.getName() + "\nAge: " + p.getAge() + "\nGender: " + p.getGender() + "\nPhone: " + p.getPhone() + "\nBlood Group: " + p.getBloodGroup() + "\nDiagnosis: " + p.getDiagnosis() + "\nHistory: " + p.getHistory());
    }

    private void schedule() {
        String pid = ask("Patient ID: ");
        String doctor = ask("Doctor: ");
        String dept = ask("Department: ");
        LocalDate d = askDate("Date (yyyy-mm-dd): ");
        LocalTime t = askTime("Time (HH:mm): ");
        boolean ok = appointments.schedule(Ids.appointment(), pid, doctor, dept, d, t);
        System.out.println(ok ? "Appointment scheduled." : "Could not schedule: patient may not exist or doctor slot is occupied.");
    }

    private void listAppointments() {
        System.out.println("\n--- APPOINTMENTS ---");
        for (Appointment a : appointments.all()) System.out.println(a);
    }

    private void history() {
        System.out.println("\n--- MEDICAL RECORDS ---");
        System.out.println("1. View Patient Records & Suffix Search");
        System.out.println("2. Add New Medical Record");
        String sub = ask("Choose (1-2): ");
        if ("2".equals(sub)) {
            String pid = ask("Patient ID: ");
            if (patients.byId(pid) == null) {
                System.out.println("Patient not found.");
                return;
            }
            String doctor = ask("Doctor Name: ");
            String diag = ask("Diagnosis: ");
            String notes = ask("Medical Notes: ");
            MedicalRecord mr = new MedicalRecord(Ids.record(), pid, LocalDate.now(), doctor, diag, notes);
            records.addRecord(mr);
            System.out.println("Medical record added successfully and persisted to medical_records.csv. Record ID: " + mr.getId());
        } else {
            String id = ask("Patient ID: ");
            List<MedicalRecord> r = records.byPatient(id);
            if (r.isEmpty()) {
                System.out.println("No records.");
                return;
            }
            for (MedicalRecord x : r) System.out.println(x);
            String k = ask("Keyword in records (CO2 Suffix Array): ");
            System.out.println("Suffix-array result: " + (records.suffixSearch(id, k) ? "FOUND" : "NOT FOUND"));
        }
    }

    private void similarity() {
        String term = ask("Entered diagnosis term: ");
        List<String> r = patients.similarDiagnoses(term);
        System.out.println("\nCO3 Levenshtein suggestions:");
        if (r.isEmpty()) System.out.println("No close text matches in registered patient history.");
        else r.forEach(System.out::println);
        System.out.println("Text similarity only; this is not a medical diagnosis.");

        // Prompt user to enter the target diagnosis word manually for algorithm performance comparison
        String target = ask("\nEnter second diagnosis term to compare algorithm performance with: ");
        if (target.isEmpty() && !r.isEmpty()) {
            // Use top match from suggestions if user hits enter
            target = r.get(0).split(" \\(")[0];
        }
        if (!target.isEmpty()) {
            AlgorithmComparator.compareDistanceAlgorithms(term, target);
        } else {
            System.out.println("No target term specified for comparison.");
        }
    }

    private void demo() {
        System.out.println("\n--- DSA USED BY REAL FEATURES ---");
        System.out.println("CO1 KMP: searches patient name/history efficiently.");
        System.out.println("CO1 Rabin-Karp: searches medical notes by keyword.");
        System.out.println("CO2 Suffix Array: searches within record text.");
        System.out.println("CO3 Levenshtein: detects likely text-entry spelling differences.");
        System.out.println("CO3 Bitmask DP: selects tests under a time budget.");

        // Run comparative analysis benchmarks across algorithms
        String sampleText = "Ananya Rao Type 2 Diabetes History of diabetes and hypertension Rohan Kumar Fracture Fall-related fracture and acute pain Meera Sharma Hypertension";
        AlgorithmComparator.compareSearchAlgorithms(sampleText, "diabetes");
        AlgorithmComparator.compareDistanceAlgorithms("diabtes", "diabetes");
        AlgorithmComparator.compareTestSelectionAlgorithms(new int[]{2, 3, 4, 5}, new int[]{4, 5, 7, 8}, 7);
    }

    private void compareCustom() {
        System.out.println("\n--- ALGORITHM COMPARISON SUITE ---");
        System.out.println("1. Compare String Search Algorithms (Naive, KMP, Z, Rabin-Karp, Suffix Array, Suffix Automaton)");
        System.out.println("2. Compare Diagnosis Text Distance Algorithms (Levenshtein, Damerau, Weighted, Needleman, Smith)");
        System.out.println("3. Compare Diagnostic Test Selection Algorithms (Bitmask DP vs 0/1 Knapsack DP)");
        String sub = ask("Choose comparison category (1-3): ");
        switch (sub) {
            case "1":
                String text = ask("Enter target text: ");
                String pattern = ask("Enter search pattern: ");
                AlgorithmComparator.compareSearchAlgorithms(text, pattern);
                break;
            case "2":
                String word1 = ask("Enter first diagnosis string: ");
                String word2 = ask("Enter second diagnosis string: ");
                AlgorithmComparator.compareDistanceAlgorithms(word1, word2);
                break;
            case "3":
                int timeBudget = askInt("Enter max diagnostic time budget: ");
                String choice = ask("Do you want to enter custom test times & benefits? (y/n): ");
                if (choice.equalsIgnoreCase("y")) {
                    int n = askInt("Number of diagnostic tests: ");
                    int[] time = new int[n];
                    int[] benefit = new int[n];
                    for (int i = 0; i < n; i++) {
                        time[i] = askInt("Test " + (i + 1) + " time cost: ");
                        benefit[i] = askInt("Test " + (i + 1) + " benefit score: ");
                    }
                    AlgorithmComparator.compareTestSelectionAlgorithms(time, benefit, timeBudget);
                } else {
                    AlgorithmComparator.compareTestSelectionAlgorithms(new int[]{2, 3, 4, 5}, new int[]{4, 5, 7, 8}, timeBudget);
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private String ask(String s) {
        System.out.print(s);
        return in.nextLine().trim();
    }

    private int askInt(String s) {
        while (true) {
            try {
                System.out.print(s);
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Please enter a valid number.");
            }
        }
    }

    private LocalDate askDate(String s) {
        while (true) {
            try {
                System.out.print(s);
                return LocalDate.parse(in.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-mm-dd format (e.g. 2026-09-27).");
            }
        }
    }

    private LocalTime askTime(String s) {
        while (true) {
            try {
                System.out.print(s);
                return LocalTime.parse(in.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid time format. Please use HH:mm format (e.g. 10:30).");
            }
        }
    }
}
