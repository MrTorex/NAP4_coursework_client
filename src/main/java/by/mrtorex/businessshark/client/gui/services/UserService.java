package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Person;
import by.mrtorex.businessshark.server.model.entities.Role;
import by.mrtorex.businessshark.server.model.entities.User;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Serializer;
import by.mrtorex.businessshark.server.utils.Pair;
import by.mrtorex.businessshark.server.utils.PasswordHasher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Сервис для управления пользователями.
 * Обеспечивает операции входа, регистрации и управления пользователями.
 */
@SuppressWarnings({"unused", "DuplicatedCode"})
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param username имя пользователя
     * @param password пароль пользователя
     * @return ответ от сервера
     */
    public Response login(String username, String password) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordHasher.hashPassword(password));

            String userJson = Serializer.toJson(user);
            Request request = new Request(Operation.LOGIN, userJson);

            Response response = ServerClient.getInstance().sendRequest(request);
            logger.info("Пользователь {} успешно вошел в систему", username);
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при входе пользователя {}: {}", username, e.getMessage());
            return new Response(false, "Ошибка при входе", null);
        }
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param username    имя пользователя
     * @param password    пароль пользователя
     * @param firstName   имя
     * @param lastName    фамилия
     * @param patronymic  отчество
     * @return ответ от сервера
     */
    public Response register(String username, String password, String firstName, String lastName, String patronymic) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordHasher.hashPassword(password));

            Role role = new Role();
            role.setName("Waiting");
            user.setRole(role);

            Person person = new Person();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setPatronymic(patronymic);
            user.setPerson(person);

            String userJson = Serializer.toJson(user);
            Request request = new Request(Operation.REGISTER, userJson);

            Response response = ServerClient.getInstance().sendRequest(request);
            logger.info("Пользователь {} успешно зарегистрирован", username);
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя {}: {}", username, e.getMessage());
            return new Response(false, "Ошибка при регистрации", null);
        }
    }

    /**
     * Получает всех пользователей.
     *
     * @return ответ от сервера со списком пользователей
     */
    public Response getAll() {
        try {
            Response response = ServerClient.getInstance().sendRequest(new Request(Operation.GET_ALL_USERS));
            logger.info("Запрошен список всех пользователей");
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при получении списка пользователей: {}", e.getMessage());
            return new Response(false, "Ошибка при получении списка пользователей", null);
        }
    }

    /**
     * Удаляет пользователя по имени.
     *
     * @param username имя пользователя
     * @return ответ от сервера
     */
    public Response delUser(String username) {
        try {
            Response response = ServerClient.getInstance().sendRequest(new Request(Operation.DELETE_USER, Serializer.toJson(username)));
            logger.info("Пользователь {} успешно удален", username);
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя {}: {}", username, e.getMessage());
            return new Response(false, "Ошибка при удалении пользователя", null);
        }
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param user объект пользователя с обновленными данными
     * @return ответ от сервера
     */
    public Response updateUser(User user) {
        try {
            Pair<User, User> dataToUpdate = new Pair<>(user, ServerClient.getCurrentUser());
            Response response = ServerClient.getInstance().sendRequest(new Request(Operation.UPDATE_USER, Serializer.toJson(dataToUpdate)));
            logger.info("Данные пользователя {} успешно обновлены", user.getUsername());
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при обновлении данных пользователя {}: {}", user.getUsername(), e.getMessage());
            return new Response(false, "Ошибка при обновлении данных пользователя", null);
        }
    }

    /**
     * Добавляет нового пользователя.
     *
     * @param user объект пользователя для добавления
     * @return ответ от сервера
     */
    public Response addUser(User user) {
        try {
            Pair<User, User> dataToAdd = new Pair<>(user, ServerClient.getCurrentUser());
            Response response = ServerClient.getInstance().sendRequest(new Request(Operation.CREATE_USER, Serializer.toJson(dataToAdd)));
            logger.info("Пользователь {} успешно добавлен", user.getUsername());
            return response;
        } catch (Exception e) {
            logger.error("Ошибка при добавлении пользователя {}: {}", user.getUsername(), e.getMessage());
            return new Response(false, "Ошибка при добавлении пользователя", null);
        }
    }
}
