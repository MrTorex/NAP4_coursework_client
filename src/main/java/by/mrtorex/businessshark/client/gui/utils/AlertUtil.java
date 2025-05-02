package by.mrtorex.businessshark.client.gui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertUtil {
    private String header;
    private String content;
    private Alert.AlertType alertType;

    public void realise() {
        complete().showAndWait();
    }

    public ButtonType realiseWithConfirmation() {
        return complete().showAndWait().orElse(ButtonType.CANCEL);
    }

    public Alert complete() {
        Alert alert = new Alert(alertType);
        alert.setTitle("Business Shark");
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert;
    }

    public static void error(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.ERROR)
                .header(header)
                .content(content)
                .build().realise();
    }

    public static void warning(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.WARNING)
                .header(header)
                .content(content)
                .build().realise();
    }

    public static void info(String header, String content) {
        AlertUtil.builder()
                .alertType(Alert.AlertType.INFORMATION)
                .header(header)
                .content(content)
                .build().realise();
    }

    public static ButtonType confirmation(String header, String content) {
        return AlertUtil.builder()
                .alertType(Alert.AlertType.CONFIRMATION)
                .header(header)
                .content(content)
                .build().realiseWithConfirmation();
    }
}
