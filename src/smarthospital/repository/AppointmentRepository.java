package smarthospital.repository;

import java.util.*;
import smarthospital.model.Appointment;
import smarthospital.util.CsvStorage;

/** Data-access layer backed by CSV file storage (appointments.csv). */
public class AppointmentRepository {
    private final List<Appointment> data = new ArrayList<>();

    public AppointmentRepository() {
        data.addAll(CsvStorage.loadAppointments());
    }

    public List<Appointment> all() {
        return new ArrayList<>(data);
    }

    public void save(Appointment a) {
        data.add(a);
        CsvStorage.appendAppointment(a);
    }
}
