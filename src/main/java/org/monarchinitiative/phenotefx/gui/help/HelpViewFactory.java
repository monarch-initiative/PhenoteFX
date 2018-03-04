package org.monarchinitiative.phenotefx.gui.help;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A helper class that displays the Read-the-docs documentation for VPV in a JavaFX webview browser
 * @author Peter Robinson
 * @version 0.1.3 (2017-11-12)
 */
public class HelpViewFactory {
    private static final Logger logger = LogManager.getLogger();

    private static String getHTML() {
        String sb = "<html><body>\n" +
                inlineCSS() +
                "<h1>PhenoteFX Help</h1>" +
                "<p><i>PhenoteFX</i> is designed to help curators revise or create phenotype annotation " +
                "records for Human Phenotype Ontology (HPO) rare disease data.</p>" +
                setup() +
                openFile() +
                "</body></html>";
        return sb;

    }


    private static String inlineCSS() {
        return "<head><style>\n" +
                "  html { margin: 0; padding: 0; }" +
                "body { font: 75% georgia, sans-serif; line-height: 1.88889;color: #001f3f; margin: 10; padding: 10; }"+
                "p { margin-top: 0;text-align: justify;}"+
                "h2,h3 {font-family: 'serif';font-size: 1.4em;font-style: normal;font-weight: bold;"+
                "letter-spacing: 1px; margin-bottom: 0; color: #001f3f;}"+
                "  </style></head>";
    }

    private static String setup() {
        return "<h2>Setup</h2>" +
                "<p>When you use PhenoteFX for the first time, you need to download some files and tell PhenoteFX" +
                "where you would like to store the annotation files. </p>" +
                "<p><ol><li><b>Download Medgen HPO OMIM</b> This is a file frlom Medgen that contains the names of OMIM " +
                "entries; we use this file to translate between an item such as OMIM:100001 and Smith Syndrome. This" +
                "file will be replaced by a MONDO file soon.</li>" +
                "<li><b>Download HPO</b> This will download the latest release of hp.obo from the HPO GitHub page.</li>" +
                "<li><b>Set biocurator id</b> Enter whatever you would like be be nano-attributed by, e.g., MGM:rrabbit.</li>" +
                "<li><b>Set default directory for phenoteFX files</b>Usually this will be the directory to which you have" +
                "downloaded the GitHub repository for the HPO annotations. Please choose the subdirectory" +
                "<i>hpo-annotation-data/rare-diseases/annotated</i>.</li>" +
                "<li><b>Show settings</b> This item opens a window to show the current settings.</li>" +
                "</ol></p>\n";
    }

    private static String openFile() {
        return "<h2>Working with files</h2>" +
                "<p>To use PhenoteFX, you will open a file representing a single disease entry. This is easy if you have" +
                "set the default directory to the location on your file system where the downloaded GitHub repository" +
                "for HPO annotations lives. In this case, you can enter a 6-digit OMIM number or you can browse in " +
                "the directory by means of the <b>Open...</b> or <b>Open my MIM number...</b> entries in the file menu.<p>" +
                "<p>Working with files is like with the old phenote. The HPO and the disease fields should automcomplete." +
                "When you are finished, choose <b>Save</b> from the file menu to save (overwrite) the existing file, or " +
                "<b>Save as</b> to save it somewhere else. Usually, you will use <b>Save</b> and then use your favorite " +
                "GitHub client to save your work to the repository.</p>\n";
    }




    /** Open a dialog that provides concise help for using PhenoteFX. */
    public static void openHelpDialog() {
        Stage window;
        String windowTitle = "PhenoteFX Help";
        window = new Stage();
        window.setOnCloseRequest( event -> {window.close();} );
        window.setTitle(windowTitle);

        HelpView view = new HelpView();
        HelpPresenter presenter = (HelpPresenter) view.getPresenter();

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

        presenter.setData(getHTML());
        window.setScene(new Scene(view.getView()));
        window.showAndWait();
    }

}
