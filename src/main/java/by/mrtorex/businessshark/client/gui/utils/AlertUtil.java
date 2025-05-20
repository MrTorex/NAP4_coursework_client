package by.mrtorex.businessshark.client.gui.utils;

import by.mrtorex.businessshark.client.Main;
import by.mrtorex.businessshark.client.gui.enums.ThemesPath;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * Утилита для отображения различных типов алертов в приложении.
 * Позволяет создавать и отображать алерты с различными заголовками и содержимым.
 */
@SuppressWarnings("unused")
@Data
@Builder
public class AlertUtil {
    private String header;
    private String content;
    private Alert.AlertType alertType;

    /**
     * Отображает алерт без ожидания подтверждения.
     */
    public void realise() {
        complete().showAndWait();
    }

    /**
     * Отображает алерт и ожидает подтверждения от пользователя.
     *
     * @return тип кнопки, на которую нажал пользователь
     */
    public ButtonType realiseWithConfirmation() {
        return complete().showAndWait().orElse(ButtonType.CANCEL);
    }

    /**
     * Создает и настраивает алерт.
     *
     * @return настроенный алерт
     */
    public Alert complete() {
        Alert alert = new Alert(alertType);
        alert.setTitle("Business Shark");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(AlertUtil.class.getResource(String.valueOf(ThemesPath.valueOf(Main.themeName).getPathToCss()))).toExternalForm());
        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(AlertUtil.class.getResource("/styles/styles.css")).toExternalForm());

        return alert;
    }

    /**
     * Отображает алерт с типом ошибки.
     *
     * @param header  заголовок алерта
     * @param content содержимое алерта
     */
    public static void error(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.ERROR)
                .header(header)
                .content(content)
                .build().realise();
    }

    /**
     * Отображает алерт с типом предупреждения.
     *
     * @param header  заголовок алерта
     * @param content содержимое алерта
     */
    public static void warning(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.WARNING)
                .header(header)
                .content(content)
                .build().realise();
    }

    /**
     * Отображает информационный алерт.
     *
     * @param header  заголовок алерта
     * @param content содержимое алерта
     */
    public static void info(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.INFORMATION)
                .header(header)
                .content(content)
                .build().realise();
    }

    /**
     * Отображает алерт с подтверждением.
     *
     * @param header  заголовок алерта
     * @param content содержимое алерта
     * @return тип кнопки, на которую нажал пользователь
     */
    public static ButtonType confirmation(String header, String content) {
        return AlertUtil.builder()
                .alertType(Alert.AlertType.CONFIRMATION)
                .header(header)
                .content(content)
                .build().realiseWithConfirmation();
    }
}
