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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class CourseDisplayController implements Initializable {
    @FXML
    private TableView<Course> courseDisplay;
    @FXML
    private TableColumn<Course,String> nameDisplay;
    @FXML
    private TableColumn<Course,Integer> capacityDisplay;
    @FXML
    private TableColumn<Course,String> yearDisplay;
    @FXML
    private TableColumn<Course,String> deliveryDisplay;
    @FXML
    private TableColumn<Course, String> dayDisplay;
    @FXML
    private TableColumn<Course, LocalTime> timeDisplay;
    @FXML
    private TableColumn<Course, Double> durationDisplay;
    @FXML
    private TableColumn<Course, Boolean> availabilityDisplay;
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Upon initialisation, the table will be populated using the courseTable through QueryToDisplay method.
        try{
            queryToDisplay();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void convertCourseListToDisplay(CourseList courses){
        /*This method will update all the courses availability to ensure that they're up to date
        * before displaying them accordingly with setItems method*/
        for (Course i: courses.getCourseList()) {
            i.updateEnrolmentSlots();
        }
        nameDisplay.setCellValueFactory(new PropertyValueFactory<Course, String>("courseName"));
        capacityDisplay.setCellValueFactory(new PropertyValueFactory<Course, Integer>("capacity"));
        yearDisplay.setCellValueFactory(new PropertyValueFactory<Course, String>("year"));
        deliveryDisplay.setCellValueFactory(new PropertyValueFactory<Course, String>("onlineDelivery"));
        dayDisplay.setCellValueFactory(new PropertyValueFactory<Course, String>("day"));
        timeDisplay.setCellValueFactory(new PropertyValueFactory<Course, LocalTime>("timeOfLecture"));
        durationDisplay.setCellValueFactory(new PropertyValueFactory<Course, Double>("durationHour"));
        availabilityDisplay.setCellValueFactory(new PropertyValueFactory<Course, Boolean>("isAvailable"));
        ObservableList<Course> observableCourses = FXCollections.observableList(courses.getCourseList());
        courseDisplay.setItems(observableCourses);
    }
    public void queryToDisplay() {
        /*This method connects to the database to select all the courseObjects in a ResultSet. This is then
        * parsed into actual objects in a CourseList method and then passed into the conversion method
        * for display.*/
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        Statement statement = null;
        try{
            connection = DriverManager.getConnection(dbURL);
            statement = connection.createStatement();
            ResultSet courseQuery = statement.executeQuery("SELECT CourseObject FROM coursesTable");
            CourseList tempList = new CourseList();
            tempList.parseResultSet(courseQuery);
            convertCourseListToDisplay(tempList);
        }
        catch(SQLException e){
            e.printStackTrace();
        } finally {
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
    }
    public void createCourseTable() {
        /*leftover code from creating a courses table when I needed a course table to read from.
        * This has been left in as a reference to what the CoursesTable looks like inside the database,
        * as well as the same reason for the student table in LoginController*/
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        Statement statement = null;
        try{
            connection = DriverManager.getConnection(dbURL);
            statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + "coursesTable"
                    +   "(courseID int NOT NULL,"
                    +   "courseName varChar(128) NOT NULL,"
                    +   "CourseObject blob NOT NULL,"
                    +   "PRIMARY KEY (courseID))");
        }
        catch(SQLException e){
            e.printStackTrace();
        } finally {
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }
    }
    public void returnToDashboard(ActionEvent event) throws IOException{
        /*Method for returning to the dashboard scene on button click*/
        try{
            Parent dashboardParent = FXMLLoader.load(getClass().getResource("DashboardScene.fxml"));
            Scene dashboardScene = new Scene(dashboardParent);
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            window.setScene(dashboardScene);
            window.show();
        }catch (IOException e){
            throw new IOException(e);
        }
    }
    public void switchToSearch(ActionEvent event) throws IOException {
        // Leads to the SearchCourse fxml
        try{
            Parent searchSceneParent = FXMLLoader.load(getClass().getResource("SearchScene.fxml"));
            Scene searchScene = new Scene(searchSceneParent);
            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
            window.setScene(searchScene);
            window.show();
        }catch (IOException e){
            throw new IOException(e);
        }
    }
    public void addCoursesToDatabase(CourseList courses) throws SQLException {
        /*Leftover Code for populating the coursesTable. Turning this into a method made it easier for
        * debugging purposes as I could simply comment out the method call instead of the entire code block.
        * If you for whatever reason want to reactivate this method, simply use it in the init method*/
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            connection = DriverManager.getConnection(dbURL);
            String preparedSQLStatement = "INSERT INTO coursesTable (courseID, courseName ,CourseObject) VALUES (?,?,?)";
            statement = connection.prepareStatement(preparedSQLStatement);
            //Method iterates over the entire course, setting the courseID starting from 1
            //and serializing the course object
            for (int i = 0; i < courses.getCourseList().size(); i++) {
                statement.setInt(1, i+1);
                statement.setString(2, courses.getCourseList().get(i).getCourseName());
                statement.setObject(3, serializeObject(courses.getCourseList().get(i)));
                statement.addBatch();
            }
            statement.executeBatch();
        }
        catch(SQLException e){
            throw e;
        } finally {
            try { statement.close(); } catch (Exception e) { /* Ignored */ }
            try { connection.close(); } catch (Exception e) { /* Ignored */ }
        }

    }
    private static byte[] serializeObject(Course courseObj) {
        //Serialises the course object parsed into the method and returns the bytes so that it can be
        //stored in a database
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(courseObj);
            byte[] courseObjAsByte = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(courseObjAsByte);
            return courseObjAsByte;
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
        return null;
    }
}
