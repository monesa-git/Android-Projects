package com.helloworld.inclass08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Inbox extends AppCompatActivity implements EmailAdapter.InteractWithRecyclerView {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<Emails> emailsArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__email);
        setTitle("Inbox");
        User user = (User) getIntent().getExtras().getSerializable("UserObject");
        Log.d("demo", user.toString());
        TextView textView = findViewById(R.id.textViewName);
        textView.setText(user.fname + " " + user.lname);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey", 0);
        Log.d("demo", preferences.getString("TOKEN_KEY", null));
        new LoadMessagesAsync().execute("");


        findViewById(R.id.compose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Inbox.this, CreateEmail.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("TOKEN_KEY", "");
                editor.commit();
                Toast.makeText(Inbox.this, "Logged out succesfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(200, intent);
                finish();
            }
        });
    }

    @Override
    public void selectedItem(Emails emailObject) {
        new DeleteMessage(emailObject).execute("");
    }

    @Override
    public void getDetails(Emails emailObject) {
        Intent intent = new Intent(Inbox.this, DisplayMessages.class);
        intent.putExtra("EmailObject", emailObject);
        startActivity(intent);
    }

    public class LoadMessagesAsync extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            emailsArrayList.clear();
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey", 0);
            final OkHttpClient client = new OkHttpClient();
            String listofEmails = null;
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox")
                    .header("Authorization", "BEARER " + preferences.getString("TOKEN_KEY", null))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                listofEmails = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return listofEmails;
        }

        @Override
        protected void onPostExecute(String emails) {
            super.onPostExecute(emails);

            if (emails != null) {
                try {
                    JSONObject root = new JSONObject(emails);
                    if (root.getString("status").equals("ok")) {
                        JSONArray rootArray = root.getJSONArray("messages");
                        for (int i = 0; i < rootArray.length(); i++) {
                            JSONObject arrayObject = rootArray.getJSONObject(i);
                            Emails email = new Emails();
                            email.id = arrayObject.getString("id");
                            email.sender_fname = arrayObject.getString("sender_fname");
                            email.sender_lname = arrayObject.getString("sender_lname");
                            email.sender_id = arrayObject.getString("sender_id");
                            email.receiver_id = arrayObject.getString("receiver_id");
                            email.message = arrayObject.getString("message");
                            email.subject = arrayObject.getString("subject");
                            email.created_at = arrayObject.getString("created_at");
                            email.updated_at = arrayObject.getString("updated_at");
                            emailsArrayList.add(email);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("demo", emailsArrayList.toString());

                if (emailsArrayList.size() > 0) {
                    //For Recycler Views:
                    recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

                    layoutManager = new LinearLayoutManager(Inbox.this);
                    recyclerView.setLayoutManager(layoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new EmailAdapter(emailsArrayList, Inbox.this);
                    recyclerView.setAdapter(mAdapter);
                }

            }
        }
    }

    public class DeleteMessage extends AsyncTask<String, Void, String> {

        Emails email;

        public DeleteMessage(Emails email) {
            this.email = email;
        }

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey", 0);
            final OkHttpClient client = new OkHttpClient();
            String deleteMessage = null;
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/"+email.id)
                    .header("Authorization", "BEARER " + preferences.getString("TOKEN_KEY", null))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                deleteMessage = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return deleteMessage;
        }

        @Override
        protected void onPostExecute(String deleteMessage) {
            super.onPostExecute(deleteMessage);
            JSONObject root = null;
            try {
                root = new JSONObject(deleteMessage);
                if(root.getString("status").equals("ok")){
                    emailsArrayList.remove(email);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(Inbox.this, "Email Sucessfully Deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Inbox.this, root.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

