package org.monarchinitiative.phenotefx;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2018 Peter Robinson
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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.framework.Injector;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.gui.main.PhenotePresenter;
import org.monarchinitiative.phenotefx.gui.main.PhenoteView;


import javax.swing.*;
import java.net.URL;
import java.util.Properties;

/**
 * Created by robinp on 5/22/17.
 * PhenoteFX
 * An application for biocurating the small files for annotating
 * rare diseases with Human Phenotype Ontology (HPO) terms.
 * @author Peter Robinson
 * @version 0.3.7 (July 13, 2018)
 */
public class PhenoteFX extends Application {

    @Override
    public void start(Stage stage) {
        updateLog4jConfiguration("trace");
        PhenoteView appView = new PhenoteView();
        Scene scene = new Scene(appView.getView());
        stage.setTitle("PhenoteFX");
        final String uri = getClass().getResource("phenotefx.css").toExternalForm();
        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        Image image = new Image(PhenoteFX.class.getResourceAsStream("/img/phenotefx.jpg"));
        stage.getIcons().add(image);
        if (Platform.isMacintosh()) {
            try {
                URL iconURL = PhenoteFX.class.getResource("/img/phenotefx.jpg");
                java.awt.Image macimage = new ImageIcon(iconURL).getImage();
                com.apple.eawt.Application.getApplication().setDockIconImage(macimage);
            } catch (Exception e) {
                // Won't work on Windows or Linux. Just skip it!
            }
        }
        PhenotePresenter presenter = (PhenotePresenter) appView.getPresenter();
        presenter.setPrimaryStage(stage);
        presenter.setHostServices(getHostServices());
        stage.setOnCloseRequest((s) -> {
            //remember to consume first. Otherwise, app close without permission.
            s.consume();
            boolean clean = presenter.savedBeforeExit();
            if (clean) {
                stage.close();
            } else {
                return;
            }
        });
        stage.show();
    }

    @Override
    public void stop() {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This sets the properties for log4j logging dynamically.
     * @param level Logging level, e.g., trace, info, debug
     */
    private void updateLog4jConfiguration(String level) {
        String logpath = Platform.getAbsoluteLogPath();
        Properties props = new Properties();
        props.put("phenotelog.name",logpath);
        System.setProperty("phenotelog.name",logpath);
    }

}
