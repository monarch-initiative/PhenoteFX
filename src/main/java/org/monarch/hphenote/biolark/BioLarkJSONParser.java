package org.monarch.hphenote.biolark;


import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.stream.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by robinp on 5/26/17.
 * File format of the JSON returned by BioLark
 * <pre>
 *     [{"start_offset":0,"end_offset":14,"length":14,"original_text":"Arachnodactyly","source":"HPO",
 *     "term":{"uri":"HP:0001166","preferredLabel":"Arachnodactyly","synonyms":["Long, slender fingers","Spider fingers","Long slender fingers"]},"negated":false},
 *     {"start_offset":32,"end_offset":41,"length":9,"original_text":"scoliosis","source":"HPO","term":{"uri":"HP:0002650","preferredLabel":"Scoliosis","synonyms":[]},"negated":false},
 *     {"start_offset":16,"end_offset":30,"length":14,"original_text":"ectopia lentis","source":"HPO","term":{"uri":"HP:0001083","preferredLabel":"Ectopia lentis",
 *     "synonyms":["Abnormality of lens position","Dislocated lens","Dislocated lenses","Lens dislocation"]},"negated":false}]";

 * </pre>
 */
public class BioLarkJSONParser {

    private String jsonText=null;

    private List<Pair<Integer>> intervals;

    private List<String> hpoTerms;

    private List<String> negatedHpoTerms;


    public BioLarkJSONParser(String json) {
        jsonText=json;
        intervals = new ArrayList<>();
        hpoTerms = new ArrayList<>();
        negatedHpoTerms = new ArrayList<>();
        parseJSON();
    }





    private void parseJSON() {
        JsonParser parser = Json.createParser(new StringReader(jsonText));
        Pair<Integer> pair=null;
        Integer left=null,right=null;
        String currentHpo=null;
        boolean isNegated=false;
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch(event) {
                case START_ARRAY:
                case END_ARRAY:
                case START_OBJECT:
                case END_OBJECT:
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_TRUE:
                    System.out.println(event.toString());
                    break;
                case KEY_NAME:

                    System.out.print(event.toString() + " " +
                            parser.getString() + " - ");
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    System.out.println(event.toString() + " " +
                            parser.getString());
                    break;
            }
        }
    }
}
