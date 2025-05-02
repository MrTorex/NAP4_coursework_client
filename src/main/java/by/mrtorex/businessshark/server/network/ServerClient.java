package by.mrtorex.businessshark.server.network;

import by.mrtorex.businessshark.server.exceptions.NoConnectionException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ResourceBundle;

public class ServerClient {
    private static ServerClient instance;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ServerClient() throws NoConnectionException {
        connect(); // Если подключение провалится, выбросится исключение NoConnectionException
    }

    // Метод для функционирования паттерна синглтона
    public static synchronized ServerClient getInstance() throws NoConnectionException {
        if (instance == null) {
            try {
                instance = new ServerClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }


    private void connect() throws NoConnectionException {
        ResourceBundle bundle = ResourceBundle.getBundle("server");

        String serverAddress = bundle.getString("SERVER_IP");
        int serverPort = Integer.parseInt(bundle.getString("SERVER_PORT"));

        try {
            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected to the server at " + serverAddress + ":" + serverPort);
        } catch (IOException e) {
            throw new NoConnectionException("Failed to connect to the server at " + serverAddress + ":" + serverPort);
        }
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            instance = null;
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Отправляет запрос и возвращает на него ответ
    public Response sendRequest(Request request) {
        try {
            System.out.println("Sending request: " + request.getOperation());
            out.writeObject(request);
            out.flush();

            return processResponse();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Response processResponse() {
        try {
            return (Response) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

