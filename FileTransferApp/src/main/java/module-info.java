module com.example.filetransferapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.filetransferapp to javafx.fxml;
    exports com.example.filetransferapp;
}