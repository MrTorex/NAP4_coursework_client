package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Stock;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Serializer;
import by.mrtorex.businessshark.server.utils.Pair;

public class PortfolioService {

    private final int currentUserId;

    public PortfolioService() {
        this.currentUserId = ServerClient.getCurrentUser().getId();
    }

    // Получить баланс пользователя (предполагается сервер возвращает строку с числом)
    public Response getAccount() {
        // Обычно баланс - это часть профиля, но если отдельный запрос нужен — можно сделать
        // Здесь просто возвращаем баланс с сервера (например, через GET_USER с userId)
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.GET_USER_ACCOUNT, Serializer.toJson(currentUserId))
        );
    }

    public Response setAccount(int userId, double account) {
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.SET_USER_ACCOUNT, Serializer.toJson(new Pair<>(userId, account)))
        );
    }

    // Получить все акции пользователя
    public Response getUserStocks() {
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.GET_ALL_USER_STOCKS, Serializer.toJson(currentUserId))
        );
    }

    // Купить акции (Stock + количество)
    public Response buyStock(Stock stock, int quantity) {
        // Пакуем данные как Object[]{ Pair<Stock, Integer> (stock+qty), Integer userId }
        Pair<Pair<Stock, Integer>, Integer>  data = new Pair<>(
                new Pair<>(stock, quantity),
                currentUserId
        );
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.ADD_USER_STOCK, Serializer.toJson(data))
        );
    }

    // Продать акции (Stock + количество)
    public Response sellStock(Stock stock, int quantity) {
        // Аналогично buyStock, но используем UPDATE_USER_STOCK
        Pair<Pair<Stock, Integer>, Integer> data = new Pair<>(
                new Pair<>(stock, quantity),
                currentUserId
        );
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.UPDATE_USER_STOCK, Serializer.toJson(data))
        );
    }

    // Получить конкретные акции пользователя (например, по ID акции)
    public Response getUserStock(int stockId) {
        Pair<Integer, Integer> data = new Pair<>(stockId, currentUserId);
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.GET_USER_STOCK, Serializer.toJson(data))
        );
    }

    // Удалить акции из портфеля (удалить связь user-stock)
    public Response deleteUserStock(int stockId) {
        Object[] data = new Object[]{currentUserId, stockId};
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.DELETE_USER_STOCK, Serializer.toJson(data))
        );
    }

    // Получить ID всех акций, которые есть у пользователя (если нужно)
    public Response getAllUserStockIds() {
        // Если нужно — можно сделать с передачей userId, иначе просто запрос без данных
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.GET_ALL_USER_STOCK_IDS)
        );
    }

    public Response getStockAvialableAmount(int stockId) {
        return ServerClient.getInstance().sendRequest(
                new Request(Operation.GET_STOCK_AVAILABLE_AMOUNT, Serializer.toJson(stockId))
        );
    }
}
