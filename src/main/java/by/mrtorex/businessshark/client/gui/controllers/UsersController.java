package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.services.RoleService;
import by.mrtorex.businessshark.client.gui.services.UserService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.client.gui.enums.ScenePath;
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

import java.net.URL;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
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

    private UserService userService;
    private RoleService roleService;

    @FXML
    void onDeleteButton(ActionEvent event) {
        User selectedUser = getSelectedUser();

        if (selectedUser == null) {
            return;
        }

        Response response = userService.delUser(selectedUser.getUsername());

        if (response.isSuccess()) {
            AlertUtil.info("User deleting", response.getMessage());

            loadUsersTable();
        } else {
            AlertUtil.error("User deleting", response.getMessage());
        }
    }

    @FXML
    void onAddButton(ActionEvent event) {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (!password.equals(confirmPassword)) {
            AlertUtil.error("Add User Error", "Passwords do not match.");
            return;
        }

        if (usernameField.getText().isEmpty() || password.isEmpty() ||
                firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                patronymicField.getText().isEmpty() || rolesComboBox.getValue() == null) {
            AlertUtil.warning("Validation Warning", "Please fill in all fields.");
            return;
        }

        User newUser = new User();
        newUser.setUsername(usernameField.getText());
        newUser.setPasswordHash(PasswordHasher.hashPassword(password));

        Role role = new Role();
        role.setName(String.valueOf(rolesComboBox.getValue()));
        newUser.setRole(role);

        Person person = new Person();
        person.setFirstName(firstNameField.getText());
        person.setLastName(lastNameField.getText());
        person.setPatronymic(patronymicField.getText());
        newUser.setPerson(person);

        String userJson = Serializer.toJson(newUser);
        Request request = new Request(Operation.REGISTER, userJson);

        Response response = ServerClient.getInstance().sendRequest(request);

        if (response.isSuccess()) {
            AlertUtil.info("User added", "User was added successfully!");
        } else {
            AlertUtil.error("User add error", response.getMessage());
        }

        this.loadInformation();
    }

    @FXML
    void onEraseButton(ActionEvent event) {
        eraseFields();
    }

    private void eraseFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        patronymicField.setText("");
        rolesComboBox.setValue(null);
    }

    private void fillFields() {
        User selectedUser = getSelectedUser();

        if (selectedUser == null) {
            return;
        }

        usernameField.setText(selectedUser.getUsername());
        passwordField.setText(selectedUser.getPasswordHash());
        confirmPasswordField.setText(selectedUser.getPasswordHash());
        firstNameField.setText(selectedUser.getPerson().getFirstName());
        lastNameField.setText(selectedUser.getPerson().getLastName());
        patronymicField.setText(selectedUser.getPerson().getPatronymic());

        rolesComboBox.setValue(selectedUser.getRole());
    }

    @FXML
    void onReInitButton(ActionEvent event) {
        loadInformation();
    }

    @FXML
    void onUpdateButton(ActionEvent event) {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertUtil.error("Update Error", "Passwords do not match.");
            return;
        }

        User selectedUser = getSelectedUser();

        if (selectedUser == null) {
            return;
        }

        Role selectedRole = getSelectedRole();

        if (selectedRole == null) {
            return;
        }

        selectedUser.setUsername(usernameField.getText());
        selectedUser.setPasswordHash(PasswordHasher.hashPassword(passwordField.getText()));
        selectedUser.getPerson().setFirstName(firstNameField.getText());
        selectedUser.getPerson().setLastName(lastNameField.getText());
        selectedUser.getPerson().setPatronymic(patronymicField.getText());
        selectedUser.setRole(selectedRole);

        Response response = userService.updateUser(selectedUser);

        if (response.isSuccess()) {
            AlertUtil.info("User updating", response.getMessage());

            loadUsersTable();
        } else {
            AlertUtil.error("User updating", response.getMessage());
        }
    }

    private User getSelectedUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            AlertUtil.warning("Selection Warning", "You may select user first!");
            return null;
        }

        return selectedUser;
    }

    private Role getSelectedRole() {
        Role selectedRole = rolesComboBox.getValue();

        if (selectedRole == null) {
            AlertUtil.warning("Selection Warning", "You may select role first!");
            return null;
        }

        return selectedRole;
    }

    @FXML
    void onBackButton(ActionEvent event) {
        Loader.loadScene((Stage) backButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userService = new UserService();
        this.roleService = new RoleService();

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
            return new SimpleStringProperty(user.getPerson() != null ? user.getRole().getName() : "");
        });

        loadInformation();

        setupTableSelectionObserver();
    }

    private void loadInformation() {
        loadUsersTable();
        loadRolesComboBox();
    }

    private void loadUsersTable() {
        usersTable.getItems().clear();

        Response response = userService.getAll();

        if (!response.isSuccess()) {
            AlertUtil.error("Load Information Error", response.getMessage());
        } else {
            usersTable.setItems(FXCollections.observableArrayList(
                    new Deserializer().extractListData(response.getData(), User.class)
            ));
        }
    }

    private void loadRolesComboBox() {
        Response response = roleService.getAll();

        if (!response.isSuccess()) {
            AlertUtil.error("Load Information Error", response.getMessage());
        } else {
            rolesComboBox.setItems(FXCollections.observableArrayList(
                    new Deserializer().extractListData(response.getData(), Role.class)
            ));
        }
    }

    private void setupTableSelectionObserver() {
        usersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFields();
            } else {
                eraseFields();
            }
        });
    }
}
