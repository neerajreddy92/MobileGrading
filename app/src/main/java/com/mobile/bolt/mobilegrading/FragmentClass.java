package com.mobile.bolt.mobilegrading;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Neeraj on 2/11/2016.
 */

public class FragmentClass extends Fragment {
    String mCurrentPhotoPath;
    static String anotherPhotoPath;
    ArrayAdapter<String> mForecastAdapter;
    final String test1="testingNeeraj";
    final int REQUEST_TAKE_PHOTO = 1;
    public FragmentClass() {
    }
    private OnFragmentInteractionListener mListener;
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String str);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button camereaButton =(Button)rootView.findViewById(R.id.beginCamera);
        camereaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });
        Button runQrButton =(Button)rootView.findViewById(R.id.runQRButton);
        runQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getQRCode();
            }
        });
        Button displayBitmap = (Button) rootView.findViewById(R.id.display_bitmap);
        displayBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayBitmapOnView();
            }
        });
        return rootView;
    }

    private void displayBitmapOnView(){
        mListener.onFragmentInteraction(anotherPhotoPath);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            Log.i(test1, "dispatchTakePictureIntent: Entering take picture intent");
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
//                ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.i(test1, "dispatchTakePictureIntent: photo file noot null");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                if(photoFile.exists()) {
                    Log.i(test1, "createImageFile:"+anotherPhotoPath);
                    Log.i(test1, "dispatchTakePictureIntent: picture file created");
                }
            }
        }else{
            Log.e(test1, "dispatchTakePictureIntent: getActivity() doesent work");
        }
    }




    private File createImageFile() throws IOException {
        // Create an image file name
        Log.i(test1, "createImageFile: new image file name created");
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
        Log.i(test1, "createImageFile:"+anotherPhotoPath);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    private void getQRCode() {
        Bitmap bMap = null;
        int count=0;
        //= BitmapFactory.decodeFile(imagePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.i(test1, "getQRCode: another photo path:"+anotherPhotoPath);
        Log.i(test1, "getQRCode: reached here");
        options.inSampleSize = count++;
        try {
            bMap = BitmapFactory.decodeFile(anotherPhotoPath, options);
            Log.i(test1, "getQRCode: option=1");
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bMap = null;
            Toast.makeText(getContext(), "out mem 1", Toast.LENGTH_SHORT).show();
            try {
                options.inSampleSize = count++;
                bMap = BitmapFactory.decodeFile(anotherPhotoPath, options);
                Log.i(test1, "getQRCode: option2");
            } catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                bMap = null;
                try {
                    options.inSampleSize = count++;
                    bMap = BitmapFactory.decodeFile(anotherPhotoPath, options);
                    Log.i(test1, "getQRCode: option3");
                } catch (OutOfMemoryError e2) {
                    e2.printStackTrace();
                    bMap = null;

                    try {
                        options.inSampleSize = count++;
                        bMap = BitmapFactory.decodeFile(anotherPhotoPath, options);
                        Log.i(test1, "option 4");
                    } catch (OutOfMemoryError e3) {
                        e3.printStackTrace();
                        bMap = null;
                    }
                }
            }
        }

        if (bMap != null) {
            Log.i(test1, "getQRCode: Entering to qr processor");
            int[] intArray;
            String contents = null;
            Result result = null;
            Reader reader = new MultiFormatReader();
            intArray = new int[bMap.getWidth()*bMap.getHeight()];
            bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
            LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                Log.i(test1, "getQRCode: reading reults");
                result = reader.decode(bitmap);

            } catch (NotFoundException e) {
                Log.e(test1, "getQRCode: Not found exception");
                e.printStackTrace();
                options.inSampleSize = count++;
                bMap = BitmapFactory.decodeFile(anotherPhotoPath, options);
                intArray = new int[bMap.getWidth()*bMap.getHeight()];
                bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try{

                    result = reader.decode(bitmap);

                }catch (Exception e1){
                    e1.printStackTrace();
                    Log.e(test1, "getQRCode: Not found exception: try1");
                    options.inSampleSize = count++;
                    bMap = BitmapFactory.decodeFile(anotherPhotoPath, options);
                    intArray = new int[bMap.getWidth()*bMap.getHeight()];
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    try{

                        result = reader.decode(bitmap);

                    }catch (Exception e2){
                        Log.e(test1, "getQRCode: Not found exception: try2");
                        e2.printStackTrace();
                        options.inSampleSize = count++;
                        bMap = BitmapFactory.decodeFile(anotherPhotoPath, options);
                        intArray = new int[bMap.getWidth()*bMap.getHeight()];
                        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                        source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                        bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        try{
                            result = reader.decode(bitmap);
                        }catch (Exception e3){
                            Log.e(test1, "getQRCode: Not found exception: try3");
                            e3.printStackTrace();
                        }
                    }
                }
            } catch (ChecksumException e) {
                Log.e(test1, "getQRCode: Not found exception");
                e.printStackTrace();

            } catch (FormatException e) {
                Log.e(test1, "getQRCode: Not found exception");
                e.printStackTrace();
            }
                if(result!=null) {
                    Log.i(test1, "getQRCode: result not null");
                    contents = result.getText();
                    Toast.makeText(getContext(), contents, Toast.LENGTH_LONG).show();
                }else{
                    Log.e(test1, "getQRCode: result is null");
                    Toast.makeText(getContext(), "QR Code not found", Toast.LENGTH_LONG).show();
                }

        } else {
            Log.e(test1, "bit map is null");
        }
    }

}

//      public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Bitmap bMap = (Bitmap) data.getExtras().get("data");
//        Toast.makeText(getContext(), "bit map generated", Toast.LENGTH_SHORT).show();
//        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
//        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
//        String contents = null;
//        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
//        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//
//        Reader reader = new MultiFormatReader();
//        Result result = null;
//        try {
//            result = reader.decode(bitmap);
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//            Log.e("Fatal Error", "Not found exception ");
//        } catch (ChecksumException e) {
//            e.printStackTrace();
//            Log.e("Fatal Error", "Not found exception ");
//        } catch (FormatException e) {
//            e.printStackTrace();
//            Log.e("Fatal Error", "Not found exception ");
//        }
//        if(result==null){
//            Log.e("Fatal Error","in Fragmentmain : result returned null");
//            Toast.makeText(getContext(), "zxing returned null", Toast.LENGTH_LONG).show();
//        }
//        contents = result.getText();
//        Toast.makeText(getContext(), contents, Toast.LENGTH_LONG).show();
//
//    }