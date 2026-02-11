module com.example.librarymanagment_system {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.librarymanagment_system to javafx.fxml;
    exports com.example.librarymanagment_system;
}