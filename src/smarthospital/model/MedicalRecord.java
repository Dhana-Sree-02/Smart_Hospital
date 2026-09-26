package smarthospital.model;
import java.time.LocalDate;
/** Historical record entry belonging to a patient. */
public class MedicalRecord {
    private final String id, patientId, doctor, diagnosis, notes;
    private final LocalDate date;
    public MedicalRecord(String id,String patientId,LocalDate date,String doctor,String diagnosis,String notes) {
        this.id=id;
        this.patientId=patientId;
        this.date=date;
        this.doctor=doctor;
        this.diagnosis=diagnosis;
        this.notes=notes;
    }
    public String getId() {
        return id;
    }
    public String getPatientId() {
        return patientId;
    }
    public LocalDate getDate() {
        return date;
    }
    public String getDoctor() {
        return doctor;
    }
    public String getDiagnosis() {
        return diagnosis;
    }
    public String getNotes() {
        return notes;
    }
    @Override public String toString() {
        return id+" | "+date+" | "+doctor+" | "+diagnosis+" | "+notes;
    }
}
