package smarthospital.model;
import java.time.LocalDate;
/** Patient information used by the hospital system. */
public class Patient {
    private final String id;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String bloodGroup;
    private String diagnosis;
    private String history;
    private final LocalDate registeredDate;
    public Patient(String id, String name, int age, String gender, String phone,
    String bloodGroup, String diagnosis, String history,
    LocalDate registeredDate) {
        this.id=id;
        this.name=name;
        this.age=age;
        this.gender=gender;
        this.phone=phone;
        this.bloodGroup=bloodGroup;
        this.diagnosis=diagnosis;
        this.history=history;
        this.registeredDate=registeredDate;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public String getGender() {
        return gender;
    }
    public String getPhone() {
        return phone;
    }
    public String getBloodGroup() {
        return bloodGroup;
    }
    public String getDiagnosis() {
        return diagnosis;
    }
    public String getHistory() {
        return history;
    }
    public LocalDate getRegisteredDate() {
        return registeredDate;
    }
    @Override public String toString() {
        return String.format("%-6s %-18s %-4d %-8s %-12s %-8s %-20s",id,name,age,gender,phone,bloodGroup,diagnosis);
    }
}
