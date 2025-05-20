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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Контроллер для управления компаниями и их акциями в клиентском GUI.
 * Обеспечивает загрузку, добавление, обновление, удаление компаний,
 * а также управление акциями, связанными с выбранной компанией.
 */
@SuppressWarnings("unused")
public class CompaniesController implements Initializable {
    private static final Logger logger = LogManager.getLogger(CompaniesController.class);

    @FXML
    private Button backToMenuButton;
    @FXML
    private TableView<Company> companiesTable;
    @FXML
    private TableColumn<Company, String> idColumn;
    @FXML
    private TableColumn<Company, String> nameColumn;

    @FXML
    private TableView<Stock> companyStocksTable;
    @FXML
    private TableColumn<Stock, String> stockIdColumn;
    @FXML
    private TableColumn<Stock, String> ticketColumn;
    @FXML
    private TableColumn<Stock, String> priceColumn;
    @FXML
    private TableColumn<Stock, String> amountColumn;

    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button eraseButton;
    @FXML
    private Button addStockButton;
    @FXML
    private Button deleteStockButton;

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<Stock> stocksComboBox;

    private CompanyService companyService;
    private StockService stockService;
    private Company selectedCompany;

    /**
     * Инициализация контроллера: настройка таблиц, загрузка данных, установка слушателей.
     *
     * @param url            URL ресурса
     * @param resourceBundle пакет ресурсов
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        companyService = new CompanyService();
        stockService = new StockService();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        stockIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ticketColumn.setCellValueFactory(new PropertyValueFactory<>("ticket"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        loadCompanies();
        loadAllStocks();

        companiesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedCompany = newValue;
                    if (newValue != null) {
                        loadCompanyStocks(newValue);
                        nameField.setText(newValue.getName());
                    } else {
                        companyStocksTable.getItems().clear();
                        nameField.clear();
                    }
                });
        logger.info("Инициализирован CompaniesController");
    }

    /**
     * Загружает список всех компаний в таблицу.
     */
    private void loadCompanies() {
        Response response = companyService.getAll();
        if (response.isSuccess()) {
            List<Company> companies = new Deserializer().extractListData(response.getData(), Company.class);
            companiesTable.setItems(FXCollections.observableArrayList(companies));
            logger.info("Загружено {} компаний", companies.size());
        } else {
            logger.error("Ошибка загрузки компаний: {}", response.getMessage());
            AlertUtil.error("Ошибка загрузки компаний", response.getMessage());
        }
    }

    /**
     * Загружает список всех акций без привязки к компаниям в комбобокс.
     */
    private void loadAllStocks() {
        Response response = stockService.getAllStocksWithNoCompany();
        if (response.isSuccess()) {
            List<Stock> stocks = new Deserializer().extractListData(response.getData(), Stock.class);
            stocksComboBox.setItems(FXCollections.observableArrayList(stocks));
            logger.info("Загружено {} свободных акций", stocks.size());
        } else {
            logger.error("Ошибка загрузки акций: {}", response.getMessage());
            AlertUtil.error("Ошибка загрузки акций", response.getMessage());
        }
    }

    /**
     * Загружает акции выбранной компании в таблицу.
     *
     * @param company выбранная компания
     */
    private void loadCompanyStocks(Company company) {
        Response response = companyService.getCompanyStocks(company);
        if (response.isSuccess()) {
            List<Stock> stocks = new Deserializer().extractListData(response.getData(), Stock.class);
            companyStocksTable.setItems(FXCollections.observableArrayList(stocks));
            logger.info("Загружено {} акций компании ID {}", stocks.size(), company.getId());
        } else {
            logger.error("Ошибка загрузки акций компании ID {}: {}", company.getId(), response.getMessage());
            AlertUtil.error("Ошибка загрузки акций компании", response.getMessage());
        }
    }

    /**
     * Обработчик добавления новой компании.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onAddButton(ActionEvent event) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            AlertUtil.warning("Предупреждение", "Введите название компании");
            logger.warn("Попытка добавления компании с пустым названием");
            return;
        }

        Company newCompany = new Company();
        newCompany.setName(name);

        Response response = companyService.addCompany(newCompany);
        if (response.isSuccess()) {
            logger.info("Компания '{}' добавлена", name);
            AlertUtil.info("Успех", "Компания добавлена успешно");
            loadCompanies();
            nameField.clear();
        } else {
            logger.error("Ошибка добавления компании '{}': {}", name, response.getMessage());
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    /**
     * Обработчик удаления выбранной компании.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onDeleteButton(ActionEvent event) {
        Company selected = companiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Предупреждение", "Выберите компанию для удаления");
            logger.warn("Попытка удаления компании без выбора");
            return;
        }

        Response response = companyService.delCompany(selected);
        if (response.isSuccess()) {
            logger.info("Компания ID {} удалена", selected.getId());
            AlertUtil.info("Успех", "Компания удалена успешно");
            loadCompanies();
            loadAllStocks();
            companiesTable.refresh();
            nameField.clear();
        } else {
            logger.error("Ошибка удаления компании ID {}: {}", selected.getId(), response.getMessage());
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    /**
     * Обработчик обновления выбранной компании.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onUpdateButton(ActionEvent event) {
        Company selected = companiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.warning("Предупреждение", "Выберите компанию для обновления");
            logger.warn("Попытка обновления компании без выбора");
            return;
        }

        String newName = nameField.getText().trim();
        if (newName.isEmpty()) {
            AlertUtil.warning("Предупреждение", "Введите новое название компании");
            logger.warn("Попытка обновления компании ID {} с пустым названием", selected.getId());
            return;
        }

        selected.setName(newName);
        Response response = companyService.updateCompany(selected);
        if (response.isSuccess()) {
            logger.info("Данные компании ID {} обновлены", selected.getId());
            AlertUtil.info("Успех", "Данные компании обновлены");
            loadCompanies();
            loadAllStocks();
            companiesTable.refresh();
            nameField.clear();
        } else {
            logger.error("Ошибка обновления компании ID {}: {}", selected.getId(), response.getMessage());
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    /**
     * Обработчик добавления акции к выбранной компании.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onAddStockButton(ActionEvent event) {
        if (selectedCompany == null) {
            AlertUtil.warning("Предупреждение", "Выберите компанию");
            logger.warn("Попытка добавления акции без выбора компании");
            return;
        }

        Stock selectedStock = stocksComboBox.getSelectionModel().getSelectedItem();
        if (selectedStock == null) {
            AlertUtil.warning("Предупреждение", "Выберите акцию");
            logger.warn("Попытка добавления акции без выбора акции");
            return;
        }

        Request request = new Request(
                Operation.JOIN_STOCK_COMPANY,
                Serializer.toJson(new Pair<>(selectedStock, selectedCompany))
        );

        Response response = ServerClient.getInstance().sendRequest(request);
        if (response.isSuccess()) {
            logger.info("Акция ID {} добавлена к компании ID {}", selectedStock.getId(), selectedCompany.getId());
            AlertUtil.info("Успех", "Акция добавлена к компании");
            loadCompanyStocks(selectedCompany);
            loadAllStocks();
            stocksComboBox.setPromptText("Выберите акцию");
        } else {
            logger.error("Ошибка добавления акции ID {} к компании ID {}: {}", selectedStock.getId(), selectedCompany.getId(), response.getMessage());
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    /**
     * Обработчик удаления акции из выбранной компании.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onDeleteStockButton(ActionEvent event) {
        if (selectedCompany == null) {
            AlertUtil.warning("Предупреждение", "Выберите компанию");
            logger.warn("Попытка удаления акции без выбора компании");
            return;
        }

        Stock selectedStock = companyStocksTable.getSelectionModel().getSelectedItem();
        if (selectedStock == null) {
            AlertUtil.warning("Предупреждение", "Выберите акцию для удаления");
            logger.warn("Попытка удаления акции без выбора акции");
            return;
        }

        Request request = new Request(
                Operation.SEPARATE_STOCK_COMPANY,
                Serializer.toJson(new Pair<>(selectedStock, selectedCompany))
        );

        Response response = ServerClient.getInstance().sendRequest(request);
        if (response.isSuccess()) {
            logger.info("Акция ID {} удалена из компании ID {}", selectedStock.getId(), selectedCompany.getId());
            AlertUtil.info("Успех", "Акция удалена из компании");
            loadCompanyStocks(selectedCompany);
            loadAllStocks();
            stocksComboBox.setPromptText("Выберите акцию");
        } else {
            logger.error("Ошибка удаления акции ID {} из компании ID {}: {}", selectedStock.getId(), selectedCompany.getId(), response.getMessage());
            AlertUtil.error("Ошибка", response.getMessage());
        }
    }

    /**
     * Очистка поля ввода имени компании.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onEraseButton(ActionEvent event) {
        nameField.clear();
        logger.info("Поле ввода имени компании очищено");
    }

    /**
     * Возврат к административному меню.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onBackToMenuButton(ActionEvent event) {
        Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
        logger.info("Переход к административному меню");
    }
}