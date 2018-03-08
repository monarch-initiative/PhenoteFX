package org.monarchinitiative.phenotefx.gui.logviewer;

/**
 * log4j levels,which are
 * TRACE &lt; DEBUG &lt; INFO &lt; WARN &lt; ERROR &lt; FATAL
 */
public enum Level {
    TRACE(1), DEBUG(2), INFO(3), WARN(4), ERROR(5),FATAL(6);

    int val;

    Level(int l){val=l;}

    public int getVal() { return val; }


    /**
     * Convert from String to enum constant. We should never reach the
     * default case, but if we do just return TRACE
     * @param s A string representing the log level
     * @return corresponding enum constant.
     */
    public static Level string2level(String s) {
        switch (s) {
            case "TRACE": return TRACE;
            case "DEBUG": return DEBUG;
            case "INFO": return INFO;
            case "WARN": return WARN;
            case "ERROR": return ERROR;
            case "FATAL": return FATAL;
            default: return TRACE;
        }
    }
}
