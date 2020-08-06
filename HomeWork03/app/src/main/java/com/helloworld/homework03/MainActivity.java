package com.helloworld.homework03;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements WeatherCityAdapter.InteractWithRecyclerView{
    ArrayList<CityDetails> cityDetailsArrayList = new ArrayList<>();
    ArrayList<ForecastClass> globalForeCastArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Weather App");
        ForecastClass savedForecast = fetchCurrentCity();
        if(savedForecast!=null){
            showProgressBarDialog();
            new LoadAsyncForecast("current", savedForecast.key, savedForecast.stateName,
                    savedForecast.countryName, savedForecast.cityName).execute(savedForecast.key);
        }

        findViewById(R.id.CurrentCityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final View customLayout = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_builder, null);
                    builder.setView(customLayout)

                    .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText countryName = customLayout.findViewById(R.id.countryNameID);
                            EditText cityName = customLayout.findViewById(R.id.cityNameID);
                            if(checkValidations(countryName)&&checkValidations(cityName)){
                                showProgressBarDialog();
                                new LoadAsyncCities("true").execute(countryName.getText().toString(),cityName.getText().toString());
                            }
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.gradient);

                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setBackgroundColor(getResources().getColor(R.color.black));
                    positiveButton.setTextColor(getResources().getColor(R.color.white));

                    Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setBackgroundColor(getResources().getColor(R.color.black));
                    negativeButton.setTextColor(getResources().getColor(R.color.white));

                }
            }
        });


        findViewById(R.id.cityPlusState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final View customLayout = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_builder, null);
                    builder.setView(customLayout)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText countryName = customLayout.findViewById(R.id.countryNameID);
                                    EditText cityName = customLayout.findViewById(R.id.cityNameID);
                                    if(checkValidations(countryName)&&checkValidations(cityName)){
                                        if(isConnected()) {
                                            showProgressBarDialog();
                                            new LoadAsyncCities("true").execute(countryName.getText().toString(), cityName.getText().toString());
                                        }
                                    }
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.gradient);
                    alertDialog.show();
                }
            }
        });

        findViewById(R.id.searchCityButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                TextView cityNameTextView = findViewById(R.id.cityNameText);
                TextView countryTextView = findViewById(R.id.countryText);
                if(checkValidations(cityNameTextView) && checkValidations(countryTextView)){
                    String cityName = cityNameTextView.getText().toString().trim();
                    String countryName = countryTextView.getText().toString().trim();
                    if(isConnected()) {
                        showProgressBarDialog();
                        new LoadAsyncCities("false").execute(countryName, cityName);
                    }
                }
            }
        });
    }

    public boolean checkValidations(TextView editText){
        if(editText.getText().toString().equals("")){
            editText.setError("Cannot be empty");
            return false;
        }else{
            return true;
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void setWeatherDetails(ForecastClass forecastObject, boolean isCurrent){
        findViewById(R.id.textView).setVisibility(TextView.INVISIBLE);
        findViewById(R.id.CurrentCityButton).setVisibility(TextView.INVISIBLE);
        TextView cityState = findViewById(R.id.cityPlusState);
        cityState.setText(forecastObject.cityName + "-"+forecastObject.stateName+", " + forecastObject.countryName);
        cityState.setVisibility(TextView.VISIBLE);

        TextView forecast = findViewById(R.id.forecastText);
        forecast.setText(forecastObject.forecast);
        forecast.setVisibility(TextView.VISIBLE);

        TextView temperature = findViewById(R.id.temperatureView);
        temperature.setText("Temperature: " + forecastObject.temperature + " F");
        temperature.setVisibility(TextView.VISIBLE);

        PrettyTime pt = new PrettyTime(Locale.getDefault());
        String timeGot = forecastObject.updatedDate;
        timeGot = timeGot.substring(0,timeGot.lastIndexOf('-'));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Long time = null;
        try {
            time = sdf.parse(timeGot).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        forecastObject.updatedDate = pt.format(new Date(time));
        TextView updated = findViewById(R.id.updatedTime);
        updated.setText("Updated: " + forecastObject.updatedDate);
        updated.setVisibility(TextView.VISIBLE);
        String forecastImageIcon = forecastObject.forecastImage.trim();
        if(forecastImageIcon.length()==1){
            forecastImageIcon = "0"+forecastImageIcon;
        }
        final ImageView imageView = findViewById(R.id.imageView);
        try {
            Picasso
                    .get()
                    .load("http://developer.accuweather.com/sites/default/files/" + URLEncoder.encode(forecastImageIcon,"UTF-8")  + "-s.png")
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageView.setVisibility(ImageView.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(MainActivity.this, "No Image found", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        TextView cityNameTextView = findViewById(R.id.cityNameText);
        TextView countryTextView = findViewById(R.id.countryText);
        cityNameTextView.setText(forecastObject.cityName);
        countryTextView.setText(forecastObject.countryName);
        fetchGlobalList();
        boolean isEnterted = false;
            for(ForecastClass checkDuplicate : globalForeCastArrayList){
                if(forecastObject.key == checkDuplicate.key){
                    forecastObject.favourite = checkDuplicate.favourite;
                    globalForeCastArrayList.remove(checkDuplicate);
                    Toast.makeText(MainActivity.this, "Current City Updated", Toast.LENGTH_SHORT).show();
                    isEnterted = true;
                    break;
                }
            }
        if(!isEnterted){
            Toast.makeText(MainActivity.this, "Current City Saved", Toast.LENGTH_SHORT).show();
        }
        globalForeCastArrayList.add(forecastObject);
        addToSharedPreference();
        addCurrentCityToSharedPreference(forecastObject);

        TextView noCity = findViewById(R.id.textView2);
        TextView searchCitytext = findViewById(R.id.textView3);

        if(globalForeCastArrayList.size()>0){
            //For Recycler Views:
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

            layoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            mAdapter = new WeatherCityAdapter(globalForeCastArrayList, MainActivity.this);
            recyclerView.setAdapter(mAdapter);
            noCity.setVisibility(TextView.INVISIBLE);
            searchCitytext.setVisibility(TextView.INVISIBLE);
        }else{
            noCity.setVisibility(TextView.VISIBLE);
            searchCitytext.setVisibility(TextView.VISIBLE);
        }
    }

    public void addCurrentCityToSharedPreference(ForecastClass forecastClass){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("currentCity",0);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(forecastClass);
        prefsEditor.putString("currentCity", json);
        prefsEditor.commit();
    }

    public void addToSharedPreference(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("savedList",0);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(globalForeCastArrayList);
        prefsEditor.putString("savedList", json);
        prefsEditor.commit();
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
    public class LoadAsyncCities extends AsyncTask<String, Void, ForecastClass> {

        String saveCity;

        public LoadAsyncCities(String saveCity) {
            this.saveCity = saveCity;
        }

        @Override
        protected ForecastClass doInBackground(String... strings) {
            cityDetailsArrayList.clear();

            final OkHttpClient client = new OkHttpClient();
            String cityDetails = null;
            String api = getResources().getString(R.string.API_KEY);
            Request request = null;
            request = new Request.Builder()
                    .url("http://dataservice.accuweather.com/locations/v1/cities/"+
                            strings[0]+"/search?apikey="+ api +"&q="+strings[1])
                    .build();
            try (Response response = client.newCall(request).execute()) {
                cityDetails = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONArray articles = null;
            ForecastClass forecastClass = new ForecastClass();
            try {
                articles = new JSONArray(cityDetails);
                JSONObject articlesJson = articles.getJSONObject(0);
                CityDetails cityDetailsObject = new CityDetails();
                cityDetailsObject.cityName = articlesJson.getString("EnglishName");
                cityDetailsObject.cityKey = Integer.parseInt(articlesJson.getString("Key").trim());
                JSONObject administrativeArea = articlesJson.getJSONObject("AdministrativeArea");
                cityDetailsObject.stateName = administrativeArea.getString("ID");
                cityDetailsObject.countryName = administrativeArea.getString("CountryID");
                cityDetailsArrayList.add(cityDetailsObject);

                //Trying to get the city weather forecast
                String weatherForecast = null;
                request = null;
                request = new Request.Builder()
                        .url("http://dataservice.accuweather.com/currentconditions/v1/"+
                                cityDetailsObject.cityKey+ "?apikey=" + api)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    weatherForecast = response.body().string();
                } catch (IOException e) {
                    forecastClass.code = "error";
                    e.printStackTrace();
                }
                JSONArray weatherDetails = new JSONArray(weatherForecast);
                JSONObject weatherJson = weatherDetails.getJSONObject(0);
                forecastClass.updatedDate = weatherJson.getString("LocalObservationDateTime");
                forecastClass.forecastImage = weatherJson.getString("WeatherIcon");
                forecastClass.forecast = weatherJson.getString("WeatherText");
                JSONObject temperatureObject = weatherJson.getJSONObject("Temperature");
                JSONObject imperialObject = temperatureObject.getJSONObject("Imperial");
                forecastClass.temperature = imperialObject.getDouble("Value");
                forecastClass.stateName = cityDetailsArrayList.get(0).stateName;
                forecastClass.cityName = cityDetailsArrayList.get(0).cityName;
                forecastClass.countryName = cityDetailsArrayList.get(0).countryName;
                forecastClass.key  = cityDetailsArrayList.get(0).cityKey;
                forecastClass.code = "success";
                forecastClass.favourite = false;
                for (int i = 1; i < articles.length(); i++) {
                    cityDetailsObject = new CityDetails();
                    articlesJson = articles.getJSONObject(i);
                    cityDetailsObject.cityName = articlesJson.getString("EnglishName");
                    cityDetailsObject.cityKey = Integer.parseInt(articlesJson.getString("Key").trim());
                    administrativeArea = articlesJson.getJSONObject("AdministrativeArea");
                    cityDetailsObject.stateName = administrativeArea.getString("ID");
                    cityDetailsObject.countryName = administrativeArea.getString("CountryID");
                    cityDetailsArrayList.add(cityDetailsObject);
                }
            } catch (JSONException e) {
                forecastClass.code = "error";
                e.printStackTrace();
            }
            return forecastClass;
        }

        @Override
        protected void onPostExecute(ForecastClass forecastClass) {
            super.onPostExecute(forecastClass);
            Log.d("Demo",forecastClass.toString());
            hideProgressBarDialog();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            if(forecastClass.code.equals("error")){
                Toast.makeText(MainActivity.this, "Some Error Occured, Please try again", Toast.LENGTH_SHORT).show();
            }else{
                if(saveCity.equals("true")){
                    setWeatherDetails(forecastClass, true);
                }else if(saveCity.equals("false")){
                    Log.d("demo", cityDetailsArrayList.toString());
                    String[] stateArray = new String[cityDetailsArrayList.size()];
                    int i = 0;
                    for(CityDetails cityDetails : cityDetailsArrayList){
                        stateArray[i++] = cityDetailsArrayList.get(0).cityName + ", " +cityDetails.stateName;
                    }
                    builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new LoadAsyncForecast("true", cityDetailsArrayList.get(which).cityKey,
                                    cityDetailsArrayList.get(which).stateName, cityDetailsArrayList.get(0).countryName,
                                    cityDetailsArrayList.get(which).cityName).execute(which);
                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.gradient);
                    alertDialog.show();
                }
              }
            }
        }


    public class LoadAsyncForecast extends AsyncTask<Integer, Void, ForecastClass> {

        String callIntent;
        int cityKey;
        String stateName, countryName, cityName;

        public LoadAsyncForecast(String callIntent, int cityKey, String stateName, String countryName, String cityName) {
            this.callIntent = callIntent;
            this.cityKey = cityKey;
            this.stateName = stateName;
            this.countryName = countryName;
            this.cityName = cityName;
        }

        @Override
        protected ForecastClass doInBackground(Integer... position) {
            final OkHttpClient client = new OkHttpClient();
            String weatherForecast = null;
            String api = getResources().getString(R.string.API_KEY);
            ForecastClass forecastClass = new ForecastClass();
            Request request = null;
            request = null;
            request = new Request.Builder()
                    .url("http://dataservice.accuweather.com/currentconditions/v1/"+
                            cityKey+ "?apikey=" + api)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                weatherForecast = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONArray weatherDetails = null;
            try {
                weatherDetails = new JSONArray(weatherForecast);
                forecastClass = new ForecastClass();
                JSONObject weatherJson = weatherDetails.getJSONObject(0);
                forecastClass.updatedDate = weatherJson.getString("LocalObservationDateTime");
                forecastClass.forecastImage = weatherJson.getString("WeatherIcon");
                forecastClass.forecast = weatherJson.getString("WeatherText");
                JSONObject temperatureObject = weatherJson.getJSONObject("Temperature");
                JSONObject imperialObject = temperatureObject.getJSONObject("Imperial");
                forecastClass.temperature = imperialObject.getDouble("Value");
                forecastClass.key = cityKey;
                forecastClass.stateName = stateName;
                forecastClass.cityName = cityName;
                forecastClass.countryName = countryName;
                forecastClass.favourite = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return forecastClass;
        }

        @Override
        protected void onPostExecute(ForecastClass forecastDetails) {
            super.onPostExecute(forecastDetails);
            Log.d("Demo", forecastDetails.toString());
            if(forecastDetails!=null){
                if(callIntent.equals("true")){
                    Intent intent = new Intent(MainActivity.this, CityWeather.class);
                    intent.putExtra("ForecastClass", forecastDetails);
                    startActivityForResult(intent, 100);
                }else if(callIntent.equals("false")){
                    PrettyTime pt = new PrettyTime(Locale.getDefault());
                    String timeGot = forecastDetails.updatedDate;
                    timeGot = timeGot.substring(0,timeGot.lastIndexOf('-'));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Long time = null;
                    try {
                        time = sdf.parse(timeGot).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    forecastDetails.updatedDate = pt.format(new Date(time));
                    boolean isEnterted = false;
                    for(ForecastClass checkDuplicate : globalForeCastArrayList){
                        if(forecastDetails.key == checkDuplicate.key){
                            forecastDetails.favourite = checkDuplicate.favourite;
                            globalForeCastArrayList.remove(checkDuplicate);
                            Toast.makeText(MainActivity.this, "City Updated", Toast.LENGTH_SHORT).show();
                            isEnterted = true;
                            break;
                        }
                    }
                    if(!isEnterted){
                        Toast.makeText(MainActivity.this, "City Saved", Toast.LENGTH_SHORT).show();
                    }
                    globalForeCastArrayList.add(forecastDetails);
                    addToSharedPreference();
                    //For Recycler Views:
                    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

                    layoutManager = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(layoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new WeatherCityAdapter(globalForeCastArrayList, MainActivity.this);
                    recyclerView.setAdapter(mAdapter);
                    TextView noCity = findViewById(R.id.textView2);
                    TextView searchCitytext = findViewById(R.id.textView3);
                    noCity.setVisibility(TextView.INVISIBLE);
                    searchCitytext.setVisibility(TextView.INVISIBLE);
                    hideProgressBarDialog();
                }else{
                    setWeatherDetails(forecastDetails, true);
                    hideProgressBarDialog();
                }
            }else{
                Toast.makeText(MainActivity.this, "Some error occured, Please try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void fetchGlobalList(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("savedList", 0);
        String getValue = preferences.getString("savedList",null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<ForecastClass>>(){}.getType();
        if(gson.fromJson(getValue,type) != null){
            globalForeCastArrayList = gson.fromJson(getValue,type);
        }
    }

    public ForecastClass fetchCurrentCity(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("currentCity", 0);
        String getValue = preferences.getString("currentCity",null);
        Gson gson = new Gson();
        return gson.fromJson(getValue,ForecastClass.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode == 200){
                ForecastClass forecastClass = (ForecastClass)data.getExtras().getSerializable("forecastdetails");
                if(forecastClass!=null) {
                    showProgressBarDialog();
                    fetchGlobalList();
                    new LoadAsyncForecast("false", forecastClass.key, forecastClass.stateName,
                                forecastClass.countryName, forecastClass.cityName).execute(forecastClass.key);
                }else{
                    Toast.makeText(this, "Some Error occured. Please try again", Toast.LENGTH_SHORT).show();
                }
            }else if(resultCode == 300){
                ForecastClass forecastClass = (ForecastClass)data.getExtras().getSerializable("forecastdetails");
                if(forecastClass!=null) {
                    showProgressBarDialog();
                    fetchGlobalList();
                    new LoadAsyncForecast("current", forecastClass.key, forecastClass.stateName,
                                forecastClass.countryName, forecastClass.cityName).execute(forecastClass.key);
                }else{
                    Toast.makeText(this, "Some Error occured. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void getDetails(ForecastClass forecastClass){
        globalForeCastArrayList.remove(forecastClass);
        mAdapter.notifyDataSetChanged();
        addToSharedPreference();
        if(globalForeCastArrayList.size()<=0){
            TextView noCity = findViewById(R.id.textView2);
            TextView searchCitytext = findViewById(R.id.textView3);
            noCity.setVisibility(TextView.VISIBLE);
            searchCitytext.setVisibility(TextView.VISIBLE);
        }
        Toast.makeText(this, "City Deleted Successfully", Toast.LENGTH_SHORT).show();
    }
//
    public void selectedItem(int position){
        if(globalForeCastArrayList.get(position).favourite == true){
            globalForeCastArrayList.get(position).favourite = false;
        }else{
            globalForeCastArrayList.get(position).favourite = true;
        }
        addToSharedPreference();
        mAdapter.notifyDataSetChanged();
    }
}
