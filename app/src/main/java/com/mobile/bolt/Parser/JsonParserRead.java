package com.mobile.bolt.Parser;

import android.util.Log;
import com.mobile.bolt.Model.QrCode;
import com.mobile.bolt.Model.Student;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neeraj on 4/6/2016.
 */

public class JsonParserRead {
    String TAG="MobileGrading";
    public List<QrCode> readQRCode(File file) throws IOException {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject grading = (JSONObject) jsonObject.get("MobileGrading");
            JSONArray Questions = (JSONArray) grading.get("Question");
            List<QrCode> qrCodes = new ArrayList<>();
            for(int i=0 ;i< Questions.size();i++){
                QrCode qrCode = new QrCode();
                JSONObject current = (JSONObject)Questions.get(i);
                qrCode.setQUESTION((String) current.get("QuestionName"));
                qrCode.setQuestionSolution((String) current.get("QuestionSolution"));
                qrCode.setMaxGrade( Float.parseFloat(String.valueOf(current.get("MaxGrade"))));
                JSONArray tags = (JSONArray)current.get("tags");
                String str="";
                for(int j=0 ;j<tags.size();j++){
                    str= str + ((JSONObject)tags.get(j)).get("TagName")+":";
                    str= str + ((JSONObject)tags.get(j)).get("TagWeight")+";";
                }
                qrCode.setVALUES(str);
                Log.d(TAG, "read json : "+qrCode.toString());
                qrCodes.add(qrCode);
            }
            return qrCodes;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "readQRCode: exception");
            Log.e(TAG, "readQRCode: "+e.toString());
            return null;
        }
    }

    public List<Student> readStudents(File file){
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject StudentGrade = (JSONObject) jsonObject.get("StudentGrade");
            JSONArray Stu = (JSONArray) StudentGrade.get("Students");
            List<Student> stundents = new ArrayList<>();
            for(int i=0 ;i< Stu.size();i++){
                Student student = new Student();
                JSONObject current = (JSONObject)Stu.get(i);
                student.setStudentID((String) current.get("ID"));
                student.setFirstName((String) current.get("FirstName"));
                student.setLastName((String) current.get("LastName"));
                Log.d(TAG, "readStudents: "+student.toString());
                stundents.add(student);
            }
            return stundents;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "readQRCode: exception");
            Log.e(TAG, "readQRCode: "+e.toString());
            return null;
        }
    }
}
