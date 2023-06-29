package com.example.assignment_two;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TimeTableApplication extends Application {
    //This method overrides the start method for Application and initialises the GUI with the TimeTable.fxml,
    //which leads to the Login menu of teh application
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TimeTableApplication.class.getResource("TimeTable.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Login to Timetable");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(); //Launches the Application
    }
}