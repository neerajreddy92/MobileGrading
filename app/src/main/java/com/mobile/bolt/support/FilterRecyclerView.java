package com.mobile.bolt.support;

import com.mobile.bolt.Model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neeraj on 4/16/2016.
 */
public class FilterRecyclerView  {
    public static List<Student> filter(List<Student> students,String qurey,Integer status){
        if(qurey== null || qurey.matches("") || qurey.length()==0) return students;
        List<Student> filteredList = new ArrayList<>();
        for (final Student student : students) {
            if(status == 5) {
                if (student.getStudentID().contains(qurey) || student.getLastName().contains(qurey)) {
                    filteredList.add(student);
                }
            }else{
                if ((student.getStudentID().contains(qurey) || student.getLastName().contains(qurey))&&student.getStatus()==status) {
                    filteredList.add(student);
                }
            }
        }
        return filteredList;
    }
}
