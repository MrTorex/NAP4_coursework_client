package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Stock;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Serializer;

public class StockService {

    public Response getAll() {
        return ServerClient.getInstance().sendRequest(new Request(Operation.GET_ALL_STOCKS));
    }

    public Response delStock(Stock stockToDelete) {
        return ServerClient.getInstance().sendRequest(new Request(Operation.DELETE_STOCK, Serializer.toJson(stockToDelete.getId())));
    }

    public Response updateStock(Stock stockToUpdate) {
        return ServerClient.getInstance().sendRequest(new Request(Operation.UPDATE_STOCK, Serializer.toJson(stockToUpdate)));
    }

    public Response addStock(Stock stock) {
        return ServerClient.getInstance().sendRequest(new Request(Operation.CREATE_STOCK, Serializer.toJson(stock)));
    }

    public Response getAllStocksWithNoCompany() {
        return ServerClient.getInstance().sendRequest(new Request(Operation.GET_ALL_STOCKS_WITH_NO_COMPANY));
    }
}
