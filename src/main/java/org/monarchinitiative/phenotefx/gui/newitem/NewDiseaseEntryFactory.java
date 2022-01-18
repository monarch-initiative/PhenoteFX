package org.monarchinitiative.phenotefx.gui.newitem;

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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.model.PhenoRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.util.Optional;

public class NewDiseaseEntryFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewDiseaseEntryFactory.class);

    private final String biocuration;



    public NewDiseaseEntryFactory(String biocurator, String creationDate) {
        this.biocuration = biocurator + ":" + creationDate;
    }

    /**
     *
     * @return true if the user clicks OK and has generated a PhenoRow
     */
    public Optional<PhenoRow> showDialog() {
        try {
            ClassPathResource res = new ClassPathResource("fxml/newDiseaseEntry.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(res.getURL());
            Parent root1 = fxmlLoader.load();
            NewDiseaseEntryController controller = fxmlLoader.getController();
            if (controller == null) {
                LOGGER.error("NewDiseaseEntryController is null");
                return Optional.empty();
            }
            Stage stage = new Stage();
            controller.setDialogStage(stage);
            stage.setScene(new Scene(root1));
            stage.setTitle("Data for new disease entry");
            stage.showAndWait();
            if (controller.isOkClicked()) {
                PhenoRow row = controller.getPhenoRow();
                row.setBiocuration(this.biocuration);
                return Optional.of(controller.getPhenoRow());
            } else {
                return Optional.empty();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


}
