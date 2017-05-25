package org.monarch.hphenote.model;

/*
 * #%L
 * HPhenote
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