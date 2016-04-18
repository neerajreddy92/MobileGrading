package com.mobile.bolt.AsyncTasks;

/**
 * Created by Neeraj on 4/15/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.DAO.StudentContractHelper;
import com.mobile.bolt.DAO.StudentDao;
import com.mobile.bolt.Model.Image;
import com.mobile.bolt.Model.Student;
import com.mobile.bolt.Parser.JsonParserWrite;
import com.mobile.bolt.support.SelectedClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Neeraj on 3/6/2016.
 */
public class WriteOutputNow extends AsyncTask<Object, Integer, Boolean> {
    private String TAG= "MobileGrading";
    Context context =null;
    String ASUAD;
    public WriteOutputNow(Context context,String ASUAD){
        this.context = context;
        this.ASUAD =ASUAD;
    }
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(Object... params) {
        Student student = (Student) params[0];
        ImageDAO imageDAO = new ImageDAO(context);
        List<Image> images=imageDAO.getAllNonUploadedImageLocations(student.getStudentID());
        if(images==null || images.isEmpty()) return false;
        try {
            File file = createNewPdfFile(images.get(0).getASU_ID());
            File textFile = createNewTextFile(images.get(0).getASU_ID());
            int indentation = 1;
            com.itextpdf.text.Image img;
            Document document = new Document(PageSize.LETTER);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            int i=0;
            for (Image image : images) {
                img =   com.itextpdf.text.Image.getInstance(image.getLocation());
                document.setPageSize(PageSize.LETTER);
                document.newPage();
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - indentation) / img.getWidth()) * 100;
                img.scalePercent(scaler);
                document.add(img);
                document.add(new Paragraph("" + image.getQrCodeSolution() + " " + image.getQrCodeValues()+" comments: "+image.getQuestionComments()+" grade:"+image.getGrade()));
                i++;
            }
            document.close();
            if(!(JsonParserWrite.writeStudentReport(textFile,images,student)))
                return false;
            for (Image image : images) {
                image.setUploaded(1);
                imageDAO.updateUploadStatusNow(image);
            }
            Log.d(TAG, "doInBackground: setting all image values uploaded status true");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground:Writing to pdf file not found exception");
        } catch (DocumentException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground:Writing to pdf Document exception");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground:Writing to pdf Malformed URL exception");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "doInBackground:Writing to pdf IO Exception exception");
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }
    @Override
    protected void onPostExecute( Boolean result){
        if(result) {
            Toast.makeText(context, "Output generated", Toast.LENGTH_SHORT).show();
            Student student = new Student();
            student.setStatus(3);
            student.setStudentID(ASUAD);
            new StudentDao(context).updateStatus(SelectedClass.getInstance().getCurrentClass(), student);
        }
        else
            Toast.makeText(context,"unable to generate output",Toast.LENGTH_SHORT).show();
    }
    public File createNewPdfFile(String name){
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        File myFile = new File(Environment.getExternalStorageDirectory() + "//Documents//pdfoutput//"+name+"_"+timeStamp + ".pdf");
        return myFile;
    }
    public File createNewTextFile(String name){
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        File myFile = new File(Environment.getExternalStorageDirectory() + "//Documents//textoutput//"+name+"_"+timeStamp + ".json");
        return myFile;
    }
}
