package by.mrtorex.businessshark.client.gui.enums;

import lombok.Getter;

/**
 * Перечисление путей к FXML-файлам для различных сцен приложения.
 */
@Getter
public enum ScenePath {
    /**
     * Путь к сцене логина.
     */
    LOGIN("/views/login.fxml"),

    /**
     * Путь к сцене регистрации.
     */
    REGISTRATION("/views/register.fxml"),

    /**
     * Путь к сцене меню пользователя.
     */
    USER_MENU("/views/usermenu.fxml"),

    /**
     * Путь к сцене меню администратора.
     */
    ADMIN_MENU("/views/adminmenu.fxml"),

    /**
     * Путь к сцене списка пользователей.
     */
    USERS("/views/users.fxml"),

    /**
     * Путь к сцене списка акций.
     */
    STOCKS("/views/stocks.fxml"),

    /**
     * Путь к сцене портфеля.
     */
    PORTFOLIO("/views/portfolio.fxml"),

    /**
     * Путь к сцене списка компаний.
     */
    COMPANIES("/views/companies.fxml"),

    /**
     * Путь к сцене редактирования информации.
     */
    EDIT_INFO("/views/editinfo.fxml");

    private final String pathToFxml;

    /**
     * Конструктор перечисления.
     *
     * @param pathToFxml путь к FXML-файлу сцены
     * @throws IllegalArgumentException если путь к FXML-файлу пустой или null
     */
    ScenePath(String pathToFxml) {
        if (pathToFxml == null || pathToFxml.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к FXML-файлу не может быть пустым или null");
        }
        this.pathToFxml = pathToFxml;
    }

}