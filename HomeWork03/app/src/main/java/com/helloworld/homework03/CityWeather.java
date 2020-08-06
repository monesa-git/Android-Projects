package com.helloworld.homework03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CityWeather extends AppCompatActivity implements DailyForeCastAdapter.InteractWithRecyclerView{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    String cityKey;
    String cityMobileLink;
    int arrayListPosition;
    String countryName;
    String cityName;
    Double temperature;
    Boolean dup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);
        setTitle("City Weather");
        TextView placeText = findViewById(R.id.countryCityText);
        //        http://dataservice.accuweather.com/forecasts/v1/daily/5day/28202?apikey=qfjJcRX3VIRPSwZL6Tp8hI2jTHA5iWAc
        ForecastClass forecastClass = (ForecastClass) getIntent().getExtras().getSerializable("ForecastClass");
        countryName = forecastClass.countryName;
        cityName = forecastClass.cityName;
        placeText.setText(cityName+", "+countryName);

        if(isConnected()) {
            new LoadFiveDaysWeatherReport().execute(String.valueOf(forecastClass.key));
        }

        findViewById(R.id.textView14).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = cityMobileLink;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForecastClass forecastClass = (ForecastClass) getIntent().getExtras().getSerializable("ForecastClass");
                Intent data = new Intent();
                data.putExtra("forecastdetails", forecastClass);
                setResult(200, data);
                finish();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForecastClass forecastClass = (ForecastClass) getIntent().getExtras().getSerializable("ForecastClass");
                Intent data = new Intent();
                data.putExtra("forecastdetails", forecastClass);
                setResult(300, data);
                finish();
            }
        });
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

    public void loadScreen(DailyForeCast foreCastInfo){
        TextView weatherDesc = findViewById(R.id.weatherdesc);
        TextView dateText = findViewById(R.id.ForecastDate);

        TextView tempDay = findViewById(R.id.temperatureFull);
        TextView dayDetailText = findViewById(R.id.DayDetails);
        TextView nightDetailText = findViewById(R.id.NightDetails);
        ImageView dayImage = findViewById(R.id.imageView2);
        ImageView nightImage = findViewById(R.id.imageView3);

        dateText.setText(foreCastInfo.date);
        weatherDesc.setText(foreCastInfo.detail);
        tempDay.setText(foreCastInfo.maxTemp+"/"+foreCastInfo.minTemp);
        dayDetailText.setText(foreCastInfo.dayDetail);
        nightDetailText.setText(foreCastInfo.nightDetail);
        cityMobileLink = foreCastInfo.mobileLink;

        String forecastDayImageIcon = foreCastInfo.dayImage.trim();
        if(forecastDayImageIcon.length()==1){
            forecastDayImageIcon = "0"+forecastDayImageIcon;
        }
        try {
            Picasso
                    .get()
                    .load("http://developer.accuweather.com/sites/default/files/" + URLEncoder.encode(forecastDayImageIcon,"UTF-8")  + "-s.png")
                    .into(dayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(CityWeather.this, "No Image found", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String forecastNightImageIcon = foreCastInfo.nightImage.trim();
        if(forecastNightImageIcon.length()==1){
            forecastNightImageIcon = "0"+forecastNightImageIcon;
        }
        try {
            Picasso
                    .get()
                    .load("http://developer.accuweather.com/sites/default/files/" + URLEncoder.encode(forecastNightImageIcon,"UTF-8")  + "-s.png")
                    .into(nightImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(CityWeather.this, "No Image found", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public class LoadFiveDaysWeatherReport extends AsyncTask<String, Void, ArrayList<DailyForeCast> >{

        @Override
        protected ArrayList<DailyForeCast>  doInBackground(String... strings) {
            ArrayList<DailyForeCast> dailyForeCastArrayList = new ArrayList<>();
            final OkHttpClient client = new OkHttpClient();
            String fiveWeatherForecast = null;
            String api = getResources().getString(R.string.API_KEY);
            ForecastClass forecastClass = new ForecastClass();
            Request request = null;
            request = null;
            request = new Request.Builder()
                    .url("http://dataservice.accuweather.com/forecasts/v1/daily/5day/"+
                            strings[0]+ "?apikey=" + api)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                fiveWeatherForecast = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONObject root = new JSONObject(fiveWeatherForecast);
                JSONObject defaultInfo = root.getJSONObject("Headline");
                JSONArray daily = root.getJSONArray("DailyForecasts");
                for(int i=0; i<daily.length(); i++){
                    DailyForeCast dailyForeCast = new DailyForeCast();
                    dailyForeCast.detail = defaultInfo.getString("Text");
                    JSONObject dailyObject = daily.getJSONObject(i);
                    dailyForeCast.date = dailyObject.getString("Date");
                    Date date = null;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    SimpleDateFormat OutputFormat = new SimpleDateFormat("dd MMM, YY");
                    try {
                        date = format.parse(dailyForeCast.date);
                        dailyForeCast.date = OutputFormat.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dailyForeCast.index = i+1;
                    JSONObject temp = dailyObject.getJSONObject("Temperature");
                    JSONObject minTemp = temp.getJSONObject("Minimum");
                    dailyForeCast.minTemp = minTemp.getString("Value")+" F";
                    JSONObject maxTemp = temp.getJSONObject("Maximum");
                    dailyForeCast.maxTemp = maxTemp.getString("Value")+" F";
                    JSONObject day = dailyObject.getJSONObject("Day");
                    dailyForeCast.dayDetail = day.getString("IconPhrase");
                    dailyForeCast.dayImage = day.getString("Icon");
                    JSONObject night = dailyObject.getJSONObject("Night");
                    dailyForeCast.nightDetail = night.getString("IconPhrase");
                    dailyForeCast.nightImage = night.getString("Icon");
                    dailyForeCast.mobileLink = dailyObject.getString("MobileLink");
                    dailyForeCastArrayList.add(dailyForeCast);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return dailyForeCastArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<DailyForeCast>  dailyForeCastArrayList) {
            super.onPostExecute(dailyForeCastArrayList);

            //For Recycler Views:
            recyclerView = (RecyclerView) findViewById(R.id.recyclerViewDaily);

            layoutManager =new LinearLayoutManager(CityWeather.this,
                    LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            mAdapter = new DailyForeCastAdapter(dailyForeCastArrayList, CityWeather.this);
            recyclerView.setAdapter(mAdapter);
            if(dailyForeCastArrayList.size()>0){
                loadScreen(dailyForeCastArrayList.get(0));
            }else{
                Toast.makeText(CityWeather.this, "City Info not found", Toast.LENGTH_SHORT).show();
                finish();
            }
            Log.d("demo", dailyForeCastArrayList.toString());
        }
    }

    public void getDetails(DailyForeCast dailyForeCast, int position){
        arrayListPosition = position;
        loadScreen(dailyForeCast);
    }
}
