package smarthospital.repository;
import java.time.LocalDate;
import java.util.*;
import smarthospital.model.Patient;
/** Data-access layer. Replace this with JDBC/SQL later without changing services. */
public class PatientRepository {
    private final List<Patient> data = new ArrayList<>();
    public PatientRepository() {
        data.add(new Patient("P1001","Ananya Rao",34,"Female","9876543210","O+","Type 2 Diabetes","History of diabetes and hypertension",LocalDate.of(2026,1,12)));
        data.add(new Patient("P1002","Rohan Kumar",28,"Male","9876501234","B+","Fracture","Fall-related fracture and acute pain",LocalDate.of(2026,2,4)));
        data.add(new Patient("P1003","Meera Sharma",52,"Female","9866012345","A+","Hypertension","Long-term blood-pressure monitoring",LocalDate.of(2026,2,19)));
        data.add(new Patient("P1004","Arjun Patel",41,"Male","9988776655","AB+","Migraine","Recurring headache history",LocalDate.of(2026,3,2)));
        data.add(new Patient("P1005","Priya Nair",25,"Female","9911223344","O-","Asthma","History of asthma and allergy documentation",LocalDate.of(2026,3,10)));
    }
    public List<Patient> all() {
        return new ArrayList<>(data);
    }
    public Patient byId(String id) {
        for(Patient p:data)if(p.getId().equalsIgnoreCase(id))return p;
        return null;
    }
    public void save(Patient p) {
        data.add(p);
    }
}
