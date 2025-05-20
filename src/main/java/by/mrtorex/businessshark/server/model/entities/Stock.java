package by.mrtorex.businessshark.server.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель акции, представляющая собой сущность с идентификатором, тикером, ценой и количеством.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    private Integer id; // Идентификатор акции
    private String ticket; // Тикер акции
    private Double price; // Цена акции
    private Integer amount; // Количество акций
}
