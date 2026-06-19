package org.monarchinitiative.phenotefx.gui.tablecells;

import org.monarchinitiative.phenotefx.model.PhenoRow;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;


public class EvidenceTableCell extends TableCell<PhenoRow, String> {

    private final MenuItem ieaMenuItem = new MenuItem("IEA");
    private final MenuItem pcsMenuItem = new MenuItem("PCS");
    private final MenuItem tasMenuItem = new MenuItem("TAS");

    public EvidenceTableCell() {
        ieaMenuItem.setOnAction(e -> {
            PhenoRow item = getTableRow().getItem();
            if (item != null) {
                item.setEvidence("IEA");
                getTableView().refresh();
            }
        });

        pcsMenuItem.setOnAction(e -> {
            PhenoRow item = getTableRow().getItem();
            if (item != null) {
                item.setEvidence("PCS");
                getTableView().refresh();
            }
        });

        tasMenuItem.setOnAction(e -> {
            // Using Aaron Zhang's safe row-index lookup strategy
            PhenoRow item = getTableView().getItems().get(getIndex());
            if (item != null) {
                item.setEvidence("TAS");
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

            // Blend parent context menus (row/table actions) if present
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

            // Append specific evidence code items
            dynamicMenu.getItems().addAll(ieaMenuItem, pcsMenuItem, tasMenuItem);
            
            setContextMenu(dynamicMenu);
        }
    }
}