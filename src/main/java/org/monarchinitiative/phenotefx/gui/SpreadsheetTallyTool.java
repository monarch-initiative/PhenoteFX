package org.monarchinitiative.phenotefx.gui;

/*
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

import org.monarchinitiative.phenotefx.gui.infoviewer.InfoViewerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A simple class to tally up mentions of features from typical spread sheets in which column 1 is the
 * name of the item and the other columns represent observations in individuals.
 */
public class SpreadsheetTallyTool {

    private final File spreadsheet;
    private final Map<String, List<String>> items;

    public SpreadsheetTallyTool() {
        spreadsheet = PopUps.selectFileToOpen(null, null, "Open spreadsheet");
        items = new HashMap<>();
    }

    public void calculateTally() {
        int uniqueint = 0;
        if (! spreadsheet.exists()) {
            PopUps.showInfoMessage("Error", "Could not find spreadsheet");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(spreadsheet))) {
            String line;
            line = br.readLine(); // skip header
            while ((line=br.readLine()) != null) {
                String []fields = line.split("\t");
                if (fields.length<2) {
                    System.out.println("[ERROR] Skipping line (only one field):" + line);
                    continue;
                }
                String itemname = fields[0];
                if (itemname == null || itemname.isEmpty()) {
                    continue;
                }
                if (items.containsKey(itemname)){
                    // duplicate field name
                    itemname = String.format("%s-%d", itemname, uniqueint++);
                } else {
                    items.put(itemname, new ArrayList<>());
                }
                for (int i=1;i<fields.length;i++) {
                    String field = fields[i] ==null ? "n/a" : fields[i]; // replace null entries
                    items.get(itemname).add(field);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getHTMLHead());
        for (String item : items.keySet()) {

            List<String> itemlist = items.get(item);
            Map<String, Long> counted = itemlist.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            sb.append(getTable(item, counted));
        }
        sb.append(getFooter());
        InfoViewerFactory.openDialog(sb.toString());
    }


    private static String getTable(String title, Map<String, Long> counted) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>\n<caption>").append( title).append( "</caption>\n")
                 .append("  <tr><th>Item</th><th>Count</th></tr>\n");
        for (Map.Entry<String, Long> entry : counted.entrySet()) {
            sb.append("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>\n");
        }
        sb.append("</table>\n<br/><br/>");
        return sb.toString();
    }



    private static String getHTMLHead() {
        return  "<html><body>\n" +
                inlineCSS() +
                "<h1>PhenoteFX: Tallying Phenotypes from Spreadsheet</h1>";

    }

    private static String getFooter() {
        return  "</body></html>";
    }

    private static String inlineCSS() {
        return "<head><style>\n" +
                "  html { margin: 0; padding: 0; }" +
                "body { font: 75% georgia, sans-serif; line-height: 1.88889;color: #001f3f; margin: 10; padding: 10; }"+
                "p { margin-top: 0;text-align: justify;}"+
                "h2,h3 {font-family: 'serif';font-size: 1.4em;font-style: normal;font-weight: bold;"+
                "letter-spacing: 1px; margin-bottom: 0; color: #001f3f;}"+
                "caption {\n" +
                "  font-weight: bold;\n" +
                "  text-align: left;\n" +
                "  border-style: solid;\n" +
                "  border-width: 1px;\n" +
                "  border-color: #666666;\n" +
                "}" +
                "table {\n" +
                "font-family: \"Lato\",\"sans-serif\";   }       /* added custom font-family  */\n" +
                " \n" +
                "table.one {                                  \n" +
                "margin-bottom: 3em; \n" +
                "border-collapse:collapse;   }   \n" +
                " \n" +
                "td {    \n" +
                "text-align: center;     \n" +
                "width: 10em;                    \n" +
                "padding: 1em;       }       \n" +
                " \n" +
                "th {   \n" +
                "text-align: center;                 \n" +
                "padding: 1em;\n" +
                "background-color: #e8503a;       /* added a red background color to the heading cells  */\n" +
                "color: white;       }                 /* added a white font color to the heading text */\n" +
                " \n" +
                "tr {    \n" +
                "height: 1em;    }\n" +
                " \n" +
                "table tr:nth-child(even) {            /* added all even rows a #eee color  */\n" +
                "    background-color: #eee;     }\n" +
                " \n" +
                "table tr:nth-child(odd) {            /* added all odd rows a #fff color  */\n" +
                "background-color:#fff;      }" +
                "  </style></head>";
    }

}
