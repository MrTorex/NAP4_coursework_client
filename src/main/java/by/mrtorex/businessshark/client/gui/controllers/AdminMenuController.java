package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminMenuController {

    @FXML
    private Button usersButton;

    @FXML
    private Button stocksButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editInfoButton;

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
}
