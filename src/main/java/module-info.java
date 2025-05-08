module by.mrtorex.businessshark.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires static lombok;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires com.google.gson;

    opens by.mrtorex.businessshark.client.gui.controllers to javafx.fxml;
    opens by.mrtorex.businessshark.server.model.entities to com.google.gson, javafx.base;
    opens by.mrtorex.businessshark.server.utils to com.google.gson, javafx.base;
    opens by.mrtorex.businessshark.server.network to com.google.gson;
    exports by.mrtorex.businessshark.server.enums to com.google.gson;
    opens by.mrtorex.businessshark.client.gui.services to javafx.fxml;
    exports by.mrtorex.businessshark.client;
}