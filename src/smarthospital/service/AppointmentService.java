package smarthospital.service;
import java.time.*;
import java.util.*;
import smarthospital.model.Appointment;
import smarthospital.repository.AppointmentRepository;
import smarthospital.repository.PatientRepository;
/** Appointment business rules: patient validation and doctor-slot conflict checking. */
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    public AppointmentService(AppointmentRepository a,PatientRepository p) {
        appointments=a;
        patients=p;
    }
    public boolean schedule(String id,String patientId,String doctor,String dept,LocalDate date,LocalTime time) {
        if(patients.byId(patientId)==null)return false;
        for(Appointment a:appointments.all())if(a.getDoctor().equalsIgnoreCase(doctor)&&a.getDate().equals(date)&&a.getTime().equals(time)&&a.getStatus()==Appointment.Status.SCHEDULED)return false;
        appointments.save(new Appointment(id,patientId,doctor,dept,date,time));
        return true;
    }
    public List<Appointment> all() {
        return appointments.all();
    }
}
