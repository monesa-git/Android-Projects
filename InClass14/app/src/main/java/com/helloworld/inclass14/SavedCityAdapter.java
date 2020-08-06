package com.helloworld.inclass14;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SavedCityAdapter extends RecyclerView.Adapter<SavedCityAdapter.MyViewHolder> implements SavedPlacesAdapter.InteractWithRecyclerViewPlaces {
    private ArrayList<CityInfo> mDataset;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewPlaces;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mPlaceAdapter;
    private RecyclerView.LayoutManager placelayoutManager;
    public static SavedCityAdapter.InteractWithRecyclerView interactMain;
    Context mainCtx;


    // Provide a suitable constructor (depends on the kind of dataset)
    public SavedCityAdapter(ArrayList<CityInfo> myDataset, Context ctx) {
        mDataset = myDataset;
        interactMain = (SavedCityAdapter.InteractWithRecyclerView) ctx;
        mainCtx = ctx;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SavedCityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_trip_list, parent, false);
        SavedCityAdapter.MyViewHolder vh = new SavedCityAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SavedCityAdapter.MyViewHolder holder, final int position) {
        final CityInfo cityInfo = mDataset.get(position);
        holder.mainActivityTripName.setText(cityInfo.tripName);
        holder.mainActivityCityName.setText(cityInfo.name+", "+cityInfo.stateName);
        db = FirebaseFirestore.getInstance();
        holder.savedConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Demo","Selected Position is :" + mDataset.get(position));
//                interactMain.getDetails(mDataset.get(position));
            }
        });

        holder.addPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactMain.getDetails(mDataset.get(position),position);
            }
        });

        holder.viewMapPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactMain.getMapDetails(mDataset.get(position),position);
            }
        });
        recyclerViewPlaces = (RecyclerView) holder.savedPlacesMainRecyclerView;
        placelayoutManager = new LinearLayoutManager(mainCtx);
        recyclerViewPlaces.setLayoutManager(placelayoutManager);

//        // specify an adapter (see also next example)
        mPlaceAdapter = new SavedPlacesAdapter( mDataset.get(position),  this);
        recyclerViewPlaces.setAdapter(mPlaceAdapter);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void selectedItem(CityInfo cityInfoObject , int pointer) {
        interactMain.deleteItem(cityInfoObject, pointer);
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        TextView mainActivityCityName;
        TextView mainActivityTripName;
        ConstraintLayout savedConstraintLayout;
        ImageButton addPlaces;
        RecyclerView savedPlacesMainRecyclerView;
        ImageButton viewMapPlaces;
        public MyViewHolder(View view) {
            super(view);
            mainActivityCityName = view.findViewById(R.id.mainActivityCityName);
            mainActivityTripName = view.findViewById(R.id.mainActivityTripName);
            savedConstraintLayout = view.findViewById(R.id.savedConstraintLayout);
            addPlaces = view.findViewById(R.id.placesAddMain);
            savedPlacesMainRecyclerView = view.findViewById(R.id.savedPlacesMainRecyclerView);
            viewMapPlaces = view.findViewById(R.id.viewMapPlaces);
        }
    }

    public interface InteractWithRecyclerView{
        public void deleteItem(CityInfo cityInfoObject ,int pointer);
        public void getDetails(CityInfo cityInfo, int position);
        public void getMapDetails(CityInfo cityInfo, int position);
    }

}
