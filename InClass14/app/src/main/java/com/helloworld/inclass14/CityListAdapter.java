package com.helloworld.inclass14;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.MyViewHolder> {
    private ArrayList<CityInfo> mDataset;
    public static InteractWithRecyclerView interact;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CityListAdapter(ArrayList<CityInfo> myDataset, Context ctx) {
        mDataset = myDataset;
        interact = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CityListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        CityInfo cityInfo = mDataset.get(position);
        holder.cityName.setText(cityInfo.name+", "+cityInfo.stateName);
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat OutputFormat = new SimpleDateFormat("MMM dd, YYYY");

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
        TextView cityName;
        ConstraintLayout constraintLayout;
        public MyViewHolder(View view) {
            super(view);
            cityName = view.findViewById(R.id.cityName);
            constraintLayout = view.findViewById(R.id.constraintLayout);
        }

    }

    public interface InteractWithRecyclerView{
//        public void selectedItem(Emails emailObject);
        public void getDetails(CityInfo cityInfo);
    }


}
