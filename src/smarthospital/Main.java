package smarthospital;

import smarthospital.repository.AppointmentRepository;
import smarthospital.repository.MedicalRecordRepository;
import smarthospital.repository.PatientRepository;
import smarthospital.service.AppointmentService;
import smarthospital.service.MedicalSearchService;
import smarthospital.service.PatientService;
import smarthospital.ui.ConsoleUI;
import smarthospital.util.Ids;
import smarthospital.test.SystemTestSuite;

/**
 * Smart Hospital Application Entry Point.
 * 
 * Layered Architecture:
 *   ConsoleUI (Presentation)
 *       ↓
 *   Services (Business Logic: PatientService, AppointmentService, MedicalSearchService)
 *       ↓
 *   Algorithm Engine (smarthospital.co1, smarthospital.co2, smarthospital.co3)
 *       ↓
 *   Repositories (Data Access: CSV Backing in data/)
 *       ↓
 *   Models (Domain Entities: Patient, Appointment, MedicalRecord)
 */
public class Main {

    public static void main(String[] args) {
        // Support running automated test suite via CLI argument
        if (args != null && args.length > 0 && "--test".equalsIgnoreCase(args[0])) {
            SystemTestSuite.runAllTests();
            return;
        }

        // Initialize repositories (backed by data/*.csv storage)
        PatientRepository patientRepo = new PatientRepository();
        AppointmentRepository appointmentRepo = new AppointmentRepository();
        MedicalRecordRepository recordRepo = new MedicalRecordRepository();

        // Synchronize auto-increment ID counters with existing persisted records
        Ids.initializeCounters(patientRepo.all(), appointmentRepo.all(), recordRepo.all());

        // Instantiate domain services
        PatientService patientService = new PatientService(patientRepo);
        AppointmentService appointmentService = new AppointmentService(appointmentRepo, patientRepo);
        MedicalSearchService recordService = new MedicalSearchService(recordRepo);

        // Launch console UI
        ConsoleUI ui = new ConsoleUI(patientService, appointmentService, recordService);
        ui.start();
    }
}
