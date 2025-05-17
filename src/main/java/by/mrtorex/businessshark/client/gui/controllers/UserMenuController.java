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

public class UserMenuController implements Initializable {

    @FXML
    private Button portfolioButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editInfoButton;

    @FXML
    private Label helloLabel;

    @FXML
    void onPortfolioButton(ActionEvent event) {
        Loader.loadScene((Stage) exitButton.getScene().getWindow(), ScenePath.PORTFOLIO);
    }

    @FXML
    void onExitButton(ActionEvent event) {
        Loader.loadScene((Stage) exitButton.getScene().getWindow(), ScenePath.LOGIN);
    }

    @FXML
    void onEditInfoButton(ActionEvent event) {
        Loader.loadScene((Stage) editInfoButton.getScene().getWindow(), ScenePath.EDIT_INFO);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        helloLabel.setText(String.format("Добро пожаловать, %s!", ServerClient.getCurrentUser().getPerson().getFirstName()));
    }
}
