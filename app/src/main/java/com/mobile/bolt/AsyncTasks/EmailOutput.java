package com.mobile.bolt.AsyncTasks;

import android.os.AsyncTask;

/**
 * Created by Neeraj on 4/16/2016.
 */
public class EmailOutput extends AsyncTask <String,Integer,Boolean>{
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public EmailOutput() {
        super();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        return null;
    }
}
