package org.monarchinitiative.phenotefx.gui.prevalencepopup;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PrevalenceFactoryTest {

    private PrevalenceFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new PrevalenceFactory(null, "JGM:azhang");
    }

    @Test
    public void openDiag() throws Exception {
        boolean updated = factory.openDiag();
    }

    @Test
    public void getPrevalences() throws Exception {
    }

}