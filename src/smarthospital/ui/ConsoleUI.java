package smarthospital.ui;
import java.time.*;
import java.util.*;
import smarthospital.model.*;
import smarthospital.service.*;
import smarthospital.util.Ids;
import smarthospital.co3.AdvancedDP;
/** Console presentation layer. UI is kept separate from business logic. */
public class ConsoleUI {
    private final Scanner in=new Scanner(System.in);
    private final PatientService patients;
    private final AppointmentService appointments;
    private final MedicalSearchService records;
    public ConsoleUI(PatientService p,AppointmentService a,MedicalSearchService r) {
        patients=p;
        appointments=a;
        records=r;
    }
    public void start() {
        while(true) {
            menu();
            String c=in.nextLine().trim();
            switch(c) {
                case "1":register();
                break;
                case "2":listPatients();
                break;
                case "3":search();
                break;
                case "4":details();
                break;
                case "5":schedule();
                break;
                case "6":listAppointments();
                break;
                case "7":history();
                break;
                case "8":similarity();
                break;
                case "9":demo();
                break;
                case "0":System.out.println("\nSession closed.");
                return;
                default:System.out.println("Invalid option.");
            }
        }
    }
    private void menu() {
        System.out.println("\n====================================================");
        System.out.println("             SMART HOSPITAL SYSTEM");
        System.out.println("====================================================");
        System.out.println("1. Register Patient");
        System.out.println("2. View Patients");
        System.out.println("3. Search Patient / Diagnosis");
        System.out.println("4. Patient Details");
        System.out.println("5. Schedule Appointment");
        System.out.println("6. View Appointments");
        System.out.println("7. Medical Records / Text Search");
        System.out.println("8. Diagnosis Text Similarity");
        System.out.println("9. DSA Demonstration");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }
    private void register() {
        System.out.println("\n--- PATIENT REGISTRATION ---");
        String name=ask("Name: ");
        int age=Integer.parseInt(ask("Age: "));
        String gender=ask("Gender: ");
        String phone=ask("Phone: ");
        String blood=ask("Blood group: ");
        String diagnosis=ask("Diagnosis label: ");
        String history=ask("History: ");
        Patient p=new Patient(Ids.patient(),name,age,gender,phone,blood,diagnosis,history,LocalDate.now());
        patients.register(p);
        System.out.println("Registered successfully. Patient ID: "+p.getId());
    }
    private void listPatients() {
        System.out.println("\n--- PATIENTS ---");
        System.out.printf("%-6s %-18s %-4s %-8s %-12s %-8s %-20s%n","ID","NAME","AGE","GENDER","PHONE","BLOOD","DIAGNOSIS");
        for(Patient p:patients.all())System.out.println(p);
    }
    private void search() {
        String k=ask("Search keyword: ");
        List<Patient> r=patients.search(k);
        System.out.println("\nCO1/KMP results:");
        if(r.isEmpty())System.out.println("No matches.");
        else for(Patient p:r)System.out.println(p);
    }
    private void details() {
        Patient p=patients.byId(ask("Patient ID: "));
        if(p==null) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.println("\nID: "+p.getId()+"\nName: "+p.getName()+"\nAge: "+p.getAge()+"\nGender: "+p.getGender()+"\nPhone: "+p.getPhone()+"\nBlood Group: "+p.getBloodGroup()+"\nDiagnosis: "+p.getDiagnosis()+"\nHistory: "+p.getHistory());
    }
    private void schedule() {
        String pid=ask("Patient ID: ");
        String doctor=ask("Doctor: ");
        String dept=ask("Department: ");
        LocalDate d=LocalDate.parse(ask("Date (yyyy-mm-dd): "));
        LocalTime t=LocalTime.parse(ask("Time (HH:mm): "));
        boolean ok=appointments.schedule(Ids.appointment(),pid,doctor,dept,d,t);
        System.out.println(ok?"Appointment scheduled.":"Could not schedule: patient may not exist or doctor slot is occupied.");
    }
    private void listAppointments() {
        System.out.println("\n--- APPOINTMENTS ---");
        for(Appointment a:appointments.all())System.out.println(a);
    }
    private void history() {
        String id=ask("Patient ID: ");
        List<MedicalRecord> r=records.byPatient(id);
        if(r.isEmpty()) {
            System.out.println("No records.");
            return;
        }
        for(MedicalRecord x:r)System.out.println(x);
        String k=ask("Keyword in records (CO2): ");
        System.out.println("Suffix-array result: "+(records.suffixSearch(id,k)?"FOUND":"NOT FOUND"));
    }
    private void similarity() {
        String term=ask("Entered diagnosis term: ");
        List<String> r=patients.similarDiagnoses(term);
        System.out.println("\nCO3 Levenshtein suggestions:");
        if(r.isEmpty())System.out.println("No close text matches.");
        else r.forEach(System.out::println);
        System.out.println("Text similarity only; this is not a medical diagnosis.");
    }
    private void demo() {
        System.out.println("\n--- DSA USED BY REAL FEATURES ---");
        System.out.println("CO1 KMP: searches patient name/history efficiently.");
        System.out.println("CO1 Rabin-Karp: searches medical notes by keyword.");
        System.out.println("CO2 Suffix Array: searches within record text.");
        System.out.println("CO3 Levenshtein: detects likely text-entry spelling differences.");
        System.out.println("CO3 Bitmask DP: selects tests under a time budget.");
        System.out.println("Example distance diabtes -> diabetes = "+AdvancedDP.levenshteinDistance("diabtes","diabetes"));
        System.out.println("Example best test benefit = "+AdvancedDP.bestDiagnosticTestBenefit(new int[] {
            2,3,4,5
        }
        ,new int[] {
            4,5,7,8
        }
        ,7));
    }
    private String ask(String s) {
        System.out.print(s);
        return in.nextLine().trim();
    }
}
