package com.helloworld.inclass07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private Button start;
    private Button finish;
    private ProgressBar progressBar;
    private ImageView imageView;
    ArrayList<QuizData> quizDataArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.buttonStart);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        if(isConnected()){
            progressBar.setVisibility(ProgressBar.VISIBLE) ;
            new LoadAsyncQuiz().execute("http://dev.theappsdr.com/apis/trivia_json/index.php");
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QuizLayout.class);
                intent.putExtra("QuizData", quizDataArrayList);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


    public class LoadAsyncQuiz extends AsyncTask<String, String, ArrayList<QuizData>>{

        @Override
        protected ArrayList<QuizData> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            String result = null;
            quizDataArrayList.clear();
            URL url = null;
            try {
                url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), "UTF-8");
                    JSONObject root = new JSONObject(result);
                    JSONArray articles = root.getJSONArray("questions");
                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject articlesJson = articles.getJSONObject(i);
                        QuizData quizData = new QuizData();
                        quizData.id = articlesJson.getString("id");
                        quizData.text = articlesJson.getString("text");
                        quizData.image = "";
                        try{
                            quizData.image = articlesJson.getString("image");
                        }catch(Exception e){
                            Log.d("Demo", "There is no image present for this");
                        }
                        JSONObject quizChoices = articlesJson.getJSONObject("choices");
                        JSONArray quizChoiceArray = quizChoices.getJSONArray("choice");
                        for(int j = 0; j < quizChoiceArray.length(); j++)
                            quizData.choices.add(quizChoiceArray.getString(j));
                        quizData.answer = quizChoices.getString("answer");
                        quizDataArrayList.add(quizData);
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
            return quizDataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<QuizData> quizData) {
            super.onPostExecute(quizData);
            Log.d("Demo",quizData.toString());
            progressBar.setVisibility(ProgressBar.VISIBLE);
            imageView.setVisibility(ImageView.VISIBLE);
            TextView loadTextView = findViewById(R.id.textView4);
            loadTextView.setText("Trivia Ready");
            start.setEnabled(true);
        }
    }
}


//[QuizData{id='0',
// text='Who is the first President of the United States of America?',
// image='http://dev.theappsdr.com/apis/trivia_json/photos/georgewashington.png',
// answer='1',
// choices=[George Washington, Thomas Jefferson, James Monroe, John Adams, Barack Obama, George Bush, Abraham Lincoln, John F. Kennedy]},
// QuizData{id='1', text='The above flag is for which country?',
// image='http://dev.theappsdr.com/apis/trivia_json/photos/egypt.png',
// answer='3', choices=[Spain, Finland, Egypt]},
// QuizData{id='2', text='The name of the soccer player in the above photo is?',
// image='http://dev.theappsdr.com/apis/trivia_json/photos/messi.png', answer='4',
// choices=[Cristiano Ronaldo, David Beckham, Carlos Tevez, Lional Messi]},
// QuizData{id='3', text='Who was the first female pilot to fly solo across the Atlantic Ocean?',
// image='http://dev.theappsdr.com/apis/trivia_json/photos/earhart.png', answer='3',
// choices=[Bonnie Gann, Elsie MacGill, Amelia Earhart, Linda Godwin]},
// QuizData{id='4', text='The above map is for which country?',
// image='http://dev.theappsdr.com/apis/trivia_json/photos/italy.png',
// answer='3', choices=[United Kingdom, France, Italy, Spain, Romania]}]//