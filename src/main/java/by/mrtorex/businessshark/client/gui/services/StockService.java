package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.exceptions.ResponseException;
import by.mrtorex.businessshark.server.model.entities.Stock;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Serializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Сервис для взаимодействия с сервером по операциям с акциями.
 */
@SuppressWarnings("unused")
public class StockService {
    private static final Logger logger = LogManager.getLogger(StockService.class);
    private final ServerClient serverClient;

    /**
     * Конструктор по умолчанию.
     */
    public StockService() {
        this.serverClient = ServerClient.getInstance();
        logger.info("Инициализирован StockService с клиентом сервера");
    }

    /**
     * Получает список всех акций.
     *
     * @return ответ сервера со списком акций
     */
    public Response getAll() {
        try {
            Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_STOCKS));
            logger.info("Запрошен список всех акций");
            return response;
        } catch (ResponseException e) {
            logger.error("Ошибка при получении списка акций: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Удаляет акцию по её идентификатору.
     *
     * @param stockToDelete акция для удаления
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если акция или её ID равны null
     */
    public Response delStock(Stock stockToDelete) {
        try {
            if (stockToDelete == null || stockToDelete.getId() == null) {
                logger.warn("Попытка удаления акции с null параметрами");
                throw new IllegalArgumentException("Акция или её ID не могут быть null");
            }
            String stockIdJson = Serializer.toJson(stockToDelete.getId());
            Response response = serverClient.sendRequest(new Request(Operation.DELETE_STOCK, stockIdJson));
            logger.info("Удалена акция с ID: {}", stockToDelete.getId());
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для удаления акции", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка удаления акции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Обновляет данные акции.
     *
     * @param stockToUpdate акция с обновлёнными данными
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если акция равна null
     */
    public Response updateStock(Stock stockToUpdate) {
        try {
            if (stockToUpdate == null) {
                logger.warn("Попытка обновления акции с null параметром");
                throw new IllegalArgumentException("Акция не может быть null");
            }
            String stockJson = Serializer.toJson(stockToUpdate);
            Response response = serverClient.sendRequest(new Request(Operation.UPDATE_STOCK, stockJson));
            logger.info("Обновлены данные акции с ID: {}", stockToUpdate.getId());
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для обновления акции", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка обновления акции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Добавляет новую акцию.
     *
     * @param stock новая акция
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если акция равна null
     */
    public Response addStock(Stock stock) {
        try {
            if (stock == null) {
                logger.warn("Попытка создания акции с null параметром");
                throw new IllegalArgumentException("Акция не может быть null");
            }
            String stockJson = Serializer.toJson(stock);
            Response response = serverClient.sendRequest(new Request(Operation.CREATE_STOCK, stockJson));
            logger.info("Создана новая акция: {}", stock.getTicket());
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для создания акции", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка создания акции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Получает список акций, не привязанных к компании.
     *
     * @return ответ сервера со списком акций
     */
    public Response getAllStocksWithNoCompany() {
        try {
            Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_STOCKS_WITH_NO_COMPANY));
            logger.info("Запрошен список акций без привязки к компании");
            return response;
        } catch (ResponseException e) {
            logger.error("Ошибка при получении списка акций без компании: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}