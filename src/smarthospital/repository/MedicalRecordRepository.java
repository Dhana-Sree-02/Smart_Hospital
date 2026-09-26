package smarthospital.repository;
import java.time.*;
import java.util.*;
import smarthospital.model.MedicalRecord;
public class MedicalRecordRepository {
    private final List<MedicalRecord> data=new ArrayList<>();
    public MedicalRecordRepository() {
        data.add(new MedicalRecord("R3001","P1001",LocalDate.of(2026,6,10),"Dr. Kavya Rao","Type 2 Diabetes","Routine follow-up and report review."));
        data.add(new MedicalRecord("R3002","P1001",LocalDate.of(2026,8,14),"Dr. Kavya Rao","Diabetes","Follow-up visit recorded in hospital system."));
        data.add(new MedicalRecord("R3003","P1003",LocalDate.of(2026,7,2),"Dr. Neha Iyer","Hypertension","Blood-pressure monitoring visit."));
    }
    public List<MedicalRecord> all() {
        return new ArrayList<>(data);
    }
    public List<MedicalRecord> byPatient(String id) {
        List<MedicalRecord> r=new ArrayList<>();
        for(MedicalRecord x:data)if(x.getPatientId().equalsIgnoreCase(id))r.add(x);
        return r;
    }
}
