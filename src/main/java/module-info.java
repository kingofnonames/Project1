module com.example.personalproject1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;


    opens com.controller to javafx.fxml;
    opens com.view to javafx.fxml;
    opens com.model to javafx.fxml;

    exports com.model;
    exports com.controller;
    exports com;
}