package com.example.assignment_two;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class StudentAccount implements Serializable {
    private String firstName;
    private String lastName;
    private String studentID;
    private ArrayList<Course> enrolmentList;
    public StudentAccount(String firstName, String lastName, String studentID){
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentID = studentID;
        this.enrolmentList = new ArrayList<>();
    }
    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public String getStudentID() {return studentID;}
    public ArrayList<Course> getEnrolmentList() {return enrolmentList;}
    /*This method will validate if the course is already enrolled for the user
    * or if the user has a course that clashes with their schedule. For clarity's sake
    * I separated the clashingSchedule into its own method below
    * Otherwise, if the student's enrolment list is completely empty
    * or if no clashes are found, then it adds a course.*/
    public boolean addCourse(Course course){
        if (this.enrolmentList.isEmpty()){
            this.enrolmentList.add(course);
            return true;
        } else {
            for (Course i: enrolmentList){
                if (i.getCourseName().equals(course.getCourseName())){
                    return false;
                }
            }
            if (checkClash(course)){
                return false;
            } else{
                this.enrolmentList.add(course);
                return true;
            }
        }
    }
    /*This method checks for clashes by comparing the already enrolled courses's schedule against the chosen course
    * This is done by comparing if either the chosen course's start or end inside another course's duration
    * E.G if a course runs from 12:30 to 14:30, a course that starts at 13:30 should return true for a clash*/
    public boolean checkClash(Course course){
        for (Course i: this.enrolmentList) {
            if (i.getDay().equals(course.getDay()))
                if (i.getTimeOfLecture().compareTo(course.getTimeOfLecture()) < 0 && i.getTimeOfLecture().plusHours((long) i.getDurationHour()).compareTo(course.getTimeOfLecture()) > 0){
                    return true;
                } else if(i.getTimeOfLecture().compareTo(course.getTimeOfLecture().plusHours((long) course.getDurationHour())) < 0 && i.getTimeOfLecture().plusHours((long) i.getDurationHour()).compareTo(course.getTimeOfLecture().plusHours((long) course.getDurationHour())) > 0){
                    return true;
                }
        }
        return false;
    }
}
