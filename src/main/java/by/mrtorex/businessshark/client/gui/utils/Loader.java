package by.mrtorex.businessshark.client.gui.utils;

import by.mrtorex.businessshark.client.Main;
import by.mrtorex.businessshark.client.gui.enums.ScenePath;

import by.mrtorex.businessshark.client.gui.enums.ThemesPath;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Loader {

    private static final Logger logger = LogManager.getLogger("loader");

    public static void loadSceneWithThrowException(Stage stage, ScenePath scenePath) throws IOException {
        logger.info("Loading scene from path: {}", scenePath.getPathToFxml());

        URL stagePathURL = Loader.class.getResource(scenePath.getPathToFxml());
        FXMLLoader loader = new FXMLLoader(stagePathURL);
        Parent root = loader.load();

        stage.setResizable(true);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(Loader.class.getResource(String.valueOf(ThemesPath.valueOf(Main.themeName).getPathToCss()))).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(Loader.class.getResource("/styles/styles.css")).toExternalForm());
        stage.setScene(scene);

        logger.info("Scene loaded successfully: {}", scenePath.getPathToFxml());
    }

    public static void loadScene(Stage stage, ScenePath scenePath) {
        try {
            loadSceneWithThrowException(stage, scenePath);
        } catch (IOException e) {
            logger.error("Error loading scene: {}", scenePath.getPathToFxml(), e);
            AlertUtil.error("Navigation Error", "Could not navigate.");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while loading scene: {}", scenePath.getPathToFxml(), e);
        }
    }

    public static void reloadForTheme() {
        loadScene(Main.getPrimaryStage(), ScenePath.LOGIN);
    }
}
