package smarthospital.model;

import java.time.LocalDate;

/**
 * Historical medical record entry belonging to a patient.
 */
public class MedicalRecord {

    private final String id;
    private final String patientId;
    private final LocalDate date;
    private final String doctor;
    private final String diagnosis;
    private final String notes;

    public MedicalRecord(String id, String patientId, LocalDate date, String doctor, String diagnosis, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.date = date;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.notes = notes;
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

    @Override
    public String toString() {
        return String.format("%-6s | %-10s | %-6s | %-16s | %-20s | %s",
                id, date, patientId, doctor, diagnosis, notes);
    }
}
