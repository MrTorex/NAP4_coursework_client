package by.mrtorex.businessshark.client.gui.services;

import by.mrtorex.businessshark.server.enums.Operation;
import by.mrtorex.businessshark.server.model.entities.Person;
import by.mrtorex.businessshark.server.model.entities.Role;
import by.mrtorex.businessshark.server.model.entities.User;
import by.mrtorex.businessshark.server.network.Request;
import by.mrtorex.businessshark.server.network.Response;
import by.mrtorex.businessshark.server.network.ServerClient;
import by.mrtorex.businessshark.server.serializer.Serializer;
import by.mrtorex.businessshark.server.utils.Pair;

public class UserService {
    public Response login(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        String userJson = Serializer.toJson(user);
        Request request = new Request(Operation.LOGIN, userJson);

        return ServerClient.getInstance().sendRequest(request);
    }

    public Response register(String username, String password, String firstName, String lastName, String patronymic) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Role role = new Role();
        role.setName("Admin");
        user.setRole(role);

        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setPatronymic(patronymic);
        user.setPerson(person);

        String userJson = Serializer.toJson(user);
        Request request = new Request(Operation.REGISTER, userJson);

        return ServerClient.getInstance().sendRequest(request);
    }

    public Response getAll() {
        return ServerClient.getInstance().sendRequest(new Request(Operation.GET_ALL_USERS));
    }

    public Response delUser(String username) {
        return ServerClient.getInstance().sendRequest(new Request(Operation.DELETE_USER, Serializer.toJson(username)));
    }

    public Response updateUser(User user) {
        Pair<User, User> dataToUpdate = new Pair<>(user, ServerClient.getCurrentUser());
        return ServerClient.getInstance().sendRequest(new Request(Operation.UPDATE_USER, Serializer.toJson(dataToUpdate)));
    }
}

