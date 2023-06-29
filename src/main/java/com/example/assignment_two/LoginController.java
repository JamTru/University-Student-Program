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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameEntry;
    @FXML
    private TextField passwordEntry;
    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Label wrongLogIn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void signUp(ActionEvent event) throws IOException {
        //This method switches the scene to the signup screen
        Parent signUpParent = FXMLLoader.load(getClass().getResource("SignUpScene.fxml"));
        Scene signUpScene = new Scene(signUpParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Sign Up to Uni Timetable");
        window.setScene(signUpScene);
        window.show();
    }

    public void logIn(ActionEvent event) throws IOException {
        /*This method when successful, intiialises the Singleton class for use in other scenes
         * as well as switching to the next scene*/
        try {
            StudentAccount accountFound = validateLogIn(event);
            if (accountFound == null) {
                /*Ignored as the validateLogIn already handles the error so nothing needs to be done here*/
            } else {
                VariableStorageSingleton loggedStudent = VariableStorageSingleton.getInstance();
                loggedStudent.setStd(accountFound);
                loggedStudent.setUsername(usernameEntry.getText());
                loggedStudent.setFontSize(20);
                loggedStudent.setFontChoice("Lucida Console");
                loggedStudent.setPassword(passwordEntry.getText());
                /*The fontsize and fontchoice are set default values.*/
                Parent dashboardParent = FXMLLoader.load(getClass().getResource("DashboardScene.fxml"));
                Scene dashboardScene = new Scene(dashboardParent);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setTitle(usernameEntry.getText() + "'s Timetable");
                window.setScene(dashboardScene);
                window.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private StudentAccount validateLogIn(ActionEvent event) throws SQLException {
        /*This method validates the log in by querying from the database the exact pair of username and
         * password submitted. This should be fine as the username itself is a primary key, so there is not
         * a possibility of a duplicate being a concern. */
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet matchingUsers = null;
        try {
            connection = DriverManager.getConnection(dbURL);
            String preparedSQLStatement = "SELECT * FROM studentTable WHERE username=? AND password=?;";
            statement = connection.prepareStatement(preparedSQLStatement);
            statement.setString(1, usernameEntry.getText());
            statement.setString(2, passwordEntry.getText());
            matchingUsers = statement.executeQuery();
            if (matchingUsers.isBeforeFirst()) { //.isBeforeFirst() checks if the ResultSet contains any values. returns true if there is
                return readSerializedData(matchingUsers.getBytes(3));
            } else {
                /*Handles no matches by changing the wrongLogIn Lable to red and stating no matches*/
                wrongLogIn.setTextFill(Color.color(0.698, 0.133, 0.133));
                wrongLogIn.setText("Wrong Username or Password");
                return null;
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                statement.close();
            } catch (Exception e) { /* Ignored */ }
            try {
                connection.close();
            } catch (Exception e) { /* Ignored */ }
        }
    }

    public static StudentAccount readSerializedData(byte[] data) {
        /*This method receives a byte array of data and serialises it back into a StudentAccount object
         * before returning the student account for use.*/
        try {
            ByteArrayInputStream baip = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(baip);
            StudentAccount storedStudent = (StudentAccount) ois.readObject();
            return storedStudent;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}