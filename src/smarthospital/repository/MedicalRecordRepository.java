package smarthospital.repository;

import java.util.ArrayList;
import java.util.List;
import smarthospital.model.MedicalRecord;
import smarthospital.util.CsvStorage;

/**
 * Data-access repository for Medical Records backed by CSV storage (medical_records.csv).
 */
public class MedicalRecordRepository {

    private final List<MedicalRecord> data = new ArrayList<>();
    private final boolean persistToDisk;

    public MedicalRecordRepository() {
        this(true);
    }

    public MedicalRecordRepository(boolean persistToDisk) {
        this.persistToDisk = persistToDisk;
        data.addAll(CsvStorage.loadMedicalRecords());
    }

    public List<MedicalRecord> all() {
        return new ArrayList<>(data);
    }

    public List<MedicalRecord> byPatient(String id) {
        List<MedicalRecord> matches = new ArrayList<>();
        if (id == null) {
            return matches;
        }
        for (MedicalRecord x : data) {
            if (x.getPatientId().equalsIgnoreCase(id)) {
                matches.add(x);
            }
        }
        return matches;
    }

    public void save(MedicalRecord r) {
        data.add(r);
        if (persistToDisk) {
            CsvStorage.appendMedicalRecord(r);
        }
    }
}
