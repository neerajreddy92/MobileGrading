package com.mobile.bolt.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.Model.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Neeraj on 4/6/2016.
 */
public class SaveGradedImage extends AsyncTask {
    String TAG = "MobileGrading";
    Context context;
    public SaveGradedImage(Context context){
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Image image = (Image) params[0];
        image.setGraded(1);
        image.setQrCodeValues((String)params[1]);
        File file = null;
        try {
            file = createImageFile();
            image.setLocation(file.getAbsolutePath());
            if(dispatchSaveImage((Bitmap)params[2], file)){
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
            return true;
        }
        return false;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        //Do not make function public.
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
