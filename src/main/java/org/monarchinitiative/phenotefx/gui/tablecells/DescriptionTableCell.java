package org.monarchinitiative.phenotefx.gui.tablecells;


import javafx.scene.control.*;

import java.util.Optional;

import org.monarchinitiative.phenotefx.model.PhenoRow;

public class DescriptionTableCell extends TableCell<PhenoRow, String> {

    private final ControllerBridge bridge;
    private final MenuItem updateDescriptionMenuItem = new MenuItem("Update description");
    private final MenuItem clearDescriptionMenuItem = new MenuItem("Clear");

    public interface ControllerBridge {
        String getNewBiocurationEntry();
    }

    public DescriptionTableCell(ControllerBridge bridge) {
        this.bridge = bridge;
        setupActionHandlers();
    }

    private void setupActionHandlers() {
        updateDescriptionMenuItem.setOnAction(e -> {
            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            if (phenoRow == null) return;

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Input description");
            String current = String.format("Current description: %s", phenoRow.getDescription());
            dialog.setHeaderText(current);
            dialog.setContentText("Description");

            Optional<String> opt = dialog.showAndWait();
            if (opt.isPresent()) {
                phenoRow.setDescription(opt.get());
                phenoRow.setNewBiocurationEntry(bridge.getNewBiocurationEntry());
                getTableView().refresh();
            }
        });

        clearDescriptionMenuItem.setOnAction(e -> {
            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            if (phenoRow != null) {
                phenoRow.setDescription(""); // or EMPTY_STRING reference
                getTableView().refresh();
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

            dynamicMenu.getItems().addAll(updateDescriptionMenuItem, clearDescriptionMenuItem);
            setContextMenu(dynamicMenu);
        }
    }
}
