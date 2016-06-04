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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Neeraj on 4/10/2016.
 * Helper class for student table , emails table and list of class table.
 * Each class has a separate table with respective students in it.
 * Model for all the class tables can be found at Model/student
 * For table description please check the tables respective model in com.mobile.bolt/Model
 * All the listed classes are stored in "classes" table.
 * saved email addresses are stored in "emails" table.
 *
 */
public class StudentDao extends SQLiteOpenHelper {
    private static final String TAG = "MobileGrading";
    private static int DATABASE_VERSION = 1;
    private static final String KEY_ASU_ID = "asuad";
    private static final String KEY_FIRST_NAME = "firstname";
    private static final String KEY_LAST_NAME = "lastname";
    private static final String KEY_STATUS = "status";
    private static final String KEY_IMAGES_TAKEN = "taken";
    private static final String KEY_IMAGES_GRADED = "graded";
    private static final String TABEL_NAME = "tabelName";
    private static StudentDao pointsDAO = null;
    private static final String DATABASE_NAME = "MobileGradingStudent";
    private static final String[] COLUMNS = {KEY_ASU_ID, KEY_FIRST_NAME, KEY_LAST_NAME, KEY_STATUS, KEY_IMAGES_TAKEN, KEY_IMAGES_GRADED};

    // TODO: 4/10/2016 change this to work with versions below jellybean.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static StudentDao getInstance(Context context) {
        if (pointsDAO == null) {
            pointsDAO = new StudentDao(context);
            pointsDAO.setWriteAheadLoggingEnabled(true);
        }
        return pointsDAO;
    }

    public StudentDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STUDENT_TABLE_Names = "CREATE TABLE classes ( " +
                "tabelName TEXT PRIMARY KEY )";
        String CREATE_EMAIL_ADDRESSES = "CREATE TABLE emails ( " +
                "names TEXT PRIMARY KEY )";
        db.execSQL(CREATE_STUDENT_TABLE_Names);
        db.execSQL(CREATE_EMAIL_ADDRESSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS classes");
        db.execSQL("DROP TABLE IF EXISTS emails");
        this.onCreate(db);
    }
    //Function to return all classes names.
    public List<String> getTabels() {
        List<String> clases = new LinkedList<String>();
        String query = "SELECT  * FROM classes";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                clases.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getTabels:retreived tabel  names " + clases.toString());
        cursor.close();
        db.close();
        return clases;
    }
    //Function to return all classes names in a string array.
    public String[] getTabelsString() {
        List<String> clases = new LinkedList<String>();
        String query = "SELECT  * FROM classes";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                clases.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getTabels:retreived tabel  names " + clases.toString());
        cursor.close();
        db.close();
        String[] emai = new String[clases.size()];
        for (int i = 0; i < clases.size(); i++) emai[i] = clases.get(i);
        return emai;
    }
    //Delete a single class.
    public void deleteStudentTable(String tabelName) {
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete("classes",
                    "tabelName" + " = ?",
                    new String[]{tabelName});
            Log.d(TAG, "delete table" + tabelName);
            String drop = "DROP TABLE "+tabelName;
            db.execSQL(drop);
            db.close();
        }
    }

    public String[] getEmails() {
        List<String> emails = new LinkedList<String>();
        String query = "SELECT  * FROM emails";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                emails.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getEmails:retreived emails " + emails.toString());
        cursor.close();
        db.close();
        String[] emai = new String[emails.size()];
        for (int i = 0; i < emails.size(); i++) emai[i] = emails.get(i);
        return emai;
    }

    public Boolean addEmail(String email) {
        if (email != null) {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("names", email);
            db.insert("emails", // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            Log.d(TAG, "addEmail: adding email " + email);
            db.close();
            return true;
        }
        return false;
    }

    public Boolean createTable(String tabelName, List<Student> students) {
        if (tabelName != null) {
            String CREATE_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS " + tabelName + " ( " +
                    "asuad TEXT PRIMARY KEY, " +
                    "firstname TEXT, " +
                    "lastname TEXT, " +
                    "status INTEGER, " +
                    "taken INTEGER, " +
                    "graded INTEGER )";
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(CREATE_STUDENT_TABLE);
            ContentValues values = new ContentValues();
            values.put(TABEL_NAME, tabelName);
            db.insert("classes", // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            Log.d(TAG, "createTable: creating table" + tabelName);
            if (students != null && !students.isEmpty()) {
                for (Student student : students) {
                    values = new ContentValues();
                    values.put(KEY_ASU_ID, student.getStudentID());
                    values.put(KEY_FIRST_NAME, student.getFirstName());
                    values.put(KEY_LAST_NAME, student.getLastName());
                    values.put(KEY_STATUS, student.getStatus());
                    values.put(KEY_IMAGES_TAKEN, student.getImagesTaken());
                    values.put(KEY_IMAGES_GRADED, student.getImagesGraded());
                    db.insert(tabelName, // table
                            null, //nullColumnHack
                            values); // key/value -> keys = column names/ values = column values
                    Log.d(TAG, "createTable: adding student " + student.toString());
                }
            }
            db.close();
            return true;
        }
        return false;
    }

    public Boolean createJustTable(String tabelName) {
        if (tabelName != null) {
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
            if (cursor == null || cursor.getCount() <= 0) {
                db.close();
                cursor.close();
                Log.d(TAG, "getAllStudents: no students present");
                return new ArrayList<>();
            }
            if (cursor.moveToFirst()) {
                do {
                    student = new Student();
                    student.setStudentID(cursor.getString(0));
                    student.setFirstName(cursor.getString(1));
                    student.setLastName(cursor.getString(2));
                    student.setStatus(Integer.valueOf(cursor.getString(3)));
                    student.setImagesTaken(Integer.valueOf(cursor.getString(4)));
                    student.setImagesGraded(Integer.valueOf(cursor.getString(5)));
                    students.add(student);
                    Log.d(TAG, "getAllStudents" + student.toString());
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return students;
        }
        return null;
    }

    public boolean addStudent(String tabelName, Student student) {
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ASU_ID, student.getStudentID());
            values.put(KEY_FIRST_NAME, student.getFirstName());
            values.put(KEY_LAST_NAME, student.getLastName());
            values.put(KEY_STATUS, student.getStatus());
            values.put(KEY_IMAGES_TAKEN, student.getImagesTaken());
            values.put(KEY_IMAGES_GRADED, student.getImagesGraded());
            db.insert(tabelName, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            db.close();
            Log.d(TAG, "add Student: " + student.toString());
            return true;
        }
        return false;
    }

    public boolean updateStudent(String tabelName, Student student) {
        Log.d(TAG, "update Student: " + student.toString());
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ASU_ID, student.getStudentID());
            values.put(KEY_FIRST_NAME, student.getFirstName());
            values.put(KEY_LAST_NAME, student.getLastName());
            values.put(KEY_STATUS, student.getStatus());
            values.put(KEY_IMAGES_TAKEN, student.getImagesTaken());
            values.put(KEY_IMAGES_GRADED, student.getImagesGraded());
            int i = db.update(tabelName, //table
                    values, // column/value
                    KEY_ASU_ID + " = ?", // selections
                    new String[]{student.getStudentID()}); //selection args
            db.close();
            return true;
        }
        return false;
    }

    public boolean updateStatus(String tabelName, Student student) {
        Log.d(TAG, "update status: " + student.toString());
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ASU_ID, student.getStudentID());
            values.put(KEY_STATUS, student.getStatus());
            int i = db.update(tabelName, //table
                    values, // column/value
                    KEY_ASU_ID + " = ?", // selections
                    new String[]{student.getStudentID()}); //selection args
            db.close();
            return true;
        }
        return false;
    }

    public boolean setImagesGraded(String tabelName, Student student) {
        Log.d(TAG, "setImagesGraded: " + student.toString());
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ASU_ID, student.getStudentID());
            values.put(KEY_IMAGES_GRADED, student.getImagesGraded());
            int i = db.update(tabelName, //table
                    values, // column/value
                    KEY_ASU_ID + " = ?", // selections
                    new String[]{student.getStudentID()}); //selection args
            db.close();
            return true;
        }
        return false;
    }

    public boolean setImagesTaken(String tabelName, Student student) {
        Log.d(TAG, "setImagesTaken: " + student.toString());
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ASU_ID, student.getStudentID());
            values.put(KEY_IMAGES_TAKEN, student.getImagesTaken());
            int i = db.update(tabelName, //table
                    values, // column/value
                    KEY_ASU_ID + " = ?", // selections
                    new String[]{student.getStudentID()}); //selection args
            db.close();
            return true;
        }
        return false;
    }

    public boolean setImagesTakenAndGraded(String tabelName, Student student) {
        Log.d(TAG, "setImagesTaken: " + student.toString());
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ASU_ID, student.getStudentID());
            values.put(KEY_IMAGES_GRADED, student.getImagesGraded());
            values.put(KEY_IMAGES_TAKEN, student.getImagesTaken());
            int i = db.update(tabelName, //table
                    values, // column/value
                    KEY_ASU_ID + " = ?", // selections
                    new String[]{student.getStudentID()}); //selection args
            db.close();
            return true;
        }
        return false;
    }

    public boolean incrementImagesGraded(String tabelName, Student student) {
        Log.d(TAG, "incrementImagesGraded: " + student.toString());
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor =
                    db.query(tabelName, // a. table
                            COLUMNS, // b. column names
                            KEY_ASU_ID + " = ?", // c. selections
                            new String[]{student.getStudentID()}, // d. selections args
                            null, // e. group by
                            null, // f. having
                            null, // g. order by
                            null); // h. limit

            if (cursor == null || cursor.getCount() <= 0) {
                db.close();
                cursor.close();
                return false;
            }
            if (cursor.moveToFirst()) {
                Integer val = Integer.parseInt(cursor.getString(5));
                Log.d(TAG, "incrementImagesTaken: from" + val + " to: " + (val + 1));
                ContentValues values = new ContentValues();
                values.put(KEY_ASU_ID, student.getStudentID());
                values.put(KEY_IMAGES_GRADED, val + 1);
                int i = db.update(tabelName, //table
                        values, // column/value
                        KEY_ASU_ID + " = ?", // selections
                        new String[]{student.getStudentID()}); //selection args
            }
            db.close();
            cursor.close();
            return true;
        }
        return false;
    }

    public void deleteStudent(String tabelName, Student student) {
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(tabelName,
                    KEY_ASU_ID + " = ?",
                    new String[]{student.getStudentID()});
            db.close();
            Log.d(TAG, "deleteStudent" + student.toString());
        }
    }

    public boolean incrementImagesTaken(String tabelName, Student student) {
        Log.d(TAG, "incrementImagesTaken: " + student.toString());
        if (tabelName != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor =
                    db.query(tabelName, // a. table
                            COLUMNS, // b. column names
                            KEY_ASU_ID + " = ?", // c. selections
                            new String[]{student.getStudentID()}, // d. selections args
                            null, // e. group by
                            null, // f. having
                            null, // g. order by
                            null); // h. limit

            if (cursor == null || cursor.getCount() <= 0) {
                db.close();
                cursor.close();
                return false;
            }
            if (cursor.moveToFirst()) {
                Integer val = Integer.parseInt(cursor.getString(4));
                Log.d(TAG, "incrementImagesTaken: from" + val + " to: " + (val + 1));
                ContentValues values = new ContentValues();
                values.put(KEY_ASU_ID, student.getStudentID());
                values.put(KEY_IMAGES_TAKEN, val + 1);
                int i = db.update(tabelName, //table
                        values, // column/value
                        KEY_ASU_ID + " = ?", // selections
                        new String[]{student.getStudentID()}); //selection args
            }
            db.close();
            cursor.close();
            return true;
        }
        return false;
    }
}
