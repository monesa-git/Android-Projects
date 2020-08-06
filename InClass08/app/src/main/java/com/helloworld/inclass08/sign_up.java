package com.helloworld.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class sign_up extends AppCompatActivity {
    private EditText fname;
    private EditText lname;
    private EditText password;
    private EditText repeatPassword;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        fname = findViewById(R.id.editTextFname);
        lname = findViewById(R.id.editTextLname);
        password = findViewById(R.id.editTextChoosePassword);
        repeatPassword = findViewById(R.id.editTextRepeatPassword);
        email = findViewById(R.id.editTextEmail);
        findViewById(R.id.buttonSignupFirst).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidations(fname) &&
                        checkValidations(lname) &&
                        checkValidations(email) &&
                        checkValidations(password) &&
                        checkValidations(repeatPassword)){
                    String fnameValue = fname.getText().toString().trim();
                    String lnameValue = lname.getText().toString().trim();
                    String passwordValue = password.getText().toString().trim();
                    String repeatPasswordValue = repeatPassword.getText().toString().trim();
                    String emailValue = email.getText().toString().trim();
                    if(passwordValue.equals(repeatPasswordValue)){
                        new createNewUser(emailValue,passwordValue,fnameValue,lnameValue).execute("");
                    }else{
                        repeatPassword.setError("Both passwords should match");
                        password.setError("Both passwords should match");
                    }
                }
            }
        });

        findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    public class createNewUser extends AsyncTask<String, Void, String>{
        String emailValue, passwordValue, fnameValue, lnameValue;
        public createNewUser(String emailValue, String passwordValue, String fnameValue, String lnameValue) {
            this.emailValue = emailValue;
            this.passwordValue = passwordValue;
            this.fnameValue = fnameValue;
            this.lnameValue = lnameValue;
        }

        @Override
        protected String doInBackground(String... strings) {

            final OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("email", emailValue)
                    .add("password",passwordValue)
                    .add("fname",fnameValue)
                    .add("lname",lnameValue)
                    .build();
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
                    .post(formBody)
                    .build();
            String responseValue = null;
            try (Response response = client.newCall(request).execute()) {
                responseValue = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseValue;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){
                JSONObject root = null;
                try {
                    root = new JSONObject(s);
                    if(root.getString("status").equals("ok")){
                        User user = new User();
                        user.fname = root.getString("user_fname").trim();
                        user.lname = root.getString("user_lname").trim();
                        user.id = root.getInt("user_id");
                        user.tokenKey = root.getString("token");
                        user.email = root.getString("user_email").trim();
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey",0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("TOKEN_KEY",user.tokenKey);
                        editor.commit();
                        Toast.makeText(sign_up.this, "User Successfully created", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(sign_up.this, Inbox.class);
                        intent.putExtra("UserObject", user);
                        startActivityForResult(intent, 100);
                        finish();
                    }else{
                        Toast.makeText(sign_up.this, root.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
