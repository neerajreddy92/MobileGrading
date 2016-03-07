package com.mobile.bolt.mobilegrading;

import android.app.ActionBar;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.qrcode.encoder.QRCode;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.DAO.QRCodeDAO;
import com.mobile.bolt.Model.Image;
import com.mobile.bolt.Model.QrCode;
import com.mobile.bolt.support.ExifUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ImageFragment extends Fragment {
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
    List<String> label;
    List<Integer> Weights;
    private String imageLocation = null;
    private String TAG = "MobileGrading";
    private List<Image> images = null;
    private ImageDAO imageDAO = null;
    QRCodeDAO qrCodeDAO=null;
    String mCurrentPhotoPath;
    private final String test1 = "MobileGrading";
    Boolean LABEL_VIEW_TRUE = false;
    String QR_CODE_QUESTION=null;
    Button[] labelButton = null;
    Button[] weightView = null;
    Button[] removeButton = null;

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
        String ASUAD = getArguments().getString("ASUAD");
        Log.d(TAG, "onCreate: entered image fragment " + ASUAD);
        images = imageDAO.getAllNonGradedImageLocations(ASUAD);
        if (images != null && !images.isEmpty()) {
            imageLocation = images.get(0).getLocation();
            QR_CODE_QUESTION=images.get(0).getQrCodeSolution();
        }else
            Log.e(TAG, "onCreate: images is empty or null");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        drawView = (DrawingView) rootView.findViewById(R.id.drawing);
        ImageButton save = (ImageButton) rootView.findViewById(R.id.process_next);
        ImageButton showLabels = (ImageButton) rootView.findViewById(R.id.process_show_labels);
        if (imageLocation != null) {
            displayBitmap(rootView);
            showLabels.setEnabled(true);
            if(!QRcodeRetreive()){
                Log.d(TAG, "onCreateView: seeing show labels as false");
                showLabels.setEnabled(false);
            }
            createLabelButtons(rootView);
        } else {
            showLabels.setEnabled(false);
            Toast.makeText(getContext(), "No images available to grade", Toast.LENGTH_SHORT).show();
        }
        drawView.setDrawingCacheEnabled(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLocation != null) {
                    Bitmap bMap = drawView.getDrawingCache();
                    if (dispatchSaveImage(bMap)) {
                        dispatchDisplayNextImage(rootView);
                    }
                }
            }
        });
        ImageButton undo = (ImageButton) rootView.findViewById(R.id.process_erase);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.undo();
            }
        });
        showLabels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLableView(rootView);
            }
        });
        ImageButton done = (ImageButton) rootView.findViewById(R.id.process_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return rootView;
    }

    private void toggleLableView(View rootView) {
        if (LABEL_VIEW_TRUE) {
            for (int i = 0; i < label.size(); i++) {
                weightView[i].setEnabled(false);
                weightView[i].setVisibility(drawView.GONE);
                labelButton[i].setEnabled(false);
                labelButton[i].setVisibility(drawView.GONE);
                removeButton[i].setEnabled(false);
                removeButton[i].setVisibility(drawView.GONE);
            }
            Log.d(TAG, "toggleLableView: disabling label view");
            LABEL_VIEW_TRUE = false;
        } else {
            for (int i = 0; i < label.size(); i++) {
                if (Weights.get(i) == 0) {
                    continue;
                }
                weightView[i].setEnabled(true);
                weightView[i].setVisibility(View.VISIBLE);
                labelButton[i].setEnabled(true);
                labelButton[i].setVisibility(View.VISIBLE);
                removeButton[i].setEnabled(true);
                removeButton[i].setVisibility(View.VISIBLE);
                Log.d(TAG, "toggleLableView: showing label view");
            }
            LABEL_VIEW_TRUE = true;
        }
    }



    private Boolean QRcodeRetreive(){
        if(QR_CODE_QUESTION==null) return false;
        qrCodeDAO = new QRCodeDAO(getContext());
        QrCode qrCode = qrCodeDAO.getSingleQRcodeLocationOnQuestion(QR_CODE_QUESTION);
        if(qrCode==null){
            return false;
        }
        String val = qrCode.getVALUES();
        label = new ArrayList<String>();
        Weights = new ArrayList<Integer>();
        String current="";
        for(int i=0;i<val.length();i++){
            char curr= val.charAt(i);
            if(curr==':'){
                label.add(current);
                current="";
                continue;
            }
            if(curr==';'){
                Weights.add(Integer.parseInt(current));
                current="";
                continue;
            }
            current=current+curr;
        }
        Log.d(TAG, "QRcodeRetreive: qr code labels" + label.toString());
        Log.d(TAG, "QRcodeRetreive: qr code weights" + Weights.toString());
        return true;
    }

    private void createLabelButtons(View rootView) {
        if (label == null || Weights == null) {
            return;
        }
            labelButton = new Button[label.size()];
            weightView = new Button[label.size()];
            removeButton = new Button[label.size()];
        LinearLayout labelBox = (LinearLayout) rootView.findViewById(R.id.label_box);
        for (int i = 0; i < label.size(); i++) {
            labelButton[i] = new Button(getContext());
            labelButton[i].setText(label.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            labelButton[i].setBackgroundColor(Color.RED);
            params.setMargins(30, 0, 0, 0);
            labelButton[i].setLayoutParams(params);
            weightView[i] = new Button(getContext());
            weightView[i].setBackgroundColor(Color.RED);
            weightView[i].setText(String.valueOf(Weights.get(i)));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.WRAP_CONTENT);
            weightView[i].setLayoutParams(lp);
            removeButton[i] = new Button(getContext());
            removeButton[i].setText("X");
            removeButton[i].setBackgroundColor(Color.RED);
            removeButton[i].setLayoutParams(lp);
            weightView[i].setEnabled(false);
            weightView[i].setVisibility(drawView.GONE);
            labelButton[i].setEnabled(false);
            labelButton[i].setVisibility(drawView.GONE);
            removeButton[i].setEnabled(false);
            removeButton[i].setVisibility(drawView.GONE);
            labelBox.addView(labelButton[i]);
            labelBox.addView(weightView[i]);
            labelBox.addView(removeButton[i]);
            labelButton[i].setOnClickListener(handleOnClickforLabel(labelButton[i], weightView[i], removeButton[i], i));
            removeButton[i].setOnClickListener(handleOnClickforRemove(removeButton[i], weightView[i], labelButton[i], i));
        }
    }

    View.OnClickListener handleOnClickforLabel(final Button labelButton, final Button weightButton, final Button removeButton, final int i) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Weights.set(i, Weights.get(i) - 1);
                if (Weights.get(i) == 0) {
                    weightButton.setEnabled(false);
                    weightButton.setVisibility(drawView.GONE);
                    labelButton.setEnabled(false);
                    labelButton.setVisibility(drawView.GONE);
                    removeButton.setEnabled(false);
                    removeButton.setVisibility(drawView.GONE);
                } else {
                    weightButton.setText(String.valueOf(Weights.get(i)));
                }
            }
        };
    }

    View.OnClickListener handleOnClickforRemove(final Button removeButton, final Button weightButton, final Button labelButton, final int i) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Weights.set(i, 0);
                weightButton.setEnabled(false);
                weightButton.setVisibility(drawView.GONE);
                labelButton.setEnabled(false);
                labelButton.setVisibility(drawView.GONE);
                removeButton.setEnabled(false);
                removeButton.setVisibility(drawView.GONE);
            }
        };
    }

    private void dispatchDisplayNextImage(View rootview) {
        if (images != null && !images.isEmpty()) {
            String val="";
            for(int  i=0;i<label.size();i++){
                val=val+label.get(i)+":";
                val=val+Weights.get(i)+";";
            }
            if(LABEL_VIEW_TRUE){
                toggleLableView(rootview);
            }
            QRcodeRetreive();
            createLabelButtons(rootview);
            Image image = images.get(0);
            QrCode qrCode = new QrCode();
            qrCode.setVALUES(val);
            qrCode.setQUESTION("start:" + image.getQrCodeSolution());
            qrCodeDAO.addQRCODELocation(qrCode);
            // TODO: 3/6/2016 make sure all the duplicates are deleted.
            image.setLocation(mCurrentPhotoPath);
            image.setGraded(1);
            image.setQrCodeSolution(qrCode.getQUESTION());
            imageDAO.updateGradedStatusNow(image);
            images.remove(0);
            if (!images.isEmpty()) {
                imageLocation = images.get(0).getLocation();
            } else {
                imageLocation = null;
                Toast.makeText(getContext(), "Image Saved. No more gradable images available", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            displayBitmap(rootview);
        }
    }

    private boolean dispatchSaveImage(Bitmap bMap) {
        // TODO: 2/25/2016 see if this process would perform well on a seperate thread. 
        File file = null;
        Log.d(TAG, "dispatchSaveImage: SAving the image");
        try {
            file = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(test1, "dispatchSaveImage: file creation threw null");
        }
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
                Log.e(test1, "dispatchSaveImage: file unwritable ");
                return false;
            }
            Log.d(TAG, "dispatchSaveImage: Retured true");
            Toast.makeText(getContext(), "Image saved", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
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
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: mcurrentphotopathupdated" + mCurrentPhotoPath);
        return image;
    }

    private void displayBitmap(View rootView) {
        // TODO: 2/25/2016 see if this task does well on a seperate thread.
        Bitmap bMap = null;
        int count = 0;
        //= BitmapFactory.decodeFile(imagePath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Log.i(test1, "displayBitmap: another photo path:" + imageLocation);
        Log.i(test1, "displayBitmap: reached here");
        options.inSampleSize = count++;
        try {
            bMap = BitmapFactory.decodeFile(imageLocation, options);
            Log.i(test1, "displayBitmap: option=1");
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bMap = null;
            Toast.makeText(getContext(), "out mem 1", Toast.LENGTH_SHORT).show();
            try {
                options.inSampleSize = count++;
                bMap = BitmapFactory.decodeFile(imageLocation, options);
                Log.i(test1, "displayBitmap: option2");
            } catch (OutOfMemoryError e1) {
                e1.printStackTrace();
                bMap = null;
                try {
                    options.inSampleSize = count++;
                    bMap = BitmapFactory.decodeFile(imageLocation, options);
                    Log.i(test1, "displayBitmap: option3");
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
//            drawView.startNew();
//            if(orientedBitmap.getWidth()>orientedBitmap.getHeight()){
//                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }else{
//                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
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
        imageDAO = new ImageDAO(context);
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
        imageDAO = null;
    }


}
