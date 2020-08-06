package com.helloworld.inclass14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TripActivity extends AppCompatActivity implements CityListAdapter.InteractWithRecyclerView{
    private EditText tripName;
    private EditText cityName;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private CityInfo globalCityInfo;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<CityInfo> cityInfoArrayList = new ArrayList<>();
    private FirebaseFirestore db;
    boolean isSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        setTitle("Add Trip");
        tripName = findViewById(R.id.tripName);
        cityName = findViewById(R.id.cityName);
        db = FirebaseFirestore.getInstance();
        findViewById(R.id.citySearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation(cityName)) {
                    String nameCity = cityName.getText().toString().trim();
                    new getCityDetailsAsync().execute(nameCity);
                }
            }
        });

        findViewById(R.id.buttonAddTrip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cityInfoArrayList.size()>0 && isSelected){
                    if(checkValidation(tripName)) {
                        db.collection("SavedTrips").document(tripName.getText().toString().trim()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(!documentSnapshot.exists()){
                                    new getCityLatLngAsync().execute();
                                }else{
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(TripActivity.this);
                                    builder1.setMessage("A trip with same name already exists, Do you want to update it?");

                                    builder1.setPositiveButton(
                                            "Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    new getCityLatLngAsync().execute();
                                                }
                                            });

                                    builder1.setNegativeButton(
                                            "No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }else {
                        Toast.makeText(TripActivity.this, "Please add a trip name before you save it!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(TripActivity.this, "Please search for the city and select one from the searched results", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean checkValidation(EditText editText) {
        if (editText.getText().toString().trim().equals("")) {
            editText.setError("Value cannot be empty");
            return false;
        }
        return true;
    }

    @Override
    public void getDetails(CityInfo cityInfo) {
        globalCityInfo = cityInfo;
        isSelected = true;
        cityName.setText(cityInfo.name+", "+cityInfo.stateName);
    }

    public class getCityDetailsAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            // https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyAbhp8MG13hC61PzTWnMNXrTJLzBzvYZJE&types=(cities)&input=charlotte
            final OkHttpClient client = new OkHttpClient();
            String cityDetails = "";
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" + getResources().getString(R.string.api_key) +
                            "&types=(cities)&input=" + strings[0])
                    .build();
            try (Response response = client.newCall(request).execute()) {
                //Log.d("Response", "doInBackground: "+response.body().string());
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String cityDetails) {
            super.onPostExecute(cityDetails);
            cityInfoArrayList = new ArrayList<>();
            if (cityDetails != null) {
                //Log.d("demo",cityDetails);
                try {
                    JSONObject root = new JSONObject(cityDetails);
                    if (root.getString("status").equals("OK")) {
                        JSONArray rootArray = root.getJSONArray("predictions");
                        for (int i = 0; i < rootArray.length(); i++) {
                            JSONObject predictionObject = rootArray.getJSONObject(i);
                            CityInfo cityInfo = new CityInfo();
                            String citydetail = predictionObject.getString("description");
                            String citydetailArray[] = citydetail.split(",");
                            cityInfo.name = citydetailArray[0];
                            cityInfo.stateName = citydetailArray[1];
                            cityInfo.placeId = predictionObject.getString("place_id");
                            cityInfoArrayList.add(cityInfo);
                        }
                    } else {
                        Toast.makeText(TripActivity.this, root.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(TripActivity.this, "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
            }
            if(cityInfoArrayList.size()>0){
                Log.d("demo",cityInfoArrayList.toString());
                if (cityInfoArrayList.size() > 0) {
                    //For Recycler Views:
                    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

                    layoutManager = new LinearLayoutManager(TripActivity.this);
                    recyclerView.setLayoutManager(layoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new CityListAdapter(cityInfoArrayList, TripActivity.this);
                    recyclerView.setAdapter(mAdapter);
                }
            }else{
                Log.d("demo","Sorry no cities found");
            }
        }
    }

    public class getCityLatLngAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            // https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAbhp8MG13hC61PzTWnMNXrTJLzBzvYZJE&placeid=ChIJgRo4_MQfVIgRZNFDv-ZQRog
            final OkHttpClient client = new OkHttpClient();
            String cityLatLngDetails = "";

            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/details/json?key=" + getResources().getString(R.string.api_key) +
                            "&placeid=" + globalCityInfo.placeId)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();

            } catch (IOException e) {

                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String cityLatLngDetails) {
            super.onPostExecute(cityLatLngDetails);
            if (!cityLatLngDetails.equals("")) {
                Log.d("demo",cityLatLngDetails);
                try {
                    JSONObject root = new JSONObject(cityLatLngDetails);
                    if (root.getString("status").equals("OK")) {
                        JSONObject rootResultObject = root.getJSONObject("result");
                        JSONObject geometryObject = rootResultObject.getJSONObject("geometry");
                        JSONObject latLngArray = geometryObject.getJSONObject("location");
                        globalCityInfo.latitude = latLngArray.getString("lat");
                        globalCityInfo.longitude = latLngArray.getString("lng");
                        globalCityInfo.tripName = tripName.getText().toString().trim();
                        Intent intent = new Intent();
                        intent.putExtra("cityInfo", globalCityInfo);
                        setResult(200, intent);
                        finish();
                    } else {
                        Toast.makeText(TripActivity.this, root.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(TripActivity.this, "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

