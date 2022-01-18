package org.monarchinitiative.phenotefx;

/*-
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2020 Peter Robinson
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

import org.monarchinitiative.phenotefx.gui.webviewerutil.InfoViewerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * For many articles we are curating, the authors put up a row with features for some system such as
 * facial dysmorphism. Each cell contains multiple entries, separated by comma. This tool allows the
 * user to copy such a row to the system clipboard. The individual columns are then separated by tab.
 * We then count up the entries and present a simple HTML table to the curator with counts for each
 * of the entries. This will not work with all supplemental tables, but it seems to be applicable to
 * a decent proportion of them.
 */
public class RowTallyTool {

    private final Map<String, Integer> items;

    private final int totalUsableColumns;

    public RowTallyTool(String row) {
        items = new HashMap<>();
        String []fields = row.split("\t");
        int N = 0;
        for (String f : fields) {
            if (f.isEmpty()){
                continue; // no observation
            } else if (f.equalsIgnoreCase("n/a") ||
                    f.equalsIgnoreCase("NA")) {
                continue; // data not available
            } else {
                N++;
            }
            String[] features = f.split("[,;.]");
            for (String feat : features) {
                feat = feat.trim().toLowerCase();
                if (feat.isEmpty() || feat.equals("a") || feat.equals("n")) {
                    continue; // reduce the noise, somewhat
                }
                items.putIfAbsent(feat, 0);
                items.merge(feat, 1, Integer::sum);
            }
        }
        totalUsableColumns = N;
    }

    public void showTable() {
        String sb = getHTMLHead() +
                getPara() +
                getTable() +
                getFooter();
        InfoViewerFactory.openDialog(sb);
    }


    private String getHTMLHead() {
        return  "<html><body>\n" +
                inlineCSS() +
                "<h1>PhenoteFX: Tallying Phenotypes from Clipboard (Row)</h1>";
    }

    private String getPara() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>Entries tallied up from row of data about phenotypic abnormalities. We counted a ")
                .append("total of ").append(totalUsableColumns).append(" columns with observed phenotype data.<p>");
        return sb.toString();
    }



    private String getTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>\n<caption>Items</caption>\n")
                .append("  <tr><th>Item</th><th>Count</th></tr>\n");
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            sb.append("<tr><td>").
                    append(entry.getKey()).
                    append( "</td><td>").
                    append(entry.getValue()).
                    append("</td></tr>\n");
        }
        sb.append("</table>\n<br/><br/>");
        return sb.toString();
    }

    private String getFooter() {
        return  "</body></html>";
    }

    private String inlineCSS() {
        return """
                <head><style>
                  html { margin: 0; padding: 0; }body { font: 75% georgia, sans-serif; line-height: 1.88889;color: #001f3f; margin: 10; padding: 10; }p { margin-top: 0;text-align: justify;}h2,h3 {font-family: 'serif';font-size: 1.4em;font-style: normal;font-weight: bold;letter-spacing: 1px; margin-bottom: 0; color: #001f3f;}caption {
                  font-weight: bold;
                  text-align: left;
                  border-style: solid;
                  border-width: 1px;
                  border-color: #666666;
                }table {
                font-family: "Lato","sans-serif";   }       /* added custom font-family  */
                \s
                table.one {
                margin-bottom: 3em;\s
                border-collapse:collapse; }
                td {
                text-align: center;
                width: 10em;
                padding: 1em; }
                th {  \s
                text-align: center;
                padding: 1em;
                background-color: #e8503a;
                color: white;   }
                tr {
                height: 1em;    }
                table tr:nth-child(even) {  background-color: #eee; }
                table tr:nth-child(odd) {   background-color:#fff; }
                </style></head>""";
    }

}
