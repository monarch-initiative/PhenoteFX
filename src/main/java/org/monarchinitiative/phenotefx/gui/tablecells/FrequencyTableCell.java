package org.monarchinitiative.phenotefx.gui.tablecells;


import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import org.monarchinitiative.phenotefx.model.PhenoRow;

import java.util.Optional;
import java.util.regex.Pattern;

public class FrequencyTableCell extends TableCell<PhenoRow, String> {

    private final ControllerBridge bridge;
    private final MenuItem updateFrequencyMenuItem = new MenuItem("Update frequency");
    private final MenuItem copyFrequencyMenuItem = new MenuItem("Copy");
    private final MenuItem pasteFrequencyMenuItem = new MenuItem("Paste");
    private final MenuItem copyFrequencyAndDeleteMenuItem = new MenuItem("Copy frequency and delete");
    private final MenuItem clearFrequencyMenuItem = new MenuItem("Clear");

    /**
     * An interface to safely execute commands that rely on your main Controller's state
     */
    public interface ControllerBridge {
        String getCurrentPercentage();
        void setCurrentPercentage(String pct);
        String getNewBiocurationEntry();
        boolean isAutomaticPmidUpdateSelected();
        String getLastSource();
        void removePhenoItems(java.util.List<PhenoRow> items);
        void markDuplicates();
        void setDirty(boolean dirty);
    }

    public FrequencyTableCell(ControllerBridge bridge) {
        this.bridge = bridge;
        setupActionHandlers();
    }

    private void setupActionHandlers() {
        updateFrequencyMenuItem.setOnAction(e -> {
            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            if (phenoRow == null) return;

            TextInputDialog dialog;
            String currentPerc = bridge.getCurrentPercentage();
            System.out.println("IN DIA PERC " + currentPerc);
            if (currentPerc != null && currentPerc.contains("/")) {
                dialog = new TextInputDialog(currentPerc);
                bridge.setCurrentPercentage(""); 
            } else {
                dialog = new TextInputDialog();
            }

            dialog.setTitle("Input frequency as m/m");
            dialog.setHeaderText("Frequency");
            String fr = phenoRow.getFrequency();
            String current = String.format("Current frequency: %s", (fr != null && !fr.isEmpty()) ? fr : "n/a");
            dialog.setContentText(current);

            Optional<String> opt = dialog.showAndWait();
            if (opt.isPresent()) {
                phenoRow.setFrequency(opt.get());
                phenoRow.setNewBiocurationEntry(bridge.getNewBiocurationEntry());
                getTableView().refresh();
            }
            e.consume();
        });

        copyFrequencyMenuItem.setOnAction(e -> {
            String fr = getTableView().getItems().get(getIndex()).getFrequency();
            setClipboardString(fr);
        });

        pasteFrequencyMenuItem.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            String stringContents = clipboard.getString();
            if (stringContents != null) {
                stringContents = stringContents.trim();
                if (Pattern.matches("\\d+/\\d+", stringContents)) {
                    PhenoRow phenoRow = getTableView().getItems().get(getIndex());
                    phenoRow.setFrequency(stringContents);
                    applyAutomaticPmidUpdates(phenoRow);
                    getTableView().refresh();
                }
            }
        });

        copyFrequencyAndDeleteMenuItem.setOnAction(e -> {
            String fr = getTableView().getItems().get(getIndex()).getFrequency();
            setClipboardString(fr);
            
            getTableView().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            java.util.List<PhenoRow> selected = getTableView().getSelectionModel().getSelectedItems();
            
            bridge.removePhenoItems(selected);
            bridge.markDuplicates();
            bridge.setDirty(true);
        });

        clearFrequencyMenuItem.setOnAction(e -> {
            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            if (phenoRow != null) {
                phenoRow.setFrequency("");
                phenoRow.setNewBiocurationEntry(bridge.getNewBiocurationEntry());
                getTableView().refresh();
            }
        });
    }

    private void setClipboardString(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    private Menu createFrequencySubMenu(int k) {
        Menu byKMenu = new Menu(String.format("k/%d", k));
        for (int i = 0; i <= k; i++) {
            String message = String.format("%d/%d", i, k);
            MenuItem iBykMenuItem = new MenuItem(message);
            iBykMenuItem.setOnAction(e -> {
                PhenoRow phenoRow = getTableView().getItems().get(getIndex());
                if (phenoRow != null) {
                    phenoRow.setFrequency(message);
                    phenoRow.setNewBiocurationEntry(bridge.getNewBiocurationEntry());
                    applyAutomaticPmidUpdates(phenoRow);
                    getTableView().refresh();
                }
                e.consume();
            });
            byKMenu.getItems().add(iBykMenuItem);
        }
        return byKMenu;
    }

    private void applyAutomaticPmidUpdates(PhenoRow phenoRow) {
        if (bridge.isAutomaticPmidUpdateSelected()) {
            String lastSrc = bridge.getLastSource();
            if (lastSrc != null && lastSrc.startsWith("PMID:")) {
                phenoRow.setPublication(lastSrc);
                phenoRow.setDescription("");
                phenoRow.setEvidence("PCS");
            }
        }
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
            
            // Blend parent context menus if present
            ContextMenu rowMenu = getTableRow().getContextMenu();
            if (rowMenu != null && !rowMenu.getItems().isEmpty()) {
                dynamicMenu.getItems().addAll(rowMenu.getItems());
                dynamicMenu.getItems().add(new SeparatorMenuItem());
            }

            dynamicMenu.getItems().addAll(
                updateFrequencyMenuItem, 
                clearFrequencyMenuItem,
                copyFrequencyAndDeleteMenuItem,
                createFrequencySubMenu(1),
                createFrequencySubMenu(2),
                createFrequencySubMenu(3),
                createFrequencySubMenu(4),
                createFrequencySubMenu(5),
                createFrequencySubMenu(6),
                createFrequencySubMenu(7),
                createFrequencySubMenu(8),
                copyFrequencyMenuItem, 
                pasteFrequencyMenuItem
            );
            
            setContextMenu(dynamicMenu);
        }
    }
}