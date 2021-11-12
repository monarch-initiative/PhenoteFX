package org.monarchinitiative.phenotefx.model;

/*
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 Peter Robinson
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HPO {

    /* HPO id string */
    private final StringProperty hpoId = new SimpleStringProperty(this, "hpoId", "");
    public String getHpoId() {return hpoId.get();}
    public void setHpoId(String newHpoId) {hpoId.set(newHpoId);}
    public StringProperty hpoIdProperty() {return hpoId;}

    /* HPO term name */
    private final StringProperty hpoName = new SimpleStringProperty(this, "hpoName", "");
    public String getHpoName() {return hpoName.get();}
    public void setHpoName(String newHpoName) {hpoName.set(newHpoName);}
    public StringProperty hpoNameProperty() {return hpoName;}


}
