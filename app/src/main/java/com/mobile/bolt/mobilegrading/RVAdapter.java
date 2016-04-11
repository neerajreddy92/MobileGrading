package com.mobile.bolt.mobilegrading;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.bolt.Model.Student;

import java.util.List;

/**
 * Created by Neeraj on 4/10/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView ASUID;
        TextView FirstName;
        TextView LastName;
        ImageButton TakePicture;
        Button StartGrading;
        Button GenOutput;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            FirstName = (TextView)itemView.findViewById(R.id.ASU_id);
            ASUID = (TextView)itemView.findViewById(R.id.First_name);
            LastName = (TextView)itemView.findViewById(R.id.last_name);
            TakePicture = (ImageButton) itemView.findViewById(R.id.take_picture);
            StartGrading = (Button) itemView.findViewById(R.id.Grade);
            GenOutput = (Button) itemView.findViewById(R.id.output);
        }
    }
    private List<Student> students ;
    RVAdapter(List<Student> students){
        this.students = students;
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
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.ASUID.setText(students.get(i).getStudentID());
        personViewHolder.FirstName.setText(students.get(i).getFirstName());
        personViewHolder.LastName.setText(students.get(i).getLastName());
        personViewHolder.TakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        personViewHolder.StartGrading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        personViewHolder.GenOutput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}