package by.mrtorex.businessshark.client;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger("main");

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            logger.info("Starting up application...");

            logger.info("Setting up primary stage (setTitle)...");
            primaryStage.setTitle("Business Shark");

            logger.info("Setting up primary stage (setIcon)...");
            InputStream iconStream = getClass().getResourceAsStream("/images/logo.png");
            Image icon = new Image(iconStream);
            primaryStage.getIcons().add(icon);

            logger.info("Setting up primary stage (loadLoginScene)...");
            Loader.loadScene(primaryStage, ScenePath.LOGIN);

            logger.info("Done setting up primary stage! Showing primary stage...");
            primaryStage.show();
            logger.info("Done showing primary stage!");
        }
        catch (Exception e) {
            logger.error(e);
            logger.info("Exiting application (emergency shutdown)...");
            throw new Exception(e);
        }
    }

    @Override
    public void stop() {
        ServerClient client = ServerClient.getInstance();

        if (client != null) {
            client.disconnect();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
        logger.info("Exiting application...");
    }
}
