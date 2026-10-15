package smarthospital.repository;

import java.util.ArrayList;
import java.util.List;
import smarthospital.model.Appointment;
import smarthospital.util.CsvStorage;

/**
 * Data-access repository for Appointments backed by CSV storage (appointments.csv).
 */
public class AppointmentRepository {

    private final List<Appointment> data = new ArrayList<>();
    private final boolean persistToDisk;

    public AppointmentRepository() {
        this(true);
    }

    public AppointmentRepository(boolean persistToDisk) {
        this.persistToDisk = persistToDisk;
        data.addAll(CsvStorage.loadAppointments());
    }

    public List<Appointment> all() {
        return new ArrayList<>(data);
    }

    public void save(Appointment a) {
        data.add(a);
        if (persistToDisk) {
            CsvStorage.appendAppointment(a);
        }
    }
}
