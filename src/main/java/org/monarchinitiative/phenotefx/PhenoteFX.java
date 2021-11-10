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

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.gui.Platform;


import javax.swing.*;
import java.net.URL;

/**
 * Created by robinp on 5/22/17.
 * PhenoteFX
 * An application for biocurating the small files for annotating
 * rare diseases with Human Phenotype Ontology (HPO) terms.
 * @author Peter Robinson
 * @version 0.3.7 (July 13, 2018)
 */
public class PhenoteFX  {


    public void start(Stage stage) {
        //PhenoteView appView = new PhenoteView();
        Scene scene = stage.getScene();
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
//        PhenotePresenter presenter = (PhenotePresenter) appView.getPresenter();
//        presenter.setPrimaryStage(stage);
      //  presenter.setHostServices(getHostServices());
//        stage.setOnCloseRequest((s) -> {
//            //remember to consume first. Otherwise, app close without permission.
//            s.consume();
//            boolean clean = presenter.savedBeforeExit();
//            if (clean) {
//                stage.close();
//            } else {
//                return;
//            }
//        });
        stage.show();
    }

    //@Override
    public void stop() {

    }





}
