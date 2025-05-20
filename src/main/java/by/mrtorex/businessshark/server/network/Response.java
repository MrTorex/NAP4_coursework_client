package by.mrtorex.businessshark.server.network;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Модель ответа, представляющая собой сущность с результатом операции, сообщением и данными.
 */
@Data
@AllArgsConstructor
public class Response implements Serializable {
    private boolean success; // Успех операции
    private String message; // Сообщение о результате операции
    private String data; // Данные ответа
}
