package com.mobile.bolt.mobilegrading;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.mobile.bolt.AsyncTasks.PopulatingOutputActivity;
import com.mobile.bolt.AsyncTasks.SetAllUploadedTrue;
import com.mobile.bolt.AsyncTasks.WriteOutput;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.Model.Image;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class saveActivity extends AppCompatActivity implements PopulatingOutputActivity.AsyncTaskCommunicate{
    private String TAG= "MobileGrading";
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        ImageDAO imageDAO = new ImageDAO(getBaseContext());
        new PopulatingOutputActivity(getBaseContext(),findViewById(android.R.id.content)).execute("something irrelavant");
        ImageButton cancel_now = (ImageButton) findViewById(R.id.cancel_now);
         cancel_now.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                finish();
             }
         });
    }

    @Override
    public void onExecute(Map<String, List<Image>> result,View rootView) {
        if(rootView!=null) {
            Log.d(TAG, "onExecute: root view not null");
            LinearLayout layout_main = (LinearLayout) rootView.findViewById(R.id.save_layout);
            LinearLayout[] layout_horizontal = new LinearLayout[result.size()];
            Set<String> keyset = result.keySet();
            int count=0;
            for(String cur : keyset){
                TextView textView = new TextView(getBaseContext());
                textView.setText(cur);
                Button button = new Button(getBaseContext());
                button.setText("save now");
                layout_horizontal[count]=new LinearLayout(getBaseContext());
                layout_horizontal[count].setOrientation(LinearLayout.HORIZONTAL);
                layout_horizontal[count].addView(textView);
                layout_horizontal[count].addView(button);
                layout_main.addView(layout_horizontal[count]);
                button.setOnClickListener(handleOnClick(layout_horizontal[count],result.get(cur)));
                count++;
            }
        }
    }

    private View.OnClickListener handleOnClick(final LinearLayout linearLayout, final List<Image> images) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                new WriteOutput(getBaseContext()).execute(images);
                new SetAllUploadedTrue(getBaseContext()).execute(images);
                linearLayout.setEnabled(false);
            }
        };
    }

}
