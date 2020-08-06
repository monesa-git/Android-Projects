package com.helloworld.finalexam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {
    private ArrayList<ScheduleClass> mDataset;
    public static InteractWithRecyclerViewMain interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ScheduleAdapter(ArrayList<ScheduleClass> myDataset, Context ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerViewMain) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ScheduleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ScheduleClass scheduleClass = mDataset.get(position);
        holder.meetingName.setText(scheduleClass.meeting_name);
        holder.meetingPlace.setText(scheduleClass.meeting_location);
        holder.meetingDate.setText(scheduleClass.meeting_date);

        holder.scheduleConstraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interact.selectedItem(mDataset.get(position));
                return false;
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        TextView meetingName, meetingPlace, meetingDate;
        ConstraintLayout scheduleConstraintLayout;
        public MyViewHolder(View view) {
            super(view);
            meetingName = view.findViewById(R.id.meetingName);
            meetingPlace = view.findViewById(R.id.meetingPlace);
            meetingDate = view.findViewById(R.id.meetingDate);
            scheduleConstraintLayout = view.findViewById(R.id.scheduleConstraintLayout);
        }

    }

    public interface InteractWithRecyclerViewMain{
        public void selectedItem(ScheduleClass scheduleClass);
        public void getDetails(ScheduleClass scheduleClass, int position);
    }

}
