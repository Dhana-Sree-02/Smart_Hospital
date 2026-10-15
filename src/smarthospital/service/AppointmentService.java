package smarthospital.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import smarthospital.model.Appointment;
import smarthospital.repository.AppointmentRepository;
import smarthospital.repository.PatientRepository;

/**
 * Service managing appointment scheduling and conflict prevention.
 */
public class AppointmentService {

    private final AppointmentRepository appointments;
    private final PatientRepository patients;

    public AppointmentService(AppointmentRepository appointments, PatientRepository patients) {
        this.appointments = appointments;
        this.patients = patients;
    }

    /**
     * Schedules a new appointment if patient exists and doctor is not double-booked.
     * 
     * @param id         Generated Appointment ID.
     * @param patientId  Existing Patient ID.
     * @param doctor     Doctor name.
     * @param department Medical department.
     * @param date       Appointment date.
     * @param time       Appointment time slot.
     * @return true if successfully scheduled; false if conflict detected or patient not found.
     */
    public boolean schedule(String id, String patientId, String doctor, String department, LocalDate date, LocalTime time) {
        // Validate patient exists in system
        if (patients.byId(patientId) == null) {
            return false;
        }

        // Prevent doctor double-booking for the exact same date and time
        for (Appointment existing : appointments.all()) {
            boolean sameDoctor = existing.getDoctor().equalsIgnoreCase(doctor);
            boolean sameDate = existing.getDate().equals(date);
            boolean sameTime = existing.getTime().equals(time);
            boolean isScheduled = (existing.getStatus() == Appointment.Status.SCHEDULED);

            if (sameDoctor && sameDate && sameTime && isScheduled) {
                return false; // Slot conflict detected
            }
        }

        Appointment newAppointment = new Appointment(id, patientId, doctor, department, date, time);
        appointments.save(newAppointment);
        return true;
    }

    public List<Appointment> all() {
        return appointments.all();
    }

    public List<Appointment> byPatient(String patientId) {
        List<Appointment> list = new ArrayList<>();
        for (Appointment a : appointments.all()) {
            if (a.getPatientId().equalsIgnoreCase(patientId)) {
                list.add(a);
            }
        }
        return list;
    }
}
