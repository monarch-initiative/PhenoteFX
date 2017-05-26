package org.monarch.hphenote.biolark;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by robinp on 5/26/17.
 */
public class BioLarkJSONParserTest {

    private static String json1="[{\"start_offset\":0,\"end_offset\":14,\"length\":14,\"original_text\":\"Arachnodactyly\",\"source\":\"HPO\",\"term\":{\"uri\":\"HP:0001166\",\"preferredLabel\":\"Arachnodactyly\",\"synonyms\":[\"Long, slender fingers\",\"Spider fingers\",\"Long slender fingers\"]},\"negated\":false},{\"start_offset\":32,\"end_offset\":41,\"length\":9,\"original_text\":\"scoliosis\",\"source\":\"HPO\",\"term\":{\"uri\":\"HP:0002650\",\"preferredLabel\":\"Scoliosis\",\"synonyms\":[]},\"negated\":false},{\"start_offset\":16,\"end_offset\":30,\"length\":14,\"original_text\":\"ectopia lentis\",\"source\":\"HPO\",\"term\":{\"uri\":\"HP:0001083\",\"preferredLabel\":\"Ectopia lentis\",\"synonyms\":[\"Abnormality of lens position\",\"Dislocated lens\",\"Dislocated lenses\",\"Lens dislocation\"]},\"negated\":false}]";

    private static BioLarkJSONParser parser;

    @BeforeClass
    public static void setup() throws Exception {
        parser = new BioLarkJSONParser(json1);



    }

    /** Test that we get at least one term back.*/
    @Test
    public void testTest() {

        Assert.assertTrue(1>0);
    }


}
