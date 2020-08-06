package com.helloworld.inclass14;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddPlaces extends AppCompatActivity implements PlacesListAdapter.InteractWithRecyclerView{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
        setTitle("Add Places");
        CityInfo cityInfo = (CityInfo) getIntent().getExtras().getSerializable("cityInfo");
        new getPlacesDetailsAsync().execute(cityInfo.latitude, cityInfo.longitude);
    }

    @Override
    public void selectedItem(PlacesDetails placesDetails) {
        Log.d("demo",placesDetails.toString());
        Intent intent = new Intent();
        boolean isContinue = true;
        CityInfo cityInfo = (CityInfo) getIntent().getExtras().getSerializable("cityInfo");
        for(PlacesDetails history : cityInfo.placesDetailsArrayList){
            if(history.name.equals(placesDetails.name)){
                Toast.makeText(this, "This places is already saved", Toast.LENGTH_SHORT).show();
                isContinue = false;
                break;
            }
        }
        if(isContinue){
            intent.putExtra("tripName",cityInfo.tripName);
            intent.putExtra("placesDetails",placesDetails);
            intent.putExtra("position",getIntent().getExtras().getInt("position"));
            setResult(2000, intent);
            finish();
        }
    }

    public class getPlacesDetailsAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            // https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyAbhp8MG13hC61PzTWnMNXrTJLzBzvYZJE&location=35.2270869,-80.8431267&radius=1000
            final OkHttpClient client = new OkHttpClient();
            String cityDetails = "";
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + getResources().getString(R.string.api_key) +
                            "&location="+ strings[0] +"," +strings[1] +"&radius=" + 1000)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String placesDetails) {
            super.onPostExecute(placesDetails);
            ArrayList<PlacesDetails> placesArrayList = new ArrayList<>();
            if (!placesDetails.equals("")) {

                try {
                    JSONObject root = new JSONObject(placesDetails);
                    if (root.getString("status").equals("OK")) {
                        JSONArray rootArray = root.getJSONArray("results");
                        for (int i = 1; i < rootArray.length(); i++) {
                            JSONObject placesArray = rootArray.getJSONObject(i);
                            PlacesDetails placesDetail = new PlacesDetails();
                            placesDetail.icon = placesArray.getString("icon");
                            placesDetail.name = placesArray.getString("name");
//                            placesDetail.rating = placesArray.getString("rating");
                            JSONObject geometryObject = placesArray.getJSONObject("geometry");
                            JSONObject latLngArray = geometryObject.getJSONObject("location");
                            placesDetail.latitude = latLngArray.getString("lat");
                            placesDetail.longitude = latLngArray.getString("lng");
                            placesArrayList.add(placesDetail);
                        }
                    } else {
                        Toast.makeText(AddPlaces.this, root.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(placesArrayList.size()>0){
                    Log.d("demo",placesArrayList.toString());
//                    For Recycler Views:
                    recyclerView = (RecyclerView) findViewById(R.id.addPlaceRecyclerView);

                    layoutManager = new LinearLayoutManager(AddPlaces.this);
                    recyclerView.setLayoutManager(layoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new PlacesListAdapter(placesArrayList, AddPlaces.this);
                    recyclerView.setAdapter(mAdapter);
                }else{
                    Log.d("demo","Sorry no cities found");
                }
            }else{
                Toast.makeText(AddPlaces.this, "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
