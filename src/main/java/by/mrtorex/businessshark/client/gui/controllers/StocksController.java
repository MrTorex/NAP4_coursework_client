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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

/**
 * Контроллер для управления акциями в графическом интерфейсе администратора.
 * Отвечает за добавление, удаление, обновление и экспорт акций.
 */
@SuppressWarnings({"unused", "SameParameterValue", "LoggingSimilarMessage", "DuplicatedCode"})
public class StocksController implements Initializable {
    private static final Logger logger = LogManager.getLogger(StocksController.class);

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
    private Button exportToExcelButton;

    @FXML
    private Button exportToMarkdownButton;

    @FXML
    private Button exportToPDFButton;

    @FXML
    private Button exportToJSONButton;

    @FXML
    private Button exportToHTMLButton;

    private final StockService stockService;

    /**
     * Конструктор по умолчанию.
     */
    public StocksController() {
        this.stockService = new StockService();
        logger.info("Инициализирован StocksController");
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
        loadStocksTable();
        setupTableSelectionObserver();
        logger.info("Инициализация StocksController завершена");
    }

    /**
     * Настраивает столбцы таблицы для отображения данных акций.
     */
    private void configureTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ticketColumn.setCellValueFactory(new PropertyValueFactory<>("ticket"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        logger.info("Настроены столбцы таблицы для отображения акций");
    }

    /**
     * Обрабатывает добавление новой акции.
     *
     * @param event событие нажатия кнопки
     */
    public void onAddButton(ActionEvent event) {
        try {
            String ticket = ticketField.getText();
            if (ticket == null || ticket.isEmpty()) {
                logger.warn("Попытка добавления акции с пустым тикером");
                AlertUtil.error("Ошибка добавления", "Тикер акции не может быть пустым.");
                return;
            }

            Double price = parseDoubleField(priceField.getText(), "цена");
            Integer amount = parseIntegerField(amountField.getText(), "количество");

            Stock newStock = new Stock();
            newStock.setTicket(ticket);
            newStock.setPrice(price);
            newStock.setAmount(amount);

            String stockJson = Serializer.toJson(newStock);
            Request request = new Request(Operation.CREATE_STOCK, stockJson);
            Response response = ServerClient.getInstance().sendRequest(request);

            if (response == null) {
                logger.error("Получен null-ответ от сервера при добавлении акции");
                AlertUtil.error("Ошибка добавления", "Сервер вернул некорректный ответ.");
                return;
            }

            if (response.isSuccess()) {
                AlertUtil.info("Добавление акции", "Акция успешно добавлена!");
                logger.info("Успешно добавлена акция с тикером: {}", ticket);
                loadStocksTable();
            } else {
                logger.error("Ошибка добавления акции: {}", response.getMessage());
                AlertUtil.error("Ошибка добавления", response.getMessage());
            }
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка ввода данных для добавления акции: {}", e.getMessage());
            AlertUtil.error("Ошибка добавления", e.getMessage());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при добавлении акции: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка добавления", "Произошла ошибка при добавлении акции: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает удаление выбранной акции.
     *
     * @param event событие нажатия кнопки
     */
    public void onDeleteButton(ActionEvent event) {
        try {
            Stock selectedStock = getSelectedStock();
            if (selectedStock == null) {
                logger.warn("Попытка удаления без выбранной акции");
                return;
            }

            Response response = stockService.delStock(selectedStock);
            if (response == null) {
                logger.error("Получен null-ответ от сервера при удалении акции ID: {}", selectedStock.getId());
                AlertUtil.error("Ошибка удаления", "Сервер вернул некорректный ответ.");
                return;
            }

            if (response.isSuccess()) {
                AlertUtil.info("Удаление акции", response.getMessage());
                logger.info("Успешно удалена акция ID: {}", selectedStock.getId());
                loadStocksTable();
            } else {
                logger.error("Ошибка удаления акции ID {}: {}", selectedStock.getId(), response.getMessage());
                AlertUtil.error("Ошибка удаления", response.getMessage());
            }
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при удалении акции: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка удаления", "Произошла ошибка при удалении акции: " + e.getMessage());
        }
    }

    /**
     * Возвращает администратора в главное меню.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onBackToMenuButton(ActionEvent event) {
        try {
            Loader.loadScene((Stage) backToMenuButton.getScene().getWindow(), ScenePath.ADMIN_MENU);
            logger.info("Переход в главное меню администратора");
        } catch (Exception e) {
            logger.error("Ошибка перехода в главное меню: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка перехода", "Не удалось вернуться в главное меню: " + e.getMessage());
        }
    }

    /**
     * Очищает поля ввода.
     *
     * @param actionEvent событие нажатия кнопки
     */
    public void onEraseButton(ActionEvent actionEvent) {
        eraseFields();
        logger.info("Поля ввода очищены");
    }

    /**
     * Обрабатывает обновление данных выбранной акции.
     *
     * @param event событие нажатия кнопки
     */
    public void onUpdateButton(ActionEvent event) {
        try {
            Stock selectedStock = getSelectedStock();
            if (selectedStock == null) {
                logger.warn("Попытка обновления без выбранной акции");
                return;
            }

            String ticket = ticketField.getText();
            if (ticket == null || ticket.isEmpty()) {
                logger.warn("Попытка обновления акции с пустым тикером");
                AlertUtil.error("Ошибка обновления", "Тикер акции не может быть пустым.");
                return;
            }

            Double price = parseDoubleField(priceField.getText(), "цена");
            Integer amount = parseIntegerField(amountField.getText(), "количество");

            selectedStock.setTicket(ticket);
            selectedStock.setPrice(price);
            selectedStock.setAmount(amount);

            Response response = stockService.updateStock(selectedStock);
            if (response == null) {
                logger.error("Получен null-ответ от сервера при обновлении акции ID: {}", selectedStock.getId());
                AlertUtil.error("Ошибка обновления", "Сервер вернул некорректный ответ.");
                return;
            }

            if (response.isSuccess()) {
                AlertUtil.info("Обновление акции", response.getMessage());
                logger.info("Успешно обновлена акция ID: {}", selectedStock.getId());
                loadStocksTable();
            } else {
                logger.error("Ошибка обновления акции ID {}: {}", selectedStock.getId(), response.getMessage());
                AlertUtil.error("Ошибка обновления", response.getMessage());
            }
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка ввода данных для обновления акции: {}", e.getMessage());
            AlertUtil.error("Ошибка обновления", e.getMessage());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при обновлении акции: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка обновления", "Произошла ошибка при обновлении акции: " + e.getMessage());
        }
    }

    /**
     * Загружает данные акций в таблицу.
     */
    private void loadStocksTable() {
        try {
            stocksTable.getItems().clear();
            Response response = stockService.getAll();
            if (response == null) {
                logger.error("Получен null-ответ от сервера при загрузке списка акций");
                AlertUtil.error("Ошибка загрузки", "Сервер вернул некорректный ответ.");
                return;
            }

            if (!response.isSuccess()) {
                logger.error("Ошибка загрузки списка акций: {}", response.getMessage());
                AlertUtil.error("Ошибка загрузки", response.getMessage());
                return;
            }

            List<Stock> stocks = new Deserializer().extractListData(response.getData(), Stock.class);
            stocksTable.setItems(FXCollections.observableArrayList(stocks));
            logger.info("Загружено {} акций в таблицу", stocks.size());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при загрузке списка акций: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка загрузки", "Произошла ошибка при загрузке акций: " + e.getMessage());
        }
    }

    /**
     * Настраивает наблюдатель за выбором строк в таблице.
     */
    private void setupTableSelectionObserver() {
        stocksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFields();
                logger.info("Выбрана акция ID: {}", newValue.getId());
            } else {
                eraseFields();
                logger.info("Сброшена выбранная акция");
            }
        });
    }

    /**
     * Получает выбранную акцию из таблицы.
     *
     * @return выбранная акция или null, если ничего не выбрано
     */
    private Stock getSelectedStock() {
        Stock selectedStock = stocksTable.getSelectionModel().getSelectedItem();
        if (selectedStock == null) {
            logger.warn("Попытка действия без выбора акции");
            AlertUtil.warning("Выбор акции", "Сначала выберите акцию!");
        }
        return selectedStock;
    }

    /**
     * Заполняет поля ввода данными выбранной акции.
     */
    private void fillFields() {
        Stock selectedStock = getSelectedStock();
        if (selectedStock == null) {
            logger.warn("Попытка заполнения полей без выбранной акции");
            return;
        }

        ticketField.setText(selectedStock.getTicket());
        priceField.setText(selectedStock.getPrice().toString());
        amountField.setText(selectedStock.getAmount().toString());
        logger.info("Поля заполнены данными акции ID: {}", selectedStock.getId());
    }

    /**
     * Очищает поля ввода.
     */
    private void eraseFields() {
        ticketField.setText("");
        priceField.setText("");
        amountField.setText("");
        logger.info("Поля ввода очищены");
    }

    /**
     * Преобразует список акций в список словарей для экспорта.
     *
     * @param stocks список акций
     * @return список словарей с данными акций
     */
    private List<Map<String, Object>> convertStocksToMapList(List<Stock> stocks) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Stock stock : stocks) {
            if (stock == null) {
                logger.warn("Обнаружена null-акция при конвертации для экспорта");
                continue;
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("ID", stock.getId());
            map.put("Ticket", stock.getTicket());
            map.put("Price", stock.getPrice());
            map.put("Amount", stock.getAmount());
            result.add(map);
        }
        logger.info("Конвертировано {} акций для экспорта", result.size());
        return result;
    }

    /**
     * Экспортирует данные акций в Excel.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToExcelButton(ActionEvent event) {
        logger.info("Начало экспорта данных акций в Excel");
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в Excel без данных");
            AlertUtil.warning("Экспорт в Excel", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Excel-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx"));
        fileChooser.setInitialFileName("stocks.xlsx");

        File file = fileChooser.showSaveDialog(exportToExcelButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в Excel отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new ExcelExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в Excel: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в Excel: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить Excel-файл: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные акций в Markdown.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToMarkdownButton(ActionEvent event) {
        logger.info("Начало экспорта данных акций в Markdown");
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в Markdown без данных");
            AlertUtil.warning("Экспорт в Markdown", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить Markdown-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown файлы", "*.md"));
        fileChooser.setInitialFileName("stocks.md");

        File file = fileChooser.showSaveDialog(exportToMarkdownButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в Markdown отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new MarkdownExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в Markdown: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в Markdown: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить Markdown-файл: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные акций в PDF.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToPDFButton(ActionEvent event) {
        logger.info("Начало экспорта данных акций в PDF");
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в PDF без данных");
            AlertUtil.warning("Экспорт в PDF", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить PDF-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF файлы", "*.pdf"));
        fileChooser.setInitialFileName("stocks.pdf");

        File file = fileChooser.showSaveDialog(exportToPDFButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в PDF отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new PdfExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в PDF: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в PDF: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить PDF-файл: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные акций в JSON.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToJSONButton(ActionEvent event) {
        logger.info("Начало экспорта данных акций в JSON");
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в JSON без данных");
            AlertUtil.warning("Экспорт в JSON", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить JSON-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON файлы", "*.json"));
        fileChooser.setInitialFileName("stocks.json");

        File file = fileChooser.showSaveDialog(exportToJSONButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в JSON отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new JsonExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в JSON: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в JSON: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить JSON-файл: " + e.getMessage());
        }
    }

    /**
     * Экспортирует данные акций в HTML.
     *
     * @param event событие нажатия кнопки
     */
    @FXML
    public void onExportToHTMLButton(ActionEvent event) {
        logger.info("Начало экспорта данных акций в HTML");
        ObservableList<Stock> items = stocksTable.getItems();

        if (items == null || items.isEmpty()) {
            logger.warn("Попытка экспорта в HTML без данных");
            AlertUtil.warning("Экспорт в HTML", "Нет данных для экспорта.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить HTML-файл");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML файлы", "*.html"));
        fileChooser.setInitialFileName("stocks.html");

        File file = fileChooser.showSaveDialog(exportToHTMLButton.getScene().getWindow());
        if (file == null) {
            logger.info("Экспорт в HTML отменен пользователем");
            return;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            List<Map<String, Object>> data = convertStocksToMapList(items);
            new HtmlExporter().export(data, out, "Список всех акций");
            AlertUtil.info("Экспорт завершён", "Файл успешно сохранён:\n" + file.getAbsolutePath());
            logger.info("Успешный экспорт в HTML: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка экспорта в HTML: {}", e.getMessage(), e);
            AlertUtil.error("Ошибка экспорта", "Не удалось сохранить HTML-файл: " + e.getMessage());
        }
    }

    /**
     * Парсит значение типа Double из текстового поля.
     *
     * @param value строковое значение
     * @param fieldName название поля для сообщения об ошибке
     * @return значение типа Double
     * @throws IllegalArgumentException если значение некорректно
     */
    private Double parseDoubleField(String value, String fieldName) {
        try {
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException(fieldName + " не может быть пустым.");
            }
            double result = Double.parseDouble(value);
            if (result <= 0) {
                throw new IllegalArgumentException(fieldName + " должно быть положительным.");
            }
            return result;
        } catch (NumberFormatException e) {
            logger.warn("Некорректный формат для {}: {}", fieldName, value);
            throw new IllegalArgumentException("Некорректный формат для " + fieldName);
        }
    }

    /**
     * Парсит значение типа Integer из текстового поля.
     *
     * @param value строковое значение
     * @param fieldName название поля для сообщения об ошибке
     * @return значение типа Integer
     * @throws IllegalArgumentException если значение некорректно
     */
    private Integer parseIntegerField(String value, String fieldName) {
        try {
            if (value == null || value.isEmpty()) {
                throw new IllegalArgumentException(fieldName + " не может быть пустым.");
            }
            int result = Integer.parseInt(value);
            if (result <= 0) {
                throw new IllegalArgumentException(fieldName + " должно быть положительным.");
            }
            return result;
        } catch (NumberFormatException e) {
            logger.warn("Некорректный формат для {}: {}", fieldName, value);
            throw new IllegalArgumentException("Некорректный формат для " + fieldName);
        }
    }
}