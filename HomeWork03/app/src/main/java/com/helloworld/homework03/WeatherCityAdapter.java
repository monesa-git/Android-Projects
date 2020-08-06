package com.helloworld.homework03;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherCityAdapter extends RecyclerView.Adapter<WeatherCityAdapter.MyViewHolder> {
    private ArrayList<ForecastClass> mDataset;
    public static InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public WeatherCityAdapter(ArrayList<ForecastClass> myDataset, Context ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WeatherCityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_cites_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ForecastClass forecastClass = mDataset.get(position);
        holder.temptext.setText(forecastClass.temperature.toString() + " F");
        holder.citycountrytext.setText(forecastClass.cityName +"-" +forecastClass.stateName + ", " + forecastClass.countryName);

        if(forecastClass.favourite == true){
            holder.imageButton.setImageResource(R.drawable.staryelloq);
        }else{
            holder.imageButton.setImageResource(R.drawable.star);
        }
//        Date date = null;
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat OutputFormat = new SimpleDateFormat("MMM dd, YYYY");
//        try {
//            date = format.parse(email.created_at);
//            email.created_at = OutputFormat.format(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        holder.timeText.setText(forecastClass.updatedDate);

        holder.cityConstraint.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interact.getDetails(mDataset.get(position));
                return false;
            }
        });

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interact.selectedItem(position);
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
        TextView citycountrytext, temptext, timeText;
        ImageButton imageButton;
        ConstraintLayout cityConstraint;
        public MyViewHolder(View view) {
            super(view);
            citycountrytext = view.findViewById(R.id.citycountrytext);
            temptext = view.findViewById(R.id.temptext);
            timeText = view.findViewById(R.id.timeText);
            cityConstraint = view.findViewById(R.id.constraint);
            imageButton = view.findViewById(R.id.imageButton);
        }

    }

    public interface InteractWithRecyclerView{
        public void selectedItem(int position);
        public void getDetails(ForecastClass forecastClass);
    }




}
