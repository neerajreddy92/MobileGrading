package com.mobile.bolt.AsyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.Model.Image;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by Neeraj on 4/6/2016.
 */
public class GenQRCode extends AsyncTask <String,Integer,Image>{
    Context context;
    String TAG = "MobileGrading";
    @Override
    protected Image doInBackground(String... params) {
        Image image=null;
        String photoPath = params[0];
        Bitmap bMap = null;
        int count=0;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.i(TAG, "getQRCode: another photo path:" + photoPath);
        Log.i(TAG, "getQRCode: reached here");
        options.inSampleSize = count++;
        try {
            bMap = BitmapFactory.decodeFile(photoPath, options);
            Log.i(TAG, "getQRCode: option=1");
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bMap = null;
            try {
                options.inSampleSize = count++;
                bMap = BitmapFactory.decodeFile(photoPath, options);
                Log.i(TAG, "getQRCode: option2");
            } catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                bMap = null;
                try {
                    options.inSampleSize = count++;
                    bMap = BitmapFactory.decodeFile(photoPath, options);
                    Log.i(TAG, "getQRCode: option3");
                } catch (OutOfMemoryError e2) {
                    e2.printStackTrace();
                    bMap = null;

                    try {
                        options.inSampleSize = count++;
                        bMap = BitmapFactory.decodeFile(photoPath, options);
                        Log.i(TAG, "option 4");
                    } catch (OutOfMemoryError e3) {
                        e3.printStackTrace();
                        bMap = null;
                    }
                }
            }
        }
        String contents = null;
        if (bMap != null) {
            Log.i(TAG, "getQRCode: Entering to qr processor");
            int[] intArray;

            Result result = null;
            Reader reader = new MultiFormatReader();
            intArray = new int[bMap.getWidth()*bMap.getHeight()];
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                Log.i(TAG, "getQRCode: reading reults");
                result = reader.decode(bitmap);

            } catch (NotFoundException e) {
                Log.e(TAG, "getQRCode: Not found exception");
                e.printStackTrace();
                options.inSampleSize = count++;
                bMap = BitmapFactory.decodeFile(photoPath, options);
                intArray = new int[bMap.getWidth()*bMap.getHeight()];
                bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try{

                    result = reader.decode(bitmap);

                }catch (Exception e1){
                    e1.printStackTrace();
                    Log.e(TAG, "getQRCode: Not found exception: try1");
                    options.inSampleSize = count++;
                    bMap = BitmapFactory.decodeFile(photoPath, options);
                    intArray = new int[bMap.getWidth()*bMap.getHeight()];
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    try{

                        result = reader.decode(bitmap);

                    }catch (Exception e2){
                        Log.e(TAG, "getQRCode: Not found exception: try2");
                        e2.printStackTrace();
                        options.inSampleSize = count++;
                        bMap = BitmapFactory.decodeFile(photoPath, options);
                        intArray = new int[bMap.getWidth()*bMap.getHeight()];
                        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                        source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                        bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        try{
                            result = reader.decode(bitmap);
                        }catch (Exception e3){
                            Log.e(TAG, "getQRCode: Not found exception: try3");
                            e3.printStackTrace();
                        }
                    }
                }
            } catch (ChecksumException e) {
                Log.e(TAG, "getQRCode: Not found exception");
                e.printStackTrace();

            } catch (FormatException e) {
                Log.e(TAG, "getQRCode: Not found exception");
                e.printStackTrace();
            }
            if(result!=null) {
                Log.i(TAG, "getQRCode: result not null");
                contents = result.getText();

                image = new Image();
                image.setASU_ID(params[1]);
                image. setLocation(photoPath);
                image.setQrCodeSolution(contents);
            }else{
                Log.e(TAG, "getQRCode: result is null");
            }

        } else {
            Log.e(TAG, "bit map is null");
        }
        return image;
    }

    public GenQRCode(Context context) {
        this.context = context;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Image image) {
        if(image!=null){
            ImageDAO imageDAO = new ImageDAO(context);
            imageDAO.addImageLocation(image);
            Toast.makeText(context,image.getQrCodeSolution(), LENGTH_LONG).show();
        }else
            Toast.makeText(context, "QR Code not found", LENGTH_LONG).show();
    }
}
