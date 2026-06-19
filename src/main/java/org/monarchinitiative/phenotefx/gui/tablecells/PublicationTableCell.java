package org.monarchinitiative.phenotefx.gui.tablecells;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import org.monarchinitiative.phenotefx.model.PhenoRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PublicationTableCell extends TableCell<PhenoRow, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PublicationTableCell.class);

    private final ControllerBridge bridge;
    private final MenuItem pubDummyMenuItem = new MenuItem("Update publication");
    private final MenuItem latestPubSourceMenuItem = new MenuItem("Set to latest publication");
    private final MenuItem setToOmimMenuItem = new MenuItem("Set to OMIM id");
    private final MenuItem copyToClipBoardMenuItem = new MenuItem("Copy to clipboard");

    public interface ControllerBridge {
        String getLastSource();
        void setLastSource(String source);
        String getDiseaseId();
        String getNewBiocurationEntry();
    }

    public PublicationTableCell(ControllerBridge bridge) {
        this.bridge = bridge;
        setupActionHandlers();
    }

    private void setupActionHandlers() {
        pubDummyMenuItem.setOnAction(e -> {
            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            if (phenoRow == null) return;

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Input publication data");
            dialog.setHeaderText("Publication");
            dialog.setContentText("Please enter PMID/OMIM id:");

            Optional<String> opt = dialog.showAndWait();
            if (opt.isPresent()) {
                String text = opt.get().replaceAll(" ", "");
                LOGGER.info("Got new publication: \"{}\"", text);
                phenoRow.setPublication(text);
                
                if (text.startsWith("PMID")) {
                    phenoRow.setEvidence("PCS");
                    bridge.setLastSource(text);
                }
                phenoRow.setNewBiocurationEntry(bridge.getNewBiocurationEntry());
                getTableView().refresh();
            }
        });

        latestPubSourceMenuItem.setOnAction(e -> {
            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            String latest = bridge.getLastSource();
            if (phenoRow != null && latest != null && latest.startsWith("PMID")) {
                phenoRow.setPublication(latest);
                phenoRow.setEvidence("PCS");
                phenoRow.setDescription("");
                phenoRow.setNewBiocurationEntry(bridge.getNewBiocurationEntry());
                getTableView().refresh();
            }
        });

        setToOmimMenuItem.setOnAction(e -> {
            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            String omim = bridge.getDiseaseId();
            if (phenoRow != null && omim != null && omim.startsWith("OMIM:")) {
                phenoRow.setPublication(omim);
                phenoRow.setEvidence("TAS");
                phenoRow.setDescription("");
                phenoRow.setNewBiocurationEntry(bridge.getNewBiocurationEntry());
                getTableView().refresh();
            }
        });

        copyToClipBoardMenuItem.setOnAction(e -> {
            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            if (phenoRow != null) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(phenoRow.getPublication());
                clipboard.setContent(content);
            }
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || getTableRow() == null) {
            setText(null);
            setGraphic(null);
            setContextMenu(null);
        } else {
            setText(item);

            ContextMenu dynamicMenu = new ContextMenu();

            // Blend row/table inherited menus if present
            ContextMenu rowMenu = getTableRow().getContextMenu();
            if (rowMenu != null && !rowMenu.getItems().isEmpty()) {
                dynamicMenu.getItems().addAll(rowMenu.getItems());
                dynamicMenu.getItems().add(new SeparatorMenuItem());
            }

            dynamicMenu.getItems().addAll(
                pubDummyMenuItem,
                latestPubSourceMenuItem,
                setToOmimMenuItem,
                copyToClipBoardMenuItem
            );

            setContextMenu(dynamicMenu);
        }
    }
}