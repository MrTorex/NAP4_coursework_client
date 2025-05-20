package by.mrtorex.businessshark.server.model.entities;

import lombok.Data;

/**
 * Модель человека, представляющая собой сущность с идентификатором и именем.
 */
@Data
public class Person {
    private Integer id; // Идентификатор человека
    private String firstName; // Имя
    private String patronymic; // Отчество
    private String lastName; // Фамилия
}
