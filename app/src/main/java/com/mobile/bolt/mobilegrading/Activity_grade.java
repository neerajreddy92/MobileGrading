package com.mobile.bolt.mobilegrading;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobile.bolt.DAO.StudentContractHelper;
import com.mobile.bolt.Model.Student;
import android.content.Context;
public class Activity_grade extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        final EditText id = (EditText) findViewById(R.id.student_id);
        final EditText fName = (EditText) findViewById(R.id.first_name);
        final EditText lName = (EditText) findViewById(R.id.last_name);
        Button btr = (Button) findViewById(R.id.verify_studentId);
        Button btr1 = (Button) findViewById(R.id.continue_grading);
        final StudentContractHelper studentDAO =new StudentContractHelper(this);
        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student stu= studentDAO.getStudent(id.getText().toString());
                if(stu!=null){
                   try {
                       Toast.makeText(getBaseContext(), "Already Exists in the database", Toast.LENGTH_LONG).show();
                       fName.setText(stu.getFirstName());
                       lName.setText(stu.getLastName());
                   }catch (NullPointerException e) {
                       e.printStackTrace();
                       Log.e("MobileGrading", "onClick: Null pointer returned");
                   }
                   }else{
                    Toast.makeText(getBaseContext(), "Dosent Exist in the database", Toast.LENGTH_LONG).show();
                }
            }
        });
        btr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student stu=new Student();
                stu.setStudentID(id.getText().toString());
                stu.setFirstName(fName.getText().toString());
                stu.setLastName(lName.getText().toString());
                if(studentDAO.addStudent(stu)){
                    Toast.makeText(getBaseContext(), "Added to database", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Already Exists", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(Activity_grade.this, Activity_grade_process.class);
                Activity_grade.this.startActivity(intent);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Activity_grade Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.mobile.bolt.mobilegrading/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Activity_grade Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.mobile.bolt.mobilegrading/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
