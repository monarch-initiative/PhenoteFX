package org.monarchinitiative.phenotefx.gui.logviewer;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2018 Peter Robinson
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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

class LogRecord {
    private String timestamp;
    private Level  level;
    private String context;
    private String message;
    private static DateFormat df = new SimpleDateFormat("dd-mm-yyyy kk:mm:ss", Locale.US);

    LogRecord(Level level, String date, String context, String message) {
        this.timestamp = date;
        this.level     = level;
        this.context   = context;
        this.message   = message;
    }

   String getTimestamp() {
        return timestamp;
    }

    Level getLevel() {
        return level;
    }

    public String getContext() {
        return context;
    }

    public String getMessage() {
        return message;
    }
}

