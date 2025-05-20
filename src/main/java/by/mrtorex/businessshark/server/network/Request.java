package by.mrtorex.businessshark.server.network;

import by.mrtorex.businessshark.server.enums.Operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * Модель запроса, представляющая собой сущность с операцией и данными.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Request implements Serializable {
    @NonNull
    private Operation operation; // Операция, которую необходимо выполнить
    private String data; // Данные запроса
}
