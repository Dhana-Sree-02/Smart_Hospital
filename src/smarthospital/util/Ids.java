package smarthospital.util;

import java.util.List;
import smarthospital.model.Appointment;
import smarthospital.model.MedicalRecord;
import smarthospital.model.Patient;

/** Identifier generator with auto-increment counters initialized from persisted data. */
public final class Ids {
    private static int patient = 1006, appointment = 2004, record = 3004;

    private Ids() {}

    public static void initializeCounters(List<Patient> patients, List<Appointment> appointments, List<MedicalRecord> records) {
        if (patients != null) {
            for (Patient p : patients) {
                if (p.getId() != null && p.getId().startsWith("P")) {
                    try {
                        int num = Integer.parseInt(p.getId().substring(1));
                        if (num >= patient) patient = num + 1;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        if (appointments != null) {
            for (Appointment a : appointments) {
                if (a.getId() != null && a.getId().startsWith("A")) {
                    try {
                        int num = Integer.parseInt(a.getId().substring(1));
                        if (num >= appointment) appointment = num + 1;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        if (records != null) {
            for (MedicalRecord r : records) {
                if (r.getId() != null && r.getId().startsWith("R")) {
                    try {
                        int num = Integer.parseInt(r.getId().substring(1));
                        if (num >= record) record = num + 1;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }

    public static String patient() {
        return "P" + patient++;
    }

    public static String appointment() {
        return "A" + appointment++;
    }

    public static String record() {
        return "R" + record++;
    }
}
