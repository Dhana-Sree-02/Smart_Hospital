package smarthospital.repository;

import java.util.ArrayList;
import java.util.List;
import smarthospital.model.Patient;
import smarthospital.util.CsvStorage;

/**
 * Data-access repository for Patients backed by CSV storage (patients.csv).
 */
public class PatientRepository {

    private final List<Patient> data = new ArrayList<>();
    private final boolean persistToDisk;

    public PatientRepository() {
        this(true);
    }

    public PatientRepository(boolean persistToDisk) {
        this.persistToDisk = persistToDisk;
        data.addAll(CsvStorage.loadPatients());
    }

    public List<Patient> all() {
        return new ArrayList<>(data);
    }

    public Patient byId(String id) {
        if (id == null) {
            return null;
        }
        for (Patient p : data) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    public void save(Patient p) {
        data.add(p);
        if (persistToDisk) {
            CsvStorage.appendPatient(p);
        }
    }
}
