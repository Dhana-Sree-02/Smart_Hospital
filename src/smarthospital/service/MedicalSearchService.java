package smarthospital.service;

import java.util.*;
import smarthospital.model.MedicalRecord;
import smarthospital.repository.MedicalRecordRepository;
import smarthospital.co1.StringAlgorithms;
import smarthospital.co2.SuffixStructures;

/**
 * Service orchestrating medical-record queries powered by CO1 and CO2 algorithms.
 * 
 * Features:
 * - Keyword search across clinical notes using KMP, Rabin-Karp, Z-Algorithm, and Naive matching.
 * - Suffix Array indexed search over medical text corpus for fast repeated substring queries.
 * - Repeated-prefix and common phrase analysis using Kasai's LCP array.
 * - Mapping matching substring offsets back to specific medical record IDs.
 */
public class MedicalSearchService {

    private final MedicalRecordRepository repository;

    public MedicalSearchService(MedicalRecordRepository repository) {
        this.repository = repository;
    }

    public List<MedicalRecord> all() {
        return repository.all();
    }

    public List<MedicalRecord> byPatient(String patientId) {
        return repository.byPatient(patientId);
    }

    public void addRecord(MedicalRecord record) {
        repository.save(record);
    }

    /**
     * CO1: Keyword search across all medical records using the specified string algorithm.
     * Searches both diagnosis and clinical notes.
     * 
     * @param keyword   Search term.
     * @param algorithm 1: KMP, 2: Rabin-Karp, 3: Z-Algorithm, 4: Naive.
     * @return List of matching MedicalRecord objects.
     */
    public List<MedicalRecord> searchRecords(String keyword, int algorithm) {
        List<MedicalRecord> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        String query = keyword.trim().toLowerCase();

        for (MedicalRecord record : repository.all()) {
            String content = (record.getDiagnosis() + " " + record.getNotes()).toLowerCase();
            boolean match = false;

            switch (algorithm) {
                case 1:
                    match = StringAlgorithms.containsKMP(content, query);
                    break;
                case 2:
                    match = StringAlgorithms.containsRabinKarp(content, query);
                    break;
                case 3:
                    match = StringAlgorithms.containsZAlgorithm(content, query);
                    break;
                case 4:
                default:
                    match = StringAlgorithms.containsNaive(content, query);
                    break;
            }

            if (match) {
                results.add(record);
            }
        }
        return results;
    }

    /**
     * Backward-compatible keyword search using Rabin-Karp.
     */
    public List<MedicalRecord> keywordSearch(String keyword) {
        return searchRecords(keyword, 2);
    }

    // =========================================================================
    // CO2: CORPUS-LEVEL SUFFIX ARRAY INDEXING & MULTI-QUERY SEARCH
    // =========================================================================

    /**
     * Pre-indexed medical text corpus supporting repeated O(m log N) queries.
     */
    public static class CorpusIndex {
        private final String combinedText;
        private final int[] suffixArray;
        private final int[] lcpArray;
        private final List<RecordSpan> recordSpans;

        private static class RecordSpan {
            final String recordId;
            final int startOffset;
            final int endOffset;

            RecordSpan(String id, int start, int end) {
                this.recordId = id;
                this.startOffset = start;
                this.endOffset = end;
            }
        }

        public CorpusIndex(List<MedicalRecord> records) {
            StringBuilder sb = new StringBuilder();
            this.recordSpans = new ArrayList<>();

            for (MedicalRecord r : records) {
                int start = sb.length();
                sb.append("[ID:").append(r.getId()).append("] ")
                  .append(r.getDiagnosis()).append(" - ").append(r.getNotes()).append("\n");
                int end = sb.length();
                recordSpans.add(new RecordSpan(r.getId(), start, end));
            }

            this.combinedText = sb.toString();
            this.suffixArray = SuffixStructures.buildSuffixArray(combinedText);
            this.lcpArray = SuffixStructures.buildLcpArray(combinedText, suffixArray);
        }

        public String getCombinedText() {
            return combinedText;
        }

        public int[] getSuffixArray() {
            return suffixArray;
        }

        public int[] getLcpArray() {
            return lcpArray;
        }

        /**
         * Fast binary-search query against precomputed Suffix Array.
         * Returns matching starting positions in the combined corpus.
         */
        public int[] queryPositions(String pattern) {
            return SuffixStructures.searchWithSuffixArray(combinedText, suffixArray, pattern);
        }

        /**
         * Maps matching character offsets back to the corresponding Medical Record IDs.
         */
        public Set<String> queryMatchingRecordIds(String pattern) {
            Set<String> matchedIds = new LinkedHashSet<>();
            int[] positions = queryPositions(pattern);

            for (int pos : positions) {
                for (RecordSpan span : recordSpans) {
                    if (pos >= span.startOffset && pos < span.endOffset) {
                        matchedIds.add(span.recordId);
                        break;
                    }
                }
            }
            return matchedIds;
        }

        /**
         * Analyzes repeated clinical phrases using Kasai's LCP array.
         */
        public SuffixStructures.RepeatedSubstringResult analyzeRepeatedPhrases() {
            return SuffixStructures.findLongestRepeatedSubstring(combinedText);
        }
    }

    /**
     * Builds a reusable indexed corpus of all current medical records.
     */
    public CorpusIndex buildCorpusIndex() {
        return new CorpusIndex(repository.all());
    }

    /**
     * Checks if a patient's records contain keyword using Suffix Array binary search.
     */
    public boolean suffixSearch(String patientId, String keyword) {
        List<MedicalRecord> patientRecords = repository.byPatient(patientId);
        if (patientRecords.isEmpty()) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (MedicalRecord r : patientRecords) {
            sb.append(r.getDiagnosis()).append(" ").append(r.getNotes()).append(" ");
        }
        return SuffixStructures.containsUsingSuffixArrayBinarySearch(sb.toString(), keyword);
    }
}
