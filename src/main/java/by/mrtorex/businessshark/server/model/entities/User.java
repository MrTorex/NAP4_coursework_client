package by.mrtorex.businessshark.server.model.entities;

import lombok.Data;

@Data
public class User {
    private Integer id;

    private String username;

    private String password;

    private Role role;

    private Person person;
}
