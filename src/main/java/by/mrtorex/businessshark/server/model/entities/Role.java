package by.mrtorex.businessshark.server.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Integer id;

    private String name;

    @Override
    public String toString() {
        return name;
    }
}
