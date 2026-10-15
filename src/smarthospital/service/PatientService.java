package smarthospital.service;

import java.util.*;
import smarthospital.model.Patient;
import smarthospital.repository.PatientRepository;
import smarthospital.co1.StringAlgorithms;
import smarthospital.co3.AdvancedDP;

/**
 * Service managing patient operations with CO1 (KMP search) and CO3 (Edit Distance similarity).
 */
public class PatientService {

    private final PatientRepository repository;

    // Fictional catalog of known medical terms for typo-tolerant lookup
    private static final String[] KNOWN_DIAGNOSES = {
        "Type 2 Diabetes",
        "Hypertension",
        "Asthma",
        "Migraine",
        "Fracture",
        "Pneumonia",
        "Bronchitis",
        "Allergic Rhinitis",
        "Osteoarthritis",
        "Acute Coronary Syndrome",
        "Dengue Fever",
        "Urinary Tract Infection"
    };

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public List<Patient> all() {
        return repository.all();
    }

    public Patient byId(String id) {
        return repository.byId(id);
    }

    public void register(Patient patient) {
        repository.save(patient);
    }

    /**
     * CO1/KMP: Searches multiple patient fields (ID, name, diagnosis, history) for a keyword.
     * 
     * @param keyword Search term.
     * @return List of matching Patient records.
     */
    public List<Patient> search(String keyword) {
        List<Patient> matches = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return matches;
        }

        String query = keyword.trim().toLowerCase();

        for (Patient p : repository.all()) {
            String combinedText = (p.getId() + " " + p.getName() + " " + p.getDiagnosis() + " " + p.getHistory()).toLowerCase();
            if (StringAlgorithms.containsKMP(combinedText, query)) {
                matches.add(p);
            }
        }
        return matches;
    }

    /**
     * CO3: Finds and ranks similar diagnosis terms using Damerau-Levenshtein & Levenshtein distance.
     * 
     * @param term Entered query string.
     * @return List of formatted suggestion strings with edit distances and explanation.
     */
    public List<String> similarDiagnoses(String term) {
        List<String> suggestions = new ArrayList<>();
        if (term == null || term.trim().isEmpty()) {
            return suggestions;
        }

        String query = term.trim();

        // Collect unique terms from registered patients and the known catalog
        Set<String> uniqueTerms = new LinkedHashSet<>();
        for (String d : KNOWN_DIAGNOSES) {
            uniqueTerms.add(d);
        }
        for (Patient p : repository.all()) {
            if (p.getDiagnosis() != null && !p.getDiagnosis().isEmpty()) {
                uniqueTerms.add(p.getDiagnosis());
            }
        }

        String[] candidateArray = uniqueTerms.toArray(new String[0]);
        AdvancedDP.DiagnosisRanking[] rankings = AdvancedDP.rankDiagnoses(query, candidateArray);

        // Include terms with reasonable distance
        int maxAcceptableDistance = Math.max(3, query.length() / 2);

        for (AdvancedDP.DiagnosisRanking r : rankings) {
            if (r.damerauDist <= maxAcceptableDistance) {
                String line = String.format("%-26s | Damerau: %d | Levenshtein: %d | Weighted Cost: %d",
                        r.term, r.damerauDist, r.levenshteinDist, r.weightedDist);
                suggestions.add(line);
            }
        }

        return suggestions;
    }
}
