package org.monarchinitiative.phenotefx.gui;

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


import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.util.Collection;

/**
 * This class adds missing (in my opinion) functionality to {@link org.controlsfx.control.textfield.TextFields} class.
 * Created by Daniel Danis on 5/31/17.
 */
public class WidthAwareTextFields {

    /**
     * Create autocompletion binding between given {@link TextField} instance and Collection of possible suggestions.
     * Additionally, bind the minWidthProperty of suggestion box to widthProperty of textField.
     * @param textField TextField to which the suggestions will be offered.
     * @param possibleSuggestions Collection of all possible suggestions.
     * @param <T> generic parameter (we use a String)
     */
    public static <T> AutoCompletionBinding<T> bindWidthAwareAutoCompletion(
            TextField textField, Collection<T> possibleSuggestions) {
        AutoCompletionTextFieldBinding<T> k = new AutoCompletionTextFieldBinding<>(textField,
                SuggestionProvider.create(possibleSuggestions));
        k.minWidthProperty().bind(textField.widthProperty());
        return k;
    }

}