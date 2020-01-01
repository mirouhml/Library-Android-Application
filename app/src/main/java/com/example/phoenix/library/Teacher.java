package com.example.phoenix.library;

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

import com.example.phoenix.library.Helper.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Teacher extends AppCompatActivity {
    String email;
    String password;
    String url;
    String id;
    String name;
    String surname;
    String rank;
    Context context;
    EditText idET;
    String checkEtudiantOrEnseignantAvailableResult;
    String checkEtudiantOrEnseignantResult;
    boolean ok = false;
    private String TAG = Teacher.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher);
        email = getIntent().getStringExtra("EMAIL");
        password = getIntent().getStringExtra("PASSWORD");
        context = getApplicationContext();
        final Button button = findViewById(R.id.btn_teacher_signup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idET = findViewById(R.id.input_teacher_idNumber);
                final EditText nameET = findViewById(R.id.input_teacher_name);
                final EditText surnameET = findViewById(R.id.input_teacher_surname);
                final EditText rankET = findViewById(R.id.input_teacher_rank);
                id = idET.getText().toString();
                name = nameET.getText().toString();
                surname = surnameET.getText().toString();
                rank = rankET.getText().toString();
                new Signup().execute();
            }
        });
    }

    private class Signup extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            if (isOnline()) {
                HttpHandler sh = new HttpHandler();
                email = email.replaceAll("\\s+","");
                password = password.replaceAll("\\s+","%20");
                id = id.replaceAll("\\s+","");
                name = name.replaceAll("\\s+","%20");
                surname = surname.replaceAll("\\s+","%20");
                rank = rank.replaceAll("\\s+","");
                url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/checkEtudiantOrEnseignantAvailable/2/"+id;
                String jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        // Getting JSON Array node
                        JSONObject books = new JSONObject(jsonStr);
                        checkEtudiantOrEnseignantAvailableResult = books.getString("checkEtudiantOrEnseignantAvailableResult");
                        if (checkEtudiantOrEnseignantAvailableResult.equals("true")){
                            url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/checkEtudiantOrEnseignant/2/"+id;
                            jsonStr = sh.makeServiceCall(url);
                            if (jsonStr != null) {
                                try {
                                    // Getting JSON Array node
                                    books = new JSONObject(jsonStr);
                                    checkEtudiantOrEnseignantResult = books.getString("checkEtudiantOrEnseignantResult");
                                    if (checkEtudiantOrEnseignantResult.equals("true")){
                                        url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/createUser/"+email+"/"+password;
                                        String jsonStr2 = sh.makeServiceCall(url);
                                        url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/addUserEnseignantInfo/"+id+"/"+email+"/"+name+"/"+surname+"/"+rank;
                                        jsonStr = sh.makeServiceCall(url);
                                        if (jsonStr != null && jsonStr2 != null)
                                            ok = true;
                                    }
                                } catch (final JSONException e) {
                                }
                            }
                        }
                    } catch (final JSONException e) {
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (checkEtudiantOrEnseignantAvailableResult.equals("false"))
                idET.setError("this ID is already used.");
            else if (checkEtudiantOrEnseignantResult.equals("false"))
                idET.setError("this ID doesn't belong to a teacher.");
                else if (ok){
                    SharedPreferences.Editor editor = Login.sharedpreferences.edit();
                    editor.putString("LOGIN", email);
                    editor.putString("Type","Enseignant");
                    editor.apply();
                    Intent intent;
                    intent = new Intent(Teacher.this,home.class);
                    intent.putExtra("EMAIL",email);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
