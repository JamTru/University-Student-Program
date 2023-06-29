package com.example.assignment_two;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CourseList implements Serializable {
    private ArrayList<Course> courseList;

    /*ArrayList was chosen again for the same reasons as the studentAccount ArrayList, given that it maintains
     * order of insertion and can pick out specific courses without knowing the exact course name*/
    public CourseList() {
        this.courseList = new ArrayList<>();
    }

    public void addCourse(Course course) {
        this.courseList.add(course);
    }

    public ArrayList<Course> getCourseList() {
        return courseList;
    }

    public boolean parseCSVList(File csvFile) throws FileNotFoundException, ClassCastException, NumberFormatException, Exception {
        /*
        This method accepts a File parameter and scans through it, skipping the first line under the assumption it is a header line
        and then reading the entire line of the CSV, separating it by the comma separation value and feeding it into an array
        This array is then used to construct a new course, and add it directly to the list.
         */
        try {
            Scanner scanVar = new Scanner(csvFile).useDelimiter(",");
            scanVar.nextLine(); // Skips Header Line//
            while (scanVar.hasNextLine()) {
                ArrayList<Object> courseUploader = new ArrayList<>(List.of(scanVar.nextLine().split(",")));
                int capacityVar = -1;
                System.out.println(courseUploader.get(1));
                if (courseUploader.get(1).equals("N/A")) {
                    capacityVar = 0;
                } else {
                    capacityVar = Integer.parseInt(String.valueOf(courseUploader.get(1)));
                }
                courseList.add(new Course(
                        (String) courseUploader.get(0),
                        capacityVar,
                        (String) courseUploader.get(2),
                        (String) courseUploader.get(3),
                        (String) courseUploader.get(4),
                        (String) courseUploader.get(5),
                        Double.parseDouble(String.valueOf(courseUploader.get(6))))
                );
            }
            scanVar.close();
            return true;

        } catch (ClassCastException e) {
            throw new ClassCastException();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }

    public boolean parseResultSet(ResultSet rs) throws SQLException {
        /*This simply will iterate through the Result Set of byte arrays, read the data to get the object and then add
         * the course to the list.*/
        try {
            while (rs.next()) {
                Course tempVar = readSerializedData(rs.getBytes(1));
                this.courseList.add(tempVar);
            }
            return true;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public static Course readSerializedData(byte[] data) {
        /*Reads the byte data parsed into the method and returns the course that the data comprises of*/
        try {
            ByteArrayInputStream baip = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(baip);
            Course courseObj = (Course) ois.readObject();
            return courseObj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
