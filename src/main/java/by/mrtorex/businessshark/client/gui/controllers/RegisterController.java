package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.UserService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.network.Response;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер для управления регистрацией пользователя в графическом интерфейсе.
 * Отвечает за обработку формы регистрации и взаимодействие с сервисом пользователей.
 */
@SuppressWarnings("unused")
public class RegisterController implements Initializable {
    private static final Logger logger = LogManager.getLogger(RegisterController.class);

    private final UserService userService;

    @FXML
    private Button backToLoginButton;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField patronymicField;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;

    /**
     * Конструктор по умолчанию.
     */
    public RegisterController() {
        this.userService = new UserService();
        logger.info("Инициализирован RegisterController");
    }

    /**
     * Инициализирует контроллер после загрузки FXML.
     *
     * @param url            расположение FXML-файла
     * @param resourceBundle ресурсы для локализации
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Начало инициализации RegisterController");
    }

    /**
     * Обрабатывает возврат на экран логина.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onBackToLoginButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) backToLoginButton.getScene().getWindow(), ScenePath.LOGIN);
            logger.info("Переход на экран логина");
        } catch (Exception e) {
            logger.error("Ошибка перехода на экран логина: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка перехода", "Не удалось вернуться на экран логина: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает регистрацию нового пользователя.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onRegisterButton(ActionEvent event) {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String patronymic = patronymicField.getText();

            logger.info("Попытка регистрации пользователя: {}", username);

            if (username == null || username.isEmpty()) {
                logger.warn("Попытка регистрации с пустым именем пользователя");
                AlertUtil.error("Ошибка регистрации", "Имя пользователя не может быть пустым.");
                return;
            }

            if (password == null || password.isEmpty()) {
                logger.warn("Попытка регистрации с пустым паролем");
                AlertUtil.error("Ошибка регистрации", "Пароль не может быть пустым.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                logger.warn("Пароли не совпадают для пользователя: {}", username);
                AlertUtil.error("Ошибка регистрации", "Пароли не совпадают.");
                return;
            }

            Response response = userService.register(username, password, firstName, lastName, patronymic);
            if (response == null) {
                logger.error("Получен null-ответ от сервиса регистрации для пользователя: {}", username);
                AlertUtil.error("Ошибка регистрации", "Сервис регистрации вернул некорректный ответ.");
                return;
            }

            if (response.isSuccess()) {
                AlertUtil.info("Успешная регистрация", "Регистрация прошла успешно. Ожидайте подтверждения администратора.");
                logger.info("Успешная регистрация пользователя: {}", username);
                Loader.loadScene((Stage) registerButton.getScene().getWindow(), ScenePath.LOGIN);
            } else {
                logger.error("Ошибка регистрации пользователя {}: {}", username, response.getMessage());
                AlertUtil.error("Ошибка регистрации", response.getMessage());
            }
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при регистрации: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка регистрации", "Произошла ошибка при регистрации: " + e.getMessage());
        }
    }
}