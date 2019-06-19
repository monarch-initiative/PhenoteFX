package org.monarchinitiative.phenotefx.gui.phenotypecommondisease;

import base.Fraction;
import base.OntoTerm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CurationMeta;
import model.Evidence;
import model.Frequency;
import model.Phenotype;
import ontology_term.BiologySex;
import org.jetbrains.annotations.NotNull;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.gui.PopUps;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhenotypeCDM {

    static ObjectMapper mapper = new ObjectMapper();

    public static PhenoRow toPhenoRow(@NotNull OntoTerm disease, @NotNull Phenotype phenotype){

        model.Onset onset = phenotype.getOnset();
        TermId onsetId = null;
        String onsetString = "";
        try {
            onsetId = TermId.of(onset.getStage().getId());
            onsetString = mapper.writeValueAsString(onset);
        } catch (Exception e){
            onsetString = "";
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

        String f_string = "";
        if (phenotype.getFrequency() != null) {
            f_string = frequencyName(phenotype.getFrequency());
        }

        PhenoRow row = new PhenoRow(
                disease.getId(),
                disease.getLabel(),
                TermId.of(phenotype.getPhenotype().getId()),
                phenotype.getPhenotype().getLabel(),
                onsetId,
                onsetString,
                f_string,
                phenotype.getImpactedSex().getLabel(),
                Boolean.toString(!phenotype.isPresent()),
                modifierString,
                "",
                evidenceType,
                evidenceId,
                curator
        );

        return row;
    }

    private static String frequencyName(model.Frequency f){
        String result = "";

        if (f.isFraction()){
            result = f.getFraction().getNumerator() + "/" + f.getFraction().getDenominator();
        } else if (f.isApproximate()){
            result = f.getApproximate().getLabel();
        } else if (f.isRange()){
            try {
                result = mapper.writeValueAsString(f);
            } catch (JsonProcessingException e) {
                result = "unable to read";
            }
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

            model.Frequency frequency = null;
            //this returns the frequency term id (or numbers)
            String frequencyString = row.getFrequency();
 System.out.println("frequency string: " + frequencyString);
            String frequencyLabel = null;
            double nominator;
            double denominator;
            double percentage;
            if (frequencyString != null && !frequencyString.isEmpty()){
                if (frequencyString.contains("%")){
                    percentage = Double.parseDouble(frequencyString.replace("%", ""));
                    frequency = new Frequency.Builder()
                            .fraction(new Fraction(percentage, 100))
                            .build();
                } else if (frequencyString.contains("/")){
                    String[] elems = frequencyString.split("/");
                    nominator = Double.parseDouble(elems[0]);
                    denominator = Double.parseDouble(elems[1]);
                    frequency = new Frequency.Builder()
                            .fraction(new Fraction(nominator, denominator))
                            .build();
                } else {
                    String frequencyId = frequencyString;
                    //find the frequencyt label
                    if (frequencyName2IdMap.values().contains(frequencyId)){
                        for (Map.Entry<String, String> e : frequencyName2IdMap.entrySet()){
                            if (e.getValue().equals(frequencyId)){
                                frequencyLabel = e.getKey();
                                break;
                            }
                        }
                        frequency = new Frequency.Builder()
                                .approximate(new OntoTerm(frequencyId, frequencyLabel))
                                .build();
                    } else {
                        PopUps.showInfoMessage(frequencyId, "frequency term not recognized: ");
                    }
                }
            }

            Evidence evidence = null;
            String evidenceTypeString = row.getEvidence();
            Evidence.EvidenceType evidenceType = null;
            if (evidenceTypeString == null || evidenceTypeString.isEmpty()){
                // do nothing
            } else if (evidenceTypeString.equals("PCS")) {
                evidenceType = Evidence.EvidenceType.PCS;
                String pub = row.getPublication();
                evidence = new Evidence.Builder().evidenceType(evidenceType).evidenceId(pub).build();
            } else if (evidenceTypeString.equals("IEA")) {
                evidenceType = Evidence.EvidenceType.IEA;
                evidence = new Evidence.Builder().evidenceType(evidenceType).build();
            } else if (evidenceTypeString.equals("TAS")){
                evidenceType = Evidence.EvidenceType.TAS;
                evidence = new Evidence.Builder().evidenceType(evidenceType).build();
            }

            OntoTerm modifier = null;
            String modifierLabel = row.getModifier();
            if (modifierLabel != null && !modifierLabel.isEmpty()){
                String modifierId = modifierName2IdMap.get(modifierLabel);
                modifier = new OntoTerm(modifierId, modifierLabel);
            }

            Phenotype phenotype = new Phenotype.Builder()
                    .phenotype(new OntoTerm(row.getPhenotypeID(), row.getPhenotypeName()))
                    .isPresent(!Boolean.parseBoolean(row.getNegation()))
                    .modifer(modifier)
                    .impactedSex(sex)
                    .addFrequency(frequency)
                    .evidence(evidence)
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
