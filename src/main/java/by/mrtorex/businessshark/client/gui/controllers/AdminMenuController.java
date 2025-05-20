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
 * Контроллер главного меню администратора.
 * Обрабатывает переходы между сценами и отображает приветственное сообщение.
 */
@SuppressWarnings("unused")
public class AdminMenuController implements Initializable {
    private static final Logger logger = LogManager.getLogger(AdminMenuController.class);

    @FXML
    public Button companiesButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button stocksButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editInfoButton;

    @FXML
    private Label helloLabel;

    /**
     * Обрабатывает нажатие кнопки перехода к списку пользователей.
     *
     * @param event событие нажатия
     */
    @FXML
    void onUsersButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) usersButton.getScene().getWindow(), ScenePath.USERS);
            logger.info("Переход к сцене пользователей");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к сцене пользователей", e);
            throw new RuntimeException("Не удалось загрузить сцену пользователей", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки перехода к списку акций.
     *
     * @param event событие нажатия
     */
    @FXML
    void onStocksButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) stocksButton.getScene().getWindow(), ScenePath.STOCKS);
            logger.info("Переход к сцене акций");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к сцене акций", e);
            throw new RuntimeException("Не удалось загрузить сцену акций", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки выхода в окно авторизации.
     *
     * @param event событие нажатия
     */
    @FXML
    void onExitButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) exitButton.getScene().getWindow(), ScenePath.LOGIN);
            logger.info("Переход к сцене авторизации");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к сцене авторизации", e);
            throw new RuntimeException("Не удалось загрузить сцену авторизации", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки перехода к редактированию информации пользователя.
     *
     * @param event событие нажатия
     */
    @FXML
    void onEditInfoButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) editInfoButton.getScene().getWindow(), ScenePath.EDIT_INFO);
            logger.info("Переход к сцене редактирования информации");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к сцене редактирования информации", e);
            throw new RuntimeException("Не удалось загрузить сцену редактирования информации", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки перехода к списку компаний.
     *
     * @param event событие нажатия
     */
    @FXML
    void onCompaniesButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) companiesButton.getScene().getWindow(), ScenePath.COMPANIES);
            logger.info("Переход к сцене компаний");
        } catch (Exception e) {
            logger.error("Ошибка при переходе к сцене компаний", e);
            throw new RuntimeException("Не удалось загрузить сцену компаний", e);
        }
    }

    /**
     * Инициализация контроллера.
     * Устанавливает приветственное сообщение с именем текущего пользователя.
     *
     * @param url            URL, использованный для загрузки FXML
     * @param resourceBundle ресурсы локализации
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            var currentUser = ServerClient.getCurrentUser();
            if (currentUser == null || currentUser.getPerson() == null || currentUser.getPerson().getFirstName() == null) {
                logger.warn("Данные текущего пользователя недоступны");
                helloLabel.setText("Добро пожаловать!");
            } else {
                helloLabel.setText(String.format("Добро пожаловать, %s!", currentUser.getPerson().getFirstName()));
                logger.info("Установлено приветственное сообщение для пользователя {}", currentUser.getPerson().getFirstName());
            }
        } catch (Exception e) {
            logger.error("Ошибка при инициализации приветственного сообщения", e);
            helloLabel.setText("Добро пожаловать!");
        }
    }
}
