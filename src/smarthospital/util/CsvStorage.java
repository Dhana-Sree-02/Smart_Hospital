package smarthospital.util;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import smarthospital.model.Appointment;
import smarthospital.model.MedicalRecord;
import smarthospital.model.Patient;

/**
 * Persists and loads Patients, Appointments, and Medical Records to/from CSV files.
 * CSV files are stored in the data/ directory.
 */
public class CsvStorage {
    private static final String DATA_DIR = "data";
    private static final String PATIENTS_FILE = DATA_DIR + "/patients.csv";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "/appointments.csv";
    private static final String RECORDS_FILE = DATA_DIR + "/medical_records.csv";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    // ==================== PATIENTS CSV ====================

    public static List<Patient> loadPatients() {
        List<Patient> list = new ArrayList<>();
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) {
            list = getInitialPatients();
            savePatients(list);
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip CSV header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> fields = parseCsvLine(line);
                if (fields.size() >= 9) {
                    Patient p = new Patient(
                        fields.get(0),
                        fields.get(1),
                        Integer.parseInt(fields.get(2)),
                        fields.get(3),
                        fields.get(4),
                        fields.get(5),
                        fields.get(6),
                        fields.get(7),
                        LocalDate.parse(fields.get(8))
                    );
                    list.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading patients.csv: " + e.getMessage());
            list = getInitialPatients();
        }
        return list;
    }

    public static void savePatients(List<Patient> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            pw.println("id,name,age,gender,phone,bloodGroup,diagnosis,history,registeredDate");
            for (Patient p : list) {
                pw.println(String.format("%s,%s,%d,%s,%s,%s,%s,%s,%s",
                    escapeCsv(p.getId()),
                    escapeCsv(p.getName()),
                    p.getAge(),
                    escapeCsv(p.getGender()),
                    escapeCsv(p.getPhone()),
                    escapeCsv(p.getBloodGroup()),
                    escapeCsv(p.getDiagnosis()),
                    escapeCsv(p.getHistory()),
                    p.getRegisteredDate()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error writing patients.csv: " + e.getMessage());
        }
    }

    public static void appendPatient(Patient p) {
        File file = new File(PATIENTS_FILE);
        boolean writeHeader = !file.exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATIENTS_FILE, true))) {
            if (writeHeader) {
                pw.println("id,name,age,gender,phone,bloodGroup,diagnosis,history,registeredDate");
            }
            pw.println(String.format("%s,%s,%d,%s,%s,%s,%s,%s,%s",
                escapeCsv(p.getId()),
                escapeCsv(p.getName()),
                p.getAge(),
                escapeCsv(p.getGender()),
                escapeCsv(p.getPhone()),
                escapeCsv(p.getBloodGroup()),
                escapeCsv(p.getDiagnosis()),
                escapeCsv(p.getHistory()),
                p.getRegisteredDate()
            ));
        } catch (IOException e) {
            System.err.println("Error appending to patients.csv: " + e.getMessage());
        }
    }

    private static List<Patient> getInitialPatients() {
        List<Patient> list = new ArrayList<>();
        list.add(new Patient("P1001","Ananya Rao",34,"Female","9876543210","O+","Type 2 Diabetes","History of diabetes and hypertension",LocalDate.of(2026,1,12)));
        list.add(new Patient("P1002","Rohan Kumar",28,"Male","9876501234","B+","Fracture","Fall-related fracture and acute pain",LocalDate.of(2026,2,4)));
        list.add(new Patient("P1003","Meera Sharma",52,"Female","9866012345","A+","Hypertension","Long-term blood-pressure monitoring",LocalDate.of(2026,2,19)));
        list.add(new Patient("P1004","Arjun Patel",41,"Male","9988776655","AB+","Migraine","Recurring headache history",LocalDate.of(2026,3,2)));
        list.add(new Patient("P1005","Priya Nair",25,"Female","9911223344","O-","Asthma","History of asthma and allergy documentation",LocalDate.of(2026,3,10)));
        return list;
    }

    // ==================== APPOINTMENTS CSV ====================

    public static List<Appointment> loadAppointments() {
        List<Appointment> list = new ArrayList<>();
        File file = new File(APPOINTMENTS_FILE);
        if (!file.exists()) {
            list = getInitialAppointments();
            saveAppointments(list);
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip CSV header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> fields = parseCsvLine(line);
                if (fields.size() >= 6) {
                    Appointment a = new Appointment(
                        fields.get(0),
                        fields.get(1),
                        fields.get(2),
                        fields.get(3),
                        LocalDate.parse(fields.get(4)),
                        LocalTime.parse(fields.get(5))
                    );
                    list.add(a);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading appointments.csv: " + e.getMessage());
            list = getInitialAppointments();
        }
        return list;
    }

    public static void saveAppointments(List<Appointment> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(APPOINTMENTS_FILE))) {
            pw.println("id,patientId,doctor,department,date,time,status");
            for (Appointment a : list) {
                pw.println(String.format("%s,%s,%s,%s,%s,%s,%s",
                    escapeCsv(a.getId()),
                    escapeCsv(a.getPatientId()),
                    escapeCsv(a.getDoctor()),
                    escapeCsv(a.getDepartment()),
                    a.getDate(),
                    a.getTime(),
                    a.getStatus()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error writing appointments.csv: " + e.getMessage());
        }
    }

    public static void appendAppointment(Appointment a) {
        File file = new File(APPOINTMENTS_FILE);
        boolean writeHeader = !file.exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(APPOINTMENTS_FILE, true))) {
            if (writeHeader) {
                pw.println("id,patientId,doctor,department,date,time,status");
            }
            pw.println(String.format("%s,%s,%s,%s,%s,%s,%s",
                escapeCsv(a.getId()),
                escapeCsv(a.getPatientId()),
                escapeCsv(a.getDoctor()),
                escapeCsv(a.getDepartment()),
                a.getDate(),
                a.getTime(),
                a.getStatus()
            ));
        } catch (IOException e) {
            System.err.println("Error appending to appointments.csv: " + e.getMessage());
        }
    }

    private static List<Appointment> getInitialAppointments() {
        List<Appointment> list = new ArrayList<>();
        list.add(new Appointment("A2001","P1001","Dr. Kavya Rao","Endocrinology",LocalDate.of(2026,9,26),LocalTime.of(10,0)));
        list.add(new Appointment("A2002","P1002","Dr. Vikram Singh","Orthopedics",LocalDate.of(2026,9,26),LocalTime.of(11,30)));
        list.add(new Appointment("A2003","P1003","Dr. Neha Iyer","Cardiology",LocalDate.of(2026,9,27),LocalTime.of(9,30)));
        return list;
    }

    // ==================== MEDICAL RECORDS CSV ====================

    public static List<MedicalRecord> loadMedicalRecords() {
        List<MedicalRecord> list = new ArrayList<>();
        File file = new File(RECORDS_FILE);
        if (!file.exists()) {
            list = getInitialMedicalRecords();
            saveMedicalRecords(list);
            return list;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Skip CSV header
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> fields = parseCsvLine(line);
                if (fields.size() >= 6) {
                    MedicalRecord r = new MedicalRecord(
                        fields.get(0),
                        fields.get(1),
                        LocalDate.parse(fields.get(2)),
                        fields.get(3),
                        fields.get(4),
                        fields.get(5)
                    );
                    list.add(r);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading medical_records.csv: " + e.getMessage());
            list = getInitialMedicalRecords();
        }
        return list;
    }

    public static void saveMedicalRecords(List<MedicalRecord> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RECORDS_FILE))) {
            pw.println("id,patientId,date,doctor,diagnosis,notes");
            for (MedicalRecord r : list) {
                pw.println(String.format("%s,%s,%s,%s,%s,%s",
                    escapeCsv(r.getId()),
                    escapeCsv(r.getPatientId()),
                    r.getDate(),
                    escapeCsv(r.getDoctor()),
                    escapeCsv(r.getDiagnosis()),
                    escapeCsv(r.getNotes())
                ));
            }
        } catch (IOException e) {
            System.err.println("Error writing medical_records.csv: " + e.getMessage());
        }
    }

    public static void appendMedicalRecord(MedicalRecord r) {
        File file = new File(RECORDS_FILE);
        boolean writeHeader = !file.exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(RECORDS_FILE, true))) {
            if (writeHeader) {
                pw.println("id,patientId,date,doctor,diagnosis,notes");
            }
            pw.println(String.format("%s,%s,%s,%s,%s,%s",
                escapeCsv(r.getId()),
                escapeCsv(r.getPatientId()),
                r.getDate(),
                escapeCsv(r.getDoctor()),
                escapeCsv(r.getDiagnosis()),
                escapeCsv(r.getNotes())
            ));
        } catch (IOException e) {
            System.err.println("Error appending to medical_records.csv: " + e.getMessage());
        }
    }

    private static List<MedicalRecord> getInitialMedicalRecords() {
        List<MedicalRecord> list = new ArrayList<>();
        list.add(new MedicalRecord("R3001","P1001",LocalDate.of(2026,6,10),"Dr. Kavya Rao","Type 2 Diabetes","Routine follow-up and report review."));
        list.add(new MedicalRecord("R3002","P1001",LocalDate.of(2026,8,14),"Dr. Kavya Rao","Diabetes","Follow-up visit recorded in hospital system."));
        list.add(new MedicalRecord("R3003","P1003",LocalDate.of(2026,7,2),"Dr. Neha Iyer","Hypertension","Blood-pressure monitoring visit."));
        return list;
    }

    // ==================== CSV HELPERS ====================

    private static String escapeCsv(String input) {
        if (input == null) return "";
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            return "\"" + input.replace("\"", "\"\"") + "\"";
        }
        return input;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    result.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        result.add(cur.toString());
        return result;
    }
}
