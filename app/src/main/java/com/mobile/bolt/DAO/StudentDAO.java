package com.mobile.bolt.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobile.bolt.Model.Student;
import java.util.List;


/**
 * Created by Neeraj on 2/23/2016.
 */
public class StudentDAO {
    StudentContractHelper mDbHelper;
    public StudentDAO(Context context) {
        mDbHelper = new StudentContractHelper(context);
    }
    public Student getStudentBasedOnId(String StudentID){
        Student std = new Student();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            Cursor res = db.rawQuery("select * from contacts where " + StudentContract.FeedEntry.COLUMN_NAME_ENTRY_ID + " = " + StudentID + "", null);
            std.setStudentID(res.getString(res.getColumnIndex(StudentContract.FeedEntry.COLUMN_NAME_ENTRY_ID)));
            std.setFirstName(res.getString(res.getColumnIndex(StudentContract.FeedEntry.COLUMN_NAME_FIRST_NAME)));
            std.setLastName(res.getString(res.getColumnIndex(StudentContract.FeedEntry.COLUMN_NAME_LAST_NAME)));
        }catch (Exception e){
            Log.e("FATAL ERROR", "getStudentBasedOnId: Query returned null");
            return null;
        }
        return std;
    }
    public Boolean insertStudent(Student student){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Insert student into the database
        ContentValues values = new ContentValues();
        values.put(StudentContract.FeedEntry.COLUMN_NAME_ENTRY_ID, student.getStudentID());
        values.put(StudentContract.FeedEntry.COLUMN_NAME_FIRST_NAME,student.getFirstName());
        values.put(StudentContract.FeedEntry.COLUMN_NAME_LAST_NAME, student.getLastName());
        long newRowId;
        newRowId = db.insert(
                StudentContract.FeedEntry.TABLE_NAME,
                null,
                values);
        return newRowId>0;
    }

    public Boolean insertMultipleStudents(List<Student> listStudents){
        Boolean result=true;
        for(Student std : listStudents){
            if(!insertStudent(std)){
                result=false;
            }
        }
        return result;
    }
}
