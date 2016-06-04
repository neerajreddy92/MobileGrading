package com.mobile.bolt.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.mobile.bolt.Model.QrCode;
import com.mobile.bolt.Model.Student;


/**
 * Created by Neeraj on 3/3/2016.
 * Qrcode table access functions.
 * For table description please check the tables respective model in com.mobile.bolt/Model
 */
public class QRCodeDAO {
    private StudentContractHelper sHelper;
    private String TAG = "MobileGrading";

    private static final String TABLE_NAME_QRCODE = "qrcode";
    private static final String KEY_QRCODE_ID = "id";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_QUESTION_SOLUTION = "questionsolution";
    private static final String KEY_QRCODE_VALUES = "val";
    private static final String KEY_QRCODE_GRADE="Grade";
    private static final String[] COLUMNS_QRCODE = {KEY_QRCODE_ID, KEY_QUESTION, KEY_QRCODE_VALUES,KEY_QUESTION_SOLUTION,KEY_QRCODE_GRADE};

    public QRCodeDAO(Context context) {
        sHelper = new StudentContractHelper(context);
    }

    public Integer addQRCODELocation(QrCode qrCode) {
        if (verifyQuestionExists(qrCode.getQUESTION())) {
            Log.d(TAG, "addQRCODELocation: question exists");
            updateStudent(qrCode);
            Log.d(TAG, "addQRCODELocation: question updated");
            return -1;
        }
        SQLiteDatabase db = sHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, qrCode.getQUESTION());
        values.put(KEY_QRCODE_VALUES, qrCode.getVALUES());
        values.put(KEY_QUESTION_SOLUTION, qrCode.getQuestionSolution());
        values.put(KEY_QRCODE_GRADE, qrCode.getMaxGrade());
        Long val= db.insert(TABLE_NAME_QRCODE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        db.close();
        Log.d(TAG, "addQRCODELocation: " + qrCode.toString());
        return val != null ? val.intValue() : null;
    }

    public QrCode getSingleQrcodeLocation(Integer id) {

        SQLiteDatabase db = sHelper.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_NAME_QRCODE, // a. table
                        COLUMNS_QRCODE, // b. column names
                        KEY_QRCODE_ID + " = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        if (cursor == null || cursor.getCount() <= 0) {
            db.close();
            cursor.close();
            Log.d(TAG, "getSingleQrcodeLocation :No image locations available for" + id);

            return null;
        } else {
            cursor.moveToFirst();
            QrCode qrCode = new QrCode();
            qrCode.setID(Integer.parseInt(cursor.getString(0)));
            qrCode.setQUESTION(cursor.getString(1));
            qrCode.setVALUES(cursor.getString(2));
            qrCode.setQuestionSolution(cursor.getString(3));
            Log.d(TAG, "getSingleImageLocation " + qrCode.toString());
            cursor.close();
            db.close();
            return qrCode;
        }
    }

    public boolean verifyQuestionExists(String question) {
        SQLiteDatabase db = sHelper.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_NAME_QRCODE, // a. table
                        COLUMNS_QRCODE, // b. column names
                        KEY_QUESTION + " = ?", // c. selections
                        new String[]{question}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        if (cursor == null || cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            Log.d(TAG, "verifyQuestionExists: " + question + " doesent exists");
            return false;
        }
        cursor.close();
        db.close();
        Log.d(TAG, "verifyQuestionExists: " + question + " exists");
        return true;
    }
    public int updateStudent(QrCode qrCode) {
        SQLiteDatabase db = sHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, qrCode.getQUESTION());
        values.put(KEY_QRCODE_VALUES, qrCode.getVALUES());
        values.put(KEY_QUESTION_SOLUTION, qrCode.getQuestionSolution());
        values.put(KEY_QRCODE_GRADE, qrCode.getMaxGrade());
        int i = db.update(TABLE_NAME_QRCODE, //table
                values, // column/value
                KEY_QUESTION + " = ?", // selections
                new String[]{qrCode.getQUESTION()}); //selection args
        db.close();
        return i;

    }
    public QrCode getSingleQRcodeLocationOnQuestion(String question) {
        SQLiteDatabase db = sHelper.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_NAME_QRCODE, // a. table
                        COLUMNS_QRCODE, // b. column names
                        KEY_QUESTION + " = ?", // c. selections
                        new String[]{question}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        if (cursor == null || cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            Log.d(TAG, "getSingleQRcodeLocationOnQuestion: " + question + " doesent exists");
            return null;
        }
        cursor.moveToFirst();
        QrCode qrCode = new QrCode();
        qrCode.setID(Integer.parseInt(cursor.getString(0)));
        qrCode.setQUESTION(cursor.getString(1));
        qrCode.setVALUES(cursor.getString(2));
        qrCode.setQuestionSolution(cursor.getString(3));
        qrCode.setMaxGrade(Float.parseFloat(cursor.getString(4)));
        cursor.close();
        db.close();
        Log.d(TAG, "getSingleQRcodeLocationOnQuestion: " + qrCode.toString());
        return qrCode;
    }

}
