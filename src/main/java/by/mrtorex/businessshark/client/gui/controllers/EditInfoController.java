package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.UserService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;

import by.mrtorex.businessshark.server.model.entities.Role;
import by.mrtorex.businessshark.server.model.entities.User;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.utils.PasswordHasher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditInfoController implements Initializable {
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
    private Button registerButton;

    @FXML
    private TextField usernameField;

    private User currentUser;

    @FXML
    void onBackToMenuButton(ActionEvent event) {
        if (ServerClient.getCurrentUser().getId() == 1)
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
        else
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.USER_MENU);
    }

    @FXML
    void onEditButton(ActionEvent event) {
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertUtil.error("Update Error", "Passwords do not match.");
            return;
        }

        currentUser.setUsername(usernameField.getText());

        if (!currentUser.getPasswordHash().equals(passwordField.getText())) {
            currentUser.setPasswordHash(PasswordHasher.hashPassword(passwordField.getText()));
        }

        currentUser.getPerson().setFirstName(firstNameField.getText());
        currentUser.getPerson().setLastName(lastNameField.getText());
        currentUser.getPerson().setPatronymic(patronymicField.getText());

        UserService userService = new UserService();
        Response response = userService.updateUser(currentUser);

        if (response.isSuccess()) {
            AlertUtil.info("Edit info", response.getMessage());
            ServerClient.setCurrentUser(null);
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.LOGIN);
        } else {
            AlertUtil.error("Edit info", response.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = ServerClient.getCurrentUser();
        passwordField.setText(currentUser.getPasswordHash());
        confirmPasswordField.setText(currentUser.getPasswordHash());
        firstNameField.setText(currentUser.getPerson().getFirstName());
        lastNameField.setText(currentUser.getPerson().getLastName());
        patronymicField.setText(currentUser.getPerson().getPatronymic());
        usernameField.setText(currentUser.getUsername());
    }
}

