package com.helloworld.homework03;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DailyForeCastAdapter extends RecyclerView.Adapter<DailyForeCastAdapter.MyViewHolder> {
    private ArrayList<DailyForeCast> mDataset;
    public static InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DailyForeCastAdapter(ArrayList<DailyForeCast> myDataset, Context ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DailyForeCastAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.five_days_forecast_list, parent, false);
        DailyForeCastAdapter.MyViewHolder vh = new DailyForeCastAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DailyForeCastAdapter.MyViewHolder holder, final int position) {
        DailyForeCast dailyForeCast = mDataset.get(position);
//        Date date = null;
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        SimpleDateFormat OutputFormat = new SimpleDateFormat("dd MMM, YY");
//        try {
//            date = format.parse(dailyForeCast.date);
//            dailyForeCast.date = OutputFormat.format(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        holder.textView.setText(dailyForeCast.date);
        String forecastDayImageIcon = dailyForeCast.dayImage.trim();
        if(forecastDayImageIcon.length()==1){
            forecastDayImageIcon = "0"+forecastDayImageIcon;
        }
        try {
            Picasso
                    .get()
                    .load("http://developer.accuweather.com/sites/default/files/" + URLEncoder.encode(forecastDayImageIcon,"UTF-8")  + "-s.png")
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            //Toast.makeText(DailyForeCastAdapter.this, "No Image found", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



//        holder.option.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("Demo","Selected Choice is : "+mDataset.get(position));
////                interact.selectedItem(mDataset.get(position));
//            }
//        });
//
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Demo","Selected Position is :" + mDataset.get(position));
                interact.getDetails(mDataset.get(position), position);
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
        ImageView imageView;
        TextView textView;
        ConstraintLayout constraintLayout;
        public MyViewHolder(View view) {
            super(view);
           imageView = view.findViewById(R.id.imageView4);
           textView = view.findViewById(R.id.fiveDayDate);
           constraintLayout = view.findViewById(R.id.ConstraintLayout);
        }

    }

    public interface InteractWithRecyclerView{
//        public void selectedItem(int position);
        public void getDetails(DailyForeCast dailyForeCast, int position);
    }



}
