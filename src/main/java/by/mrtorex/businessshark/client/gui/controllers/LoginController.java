package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.Main;
import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.enums.ThemesPath;
import by.mrtorex.businessshark.client.gui.services.UserService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.model.entities.User;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Deserializer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер экрана авторизации.
 * Обрабатывает ввод пользователя, выполнение входа, смену темы и переход к регистрации.
 */
@SuppressWarnings("unused")
public class LoginController implements Initializable {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button regButton;

    @FXML
    private TextField usernameField;

    @FXML
    private ComboBox<ThemesPath> themesComboBox;

    private UserService userService;

    /**
     * Обработка нажатия кнопки входа.
     * Выполняет авторизацию, проверяет роль и направляет на соответствующую сцену.
     */
    @FXML
    void onLoginButton(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtil.warning("Внимание", "Пожалуйста, заполните все поля.");
            return;
        }

        Response response = userService.login(username, password);

        if (response.isSuccess()) {
            User loggedUser = new Deserializer().extractData(response.getData(), User.class);
            ServerClient.setCurrentUser(loggedUser);
            logger.info("Пользователь '{}' успешно вошел в систему с ролью '{}'", username, loggedUser.getRole().getId());

            switch (loggedUser.getRole().getId()) {
                case 1 -> Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
                case 2 -> Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.USER_MENU);
                case 3 -> {
                    AlertUtil.info("Ожидание одобрения", "Ваш аккаунт ожидает одобрения администратором.");
                    logger.warn("Пользователь '{}' не одобрен администратором", username);
                }
                case 4 -> {
                    AlertUtil.error("Доступ заблокирован", "Вы были заблокированы администратором.");
                    logger.warn("Заблокированный пользователь '{}' попытался войти", username);
                }
                default -> {
                    logger.error("Неизвестная роль пользователя '{}': {}", username, loggedUser.getRole().getId());
                    throw new IllegalStateException("Unexpected role ID: " + loggedUser.getRole().getId());
                }
            }
        } else {
            AlertUtil.error("Ошибка входа", response.getMessage());
            logger.warn("Неудачная попытка входа пользователя '{}': {}", username, response.getMessage());
        }
    }

    /**
     * Обработка перехода на экран регистрации.
     */
    @FXML
    void onRegButton(ActionEvent event) {
        Loader.loadScene((Stage) regButton.getScene().getWindow(), ScenePath.REGISTRATION);
        logger.info("Переход на экран регистрации");
    }

    /**
     * Обработка изменения темы оформления.
     * Перезагружает текущее окно с выбранной темой.
     */
    @FXML
    void onThemesComboBox(ActionEvent event) {
        ThemesPath selectedTheme = themesComboBox.getValue();
        if (selectedTheme != null) {
            Main.themeName = selectedTheme.name();
            Loader.reloadForTheme();
            logger.info("Изменена тема на '{}'", selectedTheme.name());
        }
    }

    /**
     * Инициализация контроллера: загрузка списка тем и создание сервиса пользователя.
     * @param url URL ресурса
     * @param resourceBundle пакет ресурсов
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        themesComboBox.setItems(FXCollections.observableArrayList(ThemesPath.values()));
        themesComboBox.setPromptText(Main.themeName);

        this.userService = new UserService();
        logger.info("Инициализация LoginController завершена");
    }
}
