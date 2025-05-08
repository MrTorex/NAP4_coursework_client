package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;

public class RoleService {
    public Response getAll() {
        return ServerClient.getInstance().sendRequest(new Request(Operation.GET_ALL_ROLES));
    }
}
