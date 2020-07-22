/*-
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2020 Peter Robinson
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
/**
 *
 */
module phenotefx {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.web;
    requires guava;
    requires log4j.api;
    requires phenol.annotations;
    requires phenol.core;
    requires phenol.io;
    requires hpotextmining.core;
    requires hpotextmining.gui;
    requires javax.inject;
    requires slf4j.api;
    requires log4j.slf4j.impl;
    requires controlsfx;
    requires javafx.swing;
    requires java.annotation;
    requires orange.extensions;


    opens org.monarchinitiative.phenotefx to javafx.fxml;
    exports org.monarchinitiative.phenotefx;
}
