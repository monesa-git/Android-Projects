package com.helloworld.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateEmail extends AppCompatActivity {

    ArrayList<User> userArrayList = new ArrayList<>();
    int receiver_id;
    private Spinner dropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);
        setTitle("Create New Email");
        dropdown = findViewById(R.id.spinner);
        new loadUsers().execute("");
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                receiver_id = userArrayList.get(position).id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView subject = findViewById(R.id.editTextSub);
                TextView messages = findViewById(R.id.editTextMsg);
                new sendEmail(receiver_id, subject.getText().toString().trim(), messages.getText().toString().trim()).execute();
            }
        });

        findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class loadUsers extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            userArrayList.clear();
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey",0);
            final OkHttpClient client = new OkHttpClient();
            String listOfUsers = null;
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users")
                    .header("Authorization", "BEARER "+ preferences.getString("TOKEN_KEY", null))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                listOfUsers = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listOfUsers;
        }

        @Override
        protected void onPostExecute(String users) {
            if(users!=null) {
                try {
                    JSONObject root = new JSONObject(users);
                    if (root.getString("status").equals("ok")) {
                        JSONArray rootArray = root.getJSONArray("users");
                        for (int i = 0; i < rootArray.length(); i++) {
                            JSONObject arrayObject = rootArray.getJSONObject(i);
                            User userList = new User();
                            userList.id = arrayObject.getInt("id");
                            userList.fname = arrayObject.getString("fname");
                            userList.lname = arrayObject.getString("lname");
                            userArrayList.add(userList);
                        }
                    }else{
                        Toast.makeText(CreateEmail.this, root.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //create a list of items for the spinner.
                String[] items = new String[userArrayList.size()];
                int i = 0;
                for(User user : userArrayList){
                    items[i] = user.fname +" "+ user.lname;
                    i++;
                }
                //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateEmail.this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
                dropdown.setAdapter(adapter);
                Log.d("demo", userArrayList.toString());

            }
        }
    }

    public class sendEmail extends AsyncTask<String, Void, String> {

        int receiver_id;
        String subject, messages;

        public sendEmail(int receiver_id, String subject, String messages) {
            this.receiver_id = receiver_id;
            this.subject = subject;
            this.messages = messages;
        }

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey", 0);
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("receiver_id", String.valueOf(receiver_id))
                    .add("subject", subject)
                    .add("message", messages)
                    .build();
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                    .header("Authorization", "BEARER " + preferences.getString("TOKEN_KEY", null))
                    .post(formBody)
                    .build();
            final String returnResult = null;
            Response responses = null;
            User user = null;
            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return responses.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject root = null;
            if(s!=null){
                try {
                    root = new JSONObject(s);
                    if (root.getString("status").equals("ok")) {
                        Toast.makeText(CreateEmail.this, "Sent Mail Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(CreateEmail.this, root.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
