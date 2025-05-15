module com.example.something {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;

    opens com.example.something to javafx.fxml;
    exports com.example.something;
}