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


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<PhenoteFxApplication.StageReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageInitializer.class);

    @Value("classpath:phenotefx.fxml")
    private Resource fenominalFxmResource;
    private final String applicationTitle;

    private final ApplicationContext applicationContext;


    public StageInitializer(ApplicationContext context) {
        this.applicationTitle = "PhenoteFX";
        this.applicationContext = context;
    }


    @Override
    public void onApplicationEvent(PhenoteFxApplication.StageReadyEvent event) {
        System.out.println("CP="+ System.getProperty("java.class.path"));
        Stage stage = null;
        try {
            ClassPathResource res = new ClassPathResource("fxml/phenote.fxml");
            ClassPathResource cssRes = new ClassPathResource("css/phenote.css");
            LOGGER.info("Loading fxml from {}", res.getFile().getAbsoluteFile());
            FXMLLoader fxmlLoader = new FXMLLoader(res.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();
            stage = event.getStage();
            Scene scene = new Scene(parent, 1300, 950);
            scene.getStylesheets().add(cssRes.getPath());
            stage.setScene(scene);

            stage.setTitle(applicationTitle);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ClassPathResource imgRes = new ClassPathResource("img/phenotefx.jpg");
            Image image = new Image(imgRes.getInputStream());
            stage.getIcons().add(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
