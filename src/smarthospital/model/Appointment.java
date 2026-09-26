package smarthospital.model;
import java.time.LocalDate;
import java.time.LocalTime;
/** Appointment scheduled between a patient and doctor. */
public class Appointment {
    public enum Status {
        SCHEDULED, COMPLETED, CANCELLED
    }
    private final String id, patientId, doctor, department;
    private final LocalDate date;
    private final LocalTime time;
    private Status status = Status.SCHEDULED;
    public Appointment(String id,String patientId,String doctor,String department,LocalDate date,LocalTime time) {
        this.id=id;
        this.patientId=patientId;
        this.doctor=doctor;
        this.department=department;
        this.date=date;
        this.time=time;
    }
    public String getId() {
        return id;
    }
    public String getPatientId() {
        return patientId;
    }
    public String getDoctor() {
        return doctor;
    }
    public String getDepartment() {
        return department;
    }
    public LocalDate getDate() {
        return date;
    }
    public LocalTime getTime() {
        return time;
    }
    public Status getStatus() {
        return status;
    }
    @Override public String toString() {
        return String.format("%-6s %-6s %-18s %-16s %-12s %-8s %-10s",id,patientId,doctor,department,date,time,status);
    }
}
