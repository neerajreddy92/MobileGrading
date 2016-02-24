package com.mobile.bolt.mobilegrading;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Activity_grade_process extends AppCompatActivity implements FragmentClass.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_process);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentClass())
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(String str) {
        Bundle bundle=new Bundle();
        bundle.putString("picAddress", str);
        ImageFragment frag= new ImageFragment();
        frag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, frag)
                .commit();
    }
}
