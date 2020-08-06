package com.helloworld.inclass05;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView searchKeyword;
    private TextView progressTitle;
    private ProgressBar progressBar;
    private ImageButton previous;
    private ImageButton next;
    public ArrayList<String> urlList = new ArrayList<String>();
    int getIterator = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.next).setEnabled(false);
        findViewById(R.id.previous).setEnabled(false);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        progressTitle = findViewById(R.id.progressTitle);

        if (isConnected()) {
            new GetKeywords(false).execute();
        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    new GetKeywords(true).execute();
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setVisibility(ImageView.INVISIBLE);
                if (urlList.size() > 0) {
                    getIterator++;
                    if (getIterator > urlList.size() - 1) {
                        getIterator = 0;
                    }
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    progressTitle.setText("Loading next...");
                    new getImage(imageView).execute(urlList.get(getIterator));
                }
            }
        });

        findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setVisibility(ImageView.INVISIBLE);
                if (urlList.size() > 0) {
                    getIterator--;
                    if (getIterator <= -1) {
                        getIterator = urlList.size()-1;
                    }
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    progressTitle.setText("Loading previous...");
                    new getImage(imageView).execute(urlList.get(getIterator));
                }
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

    private class GetKeywords extends AsyncTask<Void, Void, String[]>{

        boolean fromCheck;

        public GetKeywords(boolean fromCheck) {
            this.fromCheck = fromCheck;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            String[] keywords = null;
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = null;
            try {
                URL url = new URL("http://dev.theappsdr.com/apis/photos/keywords.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    result = stringBuilder.toString();
                    keywords = result.split(";");
                    //arrayList.add(result);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return keywords;
        }

        @Override
        protected void onPostExecute(final String[] strings) {
            Log.d("demo",strings.toString());
            if(fromCheck == true){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose a Keyword")
                        .setItems(strings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView editText = findViewById(R.id.searchKeyword);
                                editText.setText(strings[which]);
                                searchKeyword = findViewById(R.id.searchKeyword);
                                new getKeyWordsLink().execute(searchKeyword.getText().toString());
                            }
                        });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    public class getKeyWordsLink extends AsyncTask<String, Void, ArrayList<String>>{
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            HttpURLConnection connection = null;
//            StringBuilder stringBuilder = new StringBuilder();
            urlList.clear();
            getIterator = 0;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://dev.theappsdr.com/apis/photos/index.php"
                        + "?" +"keyword="+ URLEncoder.encode(strings[0], "UTF-8"));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    while((line = reader.readLine())!=null){
                        urlList.add(line.trim());
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return urlList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> urlList) {
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setVisibility(ImageView.INVISIBLE);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressTitle.setText("Loading...");
            Log.d("demo", String.valueOf(urlList.size()));
            if(urlList.size()>0){
                if(urlList.size() == 1){
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
                new getImage(imageView).execute(urlList.get(0));
            }
            else{
                Toast.makeText(MainActivity.this, "No Images is found", Toast.LENGTH_SHORT).show();
                imageView.setVisibility(ImageView.INVISIBLE);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                progressTitle.setText("");
                next.setEnabled(false);
                previous.setEnabled(false);
                previous.setAlpha((float) 0.5);
                next.setAlpha((float) 0.5);
            }
            Log.d("Demo",urlList.toString());
        }

    }

    private class getImage extends AsyncTask<String, Void, Bitmap>{

        ImageView imageView;
        Bitmap bitmap = null;

        public getImage(ImageView iv) {
            imageView = iv;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection connection = null;
            bitmap = null;

            URL url = null;
            try {

                url = new URL(strings[0]);
                Log.d("Demo", "doInBackground: "+strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(ImageView.VISIBLE);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                progressTitle.setText("");
                if(urlList.size()==1){
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

            }else{
                Toast.makeText(MainActivity.this, "No Image is found", Toast.LENGTH_SHORT).show();
                imageView.setVisibility(ImageView.INVISIBLE);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                progressTitle.setText("");
                if(urlList.size()==1){
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

            }
        }
    }
}

