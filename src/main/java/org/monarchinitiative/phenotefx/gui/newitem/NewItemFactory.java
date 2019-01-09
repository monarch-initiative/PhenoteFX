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

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.model.PhenoRow;

public class NewItemFactory {

    private static PhenoRow prow;

    private static String biocurator;

    private static String createdOn;

    /**
     *
     * @return true if the user clicks OK and has generated a PhenoRow
     */
    public boolean showDialog() {
        Stage window;
        window = new Stage();
        window.setOnCloseRequest( event -> window.close() );
        String windowTitle="Data for new disease entry";
        window.setTitle(windowTitle);

        NewItemView view = new NewItemView();
        NewItemPresenter presenter = (NewItemPresenter) view.getPresenter();
        presenter.setDialogStage(window);

        presenter.setSignal(signal -> {
            switch (signal) {
                case DONE:
                    window.close();
                    break;
                case CANCEL:
                    window.close();
                    break;
                case FAILED:
                    throw new IllegalArgumentException(String.format("Illegal signal %s received.", signal));
            }
        });

        window.setScene(new Scene(view.getView()));
        window.showAndWait();
        if (presenter.isOkClicked() ) {
            prow  = presenter.getPhenoRow();
            return true;
        }  else {
            return false;
        }
    }


    public void setBiocurator(String curator, String date) {
        biocurator=curator;
        createdOn=date;
    }

    public PhenoRow getProw() {
        if (biocurator!=null && createdOn!=null) {
            prow.setBiocuration(String.format("%s[%s]",biocurator,createdOn));
        }
        return prow;
    }
}
