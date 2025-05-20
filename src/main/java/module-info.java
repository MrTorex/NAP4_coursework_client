/**
 * Модуль для клиентской части приложения BusinessShark.
 * <p>
 * Этот модуль включает в себя необходимые зависимости и открывает пакеты для доступа к ним.
 */
module by.mrtorex.businessshark.client {
    requires javafx.controls; // Зависимость для работы с JavaFX контролами
    requires javafx.fxml; // Зависимость для работы с FXML
    requires java.sql; // Зависимость для работы с базами данных через JDBC
    requires java.desktop; // Зависимость для работы с графическим интерфейсом
    requires static lombok; // Статическая зависимость для использования Lombok
    requires org.apache.logging.log4j; // Зависимость для логирования с использованием Log4j
    requires org.apache.logging.log4j.core; // Зависимость для ядра Log4j
    requires com.google.gson; // Зависимость для работы с JSON через Gson
    requires jbcrypt; // Зависимость для хеширования паролей с использованием BCrypt
    requires org.apache.poi.poi; // Зависимость для работы с форматами файлов Microsoft Office
    requires org.apache.poi.ooxml; // Зависимость для работы с OOXML форматами (например, .xlsx)
    requires com.github.librepdf.openpdf; // Зависимость для работы с PDF файлами

    // Открытие пакетов для доступа к ним из других модулей
    opens by.mrtorex.businessshark.client.gui.controllers to javafx.fxml; // Открытие контроллеров для FXML
    opens by.mrtorex.businessshark.server.model.entities to com.google.gson, javafx.base; // Открытие сущностей для Gson и JavaFX
    opens by.mrtorex.businessshark.server.utils to com.google.gson, javafx.base; // Открытие утилит для Gson и JavaFX
    opens by.mrtorex.businessshark.server.network to com.google.gson; // Открытие сетевых классов для Gson
    opens by.mrtorex.businessshark.client.gui.services to javafx.fxml; // Открытие сервисов для FXML
    exports by.mrtorex.businessshark.server.enums to com.google.gson; // Экспорт перечислений для Gson
    exports by.mrtorex.businessshark.client; // Экспорт клиентского пакета
    opens by.mrtorex.businessshark.client.gui.utils to com.google.gson, javafx.base; // Открытие утилит GUI для Gson и JavaFX
}
