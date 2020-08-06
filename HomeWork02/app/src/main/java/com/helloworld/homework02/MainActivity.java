package com.helloworld.homework02;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;
    private TextView searchText;
    private ListView listView;
    private Switch aSwitch;
    private AlertDialog alertDialog;
    private ProgressDialog progressDialog;
    ArrayList<MusicInfo> musicInfoArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(null);
        alertDialog.setMessage("Loading");
        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textViewLimit);
        aSwitch = findViewById(R.id.switchToggle);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("Limit : "+seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected())
                {
                    aSwitch = findViewById(R.id.switchToggle);
                    searchText = findViewById(R.id.editTextSearch);
                    String searchKeyword = searchText.getText().toString();
                    if (searchKeyword.equals("")) {
                        searchText.setError("Please enter a search Keyword");
                    } else
                    {
                        showProgressBarDialog();
                        if (searchKeyword.contains(" ")) {
                            searchKeyword.replace(" ", "+");
                        }
                        listView = findViewById(R.id.listView);
                        new GetAsyncMusicRecords(listView, seekBar.getProgress(), searchKeyword, aSwitch.isChecked())
                                .execute("https://itunes.apple.com/search");

                        Log.d("Demo", String.valueOf(aSwitch.isChecked()));
                    }

                }else{
                    Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.buttonReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView = findViewById(R.id.listView);
                searchText.setText("");
                aSwitch.setChecked(true);
                seekBar.setProgress(10);
                musicInfoArrayList.clear();
                MusicAdapter musicAdapter = new MusicAdapter(MainActivity.this,R.layout.list_view_tracks,musicInfoArrayList);
                listView.setAdapter(musicAdapter);
                musicAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.switchToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aSwitch = findViewById(R.id.switchToggle);
                musicInfoArrayList = sortArrayList(musicInfoArrayList,aSwitch.isChecked());
                MusicAdapter musicAdapter = new MusicAdapter(MainActivity.this,R.layout.list_view_tracks,musicInfoArrayList);
                listView.setAdapter(musicAdapter);
                musicAdapter.notifyDataSetChanged();
            }
        });
    }

    public ArrayList<MusicInfo> sortArrayList(ArrayList<MusicInfo> musicInfoList, boolean isChecked){
        Collections.sort(musicInfoList, new Comparator<MusicInfo>() {
            @Override
            public int compare(MusicInfo o1, MusicInfo o2) {
                if(aSwitch.isChecked() == false){
                    if(o1.track_price < o2.track_price){
                        return -1;
                    }else{
                        return 1;
                    }
                }else{
                    Date o1Date = null;
                    Date o2Date = null;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    try {
                        o1Date = format.parse(o1.release_date);
                        o2Date = format.parse(o2.release_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return o1Date.compareTo(o2Date);
                }
            }
        });
        return musicInfoList;
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

    private class GetAsyncMusicRecords extends AsyncTask<String, Void, ArrayList<MusicInfo>>{
        int seekProgress;
        String searchKeyword;
        ListView listView;
        Boolean isToggleChecked;

        public GetAsyncMusicRecords(ListView listView, int seekProgress, String searchKeyword, boolean isToggleChecked) {
            this.listView = listView;
            this.seekProgress = seekProgress;
            this.searchKeyword = searchKeyword;
            this.isToggleChecked = isToggleChecked;
        }

        @Override
        protected ArrayList<MusicInfo> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            String result;
            musicInfoArrayList.clear();
            URL url = null;
            try {
                url = new URL(strings[0]
                        + "?" +"term="+ URLEncoder.encode(searchKeyword, "UTF-8")
                        +"&limit="+ URLEncoder.encode(String.valueOf(seekProgress), "UTF-8"));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    result = IOUtils.toString(connection.getInputStream(),"UTF-8");
                    JSONObject root = new JSONObject(result);
                    JSONArray articles = root.getJSONArray("results");
                    for(int i=0; i<articles.length();i++){
                        JSONObject articlesJson = articles.getJSONObject(i);
                        MusicInfo musicInfo = new MusicInfo();
                        musicInfo.track_name = articlesJson.getString("trackName");
                        musicInfo.genre = articlesJson.getString("primaryGenreName");
                        musicInfo.artist = articlesJson.getString("artistName");
                        musicInfo.album = articlesJson.getString("collectionName");
                        musicInfo.track_price = articlesJson.getDouble("trackPrice");
                        musicInfo.release_date = articlesJson.getString("releaseDate");
                        musicInfo.collection_price = articlesJson.getDouble("collectionPrice");
                        musicInfo.trackUrl = articlesJson.getString("artworkUrl100");
                        musicInfoArrayList.add(musicInfo);
                        hideProgressBarDialog();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection!=null){
                    connection.disconnect();
                }
            }
            return musicInfoArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<MusicInfo> musicInfos) {
            super.onPostExecute(musicInfos);
            if(musicInfos.size()>0){
                musicInfoArrayList = sortArrayList(musicInfos,aSwitch.isChecked());
                Log.d("Demo",musicInfos.toString());
                Log.d("Demo",String.valueOf(musicInfos.size()));
                MusicAdapter musicAdapter = new MusicAdapter(MainActivity.this,R.layout.list_view_tracks,musicInfoArrayList);
                listView.setAdapter(musicAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, MusicInfoActivity.class);
                        MusicInfo musicInfo = musicInfoArrayList.get(position);
                        intent.putExtra("MusicInfoAttributes", musicInfo);
                        startActivity(intent);
                    }
                });
            }else{
                hideProgressBarDialog();
                Toast.makeText(MainActivity.this, "No Tracks Found!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
