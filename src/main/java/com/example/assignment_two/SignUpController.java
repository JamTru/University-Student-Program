package com.example.assignment_two;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    public TextField firstName;
    @FXML
    public TextField lastName;
    @FXML
    public TextField username;
    @FXML
    public TextField password;
    @FXML
    public TextField studentID;
    @FXML
    public Button signUpButton;
    @FXML
    public Button cancelButton;
    @FXML
    public Label errorLabel;

    public void initialize(URL url, ResourceBundle resourceBundle) {}

    /*When signing up a new student, the controller validates all fields haven't
     * been left empty.
     * If it is successful, it will create a new Student Object and then attempt insert the
     * new account into the database and then switch the scene back to login.*/
    public void signNewStudent(ActionEvent event) throws IOException {
        if (firstName.getText().isBlank() || lastName.getText().isBlank() || username.getText().isBlank() || password.getText().isBlank() || studentID.getText().isBlank()) {
            errorLabel.setText("One of the fields have been left empty.");
        } else {
            errorLabel.setText("");
            StudentAccount accountToStore = new StudentAccount(firstName.getText(), lastName.getText(), studentID.getText());
            try {
                insertNewStudent(username.getText(), password.getText(), accountToStore);
                returnToLogin(event);
            } catch (SQLException e) {
                errorLabel.setText("Sign Up Process has been interrupted.");
                e.printStackTrace();
            }
        }
    }

    /*This method simply loads the login scene and swaps over to it.*/
    public void returnToLogin(ActionEvent event) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("TimeTable.fxml"));
        Scene loginScene = new Scene(loginParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(loginScene);
        window.show();
    }

    /*This method receives the information for the three columns of the database and executes the query.*/
    public void insertNewStudent(String username, String password, StudentAccount newStudent) throws SQLException {
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(dbURL);
            String preparedSQLStatement = "INSERT INTO studentTable (username, password, StudentObject) VALUES (?,?,?)";
            statement = connection.prepareStatement(preparedSQLStatement);
            statement.setString(1, username);
            statement.setString(2, password);
            /*Objects stored in the database need to be serialized in order to be retrievable
             * and modifiable*/
            statement.setObject(3, serializeObject(newStudent));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            /*Finally statements always close the statement and connection regardless of if an error
             * occurs to ensure that it is closed as soon as possible.*/
            try {
                statement.close();
            } catch (Exception e) { /* Ignored */ }
            try {
                connection.close();
            } catch (Exception e) { /* Ignored */ }
        }
    }

    private static byte[] serializeObject(StudentAccount stdObj) {
        /*This method receives a student object and serialises it in preparation of
         * being uploaded to the database. It is simply the standard serialisation process,
         * but instead of being saved to file output, the bytearray is sent to teh database.*/
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(stdObj);
            byte[] stdObjAsByte = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(stdObjAsByte);
            return stdObjAsByte;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void createStudentTable() {
        /*This method is here to create the table because I couldn't figure out how to do it in SQLite studio
         *It simply creates the table and I felt it was relatively unnecessary to constantly run this query
         * everytime a SQL query was made, so I separated it into its own method and then left if alone.*/
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(dbURL);
            statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + "studentTable"
                    + "(username varChar(32) NOT NULL,"
                    + "password varChar(32) NOT NULL,"
                    + "StudentObject blob NOT NULL,"
                    + "PRIMARY KEY (username))");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (Exception e) { /* Ignored */ }
            try {
                connection.close();
            } catch (Exception e) { /* Ignored */ }
        }
    }
}
