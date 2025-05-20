package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.exceptions.ResponseException;
import by.mrtorex.businessshark.server.model.entities.Stock;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Serializer;
import by.mrtorex.businessshark.server.utils.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Сервис для управления портфелем пользователя, включая операции с акциями и балансом.
 */
@SuppressWarnings("unused")
public class PortfolioService {
    private static final Logger logger = LogManager.getLogger(PortfolioService.class);
    private final int currentUserId;
    private final ServerClient serverClient;

    /**
     * Конструктор сервиса портфеля.
     *
     * @throws IllegalStateException если текущий пользователь не инициализирован
     */
    public PortfolioService() {
        this.serverClient = ServerClient.getInstance();
        if (ServerClient.getCurrentUser() == null || ServerClient.getCurrentUser().getId() == null) {
            logger.error("Текущий пользователь не инициализирован");
            throw new IllegalStateException("Текущий пользователь не инициализирован");
        }
        this.currentUserId = ServerClient.getCurrentUser().getId();
        logger.info("Инициализирован PortfolioService для пользователя с ID: {}", currentUserId);
    }

    /**
     * Получает баланс текущего пользователя.
     *
     * @return ответ сервера с данными о балансе
     */
    public Response getAccount() {
        try {
            String userIdJson = Serializer.toJson(currentUserId);
            Response response = serverClient.sendRequest(new Request(Operation.GET_USER_ACCOUNT, userIdJson));
            logger.info("Запрошен баланс для пользователя с ID: {}", currentUserId);
            return response;
        } catch (ResponseException e) {
            logger.error("Ошибка при получении баланса пользователя: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Устанавливает баланс для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param account сумма баланса
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если userId или account недопустимы
     */
    @SuppressWarnings("UnusedReturnValue")
    public Response setAccount(int userId, double account) {
        try {
            if (userId <= 0) {
                logger.warn("Попытка установки баланса с недопустимым ID пользователя: {}", userId);
                throw new IllegalArgumentException("ID пользователя должен быть положительным");
            }
            if (account < 0) {
                logger.warn("Попытка установки отрицательного баланса: {}", account);
                throw new IllegalArgumentException("Баланс не может быть отрицательным");
            }
            String dataJson = Serializer.toJson(new Pair<>(userId, account));
            Response response = serverClient.sendRequest(new Request(Operation.SET_USER_ACCOUNT, dataJson));
            logger.info("Установлен баланс {} для пользователя с ID: {}", account, userId);
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для установки баланса", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка при установке баланса: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Получает список всех акций текущего пользователя.
     *
     * @return ответ сервера со списком акций
     */
    public Response getUserStocks() {
        try {
            String userIdJson = Serializer.toJson(currentUserId);
            Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_USER_STOCKS, userIdJson));
            logger.info("Запрошен список акций для пользователя с ID: {}", currentUserId);
            return response;
        } catch (ResponseException e) {
            logger.error("Ошибка при получении списка акций пользователя: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Покупает указанное количество акций для текущего пользователя.
     *
     * @param stock акция для покупки
     * @param quantity количество акций
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если параметры недопустимы
     */
    public Response buyStock(Stock stock, int quantity) {
        try {
            if (stock == null || stock.getId() == null) {
                logger.warn("Попытка покупки акции с null параметрами");
                throw new IllegalArgumentException("Акция или её ID не могут быть null");
            }
            if (quantity <= 0) {
                logger.warn("Попытка покупки недопустимого количества акций: {}", quantity);
                throw new IllegalArgumentException("Количество акций должно быть положительным");
            }
            Pair<Pair<Stock, Integer>, Integer> data = new Pair<>(new Pair<>(stock, quantity), currentUserId);
            String dataJson = Serializer.toJson(data);
            Response response = serverClient.sendRequest(new Request(Operation.ADD_USER_STOCK, dataJson));
            logger.info("Куплено {} акций с ID: {} для пользователя с ID: {}", quantity, stock.getId(), currentUserId);
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для покупки акции", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка при покупке акции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Продаёт указанное количество акций текущего пользователя.
     *
     * @param stock акция для продажи
     * @param quantity количество акций
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если параметры недопустимы
     */
    public Response sellStock(Stock stock, int quantity) {
        try {
            if (stock == null || stock.getId() == null) {
                logger.warn("Попытка продажи акции с null параметрами");
                throw new IllegalArgumentException("Акция или её ID не могут быть null");
            }
            if (quantity <= 0) {
                logger.warn("Попытка продажи недопустимого количества акций: {}", quantity);
                throw new IllegalArgumentException("Количество акций должно быть положительным");
            }
            Pair<Pair<Stock, Integer>, Integer> data = new Pair<>(new Pair<>(stock, quantity), currentUserId);
            String dataJson = Serializer.toJson(data);
            Response response = serverClient.sendRequest(new Request(Operation.UPDATE_USER_STOCK, dataJson));
            logger.info("Продано {} акций с ID: {} для пользователя с ID: {}", quantity, stock.getId(), currentUserId);
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для продажи акции", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка при продаже акции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Получает данные об акции пользователя по её идентификатору.
     *
     * @param stockId идентификатор акции
     * @return ответ сервера с данными об акции
     * @throws IllegalArgumentException если stockId недопустим
     */
    public Response getUserStock(int stockId) {
        try {
            if (stockId <= 0) {
                logger.warn("Попытка получения акции с недопустимым ID: {}", stockId);
                throw new IllegalArgumentException("ID акции должен быть положительным");
            }
            Pair<Integer, Integer> data = new Pair<>(stockId, currentUserId);
            String dataJson = Serializer.toJson(data);
            Response response = serverClient.sendRequest(new Request(Operation.GET_USER_STOCK, dataJson));
            logger.info("Запрошены данные акции с ID: {} для пользователя с ID: {}", stockId, currentUserId);
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для получения акции", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка при получении акции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Удаляет акцию из портфеля пользователя.
     *
     * @param stockId идентификатор акции
     * @return ответ сервера с результатом операции
     * @throws IllegalArgumentException если stockId недопустим
     */
    public Response deleteUserStock(int stockId) {
        try {
            if (stockId <= 0) {
                logger.warn("Попытка удаления акции с недопустимым ID: {}", stockId);
                throw new IllegalArgumentException("ID акции должен быть положительным");
            }
            Object[] data = new Object[]{currentUserId, stockId};
            String dataJson = Serializer.toJson(data);
            Response response = serverClient.sendRequest(new Request(Operation.DELETE_USER_STOCK, dataJson));
            logger.info("Удалена акция с ID: {} из портфеля пользователя с ID: {}", stockId, currentUserId);
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для удаления акции", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка при удалении акции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Получает идентификаторы всех акций текущего пользователя.
     *
     * @return ответ сервера со списком идентификаторов акций
     */
    public Response getAllUserStockIds() {
        try {
            Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_USER_STOCK_IDS));
            logger.info("Запрошен список ID акций для пользователя с ID: {}", currentUserId);
            return response;
        } catch (ResponseException e) {
            logger.error("Ошибка при получении списка ID акций: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }

    /**
     * Получает доступное количество акций по её идентификатору.
     *
     * @param stockId идентификатор акции
     * @return ответ сервера с данными о доступном количестве
     * @throws IllegalArgumentException если stockId недопустим
     */
    public Response getStockAvialableAmount(int stockId) {
        try {
            if (stockId <= 0) {
                logger.warn("Попытка получения доступного количества акции с недопустимым ID: {}", stockId);
                throw new IllegalArgumentException("ID акции должен быть положительным");
            }
            String stockIdJson = Serializer.toJson(stockId);
            Response response = serverClient.sendRequest(new Request(Operation.GET_STOCK_AVAILABLE_AMOUNT, stockIdJson));
            logger.info("Запрошено доступное количество для акции с ID: {}", stockId);
            return response;
        } catch (IllegalArgumentException e) {
            logger.error("Некорректные данные для получения доступного количества акции", e);
            return new Response(false, e.getMessage(), null);
        } catch (ResponseException e) {
            logger.error("Ошибка при получении доступного количества акции: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}