package by.mrtorex.businessshark.server.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    private Integer id;

    private String ticket;

    private Double price;

    private Integer amount;
}
