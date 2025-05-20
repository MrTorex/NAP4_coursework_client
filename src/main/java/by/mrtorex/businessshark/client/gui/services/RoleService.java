package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.exceptions.ResponseException;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Сервис для управления ролями пользователей через взаимодействие с сервером.
 */
public class RoleService {
    private static final Logger logger = LogManager.getLogger(RoleService.class);
    private final ServerClient serverClient;

    /**
     * Конструктор по умолчанию.
     */
    public RoleService() {
        this.serverClient = ServerClient.getInstance();
        logger.info("Инициализирован RoleService с клиентом сервера");
    }

    /**
     * Получает список всех ролей.
     *
     * @return ответ сервера со списком ролей
     */
    public Response getAll() {
        try {
            Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_ROLES));
            logger.info("Запрошен список всех ролей");
            return response;
        } catch (ResponseException e) {
            logger.error("Ошибка при получении списка ролей: {}", e.getMessage());
            return new Response(false, e.getMessage(), null);
        }
    }
}