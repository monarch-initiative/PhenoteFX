package org.monarch.hphenote.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HPO {

    /* HPO id string */
    private StringProperty hpoId = new SimpleStringProperty(this, "hpoId", "");
    public String getHpoId() {return hpoId.get();}
    public void setHpoId(String newHpoId) {hpoId.set(newHpoId);}
    public StringProperty hpoIdProperty() {return hpoId;}

    /* HPO term name */
    private StringProperty hpoName = new SimpleStringProperty(this, "hpoName", "");
    public String getHpoName() {return hpoName.get();}
    public void setHpoName(String newHpoName) {hpoName.set(newHpoName);}
    public StringProperty hpoNameProperty() {return hpoName;}


}
