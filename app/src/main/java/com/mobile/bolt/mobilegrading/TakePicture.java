package com.mobile.bolt.mobilegrading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import com.mobile.bolt.AsyncTasks.GenQRCode;
import com.mobile.bolt.support.PictureValues;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Neeraj on 4/15/2016.
 */
public class TakePicture{
    Context context;
    Activity activity;
    private String TAG = "MobileGrading";
    String mCurrentPhotoPath;
    static String anotherPhotoPath;
    String ASUAD = null;
    final int REQUEST_TAKE_PHOTO = 1;

    public TakePicture(Context context,Activity activity,String ASUAD) {
        this.context = context;
        this.ASUAD =ASUAD;
        this.activity = activity;
        dispatchTakePictureIntent();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PictureValues.getInstance().setASUAD(ASUAD);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            Log.i(TAG, "dispatchTakePictureIntent: Entering take picture intent");
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "dispatchTakePictureIntent: error while creating a file");
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.i(TAG, "dispatchTakePictureIntent: photo file not null");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                if (photoFile.exists()) {
                    Log.i(TAG, "createImageFile:" + anotherPhotoPath);
                    Log.i(TAG, "dispatchTakePictureIntent: picture file created");
                }

            }
        } else {
            Log.e(TAG, "dispatchTakePictureIntent: getActivity() doesent work");
        }
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        new GenQRCode(context).execute(anotherPhotoPath, ASUAD); //starting async task to genrate qr code.
//    }


    private File createImageFile() throws IOException {
        // Create an image file name
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

        // Save a file: path for use with ACTION_VIEW intents
        anotherPhotoPath = image.getAbsolutePath();
        PictureValues.getInstance().setPhotoPath(anotherPhotoPath);
        Log.i(TAG, "createImageFile:" + anotherPhotoPath);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}
