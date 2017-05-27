package org.monarch.hphenote.biolark;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by peter on 27.05.17.
 */
public class PairTest {


    @Test
    public void testGetLeft() {
        Pair pair = new Pair(3,4);
        Assert.assertEquals(new Integer(3),pair.getLeft());
    }


    @Test
    public void testGetRight() {
        Pair pair = new Pair(3,4);
        Assert.assertEquals(new Integer(4),pair.getRight());
    }

    @Test
    public void testEquals() {
        Pair pair1 = new Pair(3,4);
        Pair pair2 = new Pair(3,4);
        Assert.assertTrue(pair1.equals(pair2));
    }

    @Test
    public void testNotEquals() {
        Pair pair1 = new Pair(3,4);
        Pair pair2 = new Pair(30,4);
        Assert.assertFalse(pair1.equals(pair2));
    }


}
