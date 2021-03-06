package com.mobile.bolt.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.DAO.StudentDao;
import com.mobile.bolt.Model.Image;
import com.mobile.bolt.Model.Student;
import com.mobile.bolt.support.SelectedClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Neeraj on 4/6/2016.
 * Saves the captured image on to the database.
 * Changes graded status to '1'.
 */
public class SaveGradedImage extends AsyncTask {
    String TAG = "MobileGrading";
    Context context;
    public SaveGradedImage(Context context,Student student){
        this.context = context;this.student =student;
    }
    Student student =null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Image image = (Image) params[0];
        image.setGraded(1);
        String val="";
        List<String> label= (List<String>) params[2];
        List<Integer> Weights= (List<Integer>) params[3];
        if(label!=null){
            for(int  i=0;i<label.size();i++){
                val=val+label.get(i)+":";
                val=val+Weights.get(i)+";";
            }
        }
        image.setQrCodeValues(val);
        File file = null;
        try {
            file = createImageFile();
            image.setLocation(file.getAbsolutePath());
            if(dispatchSaveImage((Bitmap)params[1], file)){
                Log.d(TAG, "doInBackground: image saved sucessfully");
                ImageDAO imageDao = new ImageDAO(context);
                imageDao.updateGradedStatusNow(image);
            }else
                Log.e(TAG, "doInBackground: image cannot be saved");
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: file creation threw null");
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    private boolean dispatchSaveImage(Bitmap bMap,File file) {
        // TODO: 2/25/2016 see if this process would perform well on a seperate thread.
        Log.d(TAG, "dispatchSaveImage: SAving the image");
        if (file != null) {
            FileOutputStream ostream = null;
            try {
                ostream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            bMap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
            try {
                ostream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "dispatchSaveImage: file unwritable ");
                return false;
            }
            Log.d(TAG, "dispatchSaveImage: Retured true");
            new StudentDao(context).incrementImagesGraded(SelectedClass.getInstance().getCurrentClass(), student);
            return true;
        }
        return false;
    }

    private File createImageFile() throws IOException {
        Log.i(TAG, "createImageFile: new image file name created");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.d(TAG, "createImageFile: mcurrentphotopathupdated" + image.getAbsolutePath());
        return image;
    }
}
