package smarthospital;
import smarthospital.repository.*;
import smarthospital.service.*;
import smarthospital.ui.ConsoleUI;
/** Application bootstrap. Architecture: UI -> Service -> Repository -> Model. */
public class Main {
    public static void main(String[] args) {
        PatientRepository patientRepo=new PatientRepository();
        AppointmentRepository appointmentRepo=new AppointmentRepository();
        MedicalRecordRepository recordRepo=new MedicalRecordRepository();
        smarthospital.util.Ids.initializeCounters(patientRepo.all(), appointmentRepo.all(), recordRepo.all());
        PatientService patientService=new PatientService(patientRepo);
        AppointmentService appointmentService=new AppointmentService(appointmentRepo,patientRepo);
        MedicalSearchService recordService=new MedicalSearchService(recordRepo);
        new ConsoleUI(patientService,appointmentService,recordService).start();
    }
}
