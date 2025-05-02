package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class UserMenuController {

    @FXML
    private Button portfolioButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button editInfoButton;

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
}
