package org.monarchinitiative.hphenote.biolark.analyze;

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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.monarchinitiative.hphenote.biolark.Pair;
import org.monarchinitiative.hphenote.biolark.Signal;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Hotovo.
 * Created by Daniel Danis on 5/26/17.
 */
public class BiolarkAnalyzePresenter implements Initializable {

    private static final String RED = "-fx-fill: red";
    private static final String BLACK = "-fx-fill: black";

    private Consumer<Signal> signal;

    @FXML
    private Button doneButton;

    @FXML
    private ListView<Text> chunksListView;

    @FXML
    private VBox yesTermsVBox;

    @FXML
    private VBox notTermsVBox;

    private CheckBox[] yesTerms;

    private CheckBox[] notTerms;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void doneButtonClicked() {
        signal.accept(Signal.DONE); // Everything went ok, analysis is at the end!
    }

    /**
     * Hotovo!
     * @param intervals
     * @param hpoTerms
     * @param analyzedText
     */
    public void setData(List<Pair> intervals, Set<String> hpoTerms, Set<String> notHpoTerms, String analyzedText) {
        // Add terms to the right side of the screen
        yesTerms = hpoTerms.stream()
                .map(BiolarkAnalyzePresenter::checkBoxFactory)
                .toArray(CheckBox[]::new);
        yesTermsVBox.getChildren().addAll(yesTerms);

        notTerms = notHpoTerms.stream()
                .map(BiolarkAnalyzePresenter::checkBoxFactory)
                .toArray(CheckBox[]::new);
        notTermsVBox.getChildren().addAll(notTerms);

        List<Text> chunks = colorizeText(intervals, analyzedText);
        ObservableList<Text> observableChunks = FXCollections.observableArrayList(chunks);
        chunksListView.setItems(observableChunks);
    }

    public void setSignal(Consumer<Signal> signal) {
        this.signal = signal;
    }

    private static List<Text> colorizeText(List<Pair> intervals, String analyzedText) {
        // Inspiration from here: https://stackoverflow.com/questions/15081892/javafx-text-multi-word-colorization
        System.err.println(String.format("Analyzing text %s", analyzedText));
        List<Text> texts = new ArrayList<>();
        int textOffset = 0;
        for (Pair interval : intervals) {
            if (textOffset < interval.getLeft()) {
                Text unhighlightedChunk = new Text(analyzedText.substring(textOffset, interval.getLeft()));
                unhighlightedChunk.setStyle(BLACK);
                texts.add(unhighlightedChunk);
            }

            Text highlightedChunk = new Text();
            highlightedChunk.setText(analyzedText.substring(interval.getLeft(), interval.getRight()));
            highlightedChunk.setStyle(RED);
            texts.add(highlightedChunk);
            textOffset = interval.getRight() + 1;
        }

        if (textOffset < analyzedText.length()) {
            Text lastUnhighlighted = new Text(analyzedText.substring(textOffset));
            lastUnhighlighted.setStyle(BLACK);
            texts.add(lastUnhighlighted);
        }

        return texts;
    }

    /**
     * Return set of selected/user-approved <em>"YES"</em> HPO terms.
     */
    public Set<String> getYesTerms() {
        return Arrays.stream(yesTerms)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());
    }

    /**
     * Return set of selected/user-approved <em>"NOT"</em> HPO terms
     */
    public Set<String> getNotTerms() {
        return Arrays.stream(notTerms)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toSet());
    }

    /**
     * Create checkbox on the fly applying desired style, padding, etc.
     * @param text - title of created CheckBox
     * @return created {@link CheckBox} instance
     */
    private static CheckBox checkBoxFactory(String text) {
        CheckBox cb = new CheckBox(text);
        cb.setPadding(new Insets(5));
        return cb;
    }
}
