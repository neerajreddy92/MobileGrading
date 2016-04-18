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
import com.mobile.bolt.AsyncTasks.GenQRCode;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.DAO.StudentContractHelper;
import com.mobile.bolt.Model.Image;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Neeraj on 2/11/2016.
 */

public class FragmentClass extends Fragment {
    // TODO: 2/25/2016 remove the get QR code button on the view. 
    private String mCurrentPhotoPath;
    static String anotherPhotoPath;
    private ImageDAO imageDAO = null;
    String ASUAD = null;
    ArrayAdapter<String> mForecastAdapter;
    final String test1 = "MobileGrading";
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
        imageDAO = new ImageDAO(context);
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
        imageDAO = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ASUAD = getArguments().getString("ASUAD");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button camereaButton = (Button) rootView.findViewById(R.id.beginCamera);
        camereaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });
        Button displayBitmap = (Button) rootView.findViewById(R.id.display_bitmap);
        displayBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayBitmapOnView();
            }
        });
        rootView.findViewById(R.id.process_done_fragment_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return rootView;
    }

    private void displayBitmapOnView() {
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
                Log.e(test1, "dispatchTakePictureIntent: error while creating a file");
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.i(test1, "dispatchTakePictureIntent: photo file noot null");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                if (photoFile.exists()) {
                    Log.i(test1, "createImageFile:" + anotherPhotoPath);
                    Log.i(test1, "dispatchTakePictureIntent: picture file created");
                }

            }
        } else {
            Log.e(test1, "dispatchTakePictureIntent: getActivity() doesent work");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        new GenQRCode(getContext(),ASUAD).execute(anotherPhotoPath,ASUAD); //starting async task to genrate qr code.
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
        Log.i(test1, "createImageFile:" + anotherPhotoPath);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}