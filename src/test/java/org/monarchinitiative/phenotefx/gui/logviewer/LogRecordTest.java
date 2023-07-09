package org.monarchinitiative.phenotefx.gui.logviewer;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogRecordTest {



    @Test
    public void testLine1() {
        String line = "[INFO] [2023-07-09T11:04:13] - PhenoteFxApplication - Setting version to 0.8.32";
        Optional<LogRecord> opt = LogRecord.fromLine(line);
        assertTrue(opt.isPresent());
        LogRecord record = opt.get();
        assertEquals(Level.INFO, record.getLevel());
        assertEquals("2023-07-09T11:04:13", record.getTimestamp());
        assertEquals("PhenoteFxApplication", record.getContext());
        assertEquals("Setting version to 0.8.32", record.getMessage());
    }
}
