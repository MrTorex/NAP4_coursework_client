package by.mrtorex.businessshark.client.gui.controllers;

import by.mrtorex.businessshark.client.gui.enums.ScenePath;
import by.mrtorex.businessshark.client.gui.services.PortfolioService;
import by.mrtorex.businessshark.client.gui.services.StockService;
import by.mrtorex.businessshark.client.gui.utils.*;
import by.mrtorex.businessshark.server.model.entities.Stock;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Deserializer;
import by.mrtorex.businessshark.server.utils.Pair;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

public class PortfolioController implements Initializable {

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

    @FXML
    private Label balanceLabel;

    @FXML
    private TableView<Map<String, Object>> userStocksTable;

    @FXML
    private TableColumn<Map, String> userStockTicketColumn;

    @FXML
    private TableColumn<Map, String> userStockPriceColumn;

    @FXML
    private TableColumn<Map, String> userStockAmountColumn;

    @FXML
    private TextField sellTextbox;

    @FXML
    private Button sellButton;

    @FXML
    private TableView<Map<String, Object>> allStocksTable;

    @FXML
    private TableColumn<Map, String> allStockTicketColumn;

    @FXML
    private TableColumn<Map, String> allStockPriceColumn;

    @FXML
    private TableColumn<Map, String> allStockAmountColumn;

    @FXML
    private TableColumn<Map, String> allStockAviableAmountColumn;

    @FXML
    private TextField buyTextbox;

    @FXML
    private Button buyButton;

    @FXML
    private Button backToMenuButton;

    private PortfolioService portfolioService;
    private StockService stockService;
    private Double currentBalance = 0.0;

    private final Map<Integer, Stock> stockByIdMap = new HashMap<>();
    private final Map<Integer, Stock> userStockByIdMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        portfolioService = new PortfolioService();
        stockService = new StockService();

        // Пользовательские акции
        userStockTicketColumn.setCellValueFactory(new MapValueFactory<>("ticket"));
        userStockPriceColumn.setCellValueFactory(new MapValueFactory<>("price"));
        userStockAmountColumn.setCellValueFactory(new MapValueFactory<>("amount"));

        // Все акции
        allStockTicketColumn.setCellValueFactory(new MapValueFactory<>("ticket"));
        allStockPriceColumn.setCellValueFactory(new MapValueFactory<>("price"));
        allStockAmountColumn.setCellValueFactory(new MapValueFactory<>("amount"));
        allStockAviableAmountColumn.setCellValueFactory(new MapValueFactory<>("availableAmount"));

        loadPortfolioData();
    }

    private void loadPortfolioData() {
        // Баланс
        Response balanceResponse = portfolioService.getAccount();
        if (!balanceResponse.isSuccess()) {
            AlertUtil.error("Ошибка загрузки баланса", balanceResponse.getMessage());
            return;
        }
        try {
            currentBalance = Double.parseDouble(balanceResponse.getData());
            balanceLabel.setText(String.format("Ваш баланс: %.2f$", currentBalance));
        } catch (Exception e) {
            AlertUtil.error("Ошибка обработки баланса", "Некорректный формат баланса");
            return;
        }

        // Пользовательские акции
        Response userStocksResponse = portfolioService.getUserStocks();
        System.out.println(userStocksResponse);
        if (!userStocksResponse.isSuccess()) {
            AlertUtil.error("Ошибка загрузки пользовательских акций", userStocksResponse.getMessage());
            return;
        }

        Type listType = new TypeToken<List<Pair<Stock, Integer>>>() {}.getType();
        List<Pair<Stock, Integer>> userStocks = new Deserializer().extractData(userStocksResponse.getData(), listType);
        System.out.println("userStocks: " + userStocks);
        ObservableList<Map<String, Object>> userStockRows = FXCollections.observableArrayList();
        userStockByIdMap.clear();
        for (Pair<Stock, Integer> stock : userStocks) {
            Map<String, Object> row = new HashMap<>();
            row.put("ticket", stock.getKey().getTicket());
            row.put("price", String.valueOf(stock.getKey().getPrice()));
            row.put("amount", stock.getValue());
            userStockRows.add(row);
            userStockByIdMap.put(stock.getKey().getId(), stock.getKey());
        }
        userStocksTable.setItems(userStockRows);

        // Все акции
        Response allStocksResponse = stockService.getAll();
        Response noCompanyStocksResponse = stockService.getAllStocksWithNoCompany();
        System.out.println(allStocksResponse);
        if (!allStocksResponse.isSuccess()) {
            AlertUtil.error("Ошибка загрузки всех акций", allStocksResponse.getMessage());
            return;
        }

        List<Stock> allStocks = new Deserializer().extractListData(allStocksResponse.getData(), Stock.class);
        List<Stock> allNoCompanyStocks = new Deserializer().extractListData(noCompanyStocksResponse.getData(), Stock.class);
        ObservableList<Map<String, Object>> allStockRows = FXCollections.observableArrayList();
        stockByIdMap.clear();
        for (Stock stock : allStocks) {
            if (allNoCompanyStocks.contains(stock))
                continue;
            Map<String, Object> row = new HashMap<>();
            System.out.println(stock.getId());
            System.out.println(portfolioService.getStockAvialableAmount(stock.getId()));
            int available = new Deserializer().extractData(
                    portfolioService.getStockAvialableAmount(stock.getId()).getData(),
                    Integer.TYPE
            );
            row.put("ticket", stock.getTicket());
            row.put("price", String.valueOf(stock.getPrice()));
            row.put("amount", stock.getAmount());
            row.put("availableAmount", available);
            allStockRows.add(row);
            stockByIdMap.put(stock.getId(), stock);
        }
        allStocksTable.setItems(allStockRows);
    }

    @FXML
    public void onBuyButton(ActionEvent event) {
        Map<String, Object> selectedRow = allStocksTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            AlertUtil.warning("Выбор акции", "Выберите акцию для покупки.");
            return;
        }

        String qtyText = buyTextbox.getText();
        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertUtil.warning("Неверное количество", "Введите положительное число.");
            return;
        }

        String ticket = (String) selectedRow.get("ticket");
        Stock selectedStock = stockByIdMap.values().stream()
                .filter(s -> s.getTicket().equals(ticket))
                .findFirst().orElse(null);

        if (selectedStock == null) {
            AlertUtil.error("Ошибка", "Не удалось найти данные акции.");
            return;
        }

        int available = new Deserializer().extractData(
                portfolioService.getStockAvialableAmount(selectedStock.getId()).getData(),
                Integer.TYPE
        );

        if (qty > available) {
            AlertUtil.warning("Недостаточно акций", "Запрошенное количество превышает доступное.");
            return;
        }

        if (qty * selectedStock.getPrice() > currentBalance) {
            AlertUtil.warning("Недостаточно денег", "У вас недостаточно денег!");
            return;
        }

        Response response = portfolioService.buyStock(selectedStock, qty);
        if (response.isSuccess()) {
            AlertUtil.info("Покупка", response.getMessage());
            loadPortfolioData();
            portfolioService.setAccount(ServerClient.getCurrentUser().getId(), currentBalance - qty * selectedStock.getPrice());
        } else {
            AlertUtil.error("Ошибка покупки", response.getMessage());
        }
        buyTextbox.clear();
        allStocksTable.refresh();
        userStocksTable.refresh();
        loadPortfolioData();
    }

    @FXML
    public void onSellButton(ActionEvent event) {
        Map<String, Object> selectedRow = userStocksTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            AlertUtil.warning("Выбор акции", "Выберите акцию для продажи.");
            return;
        }

        String qtyText = sellTextbox.getText();
        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertUtil.warning("Неверное количество", "Введите положительное число.");
            return;
        }

        String ticket = (String) selectedRow.get("ticket");
        Stock selectedStock = userStockByIdMap.values().stream()
                .filter(s -> s.getTicket().equals(ticket))
                .findFirst().orElse(null);

        if (selectedStock == null) {
            AlertUtil.error("Ошибка", "Не удалось найти данные акции.");
            return;
        }

        if (qty > selectedStock.getAmount()) {
            AlertUtil.warning("Недостаточно акций", "Вы пытаетесь продать больше, чем у вас есть.");
            return;
        }

        Response response = portfolioService.sellStock(selectedStock, qty);
        if (response.isSuccess()) {
            AlertUtil.info("Продажа", response.getMessage());
            portfolioService.setAccount(ServerClient.getCurrentUser().getId(), currentBalance + qty * selectedStock.getPrice());
        } else {
            AlertUtil.error("Ошибка продажи", response.getMessage());
        }
        sellTextbox.clear();
        allStocksTable.refresh();
        userStocksTable.refresh();
        loadPortfolioData();
    }

    @FXML
    public void onBackToMenuButton(ActionEvent event) {
        Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.USER_MENU);
    }

    @FXML
    public void onExportToExcelButton(ActionEvent event) {
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в Excel", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Excel-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        fileChooser.setInitialFileName("user_stocks.xlsx");

        File file = fileChooser.showSaveDialog(exportToExcelButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            new ExcelExporter().export(new ArrayList<>(items), out, "Акции пользователя " + ServerClient.getCurrentUser().getUsername());
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить Excel-файл:\n" + e.getMessage());
        }
    }

    @FXML
    public void onExportToMarkdownButton(ActionEvent event) {
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в Markdown", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Markdown-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown файлы", "*.md"));
        fileChooser.setInitialFileName("user_stocks.md");

        File file = fileChooser.showSaveDialog(exportToMarkdownButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            new MarkdownExporter().export(new ArrayList<>(items), out, "Акции пользователя " + ServerClient.getCurrentUser().getUsername());
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить Markdown-файл:\n" + e.getMessage());
        }
    }

    @FXML
    public void onExportToPDFButton(ActionEvent event) {
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в PDF", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить PDF-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF файлы", "*.pdf"));
        fileChooser.setInitialFileName("user_stocks.pdf");

        File file = fileChooser.showSaveDialog(exportToPDFButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            new PdfExporter().export(new ArrayList<>(items), out, "Акции пользователя " + ServerClient.getCurrentUser().getUsername());
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить PDF-файл:\n" + e.getMessage());
        }
    }

    @FXML
    public void onExportToJSONButton(ActionEvent event) {
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в JSON", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить JSON-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON файлы", "*.json"));
        fileChooser.setInitialFileName("user_stocks.json");

        File file = fileChooser.showSaveDialog(exportToJSONButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            String title = "Акции пользователя " + ServerClient.getCurrentUser().getUsername();
            new JsonExporter().export(new ArrayList<>(items), out, title);
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить JSON-файл:\n" + e.getMessage());
        }
    }

    @FXML
    public void onExportToHTMLButton(ActionEvent event) {
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            AlertUtil.warning("Экспорт в HTML", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить HTML-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML файлы", "*.html"));
        fileChooser.setInitialFileName("user_stocks.html");

        File file = fileChooser.showSaveDialog(exportToHTMLButton.getScene().getWindow());
        if (file == null) return;

        try (OutputStream out = new FileOutputStream(file)) {
            new HtmlExporter().export(new ArrayList<>(items), out, "Акции пользователя " + ServerClient.getCurrentUser().getUsername());
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить HTML-файл:\n" + e.getMessage());
        }
    }
}
