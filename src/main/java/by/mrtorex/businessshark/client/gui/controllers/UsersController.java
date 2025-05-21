package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.PortfolioService;
import by.mrtorex.businessshark.client.gui.services.RoleService;
import by.mrtorex.businessshark.client.gui.services.UserService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Person;
import by.mrtorex.businessshark.server.model.entities.Role;
import by.mrtorex.businessshark.server.model.entities.User;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Deserializer;
import by.mrtorex.businessshark.server.serializer.Serializer;
import by.mrtorex.businessshark.server.utils.PasswordHasher;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Контроллер для управления пользователями в графическом интерфейсе администратора.
 * Отвечает за добавление, удаление, обновление пользователей и загрузку их данных.
 */
@SuppressWarnings({"unused", "DuplicatedCode", "LoggingSimilarMessage"})
public class UsersController implements Initializable {
    private static final Logger logger = LogManager.getLogger(UsersController.class);

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addButton;

    @FXML
    private Button eraseButton;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TextField firstNameField;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TableColumn<User, String> patronymicColumn;

    @FXML
    private TableColumn<User, String> idColumn;

    @FXML
    private TextField patronymicField;

    @FXML
    private Button reInitButton;

    @FXML
    private ComboBox<Role> rolesComboBox;

    @FXML
    private Button updateButton;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private Button backButton;

    private final UserService userService;
    private final RoleService roleService;
    private final PortfolioService portfolioService;

    /**
     * Конструктор по умолчанию.
     */
    public UsersController() {
        this.userService = new UserService();
        this.roleService = new RoleService();
        this.portfolioService = new PortfolioService();
        logger.info("Инициализирован UsersController");
    }

    /**
     * Инициализирует контроллер после загрузки FXML.
     *
     * @param url            расположение FXML-файла
     * @param resourceBundle ресурсы для локализации
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableColumns();
        loadInformation();
        setupTableSelectionObserver();
        logger.info("Инициализация UsersController завершена");
    }

    /**
     * Настраивает столбцы таблицы для отображения данных пользователей.
     */
    private void configureTableColumns() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new SimpleStringProperty(user.getPerson() != null ? user.getPerson().getFirstName() : "");
        });
        lastNameColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new SimpleStringProperty(user.getPerson() != null ? user.getPerson().getLastName() : "");
        });
        patronymicColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new SimpleStringProperty(user.getPerson() != null ? user.getPerson().getPatronymic() : "");
        });
        roleColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new SimpleStringProperty(user.getRole() != null ? user.getRole().getName() : "");
        });
        logger.info("Настроены столбцы таблицы для отображения пользователей");
    }

    /**
     * Обрабатывает удаление выбранного пользователя.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onDeleteButton(ActionEvent event) {
        try {
            User selectedUser = getSelectedUser();
            if (selectedUser == null) {
                logger.warn("Попытка удаления без выбранного пользователя");
                return;
            }

            Response response = userService.delUser(selectedUser.getUsername());
            if (response == null) {
                logger.error("Получен null-ответ от сервера при удалении пользователя: {}", selectedUser.getUsername());
                AlertUtil.error("Ошибка удаления", "Сервер вернул некорректный ответ.");
                return;
            }

            if (response.isSuccess()) {
                AlertUtil.info("Удаление пользователя", response.getMessage());
                logger.info("Успешно удален пользователь: {}", selectedUser.getUsername());
                loadUsersTable();
            } else {
                logger.error("Ошибка удаления пользователя {}: {}", selectedUser.getUsername(), response.getMessage());
                AlertUtil.error("Ошибка удаления", response.getMessage());
            }
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при удалении пользователя: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка удаления", "Произошла ошибка при удалении пользователя: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает добавление нового пользователя.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onAddButton(ActionEvent event) {
        try {
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String username = usernameField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String patronymic = patronymicField.getText();
            Role role = rolesComboBox.getValue();

            logger.info("Попытка добавления пользователя: {}", username);

            if (!password.equals(confirmPassword)) {
                logger.warn("Пароли не совпадают для пользователя: {}", username);
                AlertUtil.error("Ошибка добавления", "Пароли не совпадают.");
                return;
            }

            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() ||
                    lastName.isEmpty() || patronymic.isEmpty() || role == null) {
                logger.warn("Попытка добавления пользователя с пустыми полями");
                AlertUtil.warning("Ошибка ввода", "Заполните все поля.");
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPasswordHash(PasswordHasher.hashPassword(password));
            newUser.setRole(role);

            Person person = new Person();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setPatronymic(patronymic);
            newUser.setPerson(person);

            String userJson = Serializer.toJson(newUser);
            Request request = new Request(Operation.REGISTER, userJson);
            Response response = ServerClient.getInstance().sendRequest(request);

            if (response == null) {
                logger.error("Получен null-ответ от сервера при добавлении пользователя: {}", username);
                AlertUtil.error("Ошибка добавления", "Сервер вернул некорректный ответ.");
                return;
            }

            if (response.isSuccess()) {
                if (Objects.equals(role.getName(), "User")) {
                    logger.info("Добавление баланса пользователю: {}", newUser.getUsername());
                    List<User> allUsers = new Deserializer().extractListData(userService.getAll().getData(), User.class);
                    User tempUser = new User();
                    for (User user : allUsers) {
                        if (user.getUsername().equals(newUser.getUsername()))
                            tempUser = user;
                    }
                    portfolioService.setAccount(tempUser.getId(), 9000);
                }
                AlertUtil.info("Добавление пользователя", "Пользователь успешно добавлен!");
                logger.info("Успешно добавлен пользователь: {}", username);
                loadInformation();
            } else {
                logger.error("Ошибка добавления пользователя {}: {}", username, response.getMessage());
                AlertUtil.error("Ошибка добавления", response.getMessage());
            }
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при добавлении пользователя: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка добавления", "Произошла ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    /**
     * Очищает поля ввода.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onEraseButton(ActionEvent event) {
        eraseFields();
        logger.info("Поля ввода очищены");
    }

    /**
     * Перезагружает данные пользователей и ролей.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onReInitButton(ActionEvent event) {
        try {
            loadInformation();
            logger.info("Данные пользователей и ролей перезагружены");
        } catch (Exception e) {
            logger.error("Ошибка перезагрузки данных: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка перезагрузки", "Не удалось перезагрузить данные: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает обновление данных выбранного пользователя.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onUpdateButton(ActionEvent event) {
        try {
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                logger.warn("Пароли не совпадают при обновлении пользователя");
                AlertUtil.error("Ошибка обновления", "Пароли не совпадают.");
                return;
            }

            User selectedUser = getSelectedUser();
            if (selectedUser == null) {
                logger.warn("Попытка обновления без выбранного пользователя");
                return;
            }

            Role selectedRole = getSelectedRole();
            if (selectedRole == null) {
                logger.warn("Попытка обновления без выбранной роли");
                return;
            }

            String username = usernameField.getText();
            String password = passwordField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String patronymic = patronymicField.getText();

            if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || patronymic.isEmpty()) {
                logger.warn("Попытка обновления пользователя с пустыми полями");
                AlertUtil.warning("Ошибка ввода", "Заполните все поля.");
                return;
            }

            selectedUser.setUsername(username);
            if (!password.isEmpty() && !selectedUser.getPasswordHash().equals(password)) {
                selectedUser.setPasswordHash(PasswordHasher.hashPassword(password));
            }
            selectedUser.getPerson().setFirstName(firstName);
            selectedUser.getPerson().setLastName(lastName);
            selectedUser.getPerson().setPatronymic(patronymic);
            selectedUser.setRole(selectedRole);

            double checkAccount = new Deserializer().extractData(portfolioService.getAccount(selectedUser.getId()).getData(), Double.TYPE);
            if (Objects.equals(selectedRole.getName(), "User") && checkAccount == -1.0) {
                logger.info("Добавление баланса пользователю: {}", selectedUser.getUsername());
                portfolioService.setAccount(selectedUser.getId(), 9000);
            }

            Response response = userService.updateUser(selectedUser);
            if (response == null) {
                logger.error("Получен null-ответ от сервера при обновлении пользователя: {}", selectedUser.getUsername());
                AlertUtil.error("Ошибка обновления", "Сервер вернул некорректный ответ.");
                return;
            }

            if (response.isSuccess()) {
                AlertUtil.info("Обновление пользователя", response.getMessage());
                logger.info("Успешно обновлен пользователь: {}", selectedUser.getUsername());
                if (ServerClient.getCurrentUser().getId().equals(selectedUser.getId())) {
                    ServerClient.setCurrentUser(null);
                    Loader.loadScene((Stage) updateButton.getScene().getWindow(), ScenePath.LOGIN);
                    logger.info("Текущий пользователь обновлен, выполнен выход на экран логина");
                }
                loadUsersTable();
            } else {
                logger.error("Ошибка обновления пользователя {}: {}", selectedUser.getUsername(), response.getMessage());
                AlertUtil.error("Ошибка обновления", response.getMessage());
            }
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при обновлении пользователя: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка обновления", "Произошла ошибка при обновлении пользователя: " + e.getMessage());
        }
    }

    /**
     * Возвращает администратора в главное меню.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    void onBackButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) backButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
            logger.info("Переход в главное меню администратора");
        } catch (Exception e) {
            logger.error("Ошибка перехода в главное меню: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка перехода", "Не удалось вернуться в главное меню: " + e.getMessage());
        }
    }

    /**
     * Очищает поля ввода.
     */
    private void eraseFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        patronymicField.setText("");
        rolesComboBox.setValue(null);
        logger.info("Поля ввода очищены");
    }

    /**
     * Заполняет поля ввода данными выбранного пользователя.
     */
    private void fillFields() {
        User selectedUser = getSelectedUser();
        if (selectedUser == null) {
            logger.warn("Попытка заполнения полей без выбранного пользователя");
            return;
        }

        usernameField.setText(selectedUser.getUsername());
        passwordField.setText(selectedUser.getPasswordHash());
        confirmPasswordField.setText(selectedUser.getPasswordHash());
        firstNameField.setText(selectedUser.getPerson() != null ? selectedUser.getPerson().getFirstName() : "");
        lastNameField.setText(selectedUser.getPerson() != null ? selectedUser.getPerson().getLastName() : "");
        patronymicField.setText(selectedUser.getPerson() != null ? selectedUser.getPerson().getPatronymic() : "");
        rolesComboBox.setValue(selectedUser.getRole());
        logger.info("Поля заполнены данными пользователя: {}", selectedUser.getUsername());
    }

    /**
     * Получает выбранного пользователя из таблицы.
     *
     * @return выбранный пользователь или null, если ничего не выбрано
     */
    private User getSelectedUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            logger.warn("Попытка действия без выбора пользователя");
            AlertUtil.warning("Выбор пользователя", "Сначала выберите пользователя!");
        }
        return selectedUser;
    }

    /**
     * Получает выбранную роль из выпадающего списка.
     *
     * @return выбранная роль или null, если ничего не выбрано
     */
    private Role getSelectedRole() {
        Role selectedRole = rolesComboBox.getValue();
        if (selectedRole == null) {
            logger.warn("Попытка действия без выбора роли");
            AlertUtil.warning("Выбор роли", "Сначала выберите роль!");
        }
        return selectedRole;
    }

    /**
     * Загружает данные пользователей и ролей.
     */
    private void loadInformation() {
        try {
            loadUsersTable();
            loadRolesComboBox();
            logger.info("Данные пользователей и ролей загружены");
        } catch (Exception e) {
            logger.error("Ошибка загрузки данных: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка загрузки", "Не удалось загрузить данные: " + e.getMessage());
        }
    }

    /**
     * Загружает список пользователей в таблицу.
     */
    private void loadUsersTable() {
        try {
            usersTable.getItems().clear();
            Response response = userService.getAll();
            if (response == null) {
                logger.error("Получен null-ответ от сервера при загрузке списка пользователей");
                AlertUtil.error("Ошибка загрузки", "Сервер вернул некорректный ответ.");
                return;
            }

            if (!response.isSuccess()) {
                logger.error("Ошибка загрузки списка пользователей: {}", response.getMessage());
                AlertUtil.error("Ошибка загрузки", response.getMessage());
                return;
            }

            List<User> users = new Deserializer().extractListData(response.getData(), User.class);
            usersTable.setItems(FXCollections.observableArrayList(users));
            logger.info("Загружено {} пользователей в таблицу", users.size());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при загрузке списка пользователей: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка загрузки", "Произошла ошибка при загрузке пользователей: " + e.getMessage());
        }
    }

    /**
     * Загружает список ролей в выпадающий список.
     */
    private void loadRolesComboBox() {
        try {
            Response response = roleService.getAll();
            if (response == null) {
                logger.error("Получен null-ответ от сервера при загрузке списка ролей");
                AlertUtil.error("Ошибка загрузки", "Сервер вернул некорректный ответ.");
                return;
            }

            if (!response.isSuccess()) {
                logger.error("Ошибка загрузки списка ролей: {}", response.getMessage());
                AlertUtil.error("Ошибка загрузки", response.getMessage());
                return;
            }

            List<Role> roles = new Deserializer().extractListData(response.getData(), Role.class);
            rolesComboBox.setItems(FXCollections.observableArrayList(roles));
            logger.info("Загружено {} ролей в выпадающий список", roles.size());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при загрузке списка ролей: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка загрузки", "Произошла ошибка при загрузке ролей: " + e.getMessage());
        }
    }

    /**
     * Настраивает наблюдатель за выбором строк в таблице.
     */
    private void setupTableSelectionObserver() {
        usersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFields();
                logger.info("Выбран пользователь: {}", newValue.getUsername());
            } else {
                eraseFields();
                logger.info("Сброшена выбранная строка пользователя");
            }
        });
    }
}