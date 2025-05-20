package by.mrtorex.businessshark.server.exceptions;

/**
 * Исключение, возникающее при ошибках в ответах сервера.
 */
public class ResponseException extends RuntimeException {

    /**
     * Конструктор исключения с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public ResponseException(String message) {
        super(message);
    }
}
