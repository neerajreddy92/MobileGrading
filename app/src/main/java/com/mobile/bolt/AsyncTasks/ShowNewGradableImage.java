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
import com.mobile.bolt.support.LoadImage;

/**
 * Created by Neeraj on 4/6/2016.
 * Loads a new non graded images for grading.
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
        mProgressDialog.setMessage("Loading Image ...");
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
        bMap = LoadImage.load(imageLocation);
        if (bMap != null) {
            publishProgress(100);
            Bitmap orientedBitmap = ExifUtil.rotateBitmap(imageLocation, bMap);
            return orientedBitmap;
        }
        return  null;
    }

}
