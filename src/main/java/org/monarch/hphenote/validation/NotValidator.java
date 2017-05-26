package org.monarch.hphenote.validation;

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

/**
 * Created by robinp on 5/25/17.
 */
public class NotValidator {


    public static  boolean isValid(String s) {
        System.out.println("NotVA \""+s+"\"");
        if (s==null || s.isEmpty() || s.equals("") || s.equals("NOT"))
            return true;
        else
            return false;

    }

}
