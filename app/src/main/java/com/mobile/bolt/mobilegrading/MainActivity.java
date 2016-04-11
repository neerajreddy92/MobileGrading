package com.mobile.bolt.mobilegrading;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobile.bolt.AsyncTasks.ParseNewClasses;
import com.mobile.bolt.AsyncTasks.ParsingQRcode;
import com.mobile.bolt.support.StudentFeeder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    RecyclerView rv;
    private GoogleApiClient client;
    private String TAG = "MobileGrading";
    private String[] mFileList;
    private File mPath = new File(Environment.getExternalStorageDirectory() + "//Documents//QrcodesData//");
    private File cPath = new File(Environment.getExternalStorageDirectory() + "//Documents//studentsData//");
    private String mChosenFile;
    private static final String FTYPE1 = ".XML";
    private static final String FTYPE2 = ".xml";
    private static final String FTYPE3 = ".json";
    private static final String FTYPE4 = ".JSON";
    private static final int DIALOG_LOAD_FILE_QR_CODE = 1000;
    private static final int DIALOG_LOAD_FILE_CLASSES = 500;

    // TODO: 2/25/2016 add interface for file upload and json parsing.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StudentFeeder.feed(getBaseContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Button btr;
        btr = (Button) findViewById(R.id.main_button);
        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_grade.class);
                MainActivity.this.startActivity(intent);
            }
        });
        Button parserbtn = (Button) findViewById(R.id.parse_qrcodes);
        parserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFileListforQrcodeParsing();
                onCreateDialog(1000);
            }
        });
        Button writeOutput = (Button) findViewById(R.id.write_output);
        writeOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, saveActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(llm);
        RVAdapter adapter = new RVAdapter(new StudentDAO(getBaseContext()).getAllStudents("newTable"));
        rv.setAdapter(adapter);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        List<String> items = new StudentDAO(getBaseContext()).getTabels();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);// set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(this);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        getMenuInflater().inflate(R.menu.menu_activity_main, item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.add_class:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                loadFileListforClassesParsing();
                onCreateDialog(500);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
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
                "Main Page", // TODO: Define a title for the content shown.
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

    private void parseQrcode(String path) {
        if (path != null) {
            new ParsingQRcode(getBaseContext()).execute(path);

        } else
            Log.e(TAG, "parseQrcode: MainActivity  path is null");
    }

    private void loadFileListforQrcodeParsing() {
        try {
            mPath.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card " + e.toString());
        }
        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains(FTYPE1) || filename.contains(FTYPE2) ||filename.contains(FTYPE3) ||filename.contains(FTYPE4) || sel.isDirectory();
                }
            };
            mFileList = mPath.list(filter);
        } else {
            mFileList = new String[0];
        }
    }

    private void loadFileListforClassesParsing() {
        try {
            cPath.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card " + e.toString());
        }
        if (cPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains(FTYPE3) ||filename.contains(FTYPE4) || sel.isDirectory();
                }
            };
            mFileList = cPath.list(filter);
        } else {
            mFileList = new String[0];
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_LOAD_FILE_QR_CODE:
                builder.setTitle("Choose your file");
                if (mFileList == null) {
                    Log.e(TAG, "Showing file picker before loading the file list");
                    dialog = builder.create();
                    return dialog;
                }
                builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mChosenFile = mFileList[which];
                        String filePath = mPath.getAbsolutePath() + "/" + mChosenFile;
                        Toast.makeText(getBaseContext(), mChosenFile, Toast.LENGTH_LONG).show();
                        parseQrcode(filePath);
                    }
                });
                break;
            case DIALOG_LOAD_FILE_CLASSES:
                builder.setTitle("Choose your file");
                LinearLayout layout = new LinearLayout(getBaseContext());
                final RadioGroup radioGroup = new RadioGroup(getBaseContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                for (String file : mFileList) {
                    RadioButton radioButton = new RadioButton(getBaseContext());
                    radioButton.setText(file);
                    radioButton.setTextColor(Color.BLACK);
                    radioGroup.addView(radioButton);
                }
                final EditText editText = new EditText(getBaseContext());
                editText.setHint("Enter the class name");
                editText.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                editText.setTextColor(Color.BLACK);
                layout.addView(editText);
                layout.addView(radioGroup);
                builder.setView(layout);
                if (mFileList == null) {
                    Log.e(TAG, "Showing file picker before loading the file list");
                    dialog = builder.create();
                    return dialog;
                }
                builder.setPositiveButton("create class", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!(editText.getText().toString().matches(""))) {
                            if (radioGroup.getCheckedRadioButtonId() == -1) {
                                new ParseNewClasses(getBaseContext()).execute(String.valueOf(editText.getText()), "");
                                Log.d(TAG, "onClick: " + editText.getText());
                            } else {
                                String str = (String) (((RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())).getText());
                                str = cPath.getAbsolutePath() + "/" + str;
                                new ParseNewClasses(getBaseContext()).execute(editText.getText().toString(), str);
                                Log.d(TAG, "onClick: " + str);
                                Log.d(TAG, "onClick: " + editText.getText().toString());
                            }
                        } else
                            Log.d(TAG, "onClick: no class name entered");
                    }
                })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                break;
        }
        dialog = builder.show();
        return dialog;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        rv.removeAllViews();
        Log.d(TAG, "onItemSelected: new class item selected" + (String) parent.getItemAtPosition(position));
        rv.setAdapter(new RVAdapter(new StudentDAO(getBaseContext()).getAllStudents((String) parent.getItemAtPosition(position))));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}