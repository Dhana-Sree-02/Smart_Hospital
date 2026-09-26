package smarthospital.service;
import java.util.*;
import smarthospital.model.Patient;
import smarthospital.repository.PatientRepository;
import smarthospital.co1.StringAlgorithms;
import smarthospital.co3.AdvancedDP;
/** Business rules for patient operations. CO1 and CO3 are used here. */
public class PatientService {
    private final PatientRepository repo;
    public PatientService(PatientRepository repo) {
        this.repo=repo;
    }
    public List<Patient> all() {
        return repo.all();
    }
    public Patient byId(String id) {
        return repo.byId(id);
    }
    public void register(Patient p) {
        repo.save(p);
    }
    /** CO1/KMP: searches multiple patient fields for a keyword. */
    public List<Patient> search(String keyword) {
        List<Patient> r=new ArrayList<>();
        for(Patient p:repo.all()) {
            String text=p.getId()+" "+p.getName()+" "+p.getDiagnosis()+" "+p.getHistory();
            if(StringAlgorithms.containsKMP(text,keyword))r.add(p);
        }
        return r;
    }
    /** CO3/Levenshtein: finds close diagnosis labels for text-entry assistance. */
    public List<String> similarDiagnoses(String term) {
        List<String> r=new ArrayList<>();
        Set<String> seen=new HashSet<>();
        for(Patient p:repo.all()) {
            String d=p.getDiagnosis();
            int distance=AdvancedDP.levenshteinDistance(term.toLowerCase(),d.toLowerCase());
            if(distance<=Math.max(2,term.length()/3)&&seen.add(d))r.add(d+" (distance="+distance+")");
        }
        return r;
    }
}
