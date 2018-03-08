package org.monarchinitiative.phenotefx;

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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.monarchinitiative.phenotefx.framework.Injector;
import org.monarchinitiative.phenotefx.gui.Platform;
import org.monarchinitiative.phenotefx.gui.main.PhenotePresenter;
import org.monarchinitiative.phenotefx.gui.main.PhenoteView;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by robinp on 5/22/17.
 * HPO Phenote
 * An application for biocurating the small files for annotating
 * rare diseases with Human Phenotype Ontology (HPO) terms.
 * @author Peter Robinson
 * @version 0.0.2 (15 June, 2017)
 */
public class PhenoteFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        updateLog4jConfiguration();
        PhenoteView appView = new PhenoteView();
        Scene scene = new Scene(appView.getView());
        stage.setTitle("PhenoteFX");
        final String uri = getClass().getResource("phenotefx.css").toExternalForm();
        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        Image image = new Image(PhenoteFX.class.getResourceAsStream("/img/phenotefx.jpg"));
        stage.getIcons().add(image);
        if (Platform.isMacintosh()) {
            try {
                URL iconURL = PhenoteFX.class.getResource("/img/phenotefx.jpg");
                java.awt.Image macimage = new ImageIcon(iconURL).getImage();
                com.apple.eawt.Application.getApplication().setDockIconImage(macimage);
            } catch (Exception e) {
                // Won't work on Windows or Linux. Just skip it!
            }
        }
        PhenotePresenter presenter = (PhenotePresenter) appView.getPresenter();
        presenter.setPrimaryStage(stage);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        Injector.forgetAll();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This sets the location of the log4j log file to the user's .vpvgui directory.
     */
    private void updateLog4jConfiguration() {
        File dir = Platform.getPhenoteFXDir();
        String logpath = (new File(dir + File.separator + "phenotefx.log")).getAbsolutePath();
        configureLog4j("trace",logpath);
    }




    private void configureLog4j(String level, String logpath) {
        Properties props = new Properties();
        props.put("log4j.rootLogger", level+", stdlog, logfile");
        props.put("log4j.appender.stdlog", "org.apache.log4j.ConsoleAppender");
        props.put("log4j.appender.stdlog.target", "System.out");
        props.put("log4j.appender.stdlog.layout", "org.apache.log4j.PatternLayout");
        props.put("log4j.appender.stdlog.layout.ConversionPattern",
                "[%p] %d{MM-dd-yyyy HH:mm:ss} [%t] (%F:%L) - %m%n");
       props.put("log4j.appender.logfile","org.apache.log4j.RollingFileAppender");
        props.setProperty("log4j.appender.logfile.file", logpath);
        props.put("log4j.appender.logfile.MaxFileSize","100KB");
        props.put("log4j.appender.logfile.MaxBackupIndex","2");
        props.put("log4j.appender.logfile.layout","org.apache.log4j.PatternLayout");
        props.put("log4j.appender.logfile.layout.ConversionPattern","[%p] [%d{MM-dd-yyyy HH:mm:ss}] (%F:%L) - %m%n");

        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
    }
}
