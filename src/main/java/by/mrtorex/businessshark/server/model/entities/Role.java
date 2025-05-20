package by.mrtorex.businessshark.server.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель роли, представляющая собой сущность с идентификатором и названием.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Integer id; // Идентификатор роли
    private String name; // Название роли

    @Override
    public String toString() {
        return name; // Возвращает название роли
    }
}
