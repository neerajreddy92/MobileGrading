package com.mobile.bolt.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.Model.Student;
import com.mobile.bolt.support.FilterRecyclerView;
import com.mobile.bolt.support.GmailSender;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

/**
 * Created by Neeraj on 4/20/2016.
 */
public class SendEmail extends AsyncTask<Object, Integer, Boolean> {
    private String TAG = "MobileGrading";
    Context context = null;
    List<Student> students;

    public SendEmail(Context context, List<Student> students) {
        this.context = context;
        this.students = students;
    }

    @Override
    protected void onPreExecute() {
        students = FilterRecyclerView.filter(students, "", 3);
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        boolean ret=true;
        String email = "reddy.neeraj007@gmail.com";
        if(students.size()==0) return false;
        for (Student student : students) {
            String[] pdfFileNames = loadPDFFileList();
            String[] textFileNames = loadTextFileList();
            String pdfFinalFile="";
            Long pdfDate= Long.MIN_VALUE, pdfHours = Long.MIN_VALUE;
            Long textDate = Long.MIN_VALUE, textHours = Long.MIN_VALUE;
            String textFinalFile="";
            for(String pdfFile : pdfFileNames){
                String[] parts = pdfFile.substring(0,pdfFile.length()-4).split("_");;
                if(parts[0].matches(student.getStudentID())){
                    if(pdfFinalFile.matches("")){
                          pdfFinalFile = pdfFile;
                          pdfDate = Long.parseLong(parts[1]);
                          pdfHours = Long.parseLong(parts[2]);
                    }else {
                        if (Long.parseLong(parts[1]) >= pdfDate && Long.parseLong(parts[2]) >pdfHours){
                            pdfFinalFile = pdfFile;
                            pdfDate = Long.parseLong(parts[1]);
                            pdfHours = Long.parseLong(parts[2]);
                        }
                    }
                }
            }
            for(String textFile : textFileNames){
                String[] parts = textFile.substring(0,textFile.length()-5).split("_");;
                if(parts[0].matches(student.getStudentID())){
                    if(textFinalFile.matches("")){
                        textFinalFile = textFile;
                        textDate = Long.parseLong(parts[1]);
                        textHours = Long.parseLong(parts[2]);
                    }else {
                        if (Long.parseLong(parts[1]) >= textDate && Long.parseLong(parts[2]) >textHours){
                            textFinalFile = textFile;
                            textDate = Long.parseLong(parts[1]);
                            textHours = Long.parseLong(parts[2]);
                        }
                    }
                }
            }
            if(textFinalFile.matches("") || pdfFinalFile.matches("")) continue;
            File pdfFileLocation = new File(pdfPath.getAbsolutePath() + "/"+ pdfFinalFile);
            File textFileLocation = new File(textPath.getAbsolutePath() + "/"+ textFinalFile);
            Log.d(TAG, "doInBackground: sendng email for"+pdfFileLocation);
            try {
                GmailSender sender = new GmailSender("asucidse@gmail.com", "brickyard");
                DataSource source1 = new FileDataSource(pdfFileLocation);
                DataSource source2 = new FileDataSource(textFileLocation);
                sender.addAttachment(source1,source2,pdfFinalFile,textFinalFile,"Graded files for "+student.getStudentID() + " " + student.getFirstName() + " " + student.getLastName() );
                sender.sendMail("Graded files for " + student.getStudentID() + " " + student.getFirstName() + " " + student.getLastName(),
                        "This mail is sent by Mobile Grading Android Application. All information in this mail is strictly confidential",
                        "asucidse@gmail.com",
                        email);
                Log.d(TAG, "doInBackground: email sent");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
                ret = false;
            }
        }
        return ret;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "All emails generated", Toast.LENGTH_SHORT).show();

        } else
            Toast.makeText(context, "unable to generate all emails", Toast.LENGTH_SHORT).show();
    }

    private File pdfPath = new File(Environment.getExternalStorageDirectory() + "//Documents//pdfoutput//");
    private File textPath = new File(Environment.getExternalStorageDirectory() + "//Documents//textoutput//");

    private String[] loadPDFFileList() {
        String[] mFileList;
        try {
            pdfPath.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card " + e.toString());
        }
        if (pdfPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains(".pdf") || filename.contains(".PDF");
                }
            };
            mFileList = pdfPath.list(filter);
        } else {
            mFileList = new String[0];
        }
        return mFileList;
    }

    private String[] loadTextFileList() {
        String[] mFileList;
        try {
            textPath.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card " + e.toString());
        }
        if (textPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains(".JSON") || filename.contains(".json");
                }
            };
            mFileList = textPath.list(filter);
        } else {
            mFileList = new String[0];
        }
        return mFileList;
    }
}
