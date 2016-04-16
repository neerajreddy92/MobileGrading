package com.mobile.bolt.mobilegrading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.bolt.AsyncTasks.WriteOutput;
import com.mobile.bolt.AsyncTasks.WriteOutputNow;
import com.mobile.bolt.Model.Student;

import java.util.List;

/**
 * Created by Neeraj on 4/10/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{
    private Context context;
    private String TAG = "MobileGrading";
    private Activity activity;
    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView ASUID;
        TextView FirstName;
        TextView LastName;
        ImageButton TakePicture;
        Button StartGrading;
        Button GenOutput;
        ImageView emptyStar;
        ImageView halfStar;
        ImageView fullStar;
        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            FirstName = (TextView)itemView.findViewById(R.id.First_name);
            ASUID = (TextView)itemView.findViewById(R.id.ASU_id);
            LastName = (TextView)itemView.findViewById(R.id.last_name);
            TakePicture = (ImageButton) itemView.findViewById(R.id.take_picture);
            StartGrading = (Button) itemView.findViewById(R.id.Grade);
            GenOutput = (Button) itemView.findViewById(R.id.output);
            emptyStar = (ImageView) itemView.findViewById(R.id.empty_star);
            halfStar = (ImageView) itemView.findViewById(R.id.half_star);
            fullStar = (ImageView) itemView.findViewById(R.id.full_star);
        }
    }
    private List<Student> students ;
    RVAdapter(List<Student> students, Context context, Activity activity){
        this.students = students;
        this.context =context;
        this.activity = activity;
    }
    @Override
    public int getItemCount() {
        return students.size();
    }
    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }
    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder,  final int i) {
        personViewHolder.ASUID.setText(students.get(i).getStudentID());
        personViewHolder.FirstName.setText(students.get(i).getFirstName());
        personViewHolder.LastName.setText(students.get(i).getLastName());
        final Student student = students.get(i);
        String uri = "@drawable/myresource";  // where myresource (without the extension) is the file
        switch (students.get(i).getStatus()){
            case 0:
                personViewHolder.emptyStar.setVisibility(View.VISIBLE);
                break;
            case 1:
                personViewHolder.halfStar.setVisibility(View.VISIBLE);
                break;
            case 2:
                personViewHolder.fullStar.setVisibility(View.VISIBLE);
                break;
            case 3:
                personViewHolder.fullStar.setVisibility(View.VISIBLE);
                break;
        }
        personViewHolder.TakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TakePicture(context,activity,student.getStudentID());
            }
        });
        personViewHolder.StartGrading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "message";
                Intent intent = new Intent(context, GradingScreen.class);
                intent.putExtra("ASUAD", student.getStudentID());
                Log.d(TAG, "onClick: "+student.toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        personViewHolder.GenOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 4/15/2016 add checks to see if any output is available
                new WriteOutputNow(context).execute(student);
            }
        });
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}