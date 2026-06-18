package org.monarchinitiative.phenotefx;

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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class StageInitializer implements ApplicationListener<PhenoteFxApplication.StageReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageInitializer.class);

    private static String inlineCss = """
    .boxSpacing { -fx-padding: 2 10 2 10; }
    .root { -fx-font: 14px "Helvetica"; }
    .mylabel { -fx-text-fill: black; -fx-font: 16px "Helvetica"; -fx-padding: 5 10 5 10; }
    .w7 { -fx-background-color: #1c2fb1; -fx-padding: 4 4 4 4; -fx-text-fill: white; -fx-font-size: 14px; }
    .button {
       -fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%);
       -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2; -fx-text-fill: black;
       -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );
    }
    .button:hover {
       -fx-background-color: rgba(0,0,0,0.08), linear-gradient(#5a61af, #51536d), linear-gradient(#e4fbff 0%,#cee6fb 10%, #a5d3fb 50%, #88c6fb 51%, #d5faff 100%);
       -fx-text-fill: #242d35;
    }
    .button:pressed {
        -fx-background-color: #000000, linear-gradient(#7ebcea, #2f4b8f), linear-gradient(#426ab7, #263e75), linear-gradient(#395cab, #223768);
        -fx-text-fill: white;
    }
    .mytext { -fx-font-smoothing-type: lcd; -fx-fill: blue; -fx-font-size: 16pt; }
    """;

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
        Stage stage = event.getStage();
        ClassLoader classLoader = StageInitializer.class.getClassLoader();
        
        try (InputStream phenoteFxmlStream = classLoader.getResourceAsStream("fxml/phenote.fxml")) {
            if (phenoteFxmlStream == null) {
                throw new IOException("Could not load fxml/phenote.fxml stream.");
            }

            FXMLLoader fxmlLoader = new FXMLLoader();
            // CRITICAL: Force FXMLLoader to resolve your Controller from the Spring Context
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            
            Parent parent = fxmlLoader.load(phenoteFxmlStream);
            Scene scene = new Scene(parent, 1300, 950);
            
            String base64Css = Base64.getEncoder().encodeToString(inlineCss.getBytes(StandardCharsets.UTF_8));
            scene.getStylesheets().add("data:text/css;base64," + base64Css);
        
            stage.setScene(scene);
            stage.setTitle(applicationTitle);
            stage.setResizable(false);
            
            // Load application icon safely before showing the stage layout
            try (InputStream iconStream = classLoader.getResourceAsStream("img/phenotefx.jpg")) {
                if (iconStream != null) {
                    Image image = new Image(iconStream);
                    stage.getIcons().add(image);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load application stage window icon wrapper", e);
            }

            stage.show(); 
        } catch (IOException e) {
            LOGGER.error("Fatal exception during primary application frame initialization step", e);
            e.printStackTrace();
        }
    }
}