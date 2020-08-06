package com.helloworld.inclass06;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private int newsCounter = 0;
    ArrayList<News> newsArrayList = new ArrayList<>();
    private ImageButton previous, next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        previous.setEnabled(false);
        next.setEnabled(false);

        findViewById(R.id.buttonCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    final String category[] = {"Business", "Entertainment", "General", "Health", "Science", "Sports" ,"Technology"};
                    builder.setTitle("Choose Keywords")
                        .setItems(category, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView editText = findViewById(R.id.searchKeyword);
                                editText.setText(category[which]);
                                TextView searchKeyword = findViewById(R.id.searchKeyword);
                                progressBar.setVisibility(ProgressBar.VISIBLE);
                                new GetAsyncNews().execute(searchKeyword.getText().toString());
                            }
                        });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsArrayList.size() > 0) {
                    newsCounter++;
                    if (newsCounter > newsArrayList.size() - 1) {
                        newsCounter = 0;
                    }
                    TextView searchKeyword = findViewById(R.id.searchKeyword);
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    setNewsValues();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });

        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsArrayList.size() > 0) {
                    newsCounter--;
                    if (newsCounter <= -1) {
                        newsCounter = newsArrayList.size()-1;
                    }
                    TextView searchKeyword = findViewById(R.id.searchKeyword);
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    setNewsValues();

                }
            }
        });
    }

    private void setNewsValues(){
        TextView newsTitle = findViewById(R.id.newsTitle);
        newsTitle.setText(newsArrayList.get(newsCounter).title);
        TextView publishedAt = findViewById(R.id.dateText);
        publishedAt.setText(newsArrayList.get(newsCounter).publishedAt);
        TextView description = findViewById(R.id.description);
        description.setMovementMethod(new ScrollingMovementMethod());
        description.setText(newsArrayList.get(newsCounter).description);
        ImageView imageView = findViewById(R.id.imageView);
        if(isConnected()){
            Picasso
                    .get()
                    .load(newsArrayList.get(newsCounter).url)
                    .into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(MainActivity.this, "No Image found", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            });
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            imageView.setImageResource(R.drawable.nointernet);
        }
        TextView sizeText = findViewById(R.id.sizeText);
        sizeText.setText((newsCounter+1) +" of "+newsArrayList.size());
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

    public class GetAsyncNews extends AsyncTask<String, Void, ArrayList<News>>{

        @Override
        protected ArrayList<News> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            String result;
            newsArrayList.clear();
            URL url = null;
            try {
                url = new URL("http://newsapi.org/v2/top-headlines"
                        + "?" +"apiKey="+ URLEncoder.encode(getResources().getString(R.string.api_key), "UTF-8")
                        +"&country="+ URLEncoder.encode("us", "UTF-8")
                        +"&category="+ URLEncoder.encode(strings[0], "UTF-8"));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    result = IOUtils.toString(connection.getInputStream(),"UTF-8");
                    JSONObject root = new JSONObject(result);
                    JSONArray articles = root.getJSONArray("articles");
                    for(int i=0; i<articles.length();i++){
                        JSONObject articlesJson = articles.getJSONObject(i);
                        News news = new News();
                        news.title = articlesJson.getString("title");
                        news.description = articlesJson.getString("description");
                        news.publishedAt = articlesJson.getString("publishedAt");
                        news.url = articlesJson.getString("urlToImage");
                        newsArrayList.add(news);
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
            return newsArrayList;
        }


        @Override
        protected void onPostExecute(ArrayList<News> news) {
            if(newsArrayList.size()>0){
                newsCounter = 0;
                if(newsArrayList.size()==1){
                    previous.setEnabled(false);
                    next.setEnabled(false);
                    previous.setAlpha((float) 0.5);
                    next.setAlpha((float) 0.5);
                }else{
                    previous.setEnabled(true);
                    next.setEnabled(true);
                    previous.setAlpha((float) 1.0);
                    next.setAlpha((float) 1.0);
                }
                TextView newsTitle = findViewById(R.id.newsTitle);
                newsTitle.setText(newsArrayList.get(0).title);
                TextView publishedAt = findViewById(R.id.dateText);
                publishedAt.setText(newsArrayList.get(0).publishedAt);
                TextView description = findViewById(R.id.description);
                description.setText(newsArrayList.get(0).description);
                description.setMovementMethod(new ScrollingMovementMethod());
                ImageView imageView = findViewById(R.id.imageView);
                Picasso.get().load(newsArrayList
                        .get(0).url)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(MainActivity.this, "No Image found", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                            }
                        });
                TextView sizeText = findViewById(R.id.sizeText);
                sizeText.setText( "1 of "+newsArrayList.size());
                Log.d("Demo", newsArrayList.toString());
            }else{
                Toast.makeText(MainActivity.this, "No News Found", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

        }
    }
}
