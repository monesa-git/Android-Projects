package com.helloworld.inclass07;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class QuizLayout extends AppCompatActivity implements QuizAdapter.InteractWithRecyclerView {

    private int counter = 0;
    private int correctAnswerCount = 0;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView timer;
    private ProgressBar progressBarImage;
    ArrayList<QuizData> quizDataArrayList;
    private boolean isCalled = false;
    int sizeArrayList = 0;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_layout);
        quizDataArrayList = (ArrayList<QuizData>) getIntent().getExtras().getSerializable("QuizData");
        sizeArrayList = quizDataArrayList.size();
        timer = findViewById(R.id.timer);
        progressBarImage = findViewById(R.id.progressBarImage);
        timer.setText("");
        countDownTimer = new CountDownTimer(2*60*1000, 1000) {
            @Override
            public void onTick(long l) {
                long minute = l/1000/60;
                long second = (l - minute*60*1000)/1000;
                Log.d("Demo", "onTick: "+ minute+ " "+second);
                timer.setText(minute+" : "+second);
            }

            @Override
            public void onFinish() {
                callIntent();
            }
        };
        countDownTimer.start();
        getQuestion();
        findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                getQuestion();
            }
        });

        findViewById(R.id.buttonQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               countDownTimer.onFinish();
            }
        });
    }

    public void callIntent(){
        if(!isCalled){
            double percent;
            percent = correctAnswerCount * 100 / sizeArrayList ;
            Intent intent = new Intent(QuizLayout.this, Status.class);
            intent.putExtra("result", percent);
            //intent.putExtra("totalquestions", quizDataArrayList.size() );
            startActivityForResult(intent, 100);
            isCalled = true;
        }
    }

    public void getQuestion(){
        if(counter < sizeArrayList) {
            progressBarImage.setVisibility(ProgressBar.VISIBLE);
            TextView questionNumber = findViewById(R.id.questionNumber);
            TextView question = findViewById(R.id.question);
            String number = quizDataArrayList.get(counter).id.trim();
            int questionCounter = Integer.valueOf(number)+1;
            questionNumber.setText("Q"+String.valueOf(questionCounter));
            ImageView imageView = findViewById(R.id.QuizImage);
            if(quizDataArrayList.get(counter).image.equals("")){
                imageView.setImageResource(R.drawable.download);
                progressBarImage.setVisibility(ProgressBar.INVISIBLE);
            }else{
                Picasso.get()
                        .load(quizDataArrayList.get(counter).image)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBarImage.setVisibility(ProgressBar.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(QuizLayout.this, "No Image found", Toast.LENGTH_SHORT).show();
                                progressBarImage.setVisibility(ProgressBar.INVISIBLE);
                            }
                        });
            }
            question.setText(quizDataArrayList.get(counter).text);
            //For Recycler Views:
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

            layoutManager = new LinearLayoutManager(QuizLayout.this);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            mAdapter = new QuizAdapter(quizDataArrayList.get(counter).choices, QuizLayout.this);
            recyclerView.setAdapter(mAdapter);
        } else{
            Log.d("Demo", "The Value is : " + correctAnswerCount);
            Log.d("Demo", "Question: " + counter + "/" + sizeArrayList);
            countDownTimer.onFinish();
        }
    }

    @Override
    public void selectedItem(int position) {
        if(counter < sizeArrayList){
            if(quizDataArrayList.get(counter).answer.equals(String.valueOf((position+1)))){
                correctAnswerCount++;
            }
        }
        counter++;
        Log.d("Demo", String.valueOf(counter));
       getQuestion();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
          int doActivity = data.getExtras().getInt("getValue");
          if(doActivity == 1){
              finish();
          }else if(doActivity == 2){
              counter = 0;
              correctAnswerCount = 0;
              isCalled = false;
              sizeArrayList = quizDataArrayList.size();
              timer = findViewById(R.id.timer);
              timer.setText("");
              countDownTimer.start();
              getQuestion();
          }
        }
    }
}
