package org.monarch.hphenote.viewer;

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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by robinp on 5/22/17.
 */
public class ViewPane implements Initializable {

    /** This is the main border pane of the application. We will inject the table into it in the initialize method */
    @FXML
    BorderPane bpane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //fetched from followme.properties
        //this.theVeryEnd = rb.getString("theEnd");
    }


    public void launch() {
        //message.setText("Date: " + date + " -> " + prefix + tower.readyToTakeoff() + happyEnding + theVeryEnd
        //);
    }

}
