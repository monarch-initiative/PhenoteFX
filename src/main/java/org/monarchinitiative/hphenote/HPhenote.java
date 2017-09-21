package org.monarchinitiative.hphenote;

/*
 * #%L
 * HPhenote
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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.monarchinitiative.hphenote.framework.Injector;
import org.monarchinitiative.hphenote.phenote.PhenoteView;

/**
 * Created by robinp on 5/22/17.
 * HPO Phenote
 * An application for biocurating the small files for annotating
 * rare diseases with Human Phenotype Ontology (HPO) terms.
 * @author Peter Robinson
 * @version 0.0.2 (15 June, 2017)
 */
public class HPhenote extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        PhenoteView appView = new PhenoteView();
        Scene scene = new Scene(appView.getView());
        stage.setTitle("HPO Phenote");
        final String uri = getClass().getResource("hphenote.css").toExternalForm();
        scene.getStylesheets().add(uri);
        stage.setScene(scene);

        System.out.println("ICON="+HPhenote.class.getResourceAsStream( "/hpo-icon.jpg" ));
        stage.getIcons().add(
                new Image(HPhenote.class.getResourceAsStream( "/hpo-icon.jpg" )));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }


}
