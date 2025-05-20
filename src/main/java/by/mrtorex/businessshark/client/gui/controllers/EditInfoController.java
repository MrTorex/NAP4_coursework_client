package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.UserService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.model.entities.User;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.utils.PasswordHasher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер для редактирования личной информации пользователя.
 * Позволяет изменить имя, фамилию, отчество, логин и пароль.
 * После успешного обновления пользователя перенаправляет на экран логина.
 */
@SuppressWarnings("unused")
public class EditInfoController implements Initializable {
    private static final Logger logger = LogManager.getLogger(EditInfoController.class);

    @FXML
    private Button backToMenuButton;

    @FXML
    private Button editButton;

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
    private TextField usernameField;

    private User currentUser;

    /**
     * Обработчик кнопки возврата в меню.
     * Направляет пользователя в зависимости от роли (админ/пользователь).
     */
    @FXML
    void onBackToMenuButton(ActionEvent event) {
        if (ServerClient.getCurrentUser().getId() == 1) {
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
            logger.info("Переход к админ-меню");
        } else {
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.USER_MENU);
            logger.info("Переход к пользовательскому меню");
        }
    }

    /**
     * Обработчик кнопки сохранения изменений пользователя.
     * Валидирует пароль, обновляет данные и отправляет на сервер.
     */
    @FXML
    void onEditButton(ActionEvent event) {
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.equals(confirmPassword)) {
            AlertUtil.error("Ошибка обновления", "Пароли не совпадают.");
            logger.warn("Попытка обновления с несовпадающими паролями");
            return;
        }

        currentUser.setUsername(usernameField.getText().trim());

        if (!currentUser.getPasswordHash().equals(passwordField.getText())) {
            currentUser.setPasswordHash(PasswordHasher.hashPassword(passwordField.getText()));
            logger.info("Пароль пользователя ID {} обновлен", currentUser.getId());
        }

        currentUser.getPerson().setFirstName(firstNameField.getText().trim());
        currentUser.getPerson().setLastName(lastNameField.getText().trim());
        currentUser.getPerson().setPatronymic(patronymicField.getText().trim());

        UserService userService = new UserService();
        Response response = userService.updateUser(currentUser);

        if (response.isSuccess()) {
            AlertUtil.info("Редактирование", response.getMessage());
            logger.info("Данные пользователя ID {} успешно обновлены", currentUser.getId());
            ServerClient.setCurrentUser(null);
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.LOGIN);
        } else {
            AlertUtil.error("Ошибка обновления", response.getMessage());
            logger.error("Ошибка обновления данных пользователя ID {}: {}", currentUser.getId(), response.getMessage());
        }
    }

    /**
     * Инициализация контроллера: загрузка текущих данных пользователя в поля ввода.
     *
     * @param url URL ресурса
     * @param resourceBundle пакет ресурсов
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = ServerClient.getCurrentUser();

        passwordField.clear();
        confirmPasswordField.clear();

        firstNameField.setText(currentUser.getPerson().getFirstName());
        lastNameField.setText(currentUser.getPerson().getLastName());
        patronymicField.setText(currentUser.getPerson().getPatronymic());
        usernameField.setText(currentUser.getUsername());
        passwordField.setText(currentUser.getPasswordHash());
        confirmPasswordField.setText(currentUser.getPasswordHash());

        logger.info("Инициализирован EditInfoController для пользователя ID {}", currentUser.getId());
    }
}
