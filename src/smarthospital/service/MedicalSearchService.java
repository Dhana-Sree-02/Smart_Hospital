package smarthospital.service;
import java.util.*;
import smarthospital.model.MedicalRecord;
import smarthospital.repository.MedicalRecordRepository;
import smarthospital.co1.StringAlgorithms;
import smarthospital.co2.SuffixStructures;
/** Medical-record search workflow demonstrating CO1 and CO2. */
public class MedicalSearchService {
    private final MedicalRecordRepository repo;
    public MedicalSearchService(MedicalRecordRepository repo) {
        this.repo=repo;
    }
    public List<MedicalRecord> byPatient(String id) {
        return repo.byPatient(id);
    }
    /** CO1/Rabin-Karp searches diagnosis and notes. */
    public List<MedicalRecord> keywordSearch(String keyword) {
        List<MedicalRecord> r=new ArrayList<>();
        for(MedicalRecord x:repo.all())if(StringAlgorithms.containsRabinKarp(x.getDiagnosis()+" "+x.getNotes(),keyword))r.add(x);
        return r;
    }
    /** CO2 suffix array indexes one record's combined text. */
    public boolean suffixSearch(String patientId,String keyword) {
        for(MedicalRecord x:repo.byPatient(patientId))if(SuffixStructures.containsUsingSuffixArray(x.getDiagnosis()+" "+x.getNotes(),keyword))return true;
        return false;
    }
}
