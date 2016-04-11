package com.mobile.bolt.Model;

/**
 * Created by Neeraj on 2/23/2016.
 */
public class Student {
    private String StudentID;
    private String FirstName;
    private String LastName;
    private Integer Status;

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public Student(){
        StudentID ="";
        FirstName ="";
        LastName = "";
        Status=0;
    }
    public Student(String id,String firstName,String lastName,Integer Status){
        this.StudentID =id;
        this.FirstName =firstName;
        this.LastName = lastName;
        this.Status = Status;
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
        return StudentID+" "+FirstName+" "+ LastName+" status "+Status;
    }
}
