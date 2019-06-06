package org.monarchinitiative.phenotefx.gui.phenotypecommondisease;

import base.OntoTerm;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CurationMeta;
import model.Evidence;
import model.Frequency;
import model.Phenotype;
import ontology_term.BiologySex;
import org.jetbrains.annotations.NotNull;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhenotypeCDM {

    static ObjectMapper mapper = new ObjectMapper();

    public static List<PhenoRow> ofCommonDisease(@NotNull OntoTerm disease, @NotNull Phenotype phenotype){

        List<PhenoRow> phenoRows = new ArrayList<>();

        model.Onset onset = phenotype.getOnset();
        TermId onsetId = null;
        String onsetString = "";
        try {
            onsetId = TermId.of(onset.getStage().getId());
            onsetString = mapper.writeValueAsString(onset);
        } catch (Exception e){
            onsetString = "[json error]";
        }
        String modifierString = "";
        if (phenotype.getModifier() != null){
            modifierString = phenotype.getModifier().getLabel();
        }
        String evidenceType = "";
        String evidenceId = "";
        if (phenotype.getEvidence() != null){
            evidenceType = phenotype.getEvidence().getEvidenceType().toString();
            evidenceId = phenotype.getEvidence().getEvidenceId();
        }
        String curator = "";
        if (phenotype.getCurationMeta() != null){
            curator = phenotype.getCurationMeta().getCurator();
        }



        for (Frequency f : phenotype.getFrequencies()){
            PhenoRow row = new PhenoRow(
                    disease.getId(),
                    disease.getLabel(),
                    TermId.of(phenotype.getPhenotype().getId()),
                    phenotype.getPhenotype().getLabel(),
                    onsetId,
                    onsetString,
                    frequencyName(f),
                    phenotype.getImpactedSex().getLabel(),
                    Boolean.toString(!phenotype.isPresent()),
                    modifierString,
                    "no description",
                    evidenceType,
                    evidenceId,
                    curator
            );
            phenoRows.add(row);
        }
        return phenoRows;
    }

    private static String frequencyName(model.Frequency f){
        String result = "";

        try {
            result = mapper.writeValueAsString(f);
        } catch (Exception e) {
            result = "";
        }

        return result;
    }

    public static List<Phenotype> toCommonDisease(List<PhenoRow> phenoRows,
                                Map<String, String> frequencyName2IdMap,
                                Map<String, String> modifierName2IdMap,
                                String curator){

        List<Phenotype> phenotypes = new ArrayList<>();
        if (phenoRows == null || phenoRows.isEmpty()){
            return phenotypes;
        }
        for (PhenoRow row : phenoRows) {
            String sexString = row.getSex();
            OntoTerm sex;
            if (sexString.toLowerCase().equals("male")) {
                sex = BiologySex.MALE;
            } else if (sexString.toLowerCase().equals("female")) {
                sex = BiologySex.FEMALE;
            } else {
                sex = BiologySex.UNISEX;
            }
            OntoTerm frequencyterm = new OntoTerm(frequencyName2IdMap.get(row.getFrequency()), row.getFrequency());
            String evidenceTypeString = row.getEvidence();
            Evidence.EvidenceType evidenceType;
            if (evidenceTypeString.equals("PCS")) {
                evidenceType = Evidence.EvidenceType.PCS;
            } else if (evidenceTypeString.equals("IEA")) {
                evidenceType = Evidence.EvidenceType.IED;
            } else {
                evidenceType = Evidence.EvidenceType.TAS;
            }
            Phenotype phenotype = new Phenotype.Builder()
                    .phenotype(new OntoTerm(row.getPhenotypeID(), row.getPhenotypeName()))
                    .isPresent(!Boolean.parseBoolean(row.getNegation()))
                    .modifer(new OntoTerm(modifierName2IdMap.get(row.getModifier()), row.getModifier()))
                    .impactedSex(sex)
                    .addFrequency(new model.Frequency.Builder().approximate(frequencyterm).build())
                    .evidence(new Evidence.Builder().evidenceType(evidenceType).evidenceId(row.getPublication()).build())
                    .curationMeta(new CurationMeta.Builder()
                            .curator(curator)
                            .timestamp(LocalDate.now()) //TODO: we need to save curation time
                            .build())
                    .build();

            phenotypes.add(phenotype);
        }
        return phenotypes;
    }
}
