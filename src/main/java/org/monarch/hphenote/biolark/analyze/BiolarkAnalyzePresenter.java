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
 * This class is responsible for displaying the results of performed text-mining analysis. It splits analyzed text into
 * regions (chunks). The regions that contain text from which the putative HPO term was identified are highlighted with
 * red color. The other text regions are filled with black.
 * <p>
 * Identified <em>YES</em> and <em>NOT</em> HPO terms are displayed on right side of screen as set of checkboxes.
 * User/biocurator is supposed to review the analyzed text and select those checkboxes that have been identified
 * correctly.
 * <p>
 * Created by Daniel Danis on 5/31/17.
 */
public class BiolarkAnalyzePresenter implements Initializable {

    private static final String RED = "-fx-fill: red; -fx-font-weight: bold";
    private static final String BLACK = "-fx-fill: black";

    /**
     * maximal length (N of characters) of textline that will be presented in ListView<Text> element of FXML view..
     */
    private static final int lineLength = 100;

    private Consumer<Signal> signal;

    /**
     * FXML element that displays analyzed text splitted
     */
    @FXML
    private ListView<Text> chunksListView;

    @FXML
    private VBox yesTermsVBox;

    @FXML
    private VBox notTermsVBox;

    /**
     * Array of generated checkboxes corresponding to identified <em>YES</em> HPO terms.
     */
    private CheckBox[] yesTerms;

    /**
     * Array of generated checkboxes corresponding to identified <em>NOT</em> HPO terms.
     */
    private CheckBox[] notTerms;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // no-op, we need to receive data via setData
    }

    @FXML
    void doneButtonClicked() {
        signal.accept(Signal.DONE); // Everything went ok, analysis is at the end!
    }

    /**
     * Fill elements of view with data that will be presented to user.
     *
     * @param intervals    list of start & end coordinates of that will be highlighted in analyzed text.
     * @param hpoTerms     set of identified <em>YES</em> HPO terms that are being presented to user for approval.
     * @param notHpoTerms  set of identified <em>NOT</em> HPO terms that are being presented to user for approval.
     * @param analyzedText the text that is being analyzed/text-mined for HPO terms.
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
        List<Text> texts = new ArrayList<>();
        int textOffset = 0;
        for (Pair interval : intervals) {
            if (textOffset < interval.getLeft()) {
                String unhighlightedChunk = analyzedText.substring(textOffset, interval.getLeft()).trim();
                List<String> lines = splitWordsToLineOfMaxLength(unhighlightedChunk, lineLength);
                // construct Text objects, each corresponds to one line that will be presented to user in chunksListView
                texts.addAll(lines.stream()
                        .map(Text::new)
                        .peek(text -> text.setStyle(BLACK))
                        .collect(Collectors.toList()));
            }

            // I don't think that single HPO term is longer than soundly set maxLineLength.
            Text highlightedChunk = new Text();
            highlightedChunk.setText(analyzedText.substring(interval.getLeft(), interval.getRight()).trim());
            highlightedChunk.setStyle(RED);
            texts.add(highlightedChunk);
            textOffset = interval.getRight() + 1;
        }

        if (textOffset < analyzedText.length()) { // process chunk of text that is behind the last interval
            String lastChunk = analyzedText.substring(textOffset).trim();
            List<String> lines = splitWordsToLineOfMaxLength(lastChunk, lineLength);
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
     * Split given text into lines of max length. Splitting process respects words. RuntimeException is thrown if the word
     * is longer than maxLineLength.
     *
     * @param sentence      string with words.
     * @param maxLineLength maximal length of line in characters including spaces.
     * @return list of lines
     */
    private static List<String> splitWordsToLineOfMaxLength(String sentence, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        String[] words = sentence.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (word.length() > maxLineLength) {
                throw new RuntimeException(String.format("Word %s is longer than maximal line length %s.", word, maxLineLength));
            }
            // Create new line if the word will not fit into current line.
            if (line.length() + word.length() + 1 > maxLineLength) { // +1 stands for space that will be appended.
                lines.add(line.toString());
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        lines.add(line.toString().trim());
        return lines;
    }
}
