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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WithdrawController implements Initializable {
    @FXML
    private ListView<Course> enrolledInList;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button withdrawBtn;
    @FXML
    private Label errorMessage;
    private StudentAccount stdAcc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*When initialising, the controller will ensure the list can only select one object, as well as loading in the
        * student object. If it detects the student object has no enrolled courses, it will then remove the button
        * that allows withdraw from a course.*/
        enrolledInList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        VariableStorageSingleton loggedStudent = VariableStorageSingleton.getInstance();
        stdAcc = loggedStudent.getStd();
        if (stdAcc.getEnrolmentList().isEmpty()) {
            errorMessage.setTextFill(Color.color(0.698, 0.133, 0.133));
            errorMessage.setText("No Courses have been enrolled in.");
            withdrawBtn.setVisible(false);
            withdrawBtn.setManaged(false);
        } else {
            loadEnrolledCourse(stdAcc.getEnrolmentList());
        }
    }

    public void returnToDashBoard(ActionEvent event) {
        //Method to return to dashboard
        try {
            Parent dashboardParent = FXMLLoader.load(getClass().getResource("DashboardScene.fxml"));
            Scene dashboardScene = new Scene(dashboardParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(dashboardScene);
            window.show();
        } catch (IOException e) {
            errorMessage.setTextFill(Color.color(0.698, 0.133, 0.133));
            errorMessage.setText("Error in returning to Dashboard.");
        }
    }

    public void loadEnrolledCourse(ArrayList<Course> courses) {
        //Method to parse an arraylist of courses into a listview with desired formatting
        ObservableList<Course> removeableCourses = FXCollections.observableArrayList(courses);
        enrolledInList.setCellFactory(param -> new ListCell<Course>() {
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
        enrolledInList.setItems(removeableCourses);
    }

    public void withdrawFromCourse(ActionEvent event) {
        /*The course chosen will be removed from the enrolled list and will update the course to detract
        * from enrolment, before switching back to dashboard view.*/
        try {
            stdAcc.getEnrolmentList().remove(enrolledInList.getSelectionModel().getSelectedIndex());
            updateCourse(enrolledInList.getSelectionModel().getSelectedItem());
            Parent dashboardParent = FXMLLoader.load(getClass().getResource("DashboardScene.fxml"));
            Scene dashboardScene = new Scene(dashboardParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(dashboardScene);
            window.show();
        } catch (IOException e) {
            errorMessage.setTextFill(Color.color(0.698, 0.133, 0.133));
            errorMessage.setText("No selection has been made.");
        }
    }

    public void updateCourse(Course course) {
        /*WIll update the object for enrolment similar to the enrolment page, but subtracting rather than adding
        * to the enrolment count of a course.*/
        course.reduceEnrolemnt();
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
        /*This method receives a course object and serialises it in preparation of
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
