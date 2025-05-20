package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.StockService;
import by.mrtorex.businessshark.client.gui.utils.*;
import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Stock;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Deserializer;
import by.mrtorex.businessshark.server.serializer.Serializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

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

    @FXML
    public Button exportToExcelButton;

    @FXML
    public Button exportToMarkdownButton;

    @FXML
    public Button exportToPDFButton;

    @FXML
    public Button exportToJSONButton;

    @FXML
    public Button exportToHTMLButton;

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

    private List<Map<String, Object>> convertStocksToMapList(List<Stock> stocks) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Stock stock : stocks) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("ID", stock.getId());
            map.put("Ticket", stock.getTicket());
            map.put("Price", stock.getPrice());
            map.put("Amount", stock.getAmount());
            result.add(map);
        }
        return result;
    }

    @FXML
    public void onExportToExcelButton(ActionEvent event) {
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в Excel", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Excel-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        fileChooser.setInitialFileName("stocks.xlsx");

        File file = fileChooser.showSaveDialog(exportToExcelButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new ExcelExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить Excel-файл:\n" + e.getMessage());
        }
    }


    @FXML
    public void onExportToMarkdownButton(ActionEvent event) {
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в Markdown", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Markdown-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown файлы", "*.md"));
        fileChooser.setInitialFileName("stocks.md");

        File file = fileChooser.showSaveDialog(exportToMarkdownButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new MarkdownExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить Markdown-файл:\n" + e.getMessage());
        }
    }

    @FXML
    public void onExportToPDFButton(ActionEvent event) {
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в PDF", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить PDF-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF файлы", "*.pdf"));
        fileChooser.setInitialFileName("stocks.pdf");

        File file = fileChooser.showSaveDialog(exportToPDFButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new PdfExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить PDF-файл:\n" + e.getMessage());
        }
    }

    @FXML
    public void onExportToJSONButton(ActionEvent event) {
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в JSON", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить JSON-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON файлы", "*.json"));
        fileChooser.setInitialFileName("stocks.json");

        File file = fileChooser.showSaveDialog(exportToJSONButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new JsonExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить JSON-файл:\n" + e.getMessage());
        }
    }

    @FXML
    public void onExportToHTMLButton(ActionEvent event) {
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в HTML", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить HTML-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML файлы", "*.html"));
        fileChooser.setInitialFileName("stocks.html");

        File file = fileChooser.showSaveDialog(exportToHTMLButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new HtmlExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить HTML-файл:\n" + e.getMessage());
        }
    }
}
