package com.mobile.bolt.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Neeraj on 3/6/2016.
 * Class prevents out of memory exception while loading bitmap onto the screen.
 */
public class LoadImage {
    private static String TAG= "MobileGrading";
    public static Bitmap load(String imageLocation){
        Bitmap bMap = null;
        int count = 0;          // Increasing count changes the way the bitmap is loaded. 
        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.i(TAG, "LoadImage: Loading Image from path: ... " + imageLocation);
        options.inSampleSize = count++;
        try {
            bMap = BitmapFactory.decodeFile(imageLocation, options);
            Log.i(TAG, "LoadImage: option=1 Loading Image...");
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bMap = null;
            try {
                options.inSampleSize = count++;
                bMap = BitmapFactory.decodeFile(imageLocation, options);
                Log.i(TAG, "LoadImage: option2");
            } catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                bMap = null;
                try {
                    options.inSampleSize = count++;
                    bMap = BitmapFactory.decodeFile(imageLocation, options);
                    Log.i(TAG, "LoadImage: option3");
                } catch (OutOfMemoryError e2) {
                    e2.printStackTrace();
                    bMap = null;
                    try {
                        options.inSampleSize = count++;
                        bMap = BitmapFactory.decodeFile(imageLocation, options);
                        Log.i(TAG, "LoadImage: option 4");
                    } catch (OutOfMemoryError e3) {
                        e3.printStackTrace();
                        bMap = null;
                    }
                }
            }
        }
        return  bMap;
    }
}
