package by.mrtorex.businessshark.client.gui.enums;

import lombok.Getter;

@Getter
public enum ScenePath {
    LOGIN("/views/login.fxml"),
    REGISTRATION("/views/register.fxml"),
    USER_MENU("/views/usermenu.fxml"),
    ADMIN_MENU("/views/adminmenu.fxml"),
    USERS("/views/users.fxml"),
    STOCKS("/views/stocks.fxml"),
    PORTFOLIO("/views/portfolio.fxml"),
    COMPANIES("/views/companies.fxml"),
    EDIT_INFO("/views/editinfo.fxml");

    private final String pathToFxml;

    ScenePath(String pathToFxml) {
        this.pathToFxml = pathToFxml;
    }
}
