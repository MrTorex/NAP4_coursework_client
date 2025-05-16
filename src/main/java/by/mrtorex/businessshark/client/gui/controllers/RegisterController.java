package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.services.UserService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.server.network.Response;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    private UserService userService;

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
    void onRegisterButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String patronymic = patronymicField.getText();

        if (!password.equals(confirmPassword)) {
            AlertUtil.error("Registration Error", "Passwords do not match.");
            return;
        }

        Response response = userService.register(username, password, firstName, lastName, patronymic);

        if (response.isSuccess()) {
            AlertUtil.info("Registration Successful", "You have registered successfully. Wait for admin approvement.");

            Loader.loadScene((Stage) registerButton.getScene().getWindow(), ScenePath.LOGIN);
        } else {
            AlertUtil.error("Registration Error", response.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userService = new UserService();
    }
}
