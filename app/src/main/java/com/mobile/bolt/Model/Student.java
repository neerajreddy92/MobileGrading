package com.mobile.bolt.Model;

import com.itextpdf.text.*;

/**
 * Created by Neeraj on 2/23/2016.
 * Model for student table.
 * Images taken: Number of images taken for this particular student.
 * Images graded: Number of images graded for this particular student.
 */
public class Student {
    private String StudentID;
    private String FirstName;
    private String LastName;
    private Integer Status;
    private Integer ImagesTaken;
    private Integer ImagesGraded;

    public Integer getImagesGraded() {
        return ImagesGraded;
    }

    public void setImagesGraded(Integer imagesGraded) {
        ImagesGraded = imagesGraded;
    }

    public Integer getImagesTaken() {
        return ImagesTaken;
    }

    public void setImagesTaken(Integer imagesTaken) {
        ImagesTaken = imagesTaken;
    }

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
        ImagesTaken=0;
        ImagesGraded=0;
    }
    public Student(String id,String firstName,String lastName,Integer Status,Integer ImagesTaken,Integer ImagesGraded){
        this.StudentID =id;
        this.FirstName =firstName;
        this.LastName = lastName;
        this.Status = Status;
        this.ImagesTaken=ImagesTaken;
        this.ImagesGraded=ImagesGraded;
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
        return StudentID+" "+FirstName+" "+ LastName+" status "+Status+" Images Taken "+ImagesTaken+ " Images Graded "+ ImagesGraded;
    }
}
