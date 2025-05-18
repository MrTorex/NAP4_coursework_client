package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.network.ServerClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {

    @FXML
    public Button companiesButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button stocksButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editInfoButton;

    @FXML
    private Label helloLabel;

    @FXML
    void onUsersButton(ActionEvent event) {
        Loader.loadScene((Stage) usersButton.getScene().getWindow(), ScenePath.USERS);
    }

    @FXML
    void onStocksButton(ActionEvent event) {
        Loader.loadScene((Stage) stocksButton.getScene().getWindow(), ScenePath.STOCKS);
    }

    @FXML
    void onExitButton(ActionEvent event) {
        Loader.loadScene((Stage) exitButton.getScene().getWindow(), ScenePath.LOGIN);
    }

    @FXML
    void onEditInfoButton(ActionEvent event) {
        Loader.loadScene((Stage) editInfoButton.getScene().getWindow(), ScenePath.EDIT_INFO);
    }

    @FXML
    void onCompaniesButton(ActionEvent event) {
        Loader.loadScene((Stage) companiesButton.getScene().getWindow(), ScenePath.COMPANIES);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        helloLabel.setText(String.format("Добро пожаловать, %s!", ServerClient.getCurrentUser().getPerson().getFirstName()));
    }
}
