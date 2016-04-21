package com.mobile.bolt.mobilegrading;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobile.bolt.AsyncTasks.GenQRCode;
import com.mobile.bolt.AsyncTasks.ParseNewClasses;
import com.mobile.bolt.AsyncTasks.ParsingQRcode;
import com.mobile.bolt.DAO.StudentDao;
import com.mobile.bolt.Model.Student;
import com.mobile.bolt.support.FilterRecyclerView;
import com.mobile.bolt.support.PictureValues;
import com.mobile.bolt.support.PresortedSearch;
import com.mobile.bolt.support.SelectedClass;
import com.mobile.bolt.support.SimilarityMethod;
import com.mobile.bolt.support.StudentFeeder;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
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
    private static final int DIALOG_ENTER_NEW_STUDENT = 750;
    private SelectedClass selectedClass;
    private RVAdapter adapter;
    private Integer status = 0;
    List<Student> students;
    List<String> similarityMeasures;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StudentFeeder.feed(getBaseContext());
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        selectedClass = SelectedClass.getInstance();
        TextView tx = (TextView) findViewById(R.id.textView6);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/android_7.ttf");
        tx.setTypeface(custom_font);
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick: floating action button is clicked");
                onCreateDialog(750);
            }
        });
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(llm);
//        students = new StudentDao(getBaseContext()).getAllStudents("newTable");
        students = new ArrayList<>();
        adapter = new RVAdapter(students, getBaseContext(), this);
        rv.setAdapter(adapter);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        similarityMeasures= new ArrayList<>();
        similarityMeasures.add("cosine");
        similarityMeasures.add("jaccard");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        List<String> items = new StudentDao(getBaseContext()).getTabels();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);// set the adapter to provide layout of rows and content
        spinner.setOnItemSelectedListener(this);

        MenuItem item_select = menu.findItem(R.id.spinner_search);
        Spinner spinner_select = (Spinner) MenuItemCompat.getActionView(item_select);
        List<String> items_select = PresortedSearch.generateList();
        final ArrayAdapter<String> adapter_select = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, items_select);
        adapter_select.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_select.setAdapter(adapter_select);// set the adapter to provide layout of rows and content
        spinner_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status = PresortedSearch.whatType((String) parent.getItemAtPosition(position));
                dispatchQuery("");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        MenuItem item_similarity = menu.findItem(R.id.spinner_similarity);
        Spinner spinner_similarity = (Spinner) MenuItemCompat.getActionView(item_similarity);
        List<String> items_similarity = similarityMeasures;
        final ArrayAdapter<String> adapter_similarity = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, items_similarity);
        adapter_similarity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_similarity.setAdapter(adapter_similarity);// set the adapter to provide layout of rows and content
        spinner_similarity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SimilarityMethod.getInstance().setMethod((String) parent.getItemAtPosition(position));
                Log.d(TAG, "onItemSelected: setting similarity method" + (String) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dispatchQuery(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                dispatchQuery(query);
                return true;
            }
        });
        return true;
    }

    private void dispatchQuery(String query) {
        adapter.updateList(FilterRecyclerView.filter(students, query, status));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.add_class:
                loadFileListforClassesParsing();
                onCreateDialog(500);
                return true;
            case R.id.add_questions:
                loadFileListforQrcodeParsing();
                onCreateDialog(1000);
                return true;
            case R.id.refresh_page:
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.mobile.bolt.mobilegrading/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
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
                    return filename.contains(FTYPE1) || filename.contains(FTYPE2) || filename.contains(FTYPE3) || filename.contains(FTYPE4) || sel.isDirectory();
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
                    return filename.contains(FTYPE3) || filename.contains(FTYPE4) || sel.isDirectory();
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
            case DIALOG_ENTER_NEW_STUDENT:
                builder.setTitle("Enter a new Student:");
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.new_student_dialog, null);
                builder.setView(dialogView);
                builder.setPositiveButton("create class", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Student student = new Student();
                        student.setStudentID(((EditText) dialogView.findViewById(R.id.asuad_dialog)).getText().toString());
                        student.setFirstName(((EditText) dialogView.findViewById(R.id.firstname_dialog)).getText().toString());
                        student.setLastName(((EditText) dialogView.findViewById(R.id.lastname_dialog)).getText().toString());
                        addNewStudent(student);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        new GenQRCode(getBaseContext(),PictureValues.getInstance().getASUAD()).execute(PictureValues.getInstance().getPhotoPath(), PictureValues.getInstance().getASUAD()); //starting async task to genrate qr code.
    }

    public void addNewStudent(Student student) {
        Log.d(TAG, "addNewStudent: " + student.toString());
        StudentDao studentDao = new StudentDao(getBaseContext());
        studentDao.addStudent(selectedClass.getCurrentClass(),student);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        students = new StudentDao(getBaseContext()).getAllStudents((String) parent.getItemAtPosition(position));
        adapter.updateList(FilterRecyclerView.filter(students, "", status));
        selectedClass.setCurrentClass((String) parent.getItemAtPosition(position));
        Log.d(TAG, "onItemSelected: new class item selected" + (String) parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}