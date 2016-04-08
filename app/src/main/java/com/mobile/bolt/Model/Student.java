package com.mobile.bolt.Model;

/**
 * Created by Neeraj on 2/23/2016.
 */
public class Student {
    private String StudentID;
    private String FirstName;
    private String LastName;
    public Student(){
        StudentID ="";
        FirstName ="";
        LastName = "";
    }
    public String getFirstName() {
        return FirstName;
    }
    public void setFirstName(String firstName) {
        FirstName = firstName;
    }
    public String getLastName() {
        return LastName;
    }
    public void setLastName(String lastName) {
        LastName = lastName;
    }
    public String getStudentID() {
        return StudentID;
    }
    public void setStudentID(String studentID) {
        StudentID = studentID;
    }
    @Override
    public String toString(){
        return StudentID+" "+FirstName+" "+ LastName;
    }
}
