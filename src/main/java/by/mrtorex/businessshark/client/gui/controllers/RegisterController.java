package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
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

    @FXML
    void onBackToLoginButton(ActionEvent event) {
        Loader.loadScene((Stage) backToLoginButton.getScene().getWindow(), ScenePath.LOGIN);
    }

    @FXML
    void onRegisterButton(ActionEvent event) {}
}

