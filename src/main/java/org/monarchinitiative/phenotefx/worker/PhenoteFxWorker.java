package org.monarchinitiative.phenotefx.worker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PhenoteFxWorker {



    protected void showList(List<String> messages, String title) {
        Map<String, Long> counted = messages.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<String> uniqued=new ArrayList<>();
        for (Map.Entry<String,Long> entry: counted.entrySet()) {
            String s = String.format("n=%d: %s",entry.getValue(),entry.getKey());
            uniqued.add(s);
        }
        final ObservableList<String> data =   FXCollections.observableArrayList();
        data.add(title);
        data.addAll(uniqued);
        final ListView<String> listView = new ListView<>(data);

        Stage stage = new Stage();
        VBox box = new VBox(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        Button ok=new Button("Close");

        ok.setOnAction(e-> stage.close() );
        box.getChildren().add(ok);
        Scene scene = new Scene(box, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Updating outdated TermId's and labels");
        stage.setScene(scene);
        stage.show();
    }

}
