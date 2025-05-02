package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button regButton;

    @FXML
    private TextField usernameField;

    @FXML
    void onLoginButton(ActionEvent event) {
        Request request = new Request(Operation.LOGIN);
        Response response = ServerClient.getInstance().sendRequest(request);

        AlertUtil.info("Login", response.toString());

        Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.USER_MENU);
    }

    @FXML
    void onRegButton(ActionEvent event) {
        Loader.loadScene((Stage) loginButton.getScene().getWindow(), ScenePath.REGISTRATION);
    }
}
