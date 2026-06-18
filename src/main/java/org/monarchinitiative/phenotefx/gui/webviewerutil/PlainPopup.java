package org.monarchinitiative.phenotefx.gui.webviewerutil;

import javafx.stage.Stage;

public class PlainPopup extends WebViewerPopup {

    private final String html;



    public PlainPopup(String html, Stage stage) {
        super(stage);
        this.html = html;
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