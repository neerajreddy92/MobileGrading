package com.mobile.bolt.mobilegrading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mobile.bolt.support.ExifUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class    ImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DrawingView drawView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentClass.OnFragmentInteractionListener mListener;

    public ImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageFragment.
     */
    // TODO: Rename and change types and number of parameters

    private String imageLocation;
    String mCurrentPhotoPath;
    private final String test1="MobileGrading";
    public static ImageFragment newInstance(String param1, String param2) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLocation=getArguments().getString("picAddress");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        drawView = (DrawingView)rootView.findViewById(R.id.drawing);
        Button save=(Button)rootView.findViewById(R.id.process_next);
        displayBitmap(rootView);
        drawView.setDrawingCacheEnabled(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bMap = drawView.getDrawingCache();
                dispatchSaveImage(bMap);
            }
        });
        return rootView;
    }

    private void dispatchSaveImage(Bitmap bMap){
        File file=null;
        try{
            file=createImageFile();
        }catch(IOException e){
            e.printStackTrace();
            Log.e(test1, "dispatchSaveImage: file creation threw null");
        }
        if(file!=null){
            FileOutputStream ostream = null;
            try {
                ostream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bMap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
            try {
                ostream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(test1, "dispatchSaveImage: file unwritable ");
            }
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
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    private void displayBitmap(View rootView){
        Bitmap bMap = null;
        int count=0;
        //= BitmapFactory.decodeFile(imagePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.i(test1, "getQRCode: another photo path:" + imageLocation);
        Log.i(test1, "getQRCode: reached here");
        options.inSampleSize = count++;
        try {
            bMap = BitmapFactory.decodeFile(imageLocation, options);
            Log.i(test1, "getQRCode: option=1");
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bMap = null;
            Toast.makeText(getContext(), "out mem 1", Toast.LENGTH_SHORT).show();
            try {
                options.inSampleSize = count++;
                bMap = BitmapFactory.decodeFile(imageLocation, options);
                Log.i(test1, "getQRCode: option2");
            } catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                bMap = null;
                try {
                    options.inSampleSize = count++;
                    bMap = BitmapFactory.decodeFile(imageLocation, options);
                    Log.i(test1, "getQRCode: option3");
                } catch (OutOfMemoryError e2) {
                    e2.printStackTrace();
                    bMap = null;

                    try {
                        options.inSampleSize = count++;
                        bMap = BitmapFactory.decodeFile(imageLocation, options);
                        Log.i(test1, "option 4");
                    } catch (OutOfMemoryError e3) {
                        e3.printStackTrace();
                        bMap = null;
                    }
                }
            }
        }

        if (bMap != null) {
            Bitmap orientedBitmap = ExifUtil.rotateBitmap(imageLocation, bMap);
            drawView.setPicture(orientedBitmap);
//            ImageView img= (ImageView) rootView.findViewById(R.id.imageView);
//            img.setImageBitmap(orientedBitmap);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string) {
        if (mListener != null) {
            mListener.onFragmentInteraction(string);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentClass.OnFragmentInteractionListener) {
            mListener = (FragmentClass.OnFragmentInteractionListener) context;
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


}
