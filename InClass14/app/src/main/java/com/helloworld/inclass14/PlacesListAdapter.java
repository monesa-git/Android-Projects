package com.helloworld.inclass14;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlacesListAdapter extends RecyclerView.Adapter<PlacesListAdapter.MyViewHolder> {
    private ArrayList<PlacesDetails> mDataset;
    public static InteractWithRecyclerView interactPlaces;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlacesListAdapter(ArrayList<PlacesDetails> myDataset, Context ctx) {
        mDataset = myDataset;
        interactPlaces = (InteractWithRecyclerView) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlacesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.places_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        PlacesDetails placesDetails = mDataset.get(position);
        holder.placeName.setText(placesDetails.name);

        if(!placesDetails.icon.equals("")){
            Picasso.get()
                    .load(placesDetails.icon)
                    .into(holder.placeImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }

        holder.placeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactPlaces.selectedItem(mDataset.get(position));
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
        ImageButton placeAdd;
        TextView placeName;
        ImageView placeImage;
        public MyViewHolder(View view) {
            super(view);
            placeAdd = view.findViewById(R.id.placeAdd);
            placeImage = view.findViewById(R.id.placeImage);
            placeName = view.findViewById(R.id.placeName);
        }

    }
    //
    public interface InteractWithRecyclerView{
        public void selectedItem(PlacesDetails placesDetails);
//        public void getDetails(Expense expense, int position);
    }
}
