package com.helloworld.inclass08;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.MyViewHolder> {
    private ArrayList<Emails> mDataset;
    public static InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public EmailAdapter(ArrayList<Emails> myDataset, Context ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EmailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_item_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Emails email = mDataset.get(position);
        holder.subject.setText(email.subject);
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat OutputFormat = new SimpleDateFormat("MMM dd, YYYY");
        try {
            date = format.parse(email.created_at);
            email.created_at = OutputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.date.setText(email.created_at);

        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Demo","Selected Choice is : "+mDataset.get(position));
                interact.selectedItem(mDataset.get(position));
            }
        });

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Demo","Selected Position is :" + mDataset.get(position));
                interact.getDetails(mDataset.get(position));
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
        ImageButton option;
        TextView subject, date;
        ConstraintLayout constraintLayout;
        public MyViewHolder(View view) {
            super(view);
            option = view.findViewById(R.id.option);
            subject = view.findViewById(R.id.textViewSubject);
            date = view.findViewById(R.id.textViewDate);
            constraintLayout = view.findViewById(R.id.item_layout_id);
        }

    }

    public interface InteractWithRecyclerView{
        public void selectedItem(Emails emailObject);
        public void getDetails(Emails emailObject);
    }



}
