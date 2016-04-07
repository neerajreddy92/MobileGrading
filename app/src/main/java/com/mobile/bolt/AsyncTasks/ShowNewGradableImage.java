package com.mobile.bolt.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import com.mobile.bolt.mobilegrading.DrawingView;
import com.mobile.bolt.support.ExifUtil;

/**
 * Created by Neeraj on 4/6/2016.
 */
public class ShowNewGradableImage extends AsyncTask<Object, Integer, Bitmap> {
    String TAG = "MobileGrading";
    PowerManager.WakeLock mWakeLock;
    ProgressDialog mProgressDialog;
    Context context;
    DrawingView drawingView;
    public ShowNewGradableImage(Context context, DrawingView drawingView){
        this.context = context;
        this.drawingView = drawingView;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("A message");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Bitmap orientedBitmap) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if(orientedBitmap!=null) {
            drawingView.setPicture(orientedBitmap);
        }
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        String imageLocation = (String)params[0];
        Bitmap bMap = null;
        int count = 0;
        publishProgress(5);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.i(TAG, "displayBitmap: another photo path:" + imageLocation);
        options.inSampleSize = count++;
        try {
            bMap = BitmapFactory.decodeFile(imageLocation, options);
            Log.i(TAG, "displayBitmap: option=1");
        } catch (OutOfMemoryError e) {
            publishProgress(15);
            e.printStackTrace();
            bMap = null;
            try {
                options.inSampleSize = count++;
                bMap = BitmapFactory.decodeFile(imageLocation, options);
                Log.i(TAG, "displayBitmap: option2");
            } catch (OutOfMemoryError e1) {
                publishProgress(25);
                e1.printStackTrace();
                bMap = null;
                try {
                    options.inSampleSize = count++;
                    bMap = BitmapFactory.decodeFile(imageLocation, options);
                    Log.i(TAG, "displayBitmap: option3");
                } catch (OutOfMemoryError e2) {
                    publishProgress(25);
                    e2.printStackTrace();
                    bMap = null;

                    try {
                        options.inSampleSize = count++;
                        bMap = BitmapFactory.decodeFile(imageLocation, options);
                        Log.i(TAG, "option 4");
                    } catch (OutOfMemoryError e3) {
                        publishProgress(60);
                        e3.printStackTrace();
                        bMap = null;
                    }
                }
            }
        }
        if (bMap != null) {
            publishProgress(100);
            Bitmap orientedBitmap = ExifUtil.rotateBitmap(imageLocation, bMap);
            return orientedBitmap;
        }
        return  null;
    }

}
