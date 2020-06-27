package org.monarchinitiative.phenotefx;

import org.monarchinitiative.phenotefx.gui.infoviewer.InfoViewerFactory;

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

    public RowTallyTool(String row) {
        items = new HashMap<>();
        String []fields = row.split("\t");
        for (String f : fields) {
            String[] features = f.split("[,;.]");
            for (String feat : features) {
                if (feat.isEmpty() || feat.equals("a") || feat.equals("n")) {
                    continue; // reduce the noise, somewhat
                }
                items.putIfAbsent(feat, 0);
                items.merge(feat, 1, Integer::sum);
            }
        }
    }

    public void showTable() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHTMLHead());
        sb.append(getTable());
        sb.append(getFooter());
        InfoViewerFactory.openDialog(sb.toString());
    }


    private String getHTMLHead() {
        return  "<html><body>\n" +
                inlineCSS() +
                "<h1>PhenoteFX: Tallying Phenotypes from Clipboard (Row)</h1>";
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
