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
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


public class PhenoteFxApplication extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhenoteFxApplication.class);
    private ConfigurableApplicationContext applicationContext;

    static public final String PHENOTEFX_NAME_KEY = "phenotefx.name";
    static private final String PHENOTEFX_VERSION_PROP_KEY = "phenotefx_version";


    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(StockUiApplication.class).run();
        ClassPathResource applicationProps =  new ClassPathResource("application.properties");
        // export app's version into System properties
       try (InputStream is = new FileInputStream(applicationProps.getFilename())) {
            Properties properties = new Properties();
            properties.load(is);
            String version = "1.2";//properties.getProperty(FENOMINAL_VERSION_PROP_KEY, "unknown version");
            System.setProperty(PHENOTEFX_VERSION_PROP_KEY, version);
            String name = properties.getProperty(PHENOTEFX_NAME_KEY, "PhenoteFX");
            System.setProperty(PHENOTEFX_NAME_KEY, name);
        } catch (IOException e) {
           LOGGER.error("Could not load application properties: {}", e.getMessage());
        }
        File f = applicationContext.getBean("appHomeDir", File.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        // save properties
        final Properties pgProperties = applicationContext.getBean("pgProperties", Properties.class);
        final Path configFilePath = applicationContext.getBean("configFilePath", Path.class);
        try (OutputStream os = Files.newOutputStream(configFilePath)) {
            pgProperties.store(os, "Fenominal properties");
        }
        Platform.exit();
        // close the context
        applicationContext.close();
    }


    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }

}
