package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PortfolioController {

    @FXML
    private Button sellButton;

    @FXML
    private Button buyButton;

    @FXML
    private Button backToMenuButton;

    public void onBuyButton(ActionEvent event) {
    }

    @FXML
    public void onBackToMenuButton(ActionEvent event) {
        Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.USER_MENU);
    }

    public void onSellButton(ActionEvent actionEvent) {
    }
}
