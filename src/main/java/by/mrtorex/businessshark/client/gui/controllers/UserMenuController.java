package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.network.ServerClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер для главного меню пользователя в графическом интерфейсе.
 * Отвечает за навигацию по основным функциям пользователя и отображение приветственного сообщения.
 */
@SuppressWarnings("unused")
public class UserMenuController implements Initializable {
    private static final Logger logger = LogManager.getLogger(UserMenuController.class);

    @FXML
    private Button portfolioButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editInfoButton;

    @FXML
    private Label helloLabel;

    /**
     * Конструктор по умолчанию.
     */
    public UserMenuController() {
        logger.info("Инициализирован UserMenuController");
    }

    /**
     * Инициализирует контроллер после загрузки FXML.
     *
     * @param url            расположение FXML-файла
     * @param resourceBundle ресурсы для локализации
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            if (ServerClient.getCurrentUser() == null || ServerClient.getCurrentUser().getPerson() == null) {
                logger.error("Отсутствует информация о текущем пользователе");
                throw new IllegalStateException("Текущий пользователь не определён");
            }
            helloLabel.setText(String.format("Добро пожаловать, %s!", ServerClient.getCurrentUser().getPerson().getFirstName()));
            logger.info("Установлено приветственное сообщение для пользователя: {}", ServerClient.getCurrentUser().getPerson().getFirstName());
        } catch (IllegalStateException e) {
            logger.error("Ошибка инициализации UserMenuController: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Переходит на экран портфеля пользователя.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onPortfolioButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) portfolioButton.getScene().getWindow(), ScenePath.PORTFOLIO);
            logger.info("Переход на экран портфеля");
        } catch (Exception e) {
            logger.error("Ошибка перехода на экран портфеля: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось перейти на экран портфеля: " + e.getMessage());
        }
    }

    /**
     * Выполняет выход из аккаунта и переход на экран логина.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onExitButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) exitButton.getScene().getWindow(), ScenePath.LOGIN);
            logger.info("Выход из аккаунта и переход на экран логина");
        } catch (Exception e) {
            logger.error("Ошибка выхода из аккаунта: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось выйти из аккаунта: " + e.getMessage());
        }
    }

    /**
     * Переходит на экран редактирования информации пользователя.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onEditInfoButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) editInfoButton.getScene().getWindow(), ScenePath.EDIT_INFO);
            logger.info("Переход на экран редактирования информации пользователя");
        } catch (Exception e) {
            logger.error("Ошибка перехода на экран редактирования информации: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось перейти на экран редактирования информации: " + e.getMessage());
        }
    }
}