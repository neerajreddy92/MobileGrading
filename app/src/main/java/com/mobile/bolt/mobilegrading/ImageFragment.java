package com.mobile.bolt.mobilegrading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


public class ImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
        displayBitmap(rootView);
        return rootView;
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
            ImageView img= (ImageView) rootView.findViewById(R.id.imageView);
            img.setImageBitmap(bMap);
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
