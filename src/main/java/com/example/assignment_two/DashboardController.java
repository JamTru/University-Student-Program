package com.example.assignment_two;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {
    @FXML
    private MenuItem profileEditMenuBtn;
    @FXML
    private MenuItem exportMenuBtn;
    @FXML
    private MenuItem quitBtn;
    @FXML
    private MenuItem viewCourseMenuBtn;
    @FXML
    private MenuItem searchMenuBtn;
    @FXML
    private MenuItem withdrawMenuBtn;
    @FXML
    private TableView enrolledCourseTable;
    @FXML
    private TableColumn<Course, String> nameTab;
    @FXML
    private TableColumn<Course, Integer> capacityTab;
    @FXML
    private TableColumn<Course, String> yearTab;
    @FXML
    private TableColumn<Course, String> deliveryTab;
    @FXML
    private TableColumn<Course, String> dayTab;
    @FXML
    private TableColumn<Course, LocalTime> timeTab;
    @FXML
    private TableColumn<Course, Double> durationTab;
    @FXML
    private Label nameLabel;
    @FXML
    private Label studentNumberLabel;
    @FXML
    private MenuItem saveQuit;
    @FXML
    private Label fillerLabel;
    @FXML
    private Label dashboardHeader;
    @FXML
    private Label toggleLabel;
    @FXML
    private ToggleButton toggleViewBtn;
    @FXML
    private HBox scheduleView;
    @FXML
    private ListView<Course> mondayList;
    @FXML
    private ListView<Course> tuesdayList;
    @FXML
    private ListView<Course> wednesdayList;
    @FXML
    private ListView<Course> thursdayList;
    @FXML
    private ListView<Course> fridayList;

    private StudentAccount stdAccount;
    /*Boolean variable for displaying either the timetable view (True) or the list view (False) on the next
     * time the button to toggle views is pressed. It starts as false as I want to load the schedule view first
     * rather than list.*/
    private boolean displayView;
    private ArrayList<Course> stdCourseList;
    private VariableStorageSingleton loggedStudent;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*Upon initialisation, the singleton class is called to access all the saved variables
         * and then has the getter methods used appropriately to set up the appearances
         * Additionally the lists are all initialised here, while also setting the schedule view invisible.*/
        loggedStudent = VariableStorageSingleton.getInstance();
        stdAccount = loggedStudent.getStd();
        stdCourseList = stdAccount.getEnrolmentList();
        setLabels(stdAccount.getFirstName(), stdAccount.getLastName(), stdAccount.getStudentID(), loggedStudent.getFontSize(), loggedStudent.getFontChoice());
        loadStudentCourses(stdCourseList);
        enrolledCourseTable.setVisible(false); // Despite having a method for switching views, I needed to initialise hiding it as the method requires an event from button
        enrolledCourseTable.setManaged(false);
        displayView = false;
        if (!stdCourseList.isEmpty()){
            List<Course> mondaysCourses = stdCourseList.stream().filter(c -> c.getDay().equals("Monday")).sorted((
                    (o1, o2) -> o1.getTimeOfLecture().compareTo(o2.getTimeOfLecture())
            )).collect(Collectors.toList());//Each stream method is filtering courses by teh day and then sorting it by times
            loadDayList(mondaysCourses, mondayList);
            List<Course> tuesdaysCourses = stdCourseList.stream().filter(c -> c.getDay().equals("Tuesday")).sorted((
                    (o1, o2) -> o1.getTimeOfLecture().compareTo(o2.getTimeOfLecture())
            )).collect(Collectors.toList());
            loadDayList(tuesdaysCourses, tuesdayList);
            List<Course> wednesdaysCourses = stdCourseList.stream().filter(c -> c.getDay().equals("Wednesday")).sorted((
                    (o1, o2) -> o1.getTimeOfLecture().compareTo(o2.getTimeOfLecture())
            )).collect(Collectors.toList());
            loadDayList(wednesdaysCourses, wednesdayList);
            List<Course> thursdaysCourses = stdCourseList.stream().filter(c -> c.getDay().equals("Thursday")).sorted((
                    (o1, o2) -> o1.getTimeOfLecture().compareTo(o2.getTimeOfLecture())
            )).collect(Collectors.toList());
            loadDayList(thursdaysCourses, thursdayList);
            List<Course> fridaysCourses = stdCourseList.stream().filter(c -> c.getDay().equals("Friday")).sorted((
                    (o1, o2) -> o1.getTimeOfLecture().compareTo(o2.getTimeOfLecture())
            )).collect(Collectors.toList());
            loadDayList(fridaysCourses, fridayList);
        }
    }

    public void quitApplication(ActionEvent event) throws IOException {
        //Exits the program
        Platform.exit();
    }

    public void editProfile(ActionEvent event) throws IOException {
        //Leads to the Profile Editor fxml
        Parent editProfileParent = FXMLLoader.load(getClass().getResource("EditProfileScene.fxml"));
        Scene editProfileScene = new Scene(editProfileParent);
        Stage window = (Stage) nameLabel.getScene().getWindow();
        window.setScene(editProfileScene);
        window.show();
    }

    public void viewAllCourses(ActionEvent event) throws IOException {
        // Leads to the course viewer fxml
        Parent courseDisplayParent = FXMLLoader.load(getClass().getResource("FullCourseScene.fxml"));
        Scene courseDisplayScene = new Scene(courseDisplayParent);
        Stage window = (Stage) nameLabel.getScene().getWindow();
        window.setScene(courseDisplayScene);
        window.show();
    }

    public void searchCourses(ActionEvent event) throws IOException {
        // Leads to the SearchCourse fxml
        Parent searchSceneParent = FXMLLoader.load(getClass().getResource("SearchScene.fxml"));
        Scene searchScene = new Scene(searchSceneParent);
        Stage window = (Stage) nameLabel.getScene().getWindow();
        window.setScene(searchScene);
        window.show();
    }

    public void withdrawCourses(ActionEvent event) throws IOException {
        // Leads to the WithdrawCourse fxml
        Parent withdrawSceneParent = FXMLLoader.load(getClass().getResource("WithdrawScene.fxml"));
        Scene withdrawScene = new Scene(withdrawSceneParent);
        Stage window = (Stage) nameLabel.getScene().getWindow();
        window.setScene(withdrawScene);
        window.show();
    }

    public void setLabels(String fName, String lName, String stdID, int size, String fontName) {
        //Method simply sets up the name labels as well as the size of the labels.
        nameLabel.setText(fName + " " + lName);
        studentNumberLabel.setText(stdID);
        nameLabel.setFont(Font.font(fontName, size));
        studentNumberLabel.setFont(Font.font(fontName, size));
        dashboardHeader.setFont(Font.font(fontName, size));
        fillerLabel.setFont(Font.font(fontName, size));
    }

    public void loadStudentCourses(ArrayList<Course> courses) {
        //This method automatically loads a student's arraylist of courses
        //into the table view method by observing the list of courses and using the course
        //getter methods to grab the appropriate variables.
        nameTab.setCellValueFactory(new PropertyValueFactory<Course, String>("courseName"));
        capacityTab.setCellValueFactory(new PropertyValueFactory<Course, Integer>("capacity"));
        yearTab.setCellValueFactory(new PropertyValueFactory<Course, String>("year"));
        deliveryTab.setCellValueFactory(new PropertyValueFactory<Course, String>("onlineDelivery"));
        dayTab.setCellValueFactory(new PropertyValueFactory<Course, String>("day"));
        timeTab.setCellValueFactory(new PropertyValueFactory<Course, LocalTime>("timeOfLecture"));
        durationTab.setCellValueFactory(new PropertyValueFactory<Course, Double>("durationHour"));
        ObservableList<Course> observableCourses = FXCollections.observableList(courses);
        enrolledCourseTable.setItems(observableCourses);
    }

    public void changeView(ActionEvent event) {
        //The method simply detects which view is currently being displayed using a boolean variable
        //and sets up so either one becomes visible and the other invisible.
        if (displayView) {
            enrolledCourseTable.setVisible(false);
            enrolledCourseTable.setManaged(false);
            scheduleView.setVisible(true);
            scheduleView.setManaged(true);
            displayView = false;
            toggleViewBtn.setText("List");
        } else {
            scheduleView.setVisible(false);
            scheduleView.setManaged(false);
            enrolledCourseTable.setVisible(true);
            enrolledCourseTable.setManaged(true);
            displayView = true;
            toggleViewBtn.setText("Schedule");
        }
    }

    public void loadDayList(List<Course> sortedCourses, ListView<Course> day) {
        /*Method that takes a list of courses that have been already filtered and sorted and a specific listview
        * to populate with a specific formatting.*/
        ObservableList<Course> coursesForDay = FXCollections.observableArrayList(sortedCourses);
        day.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getCourseName() == null) {
                    setText(null);
                } else {
                    setText(String.format("%s | %s", item.getCourseName(), item.getTimeOfLecture().toString()));
                }
            }
        });
        day.setItems(coursesForDay);
    }

    public void saveAccountToDatabase(VariableStorageSingleton variableContainer) {
        /*Method for updating the database for changes in the account by using the singleton class
        * to retrieve both the updated account and the username which acts as a primary key to access the
        * specific entry for database.*/
        String dbURL = "jdbc:sqlite:studentsDB.db";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(dbURL);
            String preparedSQLStatement = "UPDATE studentTable SET StudentObject = ? WHERE username = ?";
            statement = connection.prepareStatement(preparedSQLStatement);
            statement.setObject(1, serializeObject(variableContainer.getStd()));
            statement.setString(2, variableContainer.getUsername());
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

    public void saveAndQuit(ActionEvent event) {
        //Calls the save function and then the quit application method
        saveAccountToDatabase(loggedStudent);
        try {
            quitApplication(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAndExport(ActionEvent event) {
        /*Method saves the account first to ensure any changes have been saved in the event filewriting goes wrong
        * after that, the file writer first writes the accounts first and last name and then iterates through
        * the accounts list of enrolled courses with their details.*/
        saveAccountToDatabase(loggedStudent);
        String fileName = loggedStudent.getUsername() + "_" + LocalTime.now().toString().replace(":", "-") + ".txt"; //This was chosen as file name so i wouldn't have to worry about duplicate file names
        File exportFile = new File(fileName);
        try {
            if (exportFile.createNewFile()) {
                FileWriter outputWriter = new FileWriter(fileName);
                BufferedWriter buffedWriter = new BufferedWriter(outputWriter);
                buffedWriter.write(loggedStudent.getStd().getFirstName() + "," + loggedStudent.getStd().getLastName());
                buffedWriter.newLine();
                for (Course i : loggedStudent.getStd().getEnrolmentList()) {
                    String lineToBeWritten = String.format("%s, %s, %s, %s, %s, %s, %s", i.getCourseName(), Integer.toString(i.getCapacity()), i.getYear(), i.getOnlineDelivery(), i.getDay(), i.getTimeOfLecture().toString(), Double.toString(i.getDurationHour()));
                    buffedWriter.write(lineToBeWritten);
                    buffedWriter.newLine();
                }
                buffedWriter.close();
            } else {
                throw new IOException("How did you manage to get here?");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
