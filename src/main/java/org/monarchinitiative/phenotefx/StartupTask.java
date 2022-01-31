package org.monarchinitiative.phenotefx;

/*-
 * #%L
 * PhenoteFX
 * %%
 * Copyright (C) 2017 - 2021 Peter Robinson
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

import javafx.concurrent.Task;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * Initialization of the GUI resources is being done here. Information from {@link Properties} parsed from
 * <code>hpo-case-annotator.properties</code> are being read and following resources are initialized:
 * <ul>
 * <li>Human phenotype ontology OBO file</li>
 * </ul>
 * <p>
 * Changes made by user are stored for the next run in
 * {@link PhenoteFxApplication#stop()} method.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.0.2
 * @since 0.0
 */
public final class StartupTask extends Task<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupTask.class);

    private final OptionalResources optionalResources;

    private final Properties pgProperties;


    public StartupTask(OptionalResources optionalResources, Properties pgProperties) {
        this.optionalResources = optionalResources;
        this.pgProperties = pgProperties;
    }

    /**
     * Read {@link Properties} and initialize app resources in the {@link OptionalResources}:
     *
     * <ul>
     * <li>HPO ontology</li>
     * </ul>
     *
     * @return nothing
     */
    @Override
    protected Void call() {
        /*
        This is the place where we deserialize HPO ontology if we know path to the OBO file.
        We need to make sure to set ontology property of `optionalResources` to null if loading fails.
        This way we ensure that GUI elements dependent on ontology presence (labels, buttons) stay disabled
        and that the user will be notified about the fact that the ontology is missing.
         */
        String ontologyPath = pgProperties.getProperty(OptionalResources.ONTOLOGY_PATH_PROPERTY);
        if (ontologyPath != null) {
            final File hpJsonFile = new File(ontologyPath);
            if (hpJsonFile.isFile()) {
                String msg = String.format("Loading HPO from file '%s'", hpJsonFile.getAbsoluteFile());
                updateMessage(msg);
                LOGGER.info(msg);
                try {
                final Ontology ontology = OntologyLoader.loadOntology(new File(ontologyPath));
                optionalResources.setOntology(ontology);
                updateMessage("Ontology loaded");
                } catch (PhenolRuntimeException e) {
                    updateMessage(String.format("Error loading HPO file : %s", e.getMessage()));
                    LOGGER.warn("Error loading HPO file: ", e);
                    optionalResources.setOntology(null);
                }
            } else {
                optionalResources.setOntology(null);
            }
        } else {
            String msg = "Need to set path to hp.obo file (See edit menu)";
            updateMessage(msg);
            LOGGER.info(msg);
            optionalResources.setOntology(null);
        }
        return null;
    }
}
