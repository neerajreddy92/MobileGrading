package com.mobile.bolt.DAO;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import com.mobile.bolt.Model.Student;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Neeraj on 4/10/2016.
 */
public class StudentDao extends SQLiteOpenHelper{
    private static final String TAG = "MobileGrading";
    private static int DATABASE_VERSION = 1;
    private static final String KEY_ASU_ID = "asuad";
    private static final String KEY_FIRST_NAME = "firstname";
    private static final String KEY_LAST_NAME = "lastname";
    private static final String KEY_STATUS = "status";
    private static final String TABEL_NAME = "tabelName";
    private static StudentDao pointsDAO=null;
    private static final String DATABASE_NAME = "MobileGradingStudent";
    // TODO: 4/10/2016 change this to work with versions below jellybean.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static StudentDao getInstance(Context context){
        if(pointsDAO==null){
            pointsDAO=new StudentDao(context);
            pointsDAO.setWriteAheadLoggingEnabled(true);
        }
        return pointsDAO;
    }
    public StudentDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //some table so as the on upgrade works fine.
        String CREATE_STUDENT_TABLE_Names = "CREATE TABLE classes ( " +
                "tabelName TEXT PRIMARY KEY )";
        db.execSQL(CREATE_STUDENT_TABLE_Names);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS classes");
        this.onCreate(db);
    }
    public List<String> getTabels(){
        List<String> clases = new LinkedList<String>();
        String query = "SELECT  * FROM classes" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                clases.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getTabels:retreived tabel  names "+clases.toString());
        cursor.close();
        db.close();
        return clases;
    }

    public  Boolean createTable(String tabelName,List<Student> students){
        if(tabelName!=null) {
            String CREATE_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS " + tabelName + " ( " +
                    "asuad TEXT PRIMARY KEY, " +
                    "firstname TEXT, " +
                    "lastname TEXT, " +
                    "status INTEGER )";
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(CREATE_STUDENT_TABLE);
            ContentValues values = new ContentValues();
            values.put(TABEL_NAME, tabelName);
            db.insert("classes", // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            Log.d(TAG, "createTable: creating table" + tabelName);
            if(students!=null || !students.isEmpty()){
                for(Student student : students){
                    values = new ContentValues();
                    values.put(KEY_ASU_ID, student.getStudentID());
                    values.put(KEY_FIRST_NAME, student.getFirstName());
                    values.put(KEY_LAST_NAME, student.getLastName());
                    values.put(KEY_STATUS, student.getStatus());
                    db.insert(tabelName, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                    Log.d(TAG, "createTable: adding student "+student.toString());
                }
            }
            db.close();
            return true;
        }
        return false;
    }

    public Boolean createJustTable(String tabelName){
        if(tabelName!=null) {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put(TABEL_NAME, tabelName);
            db.insert("classes", // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            Log.d(TAG, "createJustTable: creating table" + tabelName);
            db.close();
            return true;
        }
        return false;
    }
    public List<Student> getAllStudents(String tabelName) {
        if (tabelName != null) {
            List<Student> students = new LinkedList<Student>();
            String query = "SELECT  * FROM " + tabelName;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            Student student = null;
            if (cursor.moveToFirst()) {
                do {
                    student = new Student();
                    student.setStudentID(cursor.getString(0));
                    student.setFirstName(cursor.getString(1));
                    student.setLastName(cursor.getString(2));
                    student.setStatus(Integer.valueOf(cursor.getString(3)));
                    students.add(student);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "getAllStudents" + student.toString());
            cursor.close();
            db.close();
            return students;
        }
        return null;
    }

    public boolean addStudent(String tabelName,Student student ){
        Log.d(TAG, "add Student: " + student.toString());
        if (tabelName!=null){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ASU_ID, student.getStudentID());
            values.put(KEY_FIRST_NAME, student.getFirstName());
            values.put(KEY_LAST_NAME, student.getLastName());
            values.put(KEY_STATUS, student.getStatus());
            db.insert(tabelName, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            db.close();
            return true;
        }
        return false;
    }
}
