package by.mrtorex.businessshark.client;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.network.ServerClient;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import lombok.Getter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

/**
 * Главный класс клиентского приложения Business Shark.
 * Отвечает за запуск и настройку основного окна приложения.
 */
public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /** Название текущей темы приложения */
    public static String themeName = "SYNTHWAVE";

    /** Основная сцена приложения */
    @Getter
    private static Stage primaryStage;

    /**
     * Точка входа в JavaFX приложение.
     *
     * @param stage Основная сцена, автоматически создаваемая JavaFX
     * @throws Exception Если произошла ошибка при инициализации приложения
     */
    @Override
    public void start(Stage stage) throws Exception {
        try {
            logger.info("Запуск приложения...");
            primaryStage = stage;

            configurePrimaryStage();
            Loader.loadScene(primaryStage, ScenePath.LOGIN);

            logger.info("Отображение основного окна...");
            primaryStage.show();
            logger.info("Приложение успешно запущено");
        } catch (Exception e) {
            logger.error("Критическая ошибка при запуске приложения", e);
            logger.info("Аварийное завершение работы...");
            throw new Exception("Ошибка при запуске приложения", e);
        }
    }

    /**
     * Настраивает основные параметры главного окна приложения.
     *
     * @throws RuntimeException Если не удалось загрузить иконку приложения
     */
    private void configurePrimaryStage() {
        logger.info("Настройка основного окна...");

        primaryStage.setTitle("Business Shark");

        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/logo.png");
            if (iconStream == null) {
                throw new RuntimeException("Не удалось загрузить иконку приложения");
            }
            Image icon = new Image(iconStream);
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            logger.error("Ошибка при загрузке иконки приложения", e);
            throw new RuntimeException("Ошибка при загрузке иконки", e);
        }
    }

    /**
     * Вызывается при завершении работы приложения.
     * Обеспечивает корректное отключение от сервера.
     */
    @Override
    public void stop() {
        ServerClient client = ServerClient.getInstance();
        if (client != null) {
            client.disconnect();
        }
    }

    /**
     * Главный метод для запуска приложения.
     *
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            logger.info("Запуск JavaFX приложения...");
            Application.launch(args);
            logger.info("Приложение завершило работу");
        } catch (Exception e) {
            logger.fatal("Необработанное исключение в главном потоке", e);
        }
    }
}