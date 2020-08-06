package com.helloworld.inclass15;

import android.content.Context;
import android.util.Log;
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

public class TodoTaskAdapter extends RecyclerView.Adapter<TodoTaskAdapter.MyViewHolder> {
    private ArrayList<TodoClass> mDataset;
    public static InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TodoTaskAdapter(ArrayList<TodoClass> myDataset, Context ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TodoTaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TodoClass todoClass = mDataset.get(position);
        holder.todo_name.setText(todoClass.task_name);
        holder.checkBox.setChecked(todoClass.checked);

        PrettyTime pt = new PrettyTime(Locale.getDefault());
        String timeGot = todoClass.date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long time = null;
        try {
            time = sdf.parse(timeGot).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formatted = pt.format(new Date(time));

        holder.date.setText(formatted);
        holder.priority.setText(todoClass.priority);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interact.getDetails(mDataset.get(position), position);
            }
        });

        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
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
        TextView todo_name, priority, date;
        CheckBox checkBox;
        ConstraintLayout constraintLayout;
        public MyViewHolder(View view) {
            super(view);
            todo_name = view.findViewById(R.id.todo_name);
            priority = view.findViewById(R.id.priority);
            date = view.findViewById(R.id.date);
            checkBox = view.findViewById(R.id.checkBox);
            constraintLayout = view.findViewById(R.id.constraintLayout);
        }

    }

    public interface InteractWithRecyclerView{
        public void selectedItem(TodoClass todoClass);
        public void getDetails(TodoClass todoClass, int position);
    }



}
