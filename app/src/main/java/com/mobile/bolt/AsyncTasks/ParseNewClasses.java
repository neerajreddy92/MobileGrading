package com.mobile.bolt.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.mobile.bolt.DAO.StudentDAO;
import com.mobile.bolt.Parser.JsonParserRead;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Neeraj on 4/10/2016.
 */
public class ParseNewClasses extends AsyncTask<String,Integer,Boolean>{
    String TAG = "MobileGrading";
    PowerManager.WakeLock mWakeLock;
    Context context;
    public ParseNewClasses(Context context) {
        super();
        this.context = context;
    }
    @Override
    protected void onPreExecute() {


    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(result) Toast.makeText(context,"added to the database",Toast.LENGTH_SHORT).show();
        else Toast.makeText(context,"cannot add to the database",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String filePath = params[1];
        String className = params[0];
        if(!filePath.matches("")){
            JsonParserRead jsonParserRead = new JsonParserRead();
            File file = new File(filePath);
            return new StudentDAO(context).createTable(className,jsonParserRead.readStudents(file));
        }else{
            return new StudentDAO(context).createJustTable(className);
        }
    }
}
