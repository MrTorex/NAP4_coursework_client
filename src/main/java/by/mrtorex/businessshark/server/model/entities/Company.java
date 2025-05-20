package by.mrtorex.businessshark.server.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель компании, представляющая собой сущность с идентификатором и названием.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private Integer id; // Идентификатор компании
    private String name; // Название компании
}
