package org.monarchinitiative.phenotefx;

/*-
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2021 Peter Robinson
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

import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Main class of the fenominal GUI app
 * @author Peter N Robinson
 */
@SpringBootApplication
public class StockUiApplication {
    public static void main(String[] args) {
        String logPath = Platform.getAbsoluteLogPath();
        //Set before the logger starts.
        System.setProperty("log.name", logPath);
        Logger LOGGER = LoggerFactory.getLogger(StockUiApplication.class);
        System.out.println("LOG == " +LOGGER.toString());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dtime = dtf.format(now);
        LOGGER.error("Starting PhenoteFX: " + dtime);
        Application.launch(PhenoteFxApplication.class, args);
    }
}

