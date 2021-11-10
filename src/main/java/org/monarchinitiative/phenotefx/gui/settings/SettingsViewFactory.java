package org.monarchinitiative.phenotefx.gui.settings;

/*
 * #%L
 * PhenoteFX
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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.gui.PhenoteController;
import org.monarchinitiative.phenotefx.model.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;


/**
 * Created by peter on 01.07.17.
 */
public class SettingsViewFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsViewFactory.class);


    public static void showSettings(Settings settings) {
        LOGGER.info("Showing settings with {}", settings.toString());



        Stage window;
        String windowTitle = "PhenoteFX Settings";
        window = new Stage();
        window.setOnCloseRequest(event -> window.close() );
        window.setTitle(windowTitle);


        window.showAndWait();
    }


    private static String inlineCSS() {
        return "<style>\n" +
                "  html { margin: 0; padding: 0; }" +
    "body { font: 75% georgia, sans-serif; line-height: 1.88889;color: #001f3f; margin: 0; padding: 0; }"+
    "p { margin-top: 0;text-align: justify;}"+
    "h3 {font-family: 'serif';font-size: 1.4em;font-style: normal;font-weight: bold;"+
                "letter-spacing: 1px; margin-bottom: 0; color: #001f3f;}"+
                "  </style>";
    }

    private static String getHTML(Settings settings) {
        return String.format("<html><head>%s</head>\n"+
            "<body><h3>HPO Phenote Settings</h3>"+
            "<p><ul>"+
                "<li>Biocurator ID: %s</li>"+
                "<li>HPO file: %s</li>"+
                "<li>MedGene file: %s</li>"+
                "<li>Default directory: %s</li>"+
            "</ul></p>"+
            "</body></html>",
                inlineCSS(),
                settings.getBioCuratorId(),
                settings.getHpoFile(),
                settings.getMedgenFile(),
                settings.getDefaultDirectory()
        );
    }


}
