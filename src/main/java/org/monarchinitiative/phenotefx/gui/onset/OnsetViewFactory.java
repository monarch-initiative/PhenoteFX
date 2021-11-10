package org.monarchinitiative.phenotefx.gui.onset;

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

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by peter on 01.07.17.
 */
public class OnsetViewFactory {


    public static void showOnset() {
        Stage window;
        String windowTitle = "HPO Onset";
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







    private static String getHTML() {
        List<String> onset = new ArrayList<>();
        onset.add("Pediatric onset: before the age of 15 years, but excluding neonatal or congenital onset.");
        onset.add("Infantile onset: 28 days to one year of life.");
        onset.add("Childhood onset: 1 to 5 years");
        onset.add("Juvenile onset: 5 to 15 years.");
        onset.add("Adult onset: 16 years or later.");
        onset.add("Young adult onset: 16-40 y");
        onset.add("Middle age onset: 40-60 y");
        onset.add("Late onset: over 60 years");
        onset.add("Neonatal onset: first 28 days of life");
        onset.add("Congenital onset: present at birth");
        onset.add("Antenatal onset: Onset prior to birth.");
        onset.add("Fetal onset: Onset prior to birth but after 8 weeks of embryonic development (corresponding to a gestational age of 10 weeks).");
        onset.add("Embryonal onset: Onset of disease at up to 8 weeks following fertilization (corresponding to 10 weeks of gestation).");

        StringBuilder sb = new StringBuilder();
        String htmlHeader = String.format("<html><head>%s</head>\n"+
            "<body><h3>HPO Onset</h3>"+
            "<p><ul>\n",inlineCSS());
        sb.append(htmlHeader);
        for (String item : onset) {
            sb.append("<li>").append(item).append("</li>\n");
        }
        String htmlFooter =   "</ul></p>\n</body></html>";
        sb.append(htmlFooter);
        return sb.toString();
    }


}
