package org.monarchinitiative.hphenote.gui.settings;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.hphenote.model.Settings;



/**
 * Created by peter on 01.07.17.
 */
public class SettingsViewFactory {


    public static void showSettings(Settings settings) {
        Stage window;
        String windowTitle = "HPO Phenote Settings";
        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);

        SettingsView view = new SettingsView();
        SettingsPresenter presenter = (SettingsPresenter) view.getPresenter();
        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    window.close();
                    break;
                case CANCEL:
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }

        });
        String html=getHTML(settings);
        presenter.setData(html);

        window.setScene(new Scene(view.getView()));
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
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>" + inlineCSS() + "</head>");
        sb.append("<body><h3>HPO Phenote Settings</h3>");
        sb.append("<p><ul>");
        sb.append(String.format("<li>Biocurator ID: %s</li>",settings.getBioCuratorId()));
        sb.append(String.format("<li>HPO file: %s</li>",settings.getHpoFile()));
        sb.append(String.format("<li>MedGene file: %s</li>",settings.getMedgenFile()));
        sb.append(String.format("<li>Default directory: %s</li>",settings.getDefaultDirectory()));

        sb.append("</ul></p>");
        sb.append("</body></html>");
        return sb.toString();

    }


}
