module com.sample.flixtracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires mysql.connector.j;
    requires org.json;


    opens com.sample.flixtracker to javafx.fxml;
    exports com.sample.flixtracker;
}