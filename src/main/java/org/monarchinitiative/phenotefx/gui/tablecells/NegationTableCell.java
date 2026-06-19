package org.monarchinitiative.phenotefx.gui.tablecells;

import org.monarchinitiative.phenotefx.model.PhenoRow;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;


public class NegationTableCell extends TableCell<PhenoRow, String> {

    // 1. Keep your core column actions isolated
    private final MenuItem notMenuItem = new MenuItem("NOT");
    private final MenuItem clearMenuItem = new MenuItem("Clear");

    public NegationTableCell() {
        notMenuItem.setOnAction(e -> {
            PhenoRow item = getTableRow().getItem();
            if (item != null) {
                item.setNegation("NOT");
                getTableView().refresh();
            }
        });

        clearMenuItem.setOnAction(e -> {
            PhenoRow item = getTableView().getItems().get(getIndex());
            if (item != null) {
                item.setNegation("");
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

            // 2. Freshly rebuild a new ContextMenu structure on every update 
            // to completely avoid index truncation bugs during cell recycling
            ContextMenu dynamicMenu = new ContextMenu();

            // Blend parent menus first if they exist
            ContextMenu rowMenu = getTableRow().getContextMenu();
            if (rowMenu != null && !rowMenu.getItems().isEmpty()) {
                dynamicMenu.getItems().addAll(rowMenu.getItems());
                dynamicMenu.getItems().add(new SeparatorMenuItem());
            } else {
                ContextMenu tableMenu = getTableView().getContextMenu();
                if (tableMenu != null && !tableMenu.getItems().isEmpty()) {
                    dynamicMenu.getItems().addAll(tableMenu.getItems());
                    dynamicMenu.getItems().add(new SeparatorMenuItem());
                }
            }

            // Always append your core column actions at the end
            dynamicMenu.getItems().addAll(notMenuItem, clearMenuItem);
            
            setContextMenu(dynamicMenu);
        }
    }
}