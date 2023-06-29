package com.example.assignment_two;

import java.io.Serializable;
import java.time.LocalTime;

public class Course implements Serializable {
    private String courseName;
    private int capacity;
    private String year;
    private String onlineDelivery; //The choice to make this a String variable makes it, so I don't have to write code for when I import it from CSV//
    private String day;
    private LocalTime timeOfLecture;
    private double durationHour;
    /*These two are new variables introduced to satisfy requirements of Assignment 2
    * enrolled is a private counter to indicate how many students have enrolled in the course
    * isAvailable is a boolean variable to check if max capacity has been reached.*/
    private int enrolled;
    private boolean isAvailable;

    public Course(String courseName, int capacity, String year, String onlineDelivery, String day, String timeOfLecture, double durationHour) {
        this.courseName = courseName;
        this.capacity = capacity;
        this.onlineDelivery = onlineDelivery;
        this.year = year;
        this.day = day;
        //The if statement is here to check if the String for time can be safely converted to LocalTime
        //If the string is less than 5 characters, e.g 5:00, it appends an extra 0 to the front
        //to match the LocalTime.parse() method
        if (timeOfLecture.length() < 5){
            StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(timeOfLecture);
            this.timeOfLecture = LocalTime.parse(sb.toString());
        } else{
            this.timeOfLecture = LocalTime.parse(timeOfLecture);
        }
        this.durationHour = durationHour;
        this.enrolled = 0;
        this.isAvailable = true;
    }

    public String getCourseName() {return courseName;}
    public void setCourseName(String courseName) {this.courseName = courseName;}
    public int getCapacity() {return capacity;}
    public void setCapacity(int capacity) {this.capacity = capacity;}
    public String getYear() {return year;}
    public void setYear(String year) {this.year = year;}
    public String getOnlineDelivery() {return onlineDelivery;}
    public void setOnlineDelivery(String onlineDelivery) {this.onlineDelivery = onlineDelivery;}
    public String getDay() {return day;}
    public void setDay(String day) {this.day = day;}
    public LocalTime getTimeOfLecture() {return timeOfLecture;}
    public void setTimeOfLecture(String timeOfLecture) {this.timeOfLecture = LocalTime.parse(timeOfLecture);}
    public double getDurationHour() {return durationHour;}
    public void setDurationHour(double durationHour) {this.durationHour = durationHour;}
    public Boolean getIsAvailable(){return isAvailable;}
    /*For any markers reading this, the reason why it is called getIsAvailable instead of getisAvailable
    * is because the ProductValueFactory method kept refusing to bloody use this method unless the I was capitalised
    * and I'm leaving this comment in out of sheer anger that I needed to do this. Java why*/

    public void setAvailable(boolean available) {isAvailable = available;}

    public int getEnrolled() {return enrolled;}

    public void incrementEnrolemnt() {this.enrolled += 1;}
    public void reduceEnrolemnt() {this.enrolled -= 1;}
    public void updateEnrolmentSlots(){
        //This method simply updates the isAvailable value before display to ensure its accurate
        if (enrolled >= capacity){
            if (capacity == 0){ //Capacity being 0 indicates an online class, to which it is always available
               this.isAvailable = true;
            } else{this.isAvailable = false;}
        } else {
            this.isAvailable = true;
        }
    }


}
