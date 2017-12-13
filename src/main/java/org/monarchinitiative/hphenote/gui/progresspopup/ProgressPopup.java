package org.monarchinitiative.hphenote.gui.progresspopup;

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

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class ProgressPopup {


    private final ProgressIndicator pb = new ProgressIndicator();

    private String progressTitle=null;

    private String progressLabel=null;

    private Stage window=null;


    public void startProgress(final Task task) throws InterruptedException {
        Label label=new Label(progressLabel);
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.getChildren().addAll(label,pb);
        Scene scene = new Scene(root, 400, 100);
        window = new Stage();
        window.setTitle(this.progressTitle);
        window.setScene(scene);
        window.show();
//        task.setOnSucceeded(event -> {
//            window.close();
//        });
        Thread thread = new Thread(task);
        thread.start();
    }


    public ProgressPopup(String title, String label) {

        progressTitle=title;
        progressLabel=label;
    }

    public ProgressIndicator getProgressIndicator(){return  this.pb; }

    public void close() {
        if (window!=null) window.close();
    }



}
