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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

/**
 * Контроллер для управления портфелем пользователя в графическом интерфейсе.
 * Отвечает за отображение и управление акциями пользователя, а также за экспорт данных.
 */
@SuppressWarnings({"rawtypes", "unused"})
public class PortfolioController implements Initializable {
    private static final Logger logger = LogManager.getLogger(PortfolioController.class);

    @FXML
    private Button exportToExcelButton;

    @FXML
    private Button exportToMarkdownButton;

    @FXML
    private Button exportToPDFButton;

    @FXML
    private Button exportToJSONButton;

    @FXML
    private Button exportToHTMLButton;

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

    private final PortfolioService portfolioService;
    private final StockService stockService;
    private Double currentBalance;
    private final Map<Integer, Stock> stockByIdMap = new HashMap<>();
    private final Map<Integer, Stock> userStockByIdMap = new HashMap<>();

    /**
     * Конструктор по умолчанию.
     */
    public PortfolioController() {
        this.portfolioService = new PortfolioService();
        this.stockService = new StockService();
        this.currentBalance = 0.0;
        logger.info("Инициализирован PortfolioController");
    }

    /**
     * Инициализирует контроллер после загрузки FXML.
     *
     * @param url            расположение FXML-файла
     * @param resourceBundle ресурсы для локализации
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableColumns();
        loadPortfolioData();
        logger.info("Инициализация PortfolioController завершена");
    }

    /**
     * Настраивает столбцы таблиц для отображения данных.
     */
    private void configureTableColumns() {
        userStockTicketColumn.setCellValueFactory(new MapValueFactory<>("ticket"));
        userStockPriceColumn.setCellValueFactory(new MapValueFactory<>("price"));
        userStockAmountColumn.setCellValueFactory(new MapValueFactory<>("amount"));

        allStockTicketColumn.setCellValueFactory(new MapValueFactory<>("ticket"));
        allStockPriceColumn.setCellValueFactory(new MapValueFactory<>("price"));
        allStockAmountColumn.setCellValueFactory(new MapValueFactory<>("amount"));
        allStockAviableAmountColumn.setCellValueFactory(new MapValueFactory<>("availableAmount"));
        logger.info("Настроены столбцы таблиц для отображения данных");
    }

    /**
     * Загружает данные портфеля пользователя, включая баланс и акции.
     */
    private void loadPortfolioData() {
        try {
            loadBalance();
            loadUserStocks();
            loadAllStocks();
        } catch (Exception e) {
            logger.error("Ошибка загрузки данных портфеля: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка загрузки данных", "Не удалось загрузить данные портфеля: " + e.getMessage());
        }
    }

    /**
     * Загружает текущий баланс пользователя.
     */
    private void loadBalance() {
        Response balanceResponse = portfolioService.getAccount();
        if (!balanceResponse.isSuccess()) {
            logger.warn("Ошибка загрузки баланса: {}", balanceResponse.getMessage());
            throw new IllegalStateException("Ошибка загрузки баланса: " + balanceResponse.getMessage());
        }

        try {
            currentBalance = Double.parseDouble(balanceResponse.getData());
            balanceLabel.setText(String.format("Ваш баланс: %.2f$", currentBalance));
            logger.info("Баланс пользователя успешно загружен: {}", currentBalance);
        } catch (NumberFormatException e) {
            logger.error("Некорректный формат баланса: {}", balanceResponse.getData(), e);
            throw new IllegalStateException("Некорректный формат баланса");
        }
    }

    /**
     * Загружает акции пользователя.
     */
    private void loadUserStocks() {
        Response userStocksResponse = portfolioService.getUserStocks();
        if (!userStocksResponse.isSuccess()) {
            logger.warn("Ошибка загрузки пользовательских акций: {}", userStocksResponse.getMessage());
            throw new IllegalStateException("Ошибка загрузки пользовательских акций: " + userStocksResponse.getMessage());
        }

        try {
            Type listType = new TypeToken<List<Pair<Stock, Integer>>>() {}.getType();
            List<Pair<Stock, Integer>> userStocks = new Deserializer().extractData(userStocksResponse.getData(), listType);
            ObservableList<Map<String, Object>> userStockRows = FXCollections.observableArrayList();
            userStockByIdMap.clear();

            for (Pair<Stock, Integer> stock : userStocks) {
                if (stock.getKey() == null || stock.getValue() == null) {
                    logger.warn("Получены некорректные данные акции пользователя");
                    continue;
                }
                Map<String, Object> row = new HashMap<>();
                row.put("ticket", stock.getKey().getTicket());
                row.put("price", String.valueOf(stock.getKey().getPrice()));
                row.put("amount", stock.getValue());
                userStockRows.add(row);
                userStockByIdMap.put(stock.getKey().getId(), stock.getKey());
            }
            userStocksTable.setItems(userStockRows);
            logger.info("Загружено {} пользовательских акций", userStockRows.size());
        } catch (Exception e) {
            logger.error("Ошибка обработки пользовательских акций: {}", e.getMessage(), e);
            throw new IllegalStateException("Ошибка обработки пользовательских акций");
        }
    }

    /**
     * Загружает все доступные акции.
     */
    private void loadAllStocks() {
        Response allStocksResponse = stockService.getAll();
        Response noCompanyStocksResponse = stockService.getAllStocksWithNoCompany();
        if (!allStocksResponse.isSuccess()) {
            logger.warn("Ошибка загрузки всех акций: {}", allStocksResponse.getMessage());
            throw new IllegalStateException("Ошибка загрузки всех акций: " + allStocksResponse.getMessage());
        }

        try {
            List<Stock> allStocks = new Deserializer().extractListData(allStocksResponse.getData(), Stock.class);
            List<Stock> allNoCompanyStocks = new Deserializer().extractListData(noCompanyStocksResponse.getData(), Stock.class);
            ObservableList<Map<String, Object>> allStockRows = FXCollections.observableArrayList();
            stockByIdMap.clear();

            for (Stock stock : allStocks) {
                if (allNoCompanyStocks.contains(stock)) {
                    continue;
                }
                Response availableResponse = portfolioService.getStockAvialableAmount(stock.getId());
                if (!availableResponse.isSuccess()) {
                    logger.warn("Ошибка получения доступного количества для акции ID {}: {}", stock.getId(), availableResponse.getMessage());
                    continue;
                }
                int available = new Deserializer().extractData(availableResponse.getData(), Integer.TYPE);
                Map<String, Object> row = new HashMap<>();
                row.put("ticket", stock.getTicket());
                row.put("price", String.valueOf(stock.getPrice()));
                row.put("amount", stock.getAmount());
                row.put("availableAmount", available);
                allStockRows.add(row);
                stockByIdMap.put(stock.getId(), stock);
            }
            allStocksTable.setItems(allStockRows);
            logger.info("Загружено {} доступных акций", allStockRows.size());
        } catch (Exception e) {
            logger.error("Ошибка обработки всех акций: {}", e.getMessage(), e);
            throw new IllegalStateException("Ошибка обработки всех акций");
        }
    }

    /**
     * Обрабатывает покупку акций.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onBuyButton(ActionEvent event) {
        try {
            Map<String, Object> selectedRow = allStocksTable.getSelectionModel().getSelectedItem();
            if (selectedRow == null) {
                logger.warn("Попытка покупки без выбранной акции");
                AlertUtil.warning("Выбор акции", "Выберите акцию для покупки.");
                return;
            }

            int qty = parseQuantity(buyTextbox.getText());
            String ticket = (String) selectedRow.get("ticket");
            Stock selectedStock = stockByIdMap.values().stream()
                    .filter(s -> s.getTicket().equals(ticket))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Акция не найдена"));

            int available = new Deserializer().extractData(
                    portfolioService.getStockAvialableAmount(selectedStock.getId()).getData(),
                    Integer.TYPE
            );

            if (qty > available) {
                logger.warn("Запрошенное количество {} превышает доступное {} для акции {}", qty, available, ticket);
                AlertUtil.warning("Недостаточно акций", "Запрошенное количество превышает доступное.");
                return;
            }

            if (qty * selectedStock.getPrice() > currentBalance) {
                logger.warn("Недостаточно средств для покупки {} акций {} на сумму {}", qty, ticket, qty * selectedStock.getPrice());
                AlertUtil.warning("Недостаточно средств", "Недостаточно средств на счете.");
                return;
            }

            Response response = portfolioService.buyStock(selectedStock, qty);
            if (response.isSuccess()) {
                currentBalance -= qty * selectedStock.getPrice();
                portfolioService.setAccount(ServerClient.getCurrentUser().getId(), currentBalance);
                AlertUtil.info("Покупка", response.getMessage());
                logger.info("Успешная покупка {} акций {}", qty, ticket);
            } else {
                logger.error("Ошибка покупки акции {}: {}", ticket, response.getMessage());
                AlertUtil.error("Ошибка покупки", response.getMessage());
            }

            buyTextbox.clear();
            refreshTables();
            loadPortfolioData();
        } catch (IllegalStateException e) {
            logger.error("Ошибка покупки акции: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка покупки", e.getMessage());
        }
    }

    /**
     * Обрабатывает продажу акций.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onSellButton(ActionEvent event) {
        try {
            Map<String, Object> selectedRow = userStocksTable.getSelectionModel().getSelectedItem();
            if (selectedRow == null) {
                logger.warn("Попытка продажи без выбранной акции");
                AlertUtil.warning("Выбор акции", "Выберите акцию для продажи.");
                return;
            }

            int qty = parseQuantity(sellTextbox.getText());
            String ticket = (String) selectedRow.get("ticket");
            Stock selectedStock = userStockByIdMap.values().stream()
                    .filter(s -> s.getTicket().equals(ticket))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Акция не найдена"));

            if (qty > selectedStock.getAmount()) {
                logger.warn("Запрошенное количество {} превышает имеющееся {} для акции {}", qty, selectedStock.getAmount(), ticket);
                AlertUtil.warning("Недостаточно акций", "Вы пытаетесь продать больше, чем у вас есть.");
                return;
            }

            Response response = portfolioService.sellStock(selectedStock, qty);
            if (response.isSuccess()) {
                currentBalance += qty * selectedStock.getPrice();
                portfolioService.setAccount(ServerClient.getCurrentUser().getId(), currentBalance);
                AlertUtil.info("Продажа", response.getMessage());
                logger.info("Успешная продажа {} акций {}", qty, ticket);
            } else {
                logger.error("Ошибка продажи акции {}: {}", ticket, response.getMessage());
                AlertUtil.error("Ошибка продажи", response.getMessage());
            }

            sellTextbox.clear();
            refreshTables();
            loadPortfolioData();
        } catch (IllegalStateException e) {
            logger.error("Ошибка продажи акции: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка продажи", e.getMessage());
        }
    }

    /**
     * Возвращает пользователя в главное меню.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onBackToMenuButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.USER_MENU);
            logger.info("Переход в главное меню");
        } catch (Exception e) {
            logger.error("Ошибка перехода в главное меню: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка перехода", "Не удалось вернуться в главное меню: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные портфеля в Excel.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToExcelButton(ActionEvent event) {
        logger.info("Начало экспорта данных портфеля в Excel");
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в Excel без данных");
            AlertUtil.warning("Экспорт в Excel", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Excel-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        fileChooser.setInitialFileName("user_stocks.xlsx");

        File file = fileChooser.showSaveDialog(exportToExcelButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в Excel отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            new ExcelExporter().export(new ArrayList<>(items), out, "Акции пользователя " + ServerClient.getCurrentUser().getUsername());
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в Excel: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в Excel: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить Excel-файл: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные портфеля в Markdown.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToMarkdownButton(ActionEvent event) {
        logger.info("Начало экспорта данных портфеля в Markdown");
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в Markdown без данных");
            AlertUtil.warning("Экспорт в Markdown", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Markdown-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown файлы", "*.md"));
        fileChooser.setInitialFileName("user_stocks.md");

        File file = fileChooser.showSaveDialog(exportToMarkdownButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в Markdown отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            new MarkdownExporter().export(new ArrayList<>(items), out, "Акции пользователя " + ServerClient.getCurrentUser().getUsername());
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в Markdown: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в Markdown: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить Markdown-файл: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные портфеля в PDF.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToPDFButton(ActionEvent event) {
        logger.info("Начало экспорта данных портфеля в PDF");
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в PDF без данных");
            AlertUtil.warning("Экспорт в PDF", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить PDF-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF файлы", "*.pdf"));
        fileChooser.setInitialFileName("user_stocks.pdf");

        File file = fileChooser.showSaveDialog(exportToPDFButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в PDF отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            new PdfExporter().export(new ArrayList<>(items), out, "Акции пользователя " + ServerClient.getCurrentUser().getUsername());
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в PDF: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в PDF: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить PDF-файл: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные портфеля в JSON.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToJSONButton(ActionEvent event) {
        logger.info("Начало экспорта данных портфеля в JSON");
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в JSON без данных");
            AlertUtil.warning("Экспорт в JSON", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить JSON-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON файлы", "*.json"));
        fileChooser.setInitialFileName("user_stocks.json");

        File file = fileChooser.showSaveDialog(exportToJSONButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в JSON отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            String title = "Акции пользователя " + ServerClient.getCurrentUser().getUsername();
            new JsonExporter().export(new ArrayList<>(items), out, title);
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в JSON: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в JSON: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить JSON-файл: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные портфеля в HTML.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToHTMLButton(ActionEvent event) {
        logger.info("Начало экспорта данных портфеля в HTML");
        ObservableList<Map<String, Object>> items = userStocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в HTML без данных");
            AlertUtil.warning("Экспорт в HTML", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить HTML-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML файлы", "*.html"));
        fileChooser.setInitialFileName("user_stocks.html");

        File file = fileChooser.showSaveDialog(exportToHTMLButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в HTML отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            new HtmlExporter().export(new ArrayList<>(items), out, "Акции пользователя " + ServerClient.getCurrentUser().getUsername());
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в HTML: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в HTML: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить HTML-файл: " + e.getMessage());
        }
    }

    /**
     * Парсит количество из текстового поля.
     *
     * @param qtyText текст с количеством
     * @return количество акций
     * @throws IllegalArgumentException если количество некорректно
     */
    private int parseQuantity(String qtyText) {
        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                throw new IllegalArgumentException("Количество должно быть положительным");
            }
            return qty;
        } catch (NumberFormatException e) {
            logger.warn("Некорректное количество: {}", qtyText);
            throw new IllegalArgumentException("Введите положительное число");
        }
    }

    /**
     * Обновляет таблицы с данными.
     */
    private void refreshTables() {
        allStocksTable.refresh();
        userStocksTable.refresh();
        logger.info("Таблицы акций обновлены");
    }
}
