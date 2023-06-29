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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SearchEnrolController implements Initializable {
    @FXML
    private ListView<Course> choiceList;
    @FXML
    private TextField queryBar;
    @FXML
    private Button searchBtn;
    @FXML
    private Button returnBtn;
    @FXML
    private Label errorMessage;

    private StudentAccount loggedStudent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*Initialises the list so it can only select one object at a time, as well as loading the student account
         * to perform actions on*/
        choiceList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        VariableStorageSingleton storageSingleton = VariableStorageSingleton.getInstance();
        loggedStudent = storageSingleton.getStd();
    }

    public void searchQuery(ActionEvent event) {
        /*Gets the query from the query bar and uses it as the parameter for the LIKE in the SQL statement
         * Checks if any results were returned and if so, loads them into the list view to be selected*/
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        Statement statement = null;
        String query = queryBar.getText();
        try {
            connection = DriverManager.getConnection(dbURL);
            statement = connection.createStatement();
            ResultSet courseQuery = statement.executeQuery("SELECT CourseObject FROM coursesTable WHERE courseName LIKE '%" + query + "%'");
            CourseList tempList = new CourseList();
            if (!courseQuery.isBeforeFirst()) {
                errorMessage.setTextFill(Color.color(0.698, 0.133, 0.133));
                errorMessage.setText("Query returned no results, try again.");
            } else {
                tempList.parseResultSet(courseQuery);
                listResultConversion(tempList);
            }

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

    public void listResultConversion(CourseList courses) {
        /* Solution adapted from https://stackoverflow.com/questions/36657299/how-can-i-populate-a-listview-in-javafx-using-custom-objects/36657553#36657553
        from what I can understand, this is setting up a lambda function to override the default updateItem method of
        ListCell to customise the display of the list cell item.
        * */
        for (Course i : courses.getCourseList()) {
            i.updateEnrolmentSlots();
        }
        List<Course> availabilityFilter = courses.getCourseList().stream().filter(Course::getIsAvailable).collect(Collectors.toList());
        ObservableList<Course> courseList = FXCollections.observableList(availabilityFilter);
        choiceList.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getCourseName() == null) {
                    setText(null);
                } else {
                    setText(String.format("%s | %s | %s", item.getCourseName(), item.getDay(), item.getTimeOfLecture().toString()));
                }
            }
        });
        choiceList.setItems(courseList);
    }

    public void addCourse(ActionEvent event) {
        //Method will check if the student account is able to add the course selected and if able to,
        //updates the courses enrolment and then moves back to the dashboard
        if (choiceList.getSelectionModel().getSelectedItem() != null) {//Edge Case handling when nothing is selected
            if (loggedStudent.addCourse(choiceList.getSelectionModel().getSelectedItem())) {
                try {
                    updateCourse(choiceList.getSelectionModel().getSelectedItem());
                    Parent dashboardParent = FXMLLoader.load(getClass().getResource("DashboardScene.fxml"));
                    Scene dashboardScene = new Scene(dashboardParent);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(dashboardScene);
                    window.show();
                } catch (IOException e) {
                    errorMessage.setTextFill(Color.color(0.698, 0.133, 0.133));
                    errorMessage.setText("No selection has been made.");
                }
            } else {
                errorMessage.setTextFill(Color.color(0.698, 0.133, 0.133));
                errorMessage.setText("Course chosen clashes with other courses in schedule.");
            }
        } else {
            errorMessage.setTextFill(Color.color(0.698, 0.133, 0.133));
            errorMessage.setText("No selection has been made.");
        }
    }

    public void returnToDashBoard(ActionEvent event) {
        //Method to return to the dashboard scene
        try {
            Parent dashboardParent = FXMLLoader.load(getClass().getResource("DashboardScene.fxml"));
            Scene dashboardScene = new Scene(dashboardParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(dashboardScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage.setTextFill(Color.color(0.698, 0.133, 0.133));
            errorMessage.setText("Error in returning to Dashboard.");
        }
    }

    public void updateCourse(Course course) {
        //Increments the enrolment number for the course parsed and then updates that courses entry in the database.
        course.incrementEnrolemnt();
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(dbURL);
            String preparedSQLStatement = "UPDATE coursesTable SET CourseObject = ? WHERE courseName = ?";
            statement = connection.prepareStatement(preparedSQLStatement);
            statement.setObject(1, serializeObject(course));
            statement.setString(2, course.getCourseName());
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

    private static byte[] serializeObject(Course courseObj) {
        /*This method receives a student object and serialises it in preparation of
         * being uploaded to the database. It is simply the standard serialisation process,
         * but instead of being saved to file output, the bytearray is sent to teh database.*/
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(courseObj);
            byte[] courseObjAsByte = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(courseObjAsByte);
            return courseObjAsByte;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
