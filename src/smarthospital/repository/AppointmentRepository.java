package smarthospital.repository;
import java.time.*;
import java.util.*;
import smarthospital.model.Appointment;
public class AppointmentRepository {
    private final List<Appointment> data=new ArrayList<>();
    public AppointmentRepository() {
        data.add(new Appointment("A2001","P1001","Dr. Kavya Rao","Endocrinology",LocalDate.of(2026,9,26),LocalTime.of(10,0)));
        data.add(new Appointment("A2002","P1002","Dr. Vikram Singh","Orthopedics",LocalDate.of(2026,9,26),LocalTime.of(11,30)));
        data.add(new Appointment("A2003","P1003","Dr. Neha Iyer","Cardiology",LocalDate.of(2026,9,27),LocalTime.of(9,30)));
    }
    public List<Appointment> all() {
        return new ArrayList<>(data);
    }
    public void save(Appointment a) {
        data.add(a);
    }
}
