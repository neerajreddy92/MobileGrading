package com.mobile.bolt.support;

import android.content.Context;

import com.mobile.bolt.DAO.StudentDao;
import com.mobile.bolt.Model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neeraj on 4/10/2016.
 */
public class StudentFeeder {
    public static void feed(Context context){
        StudentDao studentDAO = new StudentDao(context);
        List<Student> students = new ArrayList<>();
        students.add(new Student("130","ghj","fghj",3,0,0));
        students.add(new Student("131","ghj","fghj",1,3,0));
        students.add(new Student("132","ghj","fghj",2,0,0));
        students.add(new Student("133","ghj","fghj",1,3,2));
        studentDAO.createTable("newTable",students);
        studentDAO.addEmail("reddy.neeraj007@gmail.com");
    }
}
