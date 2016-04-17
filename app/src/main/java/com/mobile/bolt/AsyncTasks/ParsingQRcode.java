package com.mobile.bolt.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mobile.bolt.DAO.QRCodeDAO;
import com.mobile.bolt.Model.QrCode;
import com.mobile.bolt.Parser.JsonParserRead;
import com.mobile.bolt.Parser.XMLParser;

import java.io.File;
import java.util.List;

/**
 * Created by Neeraj on 3/4/2016.
 */
public class ParsingQRcode extends AsyncTask<String,Integer,Boolean>{
    private String TAG= "MobileGrading";
    Context context;
    QRCodeDAO qrCodeDAO;
    public  ParsingQRcode(Context context){
        this.context=context;
    }
    @Override
    protected void onPreExecute() {
        qrCodeDAO= new QRCodeDAO(context);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String path = params[0];
        File newFile= new File(path);
        Log.d(TAG, "doInBackground: "+params[0]);
        if(newFile.exists()){

            try {
                List<QrCode> qrCodes;
                if(path.contains(".xml") || path.contains(".XML")){
                    XMLParser xmlParser = new XMLParser();
                    qrCodes= xmlParser.read(newFile);
                }else{
                    JsonParserRead jsonParserRead = new JsonParserRead();
                    qrCodes= jsonParserRead.readQRCode(newFile);
                }
                publishProgress(25);
                int time= 75/qrCodes.size();
                for(QrCode qrCode: qrCodes){
                    qrCodeDAO.addQRCODELocation(qrCode);
                    publishProgress(25+time);
                }
                publishProgress(100);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: xmlparser read threw an exception");

            }
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
    }

    @Override
    protected void onPostExecute(Boolean result){
        if(result)
            Toast.makeText(context,"added to the database", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
    }
}
