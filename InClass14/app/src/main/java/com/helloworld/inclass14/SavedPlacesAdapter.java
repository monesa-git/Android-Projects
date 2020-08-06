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

public class SavedPlacesAdapter extends RecyclerView.Adapter<SavedPlacesAdapter.MyViewHolder> {
    private ArrayList<PlacesDetails> mDataset;
    private CityInfo cityInfoObject;
    public static SavedPlacesAdapter.InteractWithRecyclerViewPlaces interactPlace;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SavedPlacesAdapter(CityInfo myDataset, SavedCityAdapter ctx) {
        mDataset = myDataset.placesDetailsArrayList;
        cityInfoObject = myDataset;
        interactPlace = (SavedPlacesAdapter.InteractWithRecyclerViewPlaces) ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SavedPlacesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_places_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        PlacesDetails placesDetails = mDataset.get(position);
        holder.textViewSavedPlace.setText(placesDetails.name);

        if(!placesDetails.icon.equals("")) {
            Picasso.get()
                    .load(placesDetails.icon)
                    .into(holder.imageViewSavedPlace, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }

        holder.imageButtonSavedPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactPlace.selectedItem(cityInfoObject, position);
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
        ImageView imageViewSavedPlace;
        TextView textViewSavedPlace;
        ImageButton imageButtonSavedPlace;
        public MyViewHolder(View view) {
            super(view);
            imageViewSavedPlace = view.findViewById(R.id.imageViewSavedPlace);
            textViewSavedPlace = view.findViewById(R.id.textViewSavedPlace);
            imageButtonSavedPlace = view.findViewById(R.id.imageButtonSavedPlace);
        }

    }
    //
    public interface InteractWithRecyclerViewPlaces{
        public void selectedItem(CityInfo cityInfoOnject , int pointer);
    }
}
