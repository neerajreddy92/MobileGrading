package com.mobile.bolt.mobilegrading;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.bolt.AsyncTasks.SaveGradedImage;
import com.mobile.bolt.AsyncTasks.ShowNewGradableImage;
import com.mobile.bolt.DAO.ImageDAO;
import com.mobile.bolt.DAO.QRCodeDAO;
import com.mobile.bolt.DAO.StudentDao;
import com.mobile.bolt.Model.Image;
import com.mobile.bolt.Model.QrCode;
import com.mobile.bolt.Model.Student;
import com.mobile.bolt.similaritymeasure.GenerateCosineSimilarity;
import com.mobile.bolt.similaritymeasure.GenerateJaccardSimilarity;
import com.mobile.bolt.support.SelectedClass;
import com.mobile.bolt.support.SimilarityMethod;

import java.util.ArrayList;
import java.util.List;

public class GradingScreen extends AppCompatActivity {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DrawingView drawView;
    // TODO: Rename and change types of parameters
    // TODO: Rename and change types and number of parameters
    List<String> label;
    List<Integer> Weights;
    List<Integer> WeightsOld;
    private String imageLocation = null;
    private String TAG = "MobileGrading";
    private List<Image> images = null;
    private ImageDAO imageDAO = null;
    QRCodeDAO qrCodeDAO = null;
    String mCurrentPhotoPath;
    private final String test1 = "MobileGrading";
    Boolean LABEL_VIEW_TRUE = false;
    String QR_CODE_QUESTION = null;
    private String Question_Solution = null;
    private double GRADE;
    private double MAXGRADE = 0.0;
    private double GRADEACTUAL;
    ImageButton grade_increase;
    ImageButton grade_decrease;
    Button[] labelButton = null;
    Button[] weightView = null;
    Button[] removeButton = null;
    EditText commentsEnter = null;
    TextView gradeGenerated;
    TextView qSolution = null;
    List<LinearLayout> layout_horizontal = null;
    LinearLayout horizontalLayout = null;
    String ASUAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grading_screen);
        Bundle extras = getIntent().getExtras();
        ASUAD = extras.getString("ASUAD");
        Log.d(TAG, "onCreate: entered image fragment " + ASUAD);
        gradeGenerated = (TextView) findViewById(R.id.grade_show);
        imageDAO = new ImageDAO(getBaseContext());
        images = imageDAO.getAllNonGradedImageLocations(ASUAD);
        if (images != null && !images.isEmpty()) {
            imageLocation = images.get(0).getLocation();
            QR_CODE_QUESTION = images.get(0).getQrCodeSolution();
        } else {
            Log.e(TAG, "onCreate: images is empty or null");
            Toast.makeText(getBaseContext(), "No images available to grade", Toast.LENGTH_SHORT).show();
            finish();
        }
        final View rootView = findViewById(android.R.id.content);
        drawView = (DrawingView) rootView.findViewById(R.id.drawing);
        ImageButton save = (ImageButton) rootView.findViewById(R.id.process_next);
        ImageButton showLabels = (ImageButton) rootView.findViewById(R.id.process_show_labels);
        if (imageLocation != null) {
            if (generateQuestionWeights(rootView)) {
                new ShowNewGradableImage(this, drawView).execute(imageLocation);
                showLabels.setEnabled(true);
            } else finish();
        } else {
            showLabels.setEnabled(false);
            Toast.makeText(getBaseContext(), "No images available to grade", Toast.LENGTH_SHORT).show();
            finish();
        }
        drawView.setDrawingCacheEnabled(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLocation != null) {
                    dispatchDisplayNextImage(rootView);
                }
            }
        });
        rootView.findViewById(R.id.process_refresh_labels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_horizontal != null) {
                    for (LinearLayout layout : layout_horizontal)
                        layout.setVisibility(View.GONE);
                }
                generateQuestionWeights(rootView);
                dispatchGenerateGrade();
            }
        });
        ImageButton undo = (ImageButton) rootView.findViewById(R.id.process_erase);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.undo();
            }
        });
        showLabels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLableView(rootView);
            }
        });
        ImageButton done = (ImageButton) rootView.findViewById(R.id.process_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private boolean generateQuestionWeights(View rootView) {
        if (!QRcodeRetreive()) {
            Log.d(TAG, "onCreateView: seeing show labels as false");
            rootView.findViewById(R.id.process_show_labels).setEnabled(false);
            Toast.makeText(this, "Warning: Question doesn't have respective labels in the database", Toast.LENGTH_SHORT).show();
            this.finish();
            return false;
        }
        createLabelButtons(rootView);
        return true;
    }

    private void toggleLableView(View rootView) {
        if (LABEL_VIEW_TRUE) {
            for (int i = 0; i < label.size(); i++) {
                weightView[i].setEnabled(false);
                weightView[i].setVisibility(drawView.GONE);
                labelButton[i].setEnabled(false);
                labelButton[i].setVisibility(drawView.GONE);
                removeButton[i].setEnabled(false);
                removeButton[i].setVisibility(drawView.GONE);
            }
            qSolution.setEnabled(false);
            grade_increase.setVisibility(View.INVISIBLE);
            grade_decrease.setVisibility(View.INVISIBLE);
            qSolution.setVisibility(drawView.GONE);
            commentsEnter.setEnabled(false);
            commentsEnter.setVisibility(drawView.GONE);
            rootView.findViewById(R.id.process_refresh_labels).setEnabled(false);
            rootView.findViewById(R.id.process_refresh_labels).setVisibility(drawView.GONE);
            Log.d(TAG, "toggleLableView: disabling label view");
            LABEL_VIEW_TRUE = false;
            Log.d(TAG, "dispatchDisplayNextImage: chanigng label view to " + false);
        } else {
            for (int i = 0; i < label.size(); i++) {
                if (Weights.get(i) == 0) {
                    weightView[i].setEnabled(false);
                    weightView[i].setVisibility(View.VISIBLE);
                    labelButton[i].setEnabled(false);
                    labelButton[i].setVisibility(View.VISIBLE);
                    removeButton[i].setEnabled(false);
                    removeButton[i].setVisibility(View.VISIBLE);
                    continue;
                }
                grade_increase.setVisibility(View.VISIBLE);
                grade_decrease.setVisibility(View.VISIBLE);
                weightView[i].setEnabled(true);
                weightView[i].setVisibility(View.VISIBLE);
                labelButton[i].setEnabled(true);
                labelButton[i].setVisibility(View.VISIBLE);
                removeButton[i].setEnabled(true);
                removeButton[i].setVisibility(View.VISIBLE);
                Log.d(TAG, "toggleLableView: showing label view");
            }
            qSolution.setEnabled(true);
            qSolution.setVisibility(View.VISIBLE);
            commentsEnter.setEnabled(true);
            commentsEnter.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.process_refresh_labels).setEnabled(true);
            rootView.findViewById(R.id.process_refresh_labels).setVisibility(View.VISIBLE);
            LABEL_VIEW_TRUE = true;
            Log.d(TAG, "dispatchDisplayNextImage: chanigng label view to " + true);
        }
    }


    private Boolean QRcodeRetreive() {
        if (QR_CODE_QUESTION == null) return false;
        qrCodeDAO = new QRCodeDAO(getBaseContext());
        QrCode qrCode = qrCodeDAO.getSingleQRcodeLocationOnQuestion(QR_CODE_QUESTION);
        if (qrCode == null) {
            return false;
        }
        String val = qrCode.getVALUES();
        label = new ArrayList<String>();
        Weights = new ArrayList<Integer>();
        WeightsOld = new ArrayList<Integer>();
        String current = "";
        for (int i = 0; i < val.length(); i++) {
            char curr = val.charAt(i);
            if (curr == ':') {
                label.add(current);
                current = "";
                continue;
            }
            if (curr == ';') {
                Weights.add(Integer.parseInt(current));
                WeightsOld.add(Integer.parseInt(current));
                current = "";
                continue;
            }
            current = current + curr;
        }
        Question_Solution = qrCode.getQuestionSolution();
        MAXGRADE = qrCode.getMaxGrade();
        GRADE = MAXGRADE;
        GRADEACTUAL = MAXGRADE;
        Log.d(TAG, "QRcodeRetreive: qr code labels" + label.toString());
        Log.d(TAG, "QRcodeRetreive: qr code weights" + Weights.toString());
        return true;
    }

    private void createLabelButtons(View rootView) {
        if (label == null || Weights == null) {
            return;
        }
        LinearLayout labelBox = (LinearLayout) rootView.findViewById(R.id.label_box);
        if (labelButton != null) {
            for (int i = 0; i < labelButton.length; i++) {
                labelButton[i].setVisibility(View.GONE);
                weightView[i].setVisibility(View.GONE);
                removeButton[i].setVisibility(View.GONE);
            }
            horizontalLayout.setVisibility(drawView.GONE);
        }
        grade_increase = (ImageButton) findViewById(R.id.grade_increase);
        grade_decrease = (ImageButton) findViewById(R.id.grade_decrease);
        grade_increase.setOnClickListener(onClickForGradeIncrease());
        grade_decrease.setOnClickListener(onClickForGradeDecrease());
        labelButton = new Button[label.size()];
        weightView = new Button[label.size()];
        removeButton = new Button[label.size()];
        qSolution = (TextView) rootView.findViewById(R.id.QuestionSolution);
        qSolution.setText(Question_Solution + " MAX GRADE : " + MAXGRADE);
        gradeGenerated.setText(String.valueOf(MAXGRADE));
        commentsEnter = (EditText) rootView.findViewById(R.id.commentText);
        commentsEnter.setText("");
        layout_horizontal = new ArrayList<>();
        for (int i = 0; i < label.size(); i++) {
            labelButton[i] = new Button(getBaseContext());
            labelButton[i].setText(label.get(i));
            labelButton[i].setTransformationMethod(null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 0, 0, 0);
            labelButton[i].setLayoutParams(params);
            weightView[i] = new Button(getBaseContext());
            weightView[i].setText(String.valueOf(Weights.get(i)));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.WRAP_CONTENT);
            weightView[i].setLayoutParams(lp);
            removeButton[i] = new Button(getBaseContext());
            removeButton[i].setText("X");
            removeButton[i].setLayoutParams(lp);
            labelButton[i].setBackgroundColor(Color.TRANSPARENT);
            weightView[i].setBackgroundColor(Color.TRANSPARENT);
            if (!LABEL_VIEW_TRUE) {
                removeButton[i].setVisibility(drawView.GONE);
                labelButton[i].setVisibility(drawView.GONE);
                weightView[i].setVisibility(drawView.GONE);
                labelButton[i].setEnabled(false);
                removeButton[i].setEnabled(false);
                weightView[i].setEnabled(false);
            }
            removeButton[i].setBackgroundColor(Color.TRANSPARENT);
            LinearLayout layout = new LinearLayout(getBaseContext());
            layout_horizontal.add(layout);
            horizontalLayout = layout;
            layout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.setMargins(0, 30, 0, 0);
            layout.setLayoutParams(params1);
            layout.setBackgroundColor(0xFF6886e0);
            layout.addView(labelButton[i]);
            layout.addView(weightView[i]);
            layout.addView(removeButton[i]);
            labelBox.addView(layout);
            labelButton[i].setOnClickListener(handleOnClickforLabel(labelButton[i], weightView[i], removeButton[i], i, layout));
            removeButton[i].setOnClickListener(handleOnClickforRemove(removeButton[i], weightView[i], labelButton[i], i, layout));
        }
    }

    View.OnClickListener handleOnClickforLabel(final Button labelButton, final Button weightButton, final Button removeButton, final int i, final LinearLayout layout) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Weights.set(i, Weights.get(i) - 1);
                if (Weights.get(i) == 0) {
                    weightButton.setEnabled(false);
                    labelButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    layout.setBackgroundColor(Color.GRAY);
                } else {
                    weightButton.setText(String.valueOf(Weights.get(i)));
                    layout.setBackgroundColor(Color.RED);
                }
                dispatchGenerateGrade();
            }
        };
    }

    View.OnClickListener handleOnClickforRemove(final Button removeButton, final Button weightButton, final Button labelButton, final int i, final LinearLayout layout) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Weights.set(i, 0);
                weightButton.setEnabled(false);
                labelButton.setEnabled(false);
                removeButton.setEnabled(false);
                layout.setBackgroundColor(Color.GRAY);
                dispatchGenerateGrade();
            }
        };
    }

    private void dispatchDisplayNextImage(View rootview) {
        if (images != null && !images.isEmpty()) {
            Image image = images.get(0);
            if (LABEL_VIEW_TRUE) {
                toggleLableView(rootview);
            }
            drawView.setDrawingCacheEnabled(true);
            Bitmap bMap = drawView.getDrawingCache(true).copy(Bitmap.Config.RGB_565, false);
            drawView.destroyDrawingCache();
            image.setQuestionComments(commentsEnter.getText().toString());
            image.setGrade((float) GRADE);
            image.setGradeActual((float) GRADEACTUAL);
            Student student = new Student();
            student.setStatus(2);
            student.setStudentID(ASUAD);
            new SaveGradedImage(getBaseContext(), student).execute(image, bMap, label, Weights);
            images.remove(0);
            if (!images.isEmpty()) {
                imageLocation = images.get(0).getLocation();
                QR_CODE_QUESTION = images.get(0).getQrCodeSolution();
                LABEL_VIEW_TRUE = false;
                Log.d(TAG, "dispatchDisplayNextImage: changing label view to " + false);
                if (generateQuestionWeights(rootview)) {
                    new ShowNewGradableImage(this, drawView).execute(imageLocation);
                } else finish();
            } else {
                imageLocation = null;
                new StudentDao(this).updateStatus(SelectedClass.getInstance().getCurrentClass(), student);
                Toast.makeText(getBaseContext(), "Image Saved. No more gradable images available", Toast.LENGTH_SHORT).show();
                this.finish();
            }
            dispatchGenerateGrade();
        }
    }

    View.OnClickListener onClickForGradeIncrease() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (GRADE < MAXGRADE) {
                    GRADE = GRADE + 0.1;
                    GRADE = (double) Math.round(GRADE * 100d) / 100d;
                    gradeGenerated.setText(String.valueOf(GRADE));
                } else {
                    Toast.makeText(getBaseContext(), "Cannot Increase: MaximumGrade = " + MAXGRADE, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    View.OnClickListener onClickForGradeDecrease() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (GRADE > 0) {
                    GRADE = GRADE - 0.1;
                    GRADE = (double) Math.round(GRADE * 100d) / 100d;
                    gradeGenerated.setText(String.valueOf(GRADE));
                }
            }
        };
    }

    private void dispatchGenerateGrade() {
        Double pc;
        double saliency = 1.0;
        double similarity = 0;
        switch (SimilarityMethod.getInstance().getMethodValue()) {
            case 1:
                similarity = GenerateCosineSimilarity.generate(WeightsOld, Weights);
                break;
            case 2:
                similarity = GenerateJaccardSimilarity.generate(WeightsOld, Weights);
                break;
        }
        double numerator = 0, denominator = 0;
        for (int i = 0; i < WeightsOld.size(); i++) {
            numerator = numerator + (double) Weights.get(i);
            denominator = denominator + (double) WeightsOld.get(i);
        }
        saliency = numerator / denominator;
        pc = saliency * similarity;
        Log.d(TAG, "dispatchGenerateGrade: similarity" + similarity);
        Log.d(TAG, "dispatchGenerateGrade: saliency" + saliency);
        if (pc < 0 || pc.isNaN()) pc = 0.0;
        GRADEACTUAL = pc * MAXGRADE;
        GRADEACTUAL = (double) Math.round(GRADEACTUAL * 100d) / 100d;
        GRADE = GRADEACTUAL;
        gradeGenerated.setText(String.valueOf(GRADEACTUAL));
    }
}
