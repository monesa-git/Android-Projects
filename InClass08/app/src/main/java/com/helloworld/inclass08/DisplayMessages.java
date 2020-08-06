package com.helloworld.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayMessages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__messages);
        setTitle(" ");

        Emails email = (Emails) getIntent().getExtras().getSerializable("EmailObject");
        TextView sender = findViewById(R.id.senderName);
        TextView subject = findViewById(R.id.subject);
        TextView messages = findViewById(R.id.messages);
        TextView createdTime = findViewById(R.id.createdAt);

        sender.setText("From: "+email.sender_fname+" "+email.sender_lname);
        subject.setText("Subject: "+email.subject);
        messages.setText(email.message);
        createdTime.setText(email.created_at);

        findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
