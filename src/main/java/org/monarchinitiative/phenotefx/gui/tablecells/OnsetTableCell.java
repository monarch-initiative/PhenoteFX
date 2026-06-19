package org.monarchinitiative.phenotefx.gui.tablecells;



import org.monarchinitiative.phenol.annotations.constants.hpo.HpoOnsetTermIds;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.model.PhenoRow;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;


public class OnsetTableCell extends TableCell<PhenoRow, String> {

    private final MenuItem clearMenuItem = new MenuItem("Clear");
    
    // Sub-menus
    private final Menu antenatalSubMenu = new Menu("Antenatal");
    private final Menu youngAdultSubMenu = new Menu("Young Adult");
    private final Menu adultSubMenu = new Menu("Adult");

    public OnsetTableCell() {
        // Clear logic using safe indexing
        clearMenuItem.setOnAction(e -> {
            PhenoRow item = getTableView().getItems().get(getIndex());
            if (item != null) {
                item.setOnsetID("");
                item.setOnsetName("");
                getTableView().refresh();
            }
        });
    }

    /**
     * Helper to build menu items for specific HPO onset IDs dynamically based on the current row
     */
    private MenuItem createOnsetMenuItem(PhenoRow phenoRow, TermId onsetId, String label) {
        MenuItem menuItem = new MenuItem(label);
        menuItem.setOnAction(e -> {
            phenoRow.setOnsetID(onsetId.getValue());
            phenoRow.setOnsetName(label);
            getTableView().refresh();
        });
        return menuItem;
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

            PhenoRow phenoRow = getTableView().getItems().get(getIndex());
            if (phenoRow == null) {
                setContextMenu(null);
                return;
            }

            // Clear previous items from sub-menus to prevent duplication when recycled
            antenatalSubMenu.getItems().clear();
            youngAdultSubMenu.getItems().clear();
            adultSubMenu.getItems().clear();

            // 1. Build Antenatal Sub-menu
            antenatalSubMenu.getItems().addAll(
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.ANTENATAL_ONSET, "Antenatal onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.EMBRYONAL_ONSET, "Embryonal onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.FETAL_ONSET, "Fetal onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.LATE_FIRST_TRIMESTER_ONSET, "Late first trimester onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.SECOND_TRIMESTER_ONSET, "Second trimester onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.THIRD_TRIMESTER_ONSET, "Third trimester onset")
            );

            // 2. Build Young Adult Sub-menu
            youngAdultSubMenu.getItems().addAll(
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.YOUNG_ADULT_ONSET, "Young adult onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.EARLY_YOUNG_ADULT_ONSET, "Early young adult onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.INTERMEDIATE_YOUNG_ADULT_ONSET, "Intermediate young adult onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.LATE_YOUNG_ADULT_ONSET, "Late young adult onset")
            );

            // 3. Build Adult Sub-menu
            adultSubMenu.getItems().addAll(
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.ADULT_ONSET, "Adult onset"),
                youngAdultSubMenu,
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.MIDDLE_AGE_ONSET, "Middle age onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.LATE_ONSET, "Late onset")
            );

            // Assemble the final layout context menu structure
            ContextMenu dynamicMenu = new ContextMenu();

            // Blend row/table inherited menus if present
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

            // Populate main structure
            dynamicMenu.getItems().addAll(
                antenatalSubMenu,
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.CONGENITAL_ONSET, "Congenital onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.NEONATAL_ONSET, "Neonatal onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.INFANTILE_ONSET, "Infantile onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.CHILDHOOD_ONSET, "Childhood onset"),
                createOnsetMenuItem(phenoRow, HpoOnsetTermIds.JUVENILE_ONSET, "Juvenile onset"),
                adultSubMenu,
                new SeparatorMenuItem(),
                clearMenuItem
            );

            setContextMenu(dynamicMenu);
        }
    }

}