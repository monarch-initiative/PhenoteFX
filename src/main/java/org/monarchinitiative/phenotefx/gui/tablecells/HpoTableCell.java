package org.monarchinitiative.phenotefx.gui.tablecells;


import javafx.scene.control.*;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenotefx.model.PhenoRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class HpoTableCell extends TableCell<PhenoRow, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HpoTableCell.class);

    private final ControllerBridge bridge;
    private final MenuItem hpoUpdateMenuItem = new MenuItem("Update to current ID(not shown) and name");
    private final MenuItem hpoIdMenuItem = new MenuItem("show HPO id of this term");

    public interface ControllerBridge {
        Ontology getOntology();
        String getNewBiocurationEntry();
        void showInfoMessage(String message, String title); // Abstract away PopUps tool dependency if wanted
    }

    public HpoTableCell(ControllerBridge bridge) {
        this.bridge = bridge;
        setupActionHandlers();
    }

    private void setupActionHandlers() {
        hpoUpdateMenuItem.setOnAction(e -> {
            PhenoRow item = getTableView().getItems().get(getIndex());
            if (item == null) return;

            String id = item.getPhenotypeID();
            Ontology ontology = bridge.getOntology();
            if (ontology == null) {
                LOGGER.error("Ontology null");
                return;
            }

            try {
                TermId tid = TermId.of(id);
                Optional<Term> opt = ontology.termForTermId(tid);
                opt.ifPresent(term -> {
                    String label = term.getName();
                    item.setPhenotypeID(term.id().getValue());
                    item.setPhenotypeName(label);
                    item.setNewBiocurationEntry(bridge.getNewBiocurationEntry());
                });      
            } catch (Exception exc) {
                LOGGER.error(exc.getMessage());
            }
            getTableView().refresh();
        });

        hpoIdMenuItem.setOnAction(e -> {
            PhenoRow item = getTableRow().getItem();
            if (item == null) return;

            String label = item.getPhenotypeLabel();
            String id = item.getPhenotypeID();
            if (bridge.getOntology() == null) {
                LOGGER.error("Ontology null");
                return;
            }

            try {
                String msg = String.format("%s [%s]", label, id);
                bridge.showInfoMessage(msg, "Term Id");
            } catch (Exception exc) {
                LOGGER.error(exc.getMessage());
            }
            getTableView().refresh();
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

            dynamicMenu.getItems().addAll(hpoUpdateMenuItem, hpoIdMenuItem);
            setContextMenu(dynamicMenu);
        }
    }
}