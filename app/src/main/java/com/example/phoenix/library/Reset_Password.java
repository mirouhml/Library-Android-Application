package com.example.phoenix.library;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phoenix.library.Helper.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Reset_Password extends AppCompatActivity {
    String url;
    private String TAG = Reset_Password.class.getSimpleName();
    String emailText;
    String ok = "false";
    Context context;
    View reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset__password);
        reset = findViewById(R.id.reset);
        context = getApplicationContext();
        final EditText email = findViewById(R.id.recover_email);
        Button recover = findViewById(R.id.reset_button);
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(email)){
                    emailText = email.getText().toString();
                    url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/forgotPassword/"+emailText;
                    new GetPassword().execute();
                }
            }
        });

    }
    private class GetPassword extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONObject books = new JSONObject(jsonStr);
                    ok = books.getString("forgotPasswordResult");

                } catch (final JSONException e) {
                    Log.e(TAG, "Response from url: " + e);
                }
            }
            else{
                Snackbar.make(reset, "There was a problem doing the process.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e(TAG, "PostExecute ");
                if (ok.equals("true")){
                    Toast.makeText(context,"An email will be sent to you.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Reset_Password.this,Login.class);
                    startActivity(intent);
                    finish();
                }
                else {

                    Snackbar.make(reset, "Either the email doesn't exist or something happened, contact the administration.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }
    private boolean validate(EditText _emailText) {
        boolean valid = true;
        String email = _emailText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address please.");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        return valid;
    }
}
