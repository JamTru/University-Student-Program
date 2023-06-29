package com.example.assignment_two;

public class VariableStorageSingleton {
    /*The Singleton Class was created to have a way of accessing variables across multiple scenes
     * like a global variable, but still following OOP practices. This is essentially the equivalent
     * of storing variables inside a cookie or a session on a web browser.*/
    private static VariableStorageSingleton single_instance = null;
    private StudentAccount std;
    /*The student account is saved upon logging in to perform actions like
    withdraw and enrol across multiple scenes*/
    private String username;
    /*The username is kept in particular for performing SQL queries, as it is the primary key. Logging it
     * is the least amount of effort to be able to search through the database for the right user*/
    private String password;
    /*I decided to simply store the password as a variable rather than having to query it from database as
     * the database would've been more work in terms of computation and implementation.*/
    private String fontChoice;
    private int fontSize;

    /*The fontchoice and fontsize is saved so they can be applied to the dashboard scene anytime it is initialised*/
    private VariableStorageSingleton() {
        std = null;
        username = null;
        fontChoice = null;
        fontSize = Integer.parseInt("1");
        password = null;
    }

    public static synchronized VariableStorageSingleton getInstance() {
        if (single_instance == null) {
            single_instance = new VariableStorageSingleton();
        }
        return single_instance;
    }

    public void setStd(StudentAccount std) {
        this.std = std;
    }

    public StudentAccount getStd() {
        return std;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFontChoice() {
        return fontChoice;
    }

    public void setFontChoice(String fontChoice) {
        this.fontChoice = fontChoice;
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
