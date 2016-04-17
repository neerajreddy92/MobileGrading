package com.mobile.bolt.support;

import android.util.Log;

import com.mobile.bolt.Model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neeraj on 4/16/2016.
 */
public class FilterRecyclerView {
    private static final String TAG = "MobileGrading";

    public static List<Student> filter(List<Student> students, String qurey, Integer status) {
        Log.d(TAG, "filter: " + qurey + " " + status);
        List<Student> filteredList = new ArrayList<>();
        if(qurey == null) return students;
        for (final Student student : students) {
            if (status == 0) {
                if (qurey == null || qurey.matches("") || qurey.length() == 0) {
                    return students;
                }
                if (student.getStudentID().contains(qurey) || student.getLastName().contains(qurey)) {
                    filteredList.add(student);
                }
            } else {
                if (qurey == null || qurey.matches("") || qurey.length() == 0) {
                    if(student.getStatus() == status) filteredList.add(student);
                }else if((student.getStudentID().contains(qurey) || student.getLastName().contains(qurey)) && student.getStatus() == status)
                    filteredList.add(student);
            }
        }
        return filteredList;
    }
}
