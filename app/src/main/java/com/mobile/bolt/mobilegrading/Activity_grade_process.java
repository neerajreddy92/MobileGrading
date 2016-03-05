package com.mobile.bolt.mobilegrading;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Activity_grade_process extends AppCompatActivity implements FragmentClass.OnFragmentInteractionListener {

    private String stu=null;

    // TODO: 2/25/2016 make the view look better 
    // TODO: 2/25/2016 add details of the student on the display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_process);
        stu= getIntent().getStringExtra("ASUAD");
        // TODO: 2/24/2016 handle null exceptions
        Bundle bundle=new Bundle();
        bundle.putString("ASUAD", stu);
        FragmentClass frag= new FragmentClass();
        frag.setArguments(bundle);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, frag).addToBackStack(null)
                    .commit();
        }

    }

    @Override
    public void onFragmentInteraction(String str) {
        Bundle bundle=new Bundle();
        bundle.putString("ASUAD",stu);
        ImageFragment frag= new ImageFragment();
        frag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, frag)
                .commit();
    }
}
