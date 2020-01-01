package com.example.phoenix.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.phoenix.library.Helper.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class Signup extends AppCompatActivity {
    String email;
    String password;
    String url;
    boolean ok = false;
    Context context;
    EditText emailEV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        final Button next = findViewById(R.id.btn_next);
        emailEV = findViewById(R.id.input_email);
        final EditText passwordET = findViewById(R.id.input_password);
        context = getApplicationContext();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate(emailEV,passwordET)){
                    new Check().execute();
                }
            }
        });
    }

    public boolean validate(EditText _emailText, EditText _passwordText) {
        boolean valid = true;
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address please.");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.length() <= 5 ) {
            _passwordText.setError("Enter a password that has 6 characters or more please.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private class Check extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (isOnline()) {
                email = email.replaceAll("\\s+","");
                url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/checkEmail/"+email;
                HttpHandler sh = new HttpHandler();
                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        // Getting JSON Array node
                        JSONObject books = new JSONObject(jsonStr);
                        String result = books.getString("checkEmailResult");
                        if (result.equals("false"))
                            ok = true;
                    } catch (final JSONException e) {
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (ok){
                final RadioGroup radioGroup= findViewById(R.id.radioGroup);
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioStudent){
                    Intent intent = new Intent(Signup.this,Student.class);
                    intent.putExtra("EMAIL",email);
                    intent.putExtra("PASSWORD",password);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Signup.this,Teacher.class);
                    intent.putExtra("EMAIL",email);
                    intent.putExtra("PASSWORD",password);
                    startActivity(intent);
                }
            } else {
                emailEV.setError("This email is used.");
            }
        }
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;
    }
}

