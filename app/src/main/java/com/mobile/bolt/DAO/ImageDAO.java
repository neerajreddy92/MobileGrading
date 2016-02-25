package com.mobile.bolt.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobile.bolt.Model.Image;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Neeraj on 2/24/2016.
 */
public class ImageDAO {
    private StudentContractHelper sHelper;
    private String TAG="MobileGrading";

    private static final String TABLE_NAME_IMAGE="imageStorage";
    private static final String KEY_ID="id";
    private static final String KEY_ASU_ID_IMAGE = "asuad";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_GRADED = "graded";
    private static final String KEY_UPLOADED = "uploaded";
    private static final String[] COLUMNS_IMAGE = {KEY_ID, KEY_ASU_ID_IMAGE, KEY_LOCATION,KEY_GRADED,KEY_UPLOADED};

    public ImageDAO(Context context){
        sHelper=new StudentContractHelper(context);
    }

    public boolean addImageLocation(Image image) {
        //// TODO: 2/24/2016  have to handle duplicates.
        Log.d(TAG, "addImageLocation: " + image.toString());
            SQLiteDatabase db = sHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ASU_ID_IMAGE, image.getASU_ID());
            values.put(KEY_LOCATION, image.getLocation());
            values.put(KEY_GRADED,0);
            values.put(KEY_UPLOADED,0);
            db.insert(TABLE_NAME_IMAGE, // table
                    null, //nullColumnHack
                    values); // key/value -> keys = column names/ values = column values
            db.close();
        return true;
    }


    public List<Image> getAllImageLocation(String ASU_ID){
        SQLiteDatabase db = sHelper.getReadableDatabase();
        Cursor cursor =db.query(TABLE_NAME_IMAGE, // a. table
                        COLUMNS_IMAGE, // b. column names
                        "asuad = ?", // c. selections
                        new String[]{ASU_ID}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        if (cursor == null|| cursor.getCount()<=0) {
            db.close();
            cursor.close();
            Log.d(TAG, "getAllImageLocation :No image locations available for" + ASU_ID);
            return null;
        }
        else{
            List<Image> images = new LinkedList<Image>();
            Image image=null;
            if (cursor.moveToFirst()) {
                do {
                    image = new Image();
                    image.setId(Integer.parseInt(cursor.getString(0)));
                    image.setASU_ID(cursor.getString(1));
                    image.setLocation(cursor.getString(2));
                    image.setGraded(Integer.parseInt(cursor.getString(3)));
                    image.setUploaded(Integer.parseInt(cursor.getString(4)));
                    images.add(image);
                    Log.d(TAG, "getAllImageLocation: Getting image location : "+image.toString());
                } while (cursor.moveToNext());
            }cursor.close();
            db.close();
            return images;
        }
    }



    public Image getSingleImageLocation(Integer id){

        SQLiteDatabase db = sHelper.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_NAME_IMAGE, // a. table
                        COLUMNS_IMAGE, // b. column names
                        KEY_ID+" = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        if (cursor == null|| cursor.getCount()<=0) {
            db.close();
            cursor.close();
            Log.d(TAG, "getSingleImageLocation :No image locations available for" + id);

            return null;
        }
        else{
            cursor.moveToFirst();
            Image image =  new Image();
            image.setId(Integer.parseInt(cursor.getString(0)));
            image.setASU_ID(cursor.getString(1));
            image.setLocation(cursor.getString(2));
            image.setGraded(Integer.parseInt(cursor.getString(3)));
            image.setUploaded(Integer.parseInt(cursor.getString(4)));
            Log.d(TAG, "getSingleImageLocation " + image.toString());
            cursor.close();
            db.close();
            return image;
        }
    }

    public List<Image> getAllNonGradedImageLocations(String ASU_ID){
        SQLiteDatabase db = sHelper.getReadableDatabase();
        Cursor cursor =db.query(TABLE_NAME_IMAGE, // a. table
                COLUMNS_IMAGE, // b. column names
                "asuad = ?", // c. selections
                new String[]{ASU_ID}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null|| cursor.getCount()<=0) {
            db.close();
            cursor.close();
            Log.d(TAG, "getAllImageLocation :No image locations available for" + ASU_ID);
            return null;
        }
        else{
            List<Image> images = new LinkedList<Image>();
            Image image=null;
            if (cursor.moveToFirst()) {
                do {
                    image = new Image();
                    image.setId(Integer.parseInt(cursor.getString(0)));
                    image.setASU_ID(cursor.getString(1));
                    image.setLocation(cursor.getString(2));
                    image.setGraded(Integer.parseInt(cursor.getString(3)));
                    image.setUploaded(Integer.parseInt(cursor.getString(4)));
                    if(image.getGraded()==0) {
                        images.add(image);
                    }
                    Log.d(TAG, "getAllImageLocation: Getting image location : "+image.toString());
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return images;
        }

    }

    public List<Image> getAllNonUploadedImageLocations(String ASU_ID){
        SQLiteDatabase db = sHelper.getReadableDatabase();
        Cursor cursor =db.query(TABLE_NAME_IMAGE, // a. table
                COLUMNS_IMAGE, // b. column names
                "asuad = ?", // c. selections
                new String[]{ASU_ID}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null|| cursor.getCount()<=0) {
            db.close();
            cursor.close();
            Log.d(TAG, "getAllNonUploadedImageLocations :No image locations available for" + ASU_ID);
            return null;
        }
        else{
            List<Image> images = new LinkedList<Image>();
            Image image=null;
            if (cursor.moveToFirst()) {
                do {
                    image = new Image();
                    image.setId(Integer.parseInt(cursor.getString(0)));
                    image.setASU_ID(cursor.getString(1));
                    image.setLocation(cursor.getString(2));
                    image.setGraded(Integer.parseInt(cursor.getString(3)));
                    image.setUploaded(Integer.parseInt(cursor.getString(4)));
                    if(image.getGraded()==1 && image.getUploaded()==0) {
                        images.add(image);
                    }
                    Log.d(TAG, "getAllImageLocation: Getting image location : "+image.toString());
                } while (cursor.moveToNext());
            }cursor.close();
            db.close();
            return images;
        }

    }
    public int updateGradedStatusNow(Image image) {
        SQLiteDatabase db = sHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GRADED, image.getGraded());
        values.put(KEY_LOCATION, image.getLocation());
        int i = db.update(TABLE_NAME_IMAGE, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(image.getId())}); //selection args
        db.close();
        return i;
    }

    public int updateUploadStatusNow(Image image) {
        SQLiteDatabase db = sHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UPLOADED, image.getUploaded());
        int i = db.update(TABLE_NAME_IMAGE, //table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(image.getId())}); //selection args
        db.close();
        return i;
    }
}
