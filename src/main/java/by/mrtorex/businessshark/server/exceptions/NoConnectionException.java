package by.mrtorex.businessshark.server.exceptions;

/**
 * Исключение, возникающее при отсутствии соединения.
 */
public class NoConnectionException extends RuntimeException {

    /**
     * Конструктор исключения с сообщением.
     *
     * @param message сообщение об ошибке
     */
    public NoConnectionException(String message) {
        super(message);
    }
}
