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
import android.widget.Toast;

import com.mobile.bolt.AsyncTasks.ShowNewGradableImage;
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
    private FragmentClass.OnFragmentInteractionListener mListener;

    public ImageFragment() {
        // Required empty public constructor
    }
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
    LinearLayout horizontalLayout = null;
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!(images != null && !images.isEmpty())) {
            getActivity().finish();
        }
        final View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        drawView = (DrawingView) rootView.findViewById(R.id.drawing);
        ImageButton save = (ImageButton) rootView.findViewById(R.id.process_next);
        ImageButton showLabels = (ImageButton) rootView.findViewById(R.id.process_show_labels);
        if (imageLocation != null) {
            new ShowNewGradableImage(getContext(),drawView).execute(imageLocation);
            showLabels.setEnabled(true);
            generateQuestionWeights(rootView);
        } else {
            showLabels.setEnabled(false);
            Toast.makeText(getContext(), "No images available to grade", Toast.LENGTH_SHORT).show();
        }
        drawView.setDrawingCacheEnabled(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLocation != null) {
                    drawView.setDrawingCacheEnabled(true);
                    Bitmap bMap = drawView.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);
                    drawView.destroyDrawingCache();
                    if (dispatchSaveImage(bMap)) {
                        dispatchDisplayNextImage(rootView);
                    }
                }
            }
        });
        rootView.findViewById(R.id.process_refresh_labels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQuestionWeights(rootView);
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
    private void generateQuestionWeights(View rootView){
        if(!QRcodeRetreive()){
            Log.d(TAG, "onCreateView: seeing show labels as false");
            rootView.findViewById(R.id.process_show_labels).setEnabled(false);
        }
        createLabelButtons(rootView);
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
            rootView.findViewById(R.id.process_refresh_labels).setEnabled(false);
            rootView.findViewById(R.id.process_refresh_labels).setVisibility(drawView.GONE);
            Log.d(TAG, "toggleLableView: disabling label view");
            LABEL_VIEW_TRUE = false;
            Log.d(TAG, "dispatchDisplayNextImage: chanigng label view to "+false);
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
            rootView.findViewById(R.id.process_refresh_labels).setEnabled(true);
            rootView.findViewById(R.id.process_refresh_labels).setVisibility(View.VISIBLE);
            LABEL_VIEW_TRUE = true;
            Log.d(TAG, "dispatchDisplayNextImage: chanigng label view to "+true);
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
        LinearLayout labelBox = (LinearLayout) rootView.findViewById(R.id.label_box);
        if(labelButton!=null){
            for(int i=0;i<labelButton.length;i++){
                labelButton[i].setVisibility(View.GONE);
                weightView[i].setVisibility(View.GONE);
                removeButton[i].setVisibility(View.GONE);
            }
            horizontalLayout.setVisibility(drawView.GONE);
        }
            labelButton = new Button[label.size()];
            weightView = new Button[label.size()];
            removeButton = new Button[label.size()];

        for (int i = 0; i < label.size(); i++) {
            labelButton[i] = new Button(getContext());
            labelButton[i].setText(label.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 0, 0, 0);
            labelButton[i].setLayoutParams(params);
            weightView[i] = new Button(getContext());
            weightView[i].setText(String.valueOf(Weights.get(i)));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.WRAP_CONTENT);
            weightView[i].setLayoutParams(lp);
            removeButton[i] = new Button(getContext());
            removeButton[i].setText("X");
            removeButton[i].setLayoutParams(lp);
            labelButton[i].setBackgroundColor(Color.TRANSPARENT);
            weightView[i].setBackgroundColor(Color.TRANSPARENT);
            if(!LABEL_VIEW_TRUE){
                removeButton[i].setVisibility(drawView.GONE);
                labelButton[i].setVisibility(drawView.GONE);
                weightView[i].setVisibility(drawView.GONE);
                labelButton[i].setEnabled(false);
                removeButton[i].setEnabled(false);
                weightView[i].setEnabled(false);
            }
            removeButton[i].setBackgroundColor(Color.TRANSPARENT);
            LinearLayout layout = new LinearLayout(getContext());
            horizontalLayout=layout;
            layout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.setMargins(0,30,0,0);
            layout.setLayoutParams(params1);
            layout.setBackgroundColor(0xFFd88827);
            layout.addView(labelButton[i]);
            layout.addView(weightView[i]);
            layout.addView(removeButton[i]);
            labelBox.addView(layout);
            labelButton[i].setOnClickListener(handleOnClickforLabel(labelButton[i], weightView[i], removeButton[i], i,layout));
            removeButton[i].setOnClickListener(handleOnClickforRemove(removeButton[i], weightView[i], labelButton[i], i,layout));
        }
    }

    View.OnClickListener handleOnClickforLabel(final Button labelButton, final Button weightButton, final Button removeButton, final int i,final LinearLayout layout) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Weights.set(i, Weights.get(i) - 1);
                if (Weights.get(i) == 0) {
                    weightButton.setEnabled(false);
                    labelButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    layout.setBackgroundColor(Color.GRAY);
                } else {
                    weightButton.setText(String.valueOf(Weights.get(i)));
                    layout.setBackgroundColor(Color.RED);
                }
            }
        };
    }

    View.OnClickListener handleOnClickforRemove(final Button removeButton, final Button weightButton, final Button labelButton, final int i,final LinearLayout layout) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Weights.set(i, 0);
                weightButton.setEnabled(false);
                labelButton.setEnabled(false);
                removeButton.setEnabled(false);
                layout.setBackgroundColor(Color.GRAY);
            }
        };
    }

    private void dispatchDisplayNextImage(View rootview) {
        if (images != null && !images.isEmpty()) {
            String val="";
            //// TODO: 3/6/2016 add cover if label doesent exist.  
            if(label!=null){
            for(int  i=0;i<label.size();i++){
                val=val+label.get(i)+":";
                val=val+Weights.get(i)+";";
            }
            if(LABEL_VIEW_TRUE){
                toggleLableView(rootview);
            }
            }
            Image image = images.get(0);
            // TODO: 3/6/2016 make sure all the duplicates are deleted.
            image.setLocation(mCurrentPhotoPath);
            image.setGraded(1);
            image.setQrCodeValues(val);
            imageDAO.updateGradedStatusNow(image);
            images.remove(0);
            if (!images.isEmpty()) {
                imageLocation = images.get(0).getLocation();
                QR_CODE_QUESTION= images.get(0).getQrCodeSolution();
                LABEL_VIEW_TRUE=false;
                Log.d(TAG, "dispatchDisplayNextImage: chanigng label view to "+false);
                generateQuestionWeights(rootview);
                new ShowNewGradableImage(getContext(),drawView).execute(imageLocation);
            } else {
                imageLocation = null;
                Toast.makeText(getContext(), "Image Saved. No more gradable images available", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
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
