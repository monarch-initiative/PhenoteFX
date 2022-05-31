package org.monarchinitiative.phenotefx.gui.webviewerutil;

import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.model.Settings;

public class SettingsPopup extends WebViewerPopup {

    private final String html;



    public SettingsPopup(Settings settings, Stage stage) {
        super(stage);
        this.html = getHTML(settings);
    }

    private String getHTML(Settings settings) {
        return String.format("<html><head>%s</head>\n"+
                        "<body><h2>HPO Phenote Settings</h2>"+
                        "<p>These parameters must be set (via the Setup menu) before annotating.</p>" +
                        "<p><ul>"+
                        "<li>Biocurator ID: %s</li>"+
                        "<li>HPO file: %s</li>"+
                        "<li>Default directory: %s</li>"+
                        "</ul></p>"+
                        "</body></html>",
                inlineCSS(),
                settings.getBioCuratorId(),
                settings.getHpoFile(),
                settings.getAnnotationFileDirectory()
        );
    }

    protected String inlineCSS() {
        return "<style>\n" +
                "  html { margin: 20; padding: 20; }" +
                "body { font: 100% georgia, sans-serif; line-height: 1.88889;color: #001f3f; margin: 0; padding: 0; }"+
                "p { margin-top: 10;text-align: justify;}"+
                "h2 {font-family: 'serif';font-size: 1.4em;font-style: normal;font-weight: bold;"+
                "letter-spacing: 1px; margin-bottom: 0; color: #001f3f;}"+
                "  </style>";
    }

    @Override
    public void popup() {
        showHtmlContent("Settings", html);
    }
}
