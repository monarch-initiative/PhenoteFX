package org.monarch.hphenote.biolark;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import java.util.*;

/**
 * This class is responsible for orgnazing the results of Bio-Lark based parsing of HPO texts.
 * See: Groza T,et al. (2015) Automatic concept recognition using the human
 * phenotype ontology reference and test suite corpora. Database (Oxford). pii: bav005.
 * This class will store results of parsing and present an interface for the JavaFX wdiget that
 * will present the parsed text and suggested HPO terms for vetting by the user.
 * Created by robinp on 5/26/17.
 */
public class BioLark {

    private String jsonText=null;

    private List<Pair> intervals;

    private Set<String> hpoTermLabels;

    private Set<String> hpoTermIDs;

    private Set<String> negatedHpoTermLabels;

    private Set<String> negatedHpoTermIDs;


    public BioLark(String biolarkJSON) {
        this.jsonText=biolarkJSON;
        intervals = new ArrayList<>();
        hpoTermLabels = new HashSet<>();
        hpoTermIDs = new HashSet<>();
        negatedHpoTermLabels = new HashSet<>();
        negatedHpoTermIDs = new HashSet<>();
        parseJSON();
    }


    public Set<String> getHpoTermLabels() {
        return hpoTermLabels;
    }
    public Set<String> getHpoTermIDs() {
        return hpoTermIDs;
    }

    public Set<String> getNegatedHPOTermLabels() {
        return negatedHpoTermLabels;
    }

    public Set<String> getNegatedHPOTermIDs() {
        return negatedHpoTermIDs;
    }

    /** get locations of matchinig terms in original text. */
    public List<Pair> getIntervals() { return intervals; }




    private void parseJSON() {
        JsonParser parser = Json.createParser(new StringReader(jsonText));
        String currentHpoName=null;
        String currentHpoId=null;
        boolean isNegated=false;
        boolean inStartOffset=false;
        boolean inEndOffset=false;
        boolean inPreferredLabel=false;
        boolean inURI=false;
        boolean inNegated=false;
        Integer endOffset=null;
        Integer startOffset=null;
        int objectStack=0; // simulate a simple stack of objects
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch(event) {
                case START_ARRAY:break;
                case END_ARRAY:break;
                case START_OBJECT:
                    objectStack++; break;
                case END_OBJECT:
                    /* when we get here at ZERO and the items are not null,
                     * we are done parsing one list element of the JSON, and we know if the
                     * term is negated or not.
                     */
                    objectStack--;
                    if (objectStack==0) {
                        if (currentHpoId!= null) {
                            if (isNegated) {
                                negatedHpoTermIDs.add(currentHpoId);
                            } else {
                                hpoTermIDs.add(currentHpoId);
                            }
                            currentHpoId=null;
                        }
                        if (currentHpoName != null) {
                            if (isNegated) {
                                negatedHpoTermLabels.add(currentHpoName);
                            } else {
                                hpoTermLabels.add(currentHpoName);
                            }
                            currentHpoName=null;
                        }
                    }
                    break;
                case VALUE_FALSE:
                    if (inNegated) {
                        isNegated=false;
                    }
                    break;
                case VALUE_NULL: break;
                case VALUE_TRUE:
                    if (inNegated) {
                        isNegated=true;
                    }
                    break;
                case KEY_NAME:
                    String value=parser.getString();
                    if (value.equals("preferredLabel"))
                        inPreferredLabel=true;
                    else
                        inPreferredLabel=false;
                    if (value.equals("uri"))
                        inURI=true;
                    else
                        inURI=false;
                    if (value.equals("negated"))
                        inNegated=true;
                    else
                        inNegated=false;
                    if (value.equals("start_offset"))
                        inStartOffset=true;
                    else
                        inStartOffset=false;
                    if (value.equals("end_offset"))
                        inEndOffset=true;
                    else
                        inEndOffset=false;
                    break;
                case VALUE_STRING:
                    String val = parser.getString();
                    if (inPreferredLabel) {
                        currentHpoName=val;
                        inPreferredLabel=false;
                    } else if (inURI) {
                        currentHpoId=val;
                        inURI=false;
                    }
                    break;
                case VALUE_NUMBER:
                    String number=parser.getString();
                    Integer i=null;
                    try {
                        i=Integer.parseInt(number);
                    } catch (NumberFormatException e){
                        // just skip
                    }
                    if (inEndOffset) {
                        endOffset=i;
                        if (startOffset != null && endOffset != null) {
                            Pair p = new Pair(startOffset,endOffset);
                            intervals.add(p);
                            startOffset=null;
                            endOffset=null;
                        }
                    } else if (inStartOffset) {
                        startOffset=i;
                    }
                    break;
            }
        }
        // Sort the data
        Collections.sort(intervals);
    }




}
