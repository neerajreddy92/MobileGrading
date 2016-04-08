package com.mobile.bolt.Parser;

import android.util.Log;
import com.mobile.bolt.Model.Image;
import com.mobile.bolt.Model.Student;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by Neeraj on 4/7/2016.
 */
public class JsonParserWrite {
    // TODO: 4/8/2016 add question grade to the solutions tag.  
    private static String TAG= "MobileGrading";
    public static Boolean writeStudentReport(File file, List<Image> images, Student student){
        try {
        JSONObject report = new JSONObject();
            report.put("Id",student.getStudentID());
            report.put("FirstName",student.getFirstName());
            report.put("LastName",student.getLastName());
            report.put("TotalGrade","");
            for(Image image : images){
                JSONArray solutions = new JSONArray();
                JSONObject question = new JSONObject();
                question.put("Question",image.getQrCodeSolution());
                JSONArray tags =splitTags(image.getQrCodeValues());
                question.put("tags",tags);
                solutions.add(question);
                report.put("Solutions", solutions);
            }
            FileWriter wr = new FileWriter(file);
            wr.write(report.toJSONString());
            wr.flush();
            wr.close();
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "writeStudentReport: Exception");
            Log.e(TAG, "writeStudentReport: "+e.toString());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "writeStudentReport: Exception");
            Log.e(TAG, "writeStudentReport: " + e.toString());
            return false;
        }
    }

    private static JSONArray splitTags(String tag) throws Exception{
        JSONArray tags = new JSONArray();
        String curStr="";
        JSONObject indi = new JSONObject();
        for(int i=0; i<tag.length();i++){
            char cur= tag.charAt(i);
            if(cur==':'){
                indi.put("TagName", curStr);
                curStr="";
                continue;
            }
            if(cur==';'){
                indi.put("Weight",curStr);
                curStr="";
                tags.add(indi);
                indi= new JSONObject();
                continue;
            }
            curStr=curStr+cur;
        }
        return tags;
    }
}
