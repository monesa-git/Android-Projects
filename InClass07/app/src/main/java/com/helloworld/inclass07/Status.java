package com.helloworld.inclass07;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Status extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        ProgressBar progressBar = findViewById(R.id.progressResult);
        TextView textview = findViewById(R.id.percent);
        double result =  getIntent().getExtras().getDouble("result");
        Log.d("Marks", "TotalQuestions: "+result);
        textview.setText(String.valueOf((int)result)+"%");
        TextView message = findViewById(R.id.textViewComment);
        if((int) result == 100){
            message.setText("Well done!!!");
        }else{
            message.setText("Try again and see if you can get all the correct answers!");
        }
        progressBar.setProgress((int) result);
        findViewById(R.id.buttonQuit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("getValue",1);
                setResult(200, data);
                finish();
            }
        });

        findViewById(R.id.buttonTryAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("getValue",2);
                setResult(200, data);
                finish();
            }
        });
    }
}
