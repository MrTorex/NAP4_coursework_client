package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.StockService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Stock;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Deserializer;
import by.mrtorex.businessshark.server.serializer.Serializer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StocksController implements Initializable {
    @FXML
    private Button backToMenuButton;

    @FXML
    private TableView<Stock> stocksTable;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addButton;

    @FXML
    private Button eraseButton;

    @FXML
    private Button updateButton;

    @FXML
    private TableColumn<Stock, String> idColumn;

    @FXML
    private TableColumn<Stock, String> ticketColumn;

    @FXML
    private TableColumn<Stock, String> priceColumn;

    @FXML
    private TableColumn<Stock, String> amountColumn;

    @FXML
    private TextField ticketField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField amountField;

    private StockService stockService;

    public void onAddButton(ActionEvent event) {
        String ticket = ticketField.getText();
        Double price = Double.valueOf(priceField.getText());
        Integer amount = Integer.valueOf(amountField.getText());

        Stock newStock = new Stock();

        newStock.setTicket(ticket);
        newStock.setPrice(price);
        newStock.setAmount(amount);

        String stockJson = Serializer.toJson(newStock);
        Request request = new Request(Operation.CREATE_STOCK, stockJson);

        Response response = ServerClient.getInstance().sendRequest(request);

        if (response.isSuccess()) {
            AlertUtil.info("User added", "User was added successfully!");
        } else {
            AlertUtil.error("User add error", response.getMessage());
        }

        this.loadStocksTable();
    }

    public void onDeleteButton(ActionEvent event) {
        Stock selectedStock = getSelectedStock();

        if (selectedStock == null) {
            return;
        }

        Response response = stockService.delStock(selectedStock);

        if (response.isSuccess()) {
            AlertUtil.info("Stock deleting", response.getMessage());

            loadStocksTable();
        } else {
            AlertUtil.error("Stock deleting", response.getMessage());
        }
    }

    @FXML
    public void onBackToMenuButton(ActionEvent event) {
        Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
    }

    public void onEraseButton(ActionEvent actionEvent) {
        eraseFields();
    }

    public void onUpdateButton(ActionEvent event) {

        Stock selectedStock = getSelectedStock();

        if (selectedStock == null) {
            return;
        }

        selectedStock.setTicket(ticketField.getText());
        selectedStock.setPrice(Double.valueOf(priceField.getText()));
        selectedStock.setAmount(Integer.valueOf(amountField.getText()));

        Response response = stockService.updateStock(selectedStock);

        if (response.isSuccess()) {
            AlertUtil.info("Stock updating", response.getMessage());
            loadStocksTable();
        } else {
            AlertUtil.error("Stock updating", response.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.stockService = new StockService();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ticketColumn.setCellValueFactory(new PropertyValueFactory<>("ticket"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        loadStocksTable();

        setupTableSelectionObserver();
    }

    private void loadStocksTable() {
        stocksTable.getItems().clear();

        Response response = stockService.getAll();

        if (!response.isSuccess()) {
            AlertUtil.error("Load Information Error", response.getMessage());
        } else {
            stocksTable.setItems(FXCollections.observableArrayList(
                    new Deserializer().extractListData(response.getData(), Stock.class)
            ));
        }
    }

    private void setupTableSelectionObserver() {
        stocksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFields();
            } else {
                eraseFields();
            }
        });
    }

    private Stock getSelectedStock() {
        Stock selectedStock = stocksTable.getSelectionModel().getSelectedItem();

        if (selectedStock == null) {
            AlertUtil.warning("Selection Warning", "You may select stock first!");
            return null;
        }

        return selectedStock;
    }

    private void fillFields() {
        Stock selectedStock = getSelectedStock();

        if (selectedStock == null) {
            return;
        }

        ticketField.setText(selectedStock.getTicket());
        priceField.setText(selectedStock.getPrice().toString());
        amountField.setText(selectedStock.getAmount().toString());
    }

    private void eraseFields() {
        ticketField.setText("");
        priceField.setText("");
        amountField.setText("");
    }
}
