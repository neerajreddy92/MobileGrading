package com.mobile.bolt.AsyncTasks;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.DAO.QRCodeDAO;
import com.mobile.bolt.Model.Image;
import com.mobile.bolt.Model.QrCode;
import com.mobile.bolt.mobilegrading.R;
import com.mobile.bolt.mobilegrading.saveActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Neeraj on 3/6/2016.
 */
public class PopulatingOutputActivity extends AsyncTask<String,Integer, Map<String,List<Image>>>{
    private String TAG= "MobileGrading";
    Context context;
    View rootView=null;
    AsyncTaskCommunicate asyncTaskCommunicate;
    public PopulatingOutputActivity(Context context){
        this.context=context;
    }
    public PopulatingOutputActivity(Context context,View rootView){
        this.context=context;
        this.rootView=rootView;
    }

    public interface AsyncTaskCommunicate{
        public void onExecute(Map<String,List<Image>> result,View rootView);
    }

    @Override
    protected void onPreExecute() {
        asyncTaskCommunicate= new saveActivity();
    }

    @Override
    protected   Map<String,List<Image>> doInBackground(String... params) {
        ImageDAO imageDAO = new ImageDAO(context);
        QRCodeDAO qrCodeDAO = new QRCodeDAO(context);
        List<Image> images= imageDAO.getAllNonUploadedImages();
        Map<String,List<Image>> map=new HashMap<String,List<Image>>();
        if(images==null ||  images.isEmpty()) return map;
        for(Image image :images){
            if(map.containsKey(image.getASU_ID())){
                map.get(image.getASU_ID()).add(image);
               continue;
            }else{
                LinkedList<Image> list = new LinkedList<Image>();
                list.add(image);
                map.put(image.getASU_ID(),list);
            }
        }
        Log.d(TAG, "doInBackground: populating non uploaded data");
        return map;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0])
    }
    @Override
    protected void onPostExecute( Map<String,List<Image>> result) {
        //// TODO: 3/6/2016 Modify to display first name and last name.
//        asyncTaskCommunicate.onExecute(result,rootView); CAlls the method in saveActivity
        if(rootView!=null && !result.isEmpty()) {
            LinearLayout layout_main = (LinearLayout) rootView.findViewById(R.id.save_layout);
            LinearLayout[] layout_horizontal = new LinearLayout[result.size()];
            Set<String> keyset = result.keySet();
            int count=0;
            for(String cur : keyset){
                TextView textView = new TextView(context);
                textView.setText(cur);
                textView.setTextColor(Color.BLACK);
                Button button = new Button(context);
                button.setText("Write Output");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity= Gravity.CENTER_HORIZONTAL;
                layout_horizontal[count]=new LinearLayout(context);
                layout_horizontal[count].setOrientation(LinearLayout.HORIZONTAL);
                layout_horizontal[count].addView(textView);
                layout_horizontal[count].addView(button);
                layout_horizontal[count].setLayoutParams(params);
                layout_main.addView(layout_horizontal[count]);
                button.setOnClickListener(handleOnClick(layout_horizontal[count],result.get(cur)));
                count++;
            }
        }
//        if(rootView!=null && !result.isEmpty()) {
//            LinearLayout layout_main = (LinearLayout) rootView.findViewById(R.id.save_layout);
//            LinearLayout[] layout_horizontal = new LinearLayout[result.size()];
//            Set<String> keyset = result.keySet();
//            int count=0;
//            for(String cur : keyset){
//                TextView textView = new TextView(context);
//                textView.setText(cur);
//                Button button = new Button(context);
//                button.setText("save now");
//                layout_horizontal[count]=new LinearLayout(context);
//                layout_horizontal[count].setOrientation(LinearLayout.HORIZONTAL);
//                layout_horizontal[count].addView(textView);
//                layout_horizontal[count].addView(button);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    layout_horizontal[count].setId(View.generateViewId());
//                }
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                if(count>0)
//                    params.addRule(RelativeLayout.BELOW,layout_horizontal[count-1].getId());
//                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                layout_main.addView(layout_horizontal[count],params);
//                button.setOnClickListener(handleOnClick(layout_horizontal[count],result.get(cur)));
//                count++;
//            }
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.CENTER_VERTICAL);
//            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//            layout_horizontal[(int)count/2+1].setLayoutParams(params);
//        }
    }

    private View.OnClickListener handleOnClick(final LinearLayout linearLayout, final List<Image> images) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                new WriteOutput(context).execute(images);
                new SetAllUploadedTrue(context).execute(images);
                linearLayout.setVisibility(View.INVISIBLE);
            }
        };
    }

}
