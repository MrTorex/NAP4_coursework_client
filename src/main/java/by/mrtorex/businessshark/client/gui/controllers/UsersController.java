package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class UsersController {

    @FXML
    private Button backToMenuButton;

    public void onAddButton(ActionEvent event) {
    }

    public void onDeleteButton(ActionEvent event) {
    }

    public void onChangeButton(ActionEvent event) {
    }

    @FXML
    public void onBackToMenuButton(ActionEvent event) {
        Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
    }
}
