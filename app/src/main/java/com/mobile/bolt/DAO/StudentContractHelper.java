package com.mobile.bolt.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Neeraj on 2/23/2016.
 * Helper class for Image and QRcode table.
 * For table description please check the tables respective model in com.mobile.bolt/Model
 */

public class StudentContractHelper extends SQLiteOpenHelper {
    private static final String TAG = "MobileGrading";
    private static final String KEY_ASU_ID = "asuad";
    private static final String KEY_FIRST_NAME = "firstname";
    private static final String KEY_LAST_NAME = "lastname";
    private static final String[] COLUMNS = {KEY_ASU_ID, KEY_FIRST_NAME, KEY_LAST_NAME};
    private static int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MobileGradingMain";
    public StudentContractHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGE_TABLE = "CREATE TABLE imageStorage ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "asuad TEXT, " +
                "location TEXT, " +
                "graded INTEGER, " +
                "uploaded INTEGER, " +
                "qrcodesolution INTEGER, " +
                "qrcodevalues TEXT, " +
                "comments TEXT, " +
                "Grade REAL, " +
                "GradeActual REAL, " +
                "FOREIGN KEY(qrcodesolution) REFERENCES qrcode(id))";
        String CREATE_QRCODE_TABLE = "CREATE TABLE qrcode ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question TEXT, " +
                "val TEXT, " +
                "questionsolution TEXT, " +
                "Grade REAL )";
        db.execSQL(CREATE_QRCODE_TABLE);
        db.execSQL(CREATE_IMAGE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS imageStorage");
        db.execSQL("DROP TABLE IF EXISTS qrcode");
        this.onCreate(db);
    }
}