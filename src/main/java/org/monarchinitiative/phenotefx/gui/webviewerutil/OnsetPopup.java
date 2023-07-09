package org.monarchinitiative.phenotefx.gui.webviewerutil;

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

import javafx.stage.Stage;


/**
 * Created by peter on 01.07.17.
 */
public class OnsetPopup extends WebViewerPopup {

    private static final String INDENT_1 = "&#x2022;"; //"&nbsp;&nbsp;";
    private static final String INDENT_2 = "&#x2022;&#x2022;"; //"&nbsp;&nbsp;&nbsp;&nbsp;";


    private final String html;
    public OnsetPopup(Stage stage) {
        super(stage);
        this.html = getHTML();
    }

    @Override
    public void popup() {
        showHtmlContent("HPO Onset Categories", html);
    }


    private String getRow(String cell1, String cell2) {
        return String.format("<tr><td>%s</td><td>%s</td></tr>\n", cell1, cell2);
    }
    private String getRowIndent1(String cell1, String cell2) {
        return String.format("<tr><td>%s%s</td><td>%s</td></tr>\n", INDENT_1, cell1, cell2);
    }
    private String getRowIndent2(String cell1, String cell2) {
        return String.format("<tr><td>%s%s</td><td>%s</td></tr>\n", INDENT_2, cell1, cell2);
    }

    private String getHTML() {
        StringBuilder sb = new StringBuilder();
        String htmlHeader = String.format("""
                <html><head>%s</head>
                <body><h3>HPO Onset</h3><p>
                """,inlineCSS());
        sb.append(htmlHeader);
        sb.append("<table>");
        sb.append("<thead><tr><td>Term</td><td>Definition</td></tr></thead>\n");
        sb.append("<tbody>\n");
        sb.append(getRow("Antenatal onset", "Onset prior to birth."));
        sb.append(getRowIndent1("Embryonal onset", "Onset of disease at up to 8 weeks following fertilization (corresponding to 10 weeks of gestation)."));

        sb.append(getRowIndent1("Fetal onset", "Onset prior to birth but after 8 weeks of embryonic development (corresponding to a gestational age of 10 weeks)."));
        sb.append(getRowIndent2("Late first trimester onset", "Onset at 11 0/7 to 13 6/7 weeks of gestation (inclusive)."));
        sb.append(getRowIndent2("Second trimester onset", "Onset at gestational ages from 14 0/7 weeks to 27 6/7 (inclusive)."));
        sb.append(getRowIndent2("Third trimester onset", "Onset at gestational ages from 28 weeks and zero days (28+0) of gestation and beyond."));
        sb.append(getRow("Congenital onset", "present at birth."));
        sb.append(getRow("Neonatal onset", "first 28 days of life"));
        sb.append(getRow("Pediatric onset", "before the age of 15 years, but excluding neonatal or congenital onset"));
        sb.append(getRowIndent1("Infantile onset", "28 days to one year of life"));
        sb.append(getRowIndent1("Childhood onset", "1 to 5 years"));
        sb.append(getRowIndent1("Juvenile onset", "5 to 15 years"));
        sb.append(getRow("Adult onset", "16 years or later"));
        sb.append(getRowIndent1("Young adult onset", "16-40 y"));
        sb.append(getRowIndent2("Early young adult onset","16-19 y"));
        sb.append(getRowIndent2("Intermediate young adult onset","19-25 y"));
        sb.append(getRowIndent2("Late young adult onset","25-40 y"));
        sb.append(getRowIndent1("Middle age onset", "40-60 y"));
        sb.append(getRowIndent1("Late onset", "0ver 60 y"));
        sb.append("</tbody>\n");
        sb.append("</table>\n");

        String htmlFooter =   "</p>\n</body></html>";
        sb.append(htmlFooter);
        return sb.toString();
    }


}
