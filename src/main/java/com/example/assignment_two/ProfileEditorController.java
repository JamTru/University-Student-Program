package com.example.assignment_two;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ProfileEditorController implements Initializable {
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private TextField firstNameChange;
    @FXML
    private TextField lastNameChange;
    @FXML
    private TextField passwordChange;
    @FXML
    private ListView<String> fontList;
    @FXML
    private Label sampleText;
    @FXML
    private Slider fontSlider;
    @FXML
    private Button updateBtn;
    private ArrayList<String> fontFamilies = new ArrayList<>();
    private String fontChoice = null;
    private VariableStorageSingleton loggedStudent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //On initialisation, all the menus are populated and the list is single choice only
        loggedStudent = VariableStorageSingleton.getInstance();
        fontFamilies.add("Lucida Console");
        fontFamilies.add("Comic Sans MS");
        fontFamilies.add("SansSerif");
        fontFamilies.add("Dubai Medium");
        ObservableList<String> fontChoices = FXCollections.observableArrayList(fontFamilies);
        fontList.setItems(fontChoices);
        fontList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void updateSampleText(ActionEvent event) throws IOException {
        //This function simply changes the font and size of the sample text as a demo for the user
        fontChoice = fontList.getSelectionModel().getSelectedItem();
        if (fontChoice == null) {
            sampleText.setFont(Font.font("Lucida Console", fontSlider.getValue()));
        } else {
            sampleText.setFont(Font.font(fontChoice, fontSlider.getValue()));

        }
        System.out.println(fontSlider.getValue());
    }

    public void finalizeChanges(ActionEvent event) throws IOException {
        /*Method gathers all the changes desired by the user and then executes the changes necessary
        * to display as well as updating any information necessary about the student account.*/
        String username = loggedStudent.getUsername();
        StudentAccount studentChange = loggedStudent.getStd();
        String firstName = studentChange.getFirstName();
        String lastName = studentChange.getLastName();
        String password = loggedStudent.getPassword();
        //These variables are stored so that regardless of what is inputted, there will always be something
        //in the variable to avoid NullPointerExceptions or otherwise.
        if (!firstNameChange.getText().isBlank()) {
            firstName = firstNameChange.getText();
        }
        if (!lastNameChange.getText().isBlank()) {
            lastName = lastNameChange.getText();
        }
        if (!passwordChange.getText().isBlank()) {
            password = passwordChange.getText();
        }
        if (fontChoice == null) {
            loggedStudent.setFontChoice("Lucida Console");
        } else {
            loggedStudent.setFontChoice(fontChoice);
        }
        //Each variable is checked and only when there is an input from user does it change.
        System.out.println(password);
        loggedStudent.setFontSize((int) fontSlider.getValue());
        updateDatabase(firstName, lastName, password);
        returnToDashboard(event);
    }

    public void updateDatabase(String fName, String lName, String pWord) {
        /*Using the new variables passed into the method, the method updates the account in the database with the
        * new changes using an SQL query and serialising teh new object into the database.*/
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            StudentAccount changedStudent = loggedStudent.getStd();
            changedStudent.setFirstName(fName);
            changedStudent.setLastName(lName);
            connection = DriverManager.getConnection(dbURL);
            String preparedSQLStatement = "UPDATE studentTable SET password = ?, StudentObject = ? WHERE username = ?";
            statement = connection.prepareStatement(preparedSQLStatement);
            statement.setString(1, pWord);
            statement.setObject(2, serializeObject(changedStudent));
            statement.setString(3, loggedStudent.getUsername());
            statement.executeUpdate();
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

    public void returnToDashboard(ActionEvent event) throws IOException {
        //Method to return to the dashboard scene
        try {
            Parent dashboardParent = FXMLLoader.load(getClass().getResource("DashboardScene.fxml"));
            Scene dashboardScene = new Scene(dashboardParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(dashboardScene);
            window.show();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

}
