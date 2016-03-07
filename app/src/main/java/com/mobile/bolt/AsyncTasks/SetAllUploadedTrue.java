package com.mobile.bolt.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.Model.Image;
import java.util.List;

/**
 * Created by Neeraj on 3/6/2016.
 */
public class SetAllUploadedTrue extends AsyncTask<List<Image>,Integer,Boolean> {
    Context context=null;
    private String TAG= "MobileGrading";
    public SetAllUploadedTrue(Context context){
        this.context=context;
    }

    @Override
    protected Boolean doInBackground(List<Image>... params) {
     try{
        ImageDAO imageDAO = new ImageDAO(context);
        List<Image> images = params[0];
        for (Image image : images) {
            image.setUploaded(1);
            imageDAO.updateUploadStatusNow(image);
        }
         Log.d(TAG, "doInBackground: setting all image values uploaded status true");
        return true;
    }catch (Exception e){
         e.printStackTrace();
         Log.d(TAG, "doInBackground: unable to set all image values uploaded status true ");
     }
    return false;

    }
}
