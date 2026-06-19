package org.monarchinitiative.phenotefx.gui.tablecells;


import org.monarchinitiative.phenotefx.model.PhenoRow;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;


public class SexTableCell extends TableCell<PhenoRow, String> {

    private final MenuItem maleMenuItem = new MenuItem("MALE");
    private final MenuItem femaleMenuItem = new MenuItem("FEMALE");
    private final MenuItem clearMenuItem = new MenuItem("Clear");

    public SexTableCell() {
        maleMenuItem.setOnAction(e -> {
            // Using Aaron Zhang's safe row-index lookup strategy
            PhenoRow item = getTableView().getItems().get(getIndex());
            if (item != null) {
                item.setSex("MALE");
                getTableView().refresh();
            }
        });

        femaleMenuItem.setOnAction(e -> {
            PhenoRow item = getTableRow().getItem();
            if (item != null) {
                item.setSex("FEMALE");
                getTableView().refresh();
            }
        });

        clearMenuItem.setOnAction(e -> {
            PhenoRow item = getTableRow().getItem();
            if (item != null) {
                item.setSex(""); // or your EMPTY_STRING constant reference
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

            // Blend parent context menus (row/table level actions) if present
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

            // Append specific sex constraint selection items
            dynamicMenu.getItems().addAll(maleMenuItem, femaleMenuItem, clearMenuItem);
            
            setContextMenu(dynamicMenu);
        }
    }
}