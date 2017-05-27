package org.monarch.hphenote.biolark;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by robinp on 5/26/17.
 */
public class BioLarkTest {
    private static String json1="[{\"start_offset\":0,\"end_offset\":14,\"length\":14,\"original_text\":\"Arachnodactyly\",\"source\":\"HPO\",\"term\":{\"uri\":\"HP:0001166\",\"preferredLabel\":\"Arachnodactyly\",\"synonyms\":[\"Long, slender fingers\",\"Spider fingers\",\"Long slender fingers\"]},\"negated\":false},{\"start_offset\":32,\"end_offset\":41,\"length\":9,\"original_text\":\"scoliosis\",\"source\":\"HPO\",\"term\":{\"uri\":\"HP:0002650\",\"preferredLabel\":\"Scoliosis\",\"synonyms\":[]},\"negated\":false},{\"start_offset\":16,\"end_offset\":30,\"length\":14,\"original_text\":\"ectopia lentis\",\"source\":\"HPO\",\"term\":{\"uri\":\"HP:0001083\",\"preferredLabel\":\"Ectopia lentis\",\"synonyms\":[\"Abnormality of lens position\",\"Dislocated lens\",\"Dislocated lenses\",\"Lens dislocation\"]},\"negated\":false}]";

    private static BioLark parser;

    @BeforeClass
    public static void setup() throws Exception {
        parser = new BioLark(json1);
    }

    /** Test that we get the same intervals back.*/
    @Test
    public void testIntervals() {
        //parser.debugPrint();
        List<Pair> expected = new ArrayList<>();
        expected.add(new Pair(0,14));
        expected.add(new Pair(16,30));
        expected.add(new Pair(32,41));
        List<Pair> actual = parser.getIntervals();
        Assert.assertTrue(expected.equals(actual));
    }

    /** Test that we get the HPO Labels.*/
    @Test
    public void testTermLabels() {
        Set<String> expected = new HashSet<>();
        expected.add("Arachnodactyly");
        expected.add("Scoliosis");
        expected.add("Ectopia lentis");
        Set<String> actual = parser.getHpoTermLabels();
        Assert.assertEquals(expected,actual);
    }

    /** Test that we get the HPO IDs.*/

    @Test
    public void testTermIDs() {
        Set<String> expected = new HashSet<>();
        expected.add("HP:0001166");
        expected.add("HP:0002650");
        expected.add("HP:0001083");
        Set<String> actual = parser.getHpoTermIDs();
        Assert.assertEquals(expected,actual);
    }

}
