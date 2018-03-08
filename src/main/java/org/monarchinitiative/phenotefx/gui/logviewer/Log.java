package org.monarchinitiative.phenotefx.gui.logviewer;


import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

class Log {
    private static final int MAX_LOG_ENTRIES = 1_000_000;

    private final BlockingDeque<LogRecord> log = new LinkedBlockingDeque<>(MAX_LOG_ENTRIES);

    void drainTo(Collection<? super LogRecord> collection) {
        log.drainTo(collection);
    }

    void offer(LogRecord record) {
        log.offer(record);
    }
}

