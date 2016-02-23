package com.mobile.bolt.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mobile.bolt.Model.Student;

import java.util.List;

/**
 * Created by Neeraj on 2/23/2016.
 */
public class StudentContractHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StudentContract.FeedEntry.TABLE_NAME + " (" +
                    StudentContract.FeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP+
                    StudentContract.FeedEntry.COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                    StudentContract.FeedEntry.COLUMN_NAME_LAST_NAME + TEXT_TYPE +
     " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StudentContract.FeedEntry.TABLE_NAME;
    public StudentContractHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        onCreate(this.getWritableDatabase());
    }
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(SQL_CREATE_ENTRIES);
        Log.v("MobileGrading", "onCreate: New Table Created");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Student getStudentBasedOnId(String StudentID){
        Student std = new Student();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor res = db.rawQuery("select * from "+ StudentContract.FeedEntry.TABLE_NAME +" where " + StudentContract.FeedEntry.COLUMN_NAME_ENTRY_ID + " = " + StudentID + "", null);
            std.setStudentID(res.getString(res.getColumnIndex(StudentContract.FeedEntry.COLUMN_NAME_ENTRY_ID)));
            std.setFirstName(res.getString(res.getColumnIndex(StudentContract.FeedEntry.COLUMN_NAME_FIRST_NAME)));
            std.setLastName(res.getString(res.getColumnIndex(StudentContract.FeedEntry.COLUMN_NAME_LAST_NAME)));
        }catch (Exception e){
            Log.e("MobileGrading", "getStudentBasedOnId: Query returned null");
            return null;
        }
        return std;
    }

    public Boolean insertStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
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
        Log.v("MobileGrading", "insertStudent: new table row created row number "+newRowId);
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
