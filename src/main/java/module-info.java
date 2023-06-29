module com.example.assignment_two {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.assignment_two to javafx.fxml;
    exports com.example.assignment_two;
}