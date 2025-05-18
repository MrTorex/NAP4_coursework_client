package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.CompanyService;
import by.mrtorex.businessshark.client.gui.services.StockService;
import by.mrtorex.businessshark.client.gui.utils.AlertUtil;
import by.mrtorex.businessshark.client.gui.utils.Loader;
import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Company;
import by.mrtorex.businessshark.server.model.entities.Stock;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Deserializer;
import by.mrtorex.businessshark.server.serializer.Serializer;
import by.mrtorex.businessshark.server.utils.Pair;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CompaniesController implements Initializable {
    @FXML private Button backToMenuButton;
    @FXML private TableView<Company> companiesTable;
    @FXML private TableColumn<Company, String> idColumn;
    @FXML private TableColumn<Company, String> nameColumn;

    @FXML private TableView<Stock> companyStocksTable;
    @FXML private TableColumn<Stock, String> stockIdColumn;
    @FXML private TableColumn<Stock, String> ticketColumn;
    @FXML private TableColumn<Stock, String> priceColumn;
    @FXML private TableColumn<Stock, String> amountColumn;

    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button updateButton;
    @FXML private Button eraseButton;
    @FXML private Button addStockButton;
    @FXML private Button deleteStockButton;

    @FXML private TextField nameField;
    @FXML private ComboBox<Stock> stocksComboBox;

    private CompanyService companyService;
    private StockService stockService;
    private Company selectedCompany;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.companyService = new CompanyService();
        this.stockService = new StockService();

        // Настройка таблицы компаний
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Настройка таблицы акций компании
        stockIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ticketColumn.setCellValueFactory(new PropertyValueFactory<>("ticket"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Загрузка данных
        loadCompanies();
        loadAllStocks();

        // Обработчик выбора компании
        companiesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedCompany = newValue;
                    if (newValue != null) {
                        loadCompanyStocks(newValue);
                        nameField.setText(newValue.getName());
                    }
                });
    }

    private void loadCompanies() {
        Response response = companyService.getAll();
        if (response.isSuccess()) {
            List<Company> companies = new Deserializer().extractListData(response.getData(), Company.class);
            companiesTable.setItems(FXCollections.observableArrayList(companies));
        } else {
            AlertUtil.error("Ошибка загрузки компаний", response.getMessage());
        }
    }

    private void loadAllStocks() {
        Response response = stockService.getAllStocksWithNoCompany();
        if (response.isSuccess()) {
            List<Stock> stocks = new Deserializer().extractListData(response.getData(), Stock.class);
            stocksComboBox.setItems(FXCollections.observableArrayList(stocks));
        } else {
            AlertUtil.error("Ошибка загрузки акций", response.getMessage());
        }
    }

    private void loadCompanyStocks(Company company) {
        Response response = companyService.getCompanyStocks(company);

        if (response.isSuccess()) {
            List<Stock> stocks = new Deserializer().extractListData(response.getData(), Stock.class);
            companyStocksTable.setItems(FXCollections.observableArrayList(stocks));
        } else {
            AlertUtil.error("Ошибка загрузки акций компании", response.getMessage());
        }
    }

    @FXML
    public void onAddButton(ActionEvent event) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            AlertUtil.warning("Предупреждение", "Введите название компании");
            return;
        }

        Company newCompany = new Company();
        newCompany.setName(name);

        Response response = companyService.addCompany(newCompany);
        if (response.isSuccess()) {
            AlertUtil.info("Успех", "Компания добавлена успешно");
            loadCompanies();
            nameField.clear();
        } else {
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    @FXML
    public void onDeleteButton(ActionEvent event) {
        Company selected = companiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Предупреждение", "Выберите компанию для удаления");
            return;
        }

        Response response = companyService.delCompany(selected);
        if (response.isSuccess()) {
            AlertUtil.info("Успех", "Компания удалена успешно");
            loadCompanies();
            loadAllStocks();
            companiesTable.refresh();
            nameField.clear();
        } else {
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    @FXML
    public void onUpdateButton(ActionEvent event) {
        Company selected = companiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Предупреждение", "Выберите компанию для обновления");
            return;
        }

        String newName = nameField.getText().trim();
        if (newName.isEmpty()) {
            AlertUtil.warning("Предупреждение", "Введите новое название компании");
            return;
        }

        selected.setName(newName);
        Response response = companyService.updateCompany(selected);
        if (response.isSuccess()) {
            AlertUtil.info("Успех", "Данные компании обновлены");
            loadCompanies();
            loadAllStocks();
            companiesTable.refresh();
            nameField.clear();
        } else {
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    @FXML
    public void onAddStockButton(ActionEvent event) {
        if (selectedCompany == null) {
            AlertUtil.warning("Предупреждение", "Выберите компанию");
            return;
        }

        Stock selectedStock = stocksComboBox.getSelectionModel().getSelectedItem();
        if (selectedStock == null) {
            AlertUtil.warning("Предупреждение", "Выберите акцию");
            return;
        }

        Request request = new Request(
                Operation.JOIN_STOCK_COMPANY,
                Serializer.toJson(new Pair<>(selectedStock, selectedCompany))
        );

        Response response = ServerClient.getInstance().sendRequest(request);
        if (response.isSuccess()) {
            AlertUtil.info("Успех", "Акция добавлена к компании");
            loadCompanyStocks(selectedCompany);
            loadAllStocks();
            stocksComboBox.setPromptText("Выберите акцию");
        } else {
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    @FXML
    public void onDeleteStockButton(ActionEvent event) {
        if (selectedCompany == null) {
            AlertUtil.warning("Предупреждение", "Выберите компанию");
            return;
        }

        Stock selectedStock = companyStocksTable.getSelectionModel().getSelectedItem();
        if (selectedStock == null) {
            AlertUtil.warning("Предупреждение", "Выберите акцию для удаления");
            return;
        }

        Request request = new Request(
                Operation.SEPARATE_STOCK_COMPANY,
                Serializer.toJson(new Pair<>(selectedStock, selectedCompany))
        );

        Response response = ServerClient.getInstance().sendRequest(request);
        if (response.isSuccess()) {
            AlertUtil.info("Успех", "Акция удалена из компании");
            loadCompanyStocks(selectedCompany);
            loadAllStocks();
            stocksComboBox.setPromptText("Выберите акцию");
        } else {
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    @FXML
    public void onEraseButton(ActionEvent event) {
        nameField.clear();
    }

    @FXML
    public void onBackToMenuButton(ActionEvent event) {
        Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
    }
}