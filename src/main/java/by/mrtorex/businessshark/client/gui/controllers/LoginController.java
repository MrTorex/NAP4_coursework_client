package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.UserService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.model.entities.User;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Deserializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button regButton;

    @FXML
    private TextField usernameField;

    private UserService userService;

    @FXML
    void onLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Response response = userService.login(username, password);

        if (response.isSuccess()) {
            ServerClient.setCurrentUser(new Deserializer().extractData(response.getData(), User.class));

            switch (ServerClient.getCurrentUser().getRole().getId())
            {
                case 1 -> Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
                case 2 -> Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.USER_MENU);
                case 3 -> AlertUtil.info("Please, wait", "You weren't approved by admin.");
                case 4 -> AlertUtil.error("Login Error", "You was banned by admin!");
                default -> throw new IllegalStateException("Unexpected value: " + ServerClient.getCurrentUser().getRole().getId());
            }
        } else {
            AlertUtil.error("Login Error", response.getMessage());
        }
    }

    @FXML
    void onRegButton(ActionEvent event) {
        Loader.loadScene((Stage) regButton.getScene().getWindow(), ScenePath.REGISTRATION);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userService = new UserService();
    }
}
