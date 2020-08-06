package com.helloworld.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText login;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Mailer");
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);

        findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(checkValidations(login)&&checkValidations(password)){
                    String loginText = login.getText().toString().trim();
                    String passwordText = password.getText().toString().trim();
                    Log.d("demo",loginText+" "+passwordText);
                    new getTokeyAsync(loginText, passwordText).execute();
                }
            }
        });

        findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sign_up.class);
                startActivity(intent);
            }
        });
    }

    public boolean checkValidations(EditText editText){
        if(editText.getText().toString().equals("")){
            editText.setError("Cannot be empty");
            return false;
        }else{
            return true;
        }
    }

    public class getTokeyAsync extends AsyncTask<String, Void, String>{

        String username, password;

        public getTokeyAsync(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... strings) {
            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("email", username)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
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
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject root = null;
            try {
                root = new JSONObject(result);
                if(root.getString("status").equals("ok")){
                    User user = new User();
                    user.fname = root.getString("user_fname");
                    user.lname = root.getString("user_lname");
                    user.id = root.getInt("user_id");
                    user.tokenKey = root.getString("token");
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey",0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("TOKEN_KEY",user.tokenKey);
                    editor.commit();
                    user.email = root.getString("user_email");
                    Intent intent = new Intent(MainActivity.this, Inbox.class);
                    intent.putExtra("UserObject", user);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, root.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
