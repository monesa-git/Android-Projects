package com.helloworld.inclass14;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SavedCityAdapter.InteractWithRecyclerView{
    private RecyclerView recyclerViewPlaces;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    ArrayList<CityInfo> globalList = new ArrayList<>();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Trips");

        //getting the already saved trips from the collection
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        showProgressBarDialog();

        recyclerView = (RecyclerView) findViewById(R.id.savedRecyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        mAdapter = new SavedCityAdapter(globalList, MainActivity.this);
        recyclerView.setAdapter(mAdapter);
        db.collection("SavedTrips").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("TAG", "New Msg: " + dc.getDocument().toObject(CityInfo.class));
                            globalList.add(dc.getDocument().toObject(CityInfo.class));
                            break;
                        case MODIFIED:
                            Log.d("TAG", "Modified Msg: " + dc.getDocument().toObject(CityInfo.class));
                            CityInfo current = dc.getDocument().toObject(CityInfo.class);
                            int i=0;
                            for(CityInfo cityInfoObject : globalList){
                                if(cityInfoObject.tripName.equals(current.tripName)){
                                    if(cityInfoObject.name.equals(current.name)){
                                        cityInfoObject.placesDetailsArrayList = current.placesDetailsArrayList;
                                        globalList.get(i).placesDetailsArrayList = cityInfoObject.placesDetailsArrayList;
                                    }else{
                                        cityInfoObject.name = current.name;
                                        cityInfoObject.stateName = current.stateName;
                                        cityInfoObject.latitude = current.latitude;
                                        cityInfoObject.longitude = current.longitude;
                                        cityInfoObject.placesDetailsArrayList = current.placesDetailsArrayList;
                                        globalList.remove(i);
                                        globalList.add(cityInfoObject);
                                        Toast.makeText(MainActivity.this, "Trip successfully updated", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                                i++;
                            }
                        case REMOVED:
                            Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(CityInfo.class));
                            CityInfo currentRemoved = dc.getDocument().toObject(CityInfo.class);
                            int j=0;
                            for(CityInfo cityInfoObject : globalList){
                                if(cityInfoObject.tripName.equals(currentRemoved.tripName)){
                                    cityInfoObject.placesDetailsArrayList = currentRemoved.placesDetailsArrayList;
                                    globalList.get(j).placesDetailsArrayList = cityInfoObject.placesDetailsArrayList;
                                    break;
                                }
                                j++;
                            }
                            break;
                    }
                }
                //For Recycler Views:
                mAdapter.notifyDataSetChanged();
                hideProgressBarDialog();
            }
        });

        findViewById(R.id.addTripMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TripActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //getting result from search city screen
        if(requestCode == 100 && resultCode == 200 && data != null){
            final CityInfo cityInfo = (CityInfo) data.getExtras().getSerializable("cityInfo");
            Log.d("demo","Received the cityInfo"+cityInfo.toString());

            showProgressBarDialog();

            //Saving the added trip to the firebase
            db.collection("SavedTrips").document(cityInfo.tripName)
                    .set(cityInfo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Trip successfully saved", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
                }
            });
            hideProgressBarDialog();
        }

        //getting result from search place screen
        if(requestCode == 1000 && resultCode == 2000 && data!=null){
            final PlacesDetails placesDetails = (PlacesDetails) data.getExtras().getSerializable("placesDetails");
            String tripName = data.getExtras().getString("tripName");
            int position = data.getExtras().getInt("position");
            CityInfo cityInfo = globalList.get(position);
            cityInfo.placesDetailsArrayList.add(placesDetails);
            globalList.get(position).placesDetailsArrayList = cityInfo.placesDetailsArrayList;

            db.collection("SavedTrips").document(cityInfo.tripName).update("placesDetailsArrayList",cityInfo.placesDetailsArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("demo","It is coming to the places success");
                    Toast.makeText(MainActivity.this, "Place successfully added", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("demo","It is coming to the places failure");
                    Toast.makeText(MainActivity.this, "Some error occurred. Please try to add again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void deleteItem(CityInfo cityInfoObject, int pointer) {
        int i = 0;
        for(CityInfo currentObject : globalList){
            if(currentObject.tripName.equals(cityInfoObject.tripName)){
                currentObject.placesDetailsArrayList.remove(pointer);
                db.collection("SavedTrips").document(currentObject.tripName).update("placesDetailsArrayList",currentObject.placesDetailsArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("demo","It is coming to the places success");
                        Toast.makeText(MainActivity.this, "Place successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo","It is coming to the places failure");
                        Toast.makeText(MainActivity.this, "Some error occurred. Please try to delete again", Toast.LENGTH_SHORT).show();
                    }
                });
                globalList.get(i).placesDetailsArrayList = currentObject.placesDetailsArrayList;
            }
            i++;
        }
    }

    @Override
    public void getDetails(CityInfo cityInfo, int position) {
        Intent intent = new Intent(MainActivity.this, AddPlaces.class);
        intent.putExtra("cityInfo",cityInfo);
        intent.putExtra("position",position);
        startActivityForResult(intent, 1000);
    }

    @Override
    public void getMapDetails(CityInfo cityInfo, int position) {
        if(cityInfo.placesDetailsArrayList.size()>0){
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("cityInfo",cityInfo);
            intent.putExtra("position",position);
            startActivityForResult(intent, 10000);
        }else{
            Toast.makeText(this, "Please save places to display it in Map ", Toast.LENGTH_SHORT).show();
        }
    }

    public void showProgressBarDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressBarDialog()
    {
        progressDialog.dismiss();
    }
}
