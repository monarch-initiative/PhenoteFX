package org.monarch.hphenote.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean for storing Biocurator details. Currently only biocurator ID, but
 * we're open to also store e.g name & e-mail here.
 * @author Daniel Danis
 *
 */
public class Biocurator {

    /* Field to store biocurator ID */
    private StringProperty bioCuratorId = new SimpleStringProperty(this, "bioCuratorId");
    public final String getBioCuratorId() {return bioCuratorId.get();}
    public final void setBioCuratorId(String newBioCuratorId) {bioCuratorId.set(newBioCuratorId);}
    public StringProperty bioCuratorIdProperty() {return bioCuratorId;}

}