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

/**
 * Утилита для загрузки сцен в приложении.
 * Позволяет загружать FXML-сцены и управлять стилями.
 */
public class Loader {
    private static final Logger logger = LogManager.getLogger(Loader.class);

    /**
     * Загружает сцену из указанного пути с выбросом исключения в случае ошибки.
     *
     * @param stage    основная сцена приложения
     * @param scenePath путь к FXML-файлу
     * @throws IOException если произошла ошибка при загрузке сцены
     */
    public static void loadSceneWithThrowException(Stage stage, ScenePath scenePath) throws IOException {
        logger.info("Загрузка сцены из пути: {}", scenePath.getPathToFxml());

        URL stagePathURL = Loader.class.getResource(scenePath.getPathToFxml());
        FXMLLoader loader = new FXMLLoader(stagePathURL);
        Parent root = loader.load();

        stage.setResizable(true);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(Loader.class.getResource(String.valueOf(ThemesPath.valueOf(Main.themeName).getPathToCss()))).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(Loader.class.getResource("/styles/styles.css")).toExternalForm());
        stage.setScene(scene);

        logger.info("Сцена успешно загружена: {}", scenePath.getPathToFxml());
    }

    /**
     * Загружает сцену из указанного пути и обрабатывает возможные ошибки.
     *
     * @param stage    основная сцена приложения
     * @param scenePath путь к FXML-файлу
     */
    public static void loadScene(Stage stage, ScenePath scenePath) {
        try {
            loadSceneWithThrowException(stage, scenePath);
        } catch (IOException e) {
            logger.error("Ошибка при загрузке сцены: {}", scenePath.getPathToFxml(), e);
            AlertUtil.error("Ошибка навигации", "Не удалось перейти к сцене.");
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при загрузке сцены: {}", scenePath.getPathToFxml(), e);
            AlertUtil.error("Неожиданная ошибка", "Произошла ошибка при загрузке сцены.");
        }
    }

    /**
     * Перезагружает сцену для применения темы.
     */
    public static void reloadForTheme() {
        loadScene(Main.getPrimaryStage(), ScenePath.LOGIN);
    }
}
