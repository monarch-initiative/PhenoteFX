package org.monarch.hphenote.biolark;

/*
 * #%L
 * HPhenote
 * %%
 * Copyright (C) 2017 Peter Robinson
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
