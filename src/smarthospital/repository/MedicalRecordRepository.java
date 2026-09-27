package smarthospital.repository;

import java.util.*;
import smarthospital.model.MedicalRecord;
import smarthospital.util.CsvStorage;

/** Data-access layer backed by CSV file storage (medical_records.csv). */
public class MedicalRecordRepository {
    private final List<MedicalRecord> data = new ArrayList<>();

    public MedicalRecordRepository() {
        data.addAll(CsvStorage.loadMedicalRecords());
    }

    public List<MedicalRecord> all() {
        return new ArrayList<>(data);
    }

    public List<MedicalRecord> byPatient(String id) {
        List<MedicalRecord> r = new ArrayList<>();
        for (MedicalRecord x : data) {
            if (x.getPatientId().equalsIgnoreCase(id)) r.add(x);
        }
        return r;
    }

    public void save(MedicalRecord r) {
        data.add(r);
        CsvStorage.appendMedicalRecord(r);
    }
}
