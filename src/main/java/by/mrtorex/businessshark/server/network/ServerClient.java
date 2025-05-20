package by.mrtorex.businessshark.server.network;

import by.mrtorex.businessshark.server.exceptions.NoConnectionException;
import by.mrtorex.businessshark.server.model.entities.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ResourceBundle;

/**
 * Класс для управления подключением клиента к серверу.
 * Реализует паттерн Singleton для обеспечения единственного экземпляра клиента.
 */
public class ServerClient {
    private static final Logger logger = LogManager.getLogger(ServerClient.class);
    private static ServerClient instance;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    @Getter
    @Setter
    private static User currentUser;

    /**
     * Приватный конструктор для инициализации подключения к серверу.
     *
     * @throws NoConnectionException если не удалось подключиться к серверу
     */
    private ServerClient() throws NoConnectionException {
        connect(); // Если подключение провалится, выбросится исключение NoConnectionException
    }

    /**
     * Метод для получения единственного экземпляра класса ServerClient.
     *
     * @return единственный экземпляр ServerClient
     * @throws NoConnectionException если не удалось подключиться к серверу
     */
    public static synchronized ServerClient getInstance() throws NoConnectionException {
        if (instance == null) {
            try {
                instance = new ServerClient();
            } catch (Exception e) {
                logger.error("Ошибка при создании экземпляра ServerClient: {}", e.getMessage());
                throw new NoConnectionException("Не удалось создать экземпляр ServerClient");
            }
        }
        return instance;
    }

    /**
     * Метод для подключения к серверу.
     *
     * @throws NoConnectionException если не удалось подключиться к серверу
     */
    private void connect() throws NoConnectionException {
        ResourceBundle bundle = ResourceBundle.getBundle("server");

        String serverAddress = bundle.getString("SERVER_IP");
        int serverPort = Integer.parseInt(bundle.getString("SERVER_PORT"));

        try {
            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            logger.info("Подключено к серверу по адресу {}:{}", serverAddress, serverPort);
        } catch (IOException e) {
            throw new NoConnectionException("Не удалось подключиться к серверу по адресу " + serverAddress + ":" + serverPort);
        }
    }

    /**
     * Метод для отключения от сервера.
     */
    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            instance = null;
            logger.info("Отключено от сервера.");
        } catch (IOException e) {
            logger.error("Ошибка при отключении от сервера: {}", e.getMessage());
        }
    }

    /**
     * Отправляет запрос на сервер и возвращает ответ.
     *
     * @param request запрос для отправки
     * @return ответ от сервера
     */
    public Response sendRequest(Request request) {
        try {
            logger.info("Отправка запроса: {}", request.getOperation());
            out.writeObject(request);
            out.flush();

            return processResponse();
        } catch (IOException e) {
            logger.error("Ошибка при отправке запроса: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Обрабатывает ответ от сервера.
     *
     * @return ответ от сервера
     */
    private Response processResponse() {
        try {
            return (Response) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка при обработке ответа: {}", e.getMessage());
            return null;
        }
    }
}
