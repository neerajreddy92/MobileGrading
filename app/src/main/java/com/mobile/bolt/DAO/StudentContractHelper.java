package com.mobile.bolt.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.mobile.bolt.Model.Student;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Neeraj on 2/23/2016.
 */

public class StudentContractHelper extends SQLiteOpenHelper {
    private static final String TAG="MobileGrading";
    private static final String TABLE_NAME="student";
    private static final String KEY_ASU_ID = "asuad";
    private static final String KEY_FIRST_NAME = "firstname";
    private static final String KEY_LAST_NAME = "lastname";
    private static final String[] COLUMNS = {KEY_ASU_ID,KEY_FIRST_NAME,KEY_LAST_NAME};
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "studentDB";

    public StudentContractHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENT_TABLE = "CREATE TABLE student ( " +
                "asuad TEXT PRIMARY KEY, " +
                "firstname TEXT, "+
                "lastname TEXT )";

        db.execSQL(CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS student");

        this.onCreate(db);
    }

    public void addStudent(Student student) {
        Log.d(TAG,"add Student: "+student.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ASU_ID,student.getStudentID());
        values.put(KEY_FIRST_NAME, student.getFirstName());
        values.put(KEY_LAST_NAME, student.getLastName());

        db.insert(TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        db.close();
    }

    public Student getStudent(String id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_NAME, // a. table
                        COLUMNS, // b. column names
                        KEY_ASU_ID+" = ?", // c. selections
                        new String[] { id }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();
        Student student=new Student();
        student.setStudentID(cursor.getString(0));
        student.setFirstName(cursor.getString(1));
        student.setLastName(cursor.getString(2));
        Log.d(TAG, "getStudent "+student.toString());
        return student;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new LinkedList<Student>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Student student=null;
        if (cursor.moveToFirst()) {
            do {
                student = new Student();
                student.setStudentID(cursor.getString(0));
                student.setFirstName(cursor.getString(1));
                student.setLastName(cursor.getString(2));
                students.add(student);
            } while (cursor.moveToNext());
        }
        Log.d(TAG,"getAllStudents"+student.toString());
        return students;
    }

    public int updateBook(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ASU_ID,student.getStudentID());
        values.put(KEY_FIRST_NAME, student.getFirstName());
        values.put(KEY_FIRST_NAME, student.getLastName());
        int i = db.update(TABLE_NAME, //table
                values, // column/value
                KEY_ASU_ID+" = ?", // selections
                new String[] {student.getStudentID() }); //selection args
        db.close();
        return i;

    }

    // Deleting single Student
    public void deleteBook(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,
                KEY_ASU_ID + " = ?",
                new String[]{student.getStudentID()});
        db.close();
        Log.d(TAG,"deleteBook"+student.toString());
    }
}
