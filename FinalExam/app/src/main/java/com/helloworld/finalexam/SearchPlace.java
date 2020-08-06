package com.helloworld.finalexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchPlace extends AppCompatActivity implements PlacesAdapter.InteractWithRecyclerView{

    private EditText searchPlace;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<String> globalList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        setTitle("Add Place");

        searchPlace = findViewById(R.id.editTextSearchPlace);

        findViewById(R.id.buttonSearchPlace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation(searchPlace)){
                    new GetPlaces().execute(searchPlace.getText().toString().trim());
                }else{
                    Toast.makeText(SearchPlace.this, "Search keyword cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private boolean checkValidation(EditText editText){
        if(editText.getText().toString().trim().equals("")){
            return false;
        }
        return true;
    }

    @Override
    public void getDetails(String meetingPlace, int position) {
        Intent intent = new Intent();
        intent.putExtra("meetingPlace", meetingPlace);
        setResult(2000, intent);
        finish();
    }


    public class GetPlaces extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            // https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyAbhp8MG13hC61PzTWnMNXrTJLzBzvYZJE&placeid=ChIJgRo4_MQfVIgRZNFDv-ZQRog

//            https://maps.googleapis.com/maps/api/place/autocomplete/json?input=1600+woodward&key=AIzaSyCxMawxVMDVCRJ_mnP-Qur9Nr6pBs0JBvk&sessiontoken=1234567890

            final OkHttpClient client = new OkHttpClient();

            Calendar calendar = Calendar.getInstance();

            String token = UUID.randomUUID().toString().toUpperCase()
                    + "|" + "userid" + "|"
                    + calendar.getTimeInMillis();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=1600" + strings[0]
                            + "&key="+ getResources().getString(R.string.api_key) +
                            "&sessionToken=" + token)
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
            globalList.clear();
            if (!placesDetails.equals("")) {
                Log.d("demo",placesDetails);
                try {
                    JSONObject root = new JSONObject(placesDetails);
                    if (root.getString("status").equals("OK")) {
                        JSONArray rootArray = root.getJSONArray("predictions");
                        for (int i = 0; i < rootArray.length(); i++) {
                            JSONObject placesArray = rootArray.getJSONObject(i);
                            String description = placesArray.getString("description");
                            String[] descArray = description.split("1600");
                            if(descArray.length > 0){
                                description = descArray[1];
                            }
                            globalList.add(description);
                        }
                        if(globalList.size()>0){

                            recyclerView = (RecyclerView) findViewById(R.id.placesRecylerView);
                            layoutManager = new LinearLayoutManager(SearchPlace.this);
                            recyclerView.setLayoutManager(layoutManager);
                            // specify an adapter (see also next example)
                            mAdapter = new PlacesAdapter(globalList, SearchPlace.this);
                            recyclerView.setAdapter(mAdapter);

                        }
                    } else {
                        Toast.makeText(SearchPlace.this, root.getString("status"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(SearchPlace.this, "Some error occured. Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
