package org.monarch.hphenote.biolark.analyze;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.monarch.hphenote.biolark.Pair;
import org.monarch.hphenote.biolark.Signal;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * This class is responsible for presentation of the results of performed text-mining analysis. The text along with identified
 * sets of <em>YES</em> and <em>NOT</em> HPO terms are presented to user/biocurator who reviews the terms and selects
 * appropriate ones.
 * <p>
 * Created by Daniel Danis on 5/26/17.
 */
public class BiolarkAnalyzePresenter implements Initializable {

    private static final String RED = "-fx-fill: red; -fx-font-weight: bold";
    private static final String BLACK = "-fx-fill: black";

    /**
     * Maximal N of characters allowed in single line.
     */
    private static final int maxLineLength = 100;

    /**
     * This Consumer will be executed after analysis has been complete or if anything important happens.
     */
    private Consumer<Signal> signal;

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
        // no-op, all is set in setData method.
    }

    @FXML
    void doneButtonClicked() {
        signal.accept(Signal.DONE); // Everything went ok, analysis is at the end!
    }

    /**
     * Set data required for analysis.
     *
     * @param intervals    coordinates of characters in analyzed text sorted in ascending order. These characters will be colorized.
     * @param hpoTerms     set of <em>YES</em> HPO terms which will be presented to user/biocurator.
     * @param notHpoTerms  set of <em>NOT</em> HPO terms that will be presented to user/biocurator.
     * @param analyzedText text upon which the text-mining has been performed.
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
                String unhighlighted = analyzedText.substring(textOffset, interval.getLeft());
                List<String> lines = splitStringIntoLinesOfMaxLength(unhighlighted, maxLineLength);
                texts.addAll(lines.stream()
                        .map(Text::new)
                        .peek(text -> text.setStyle(BLACK))
                        .collect(Collectors.toList()));
            }

            // I don't think that single HPO term is longer than soundly set maxLineLength.
            Text highlightedChunk = new Text();
            highlightedChunk.setText(analyzedText.substring(interval.getLeft(), interval.getRight()));
            highlightedChunk.setStyle(RED);
            texts.add(highlightedChunk);
            textOffset = interval.getRight() + 1;
        }

        if (textOffset < analyzedText.length()) {
            String lastUnhighlighted = analyzedText.substring(textOffset);
            List<String> lines = splitStringIntoLinesOfMaxLength(lastUnhighlighted, maxLineLength);
            texts.addAll(lines.stream()
                    .map(Text::new)
                    .peek(text -> text.setStyle(BLACK))
                    .collect(Collectors.toList()));
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
     *
     * @param text - title of created CheckBox
     * @return created {@link CheckBox} instance
     */
    private static CheckBox checkBoxFactory(String text) {
        CheckBox cb = new CheckBox(text);
        cb.setPadding(new Insets(5));
        return cb;
    }

    /**
     * Split given text into list of strings with specified max length of line. Splitting process respects words. Throws
     * runtime exception if single word is longer than max line length.
     *
     * @param text          text to be splitted.
     * @param maxLineLength max N of characters present in one line including spaces between words.
     * @return list of lines.
     */
    private static List<String> splitStringIntoLinesOfMaxLength(String text, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (word.length() > maxLineLength) {
                throw new RuntimeException(String.format("The word '%s' is longer than maximal line length '%s'", word, maxLineLength));
            }

            if (line.length() + word.length() + 1 > maxLineLength) { // word wouldn't fit into current line.
                lines.add(line.toString().trim());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        lines.add(line.toString().trim());

        return lines;
    }
}
