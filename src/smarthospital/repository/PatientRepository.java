package smarthospital.repository;

import java.util.*;
import smarthospital.model.Patient;
import smarthospital.util.CsvStorage;

/** Data-access layer backed by CSV file storage (patients.csv). */
public class PatientRepository {
    private final List<Patient> data = new ArrayList<>();

    public PatientRepository() {
        data.addAll(CsvStorage.loadPatients());
    }

    public List<Patient> all() {
        return new ArrayList<>(data);
    }

    public Patient byId(String id) {
        for (Patient p : data) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    public void save(Patient p) {
        data.add(p);
        CsvStorage.appendPatient(p);
    }
}
