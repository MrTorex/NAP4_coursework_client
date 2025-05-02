module by.mrtorex.businessshark.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires static lombok;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    opens by.mrtorex.businessshark.client.gui.controllers to javafx.fxml;
    exports by.mrtorex.businessshark.client;
}