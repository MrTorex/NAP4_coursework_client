package by.mrtorex.businessshark.server.model.entities;

import lombok.Data;

/**
 * Модель пользователя, представляющая собой сущность с идентификатором, именем пользователя, хешем пароля, ролью и личными данными.
 */
@Data
public class User {
    private Integer id; // Идентификатор пользователя
    private String username; // Имя пользователя
    private String passwordHash; // Хеш пароля
    private Role role; // Роль пользователя
    private Person person; // Личные данные пользователя
}
