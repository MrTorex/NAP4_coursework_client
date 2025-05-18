package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Company;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Serializer;

public class CompanyService {

    public Response getAll() {
        return ServerClient.getInstance().sendRequest(new Request(Operation.GET_ALL_COMPANIES));
    }

    public Response delCompany(Company company) {
        return ServerClient.getInstance().sendRequest(new Request(Operation.DELETE_COMPANY, Serializer.toJson(company.getId())));
    }

    public Response updateCompany(Company company) {
        return ServerClient.getInstance().sendRequest(new Request(Operation.UPDATE_COMPANY, Serializer.toJson(company)));
    }

    public Response addCompany(Company company) {
        return ServerClient.getInstance().sendRequest(new Request(Operation.CREATE_COMPANY, Serializer.toJson(company)));
    }

    public Response getCompanyStocks(Company company) {
        return ServerClient.getInstance().sendRequest(new Request(Operation.GET_STOCKS_BY_COMPANY, Serializer.toJson(company)));
    }
}