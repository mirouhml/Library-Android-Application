package com.example.phoenix.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phoenix.library.Helper.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditInfo extends AppCompatActivity {
    String email;
    private String TAG = EditInfo.class.getSimpleName();
    String type;
    String url;
    String resultName;
    JSONArray infoJson = null;
    EditText idET;
    EditText nameET;
    EditText surnameET;
    EditText specialtyET;
    EditText rankET;
    Intent intent;
    Context context;
    boolean ok = false;
    int Do;
    AppCompatActivity this_activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info);
        intent = getIntent();
        this_activity = this;
        context = getApplicationContext();
        idET = findViewById(R.id.input_idNumber);
        nameET = findViewById(R.id.input_name);
        surnameET = findViewById(R.id.input_surname);
        specialtyET = findViewById(R.id.input_specialty);
        rankET = findViewById(R.id.input_Level);
        email = Login.sharedpreferences.getString("LOGIN",null);
        type = Login.sharedpreferences.getString("TYPE",null);
        if (type.equals("Teacher")){
            Do = 1;
            url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/checkUserEnseignantInfo/"+email;
            resultName = "checkUserEnseignantInfoResult";
            new GetInfo().execute();
        }else{
            Do = 1;
            url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/checkUserEtudiantInfo/"+email;
            resultName = "checkUserEtudiantInfoResult";
            new GetInfo().execute();
        }
        Button save = findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Do = 2;
                String ID = idET.getText().toString();
                ID = ID.replaceAll("\\s+","");
                String name = nameET.getText().toString();
                name = name.replaceAll("\\s+","%20");
                String surname = surnameET.getText().toString();
                surname = surname.replaceAll("\\s+","%20");
                String rank = rankET.getText().toString();
                rank = rank.replaceAll("\\s+","");
                if (type.equals("Teacher")){
                    url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/updateUserEnseignantInfo/"+ID+"/"+name+"/"+surname+"/"+rank;
                    resultName = "checkUserEnseignantInfoResult";
                    new GetInfo().execute();
                }else{
                    String specialty = specialtyET.getText().toString();
                    specialty = specialty.replaceAll("\\s+","%20");
                    url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/updateUserEtudiantInfo/"+ID+"/"+name+"/"+surname+"/"+specialty+"/"+rank;
                    resultName = "checkUserEtudiantInfoResult";
                    new GetInfo().execute();
                }
            }
        });
    }
    private class GetInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            switch (Do){
                case 1 :{
                    String jsonStr = sh.makeServiceCall(url);
                    Log.e(TAG, "Response from url: " + jsonStr);
                    if (jsonStr != null) {
                        try {
                            // Getting JSON Array node
                            JSONObject books = new JSONObject(jsonStr);
                            infoJson = books.getJSONArray(resultName);
                        } catch (final JSONException e) {
                            Log.e(TAG, "Response from url: " + e);
                        }
                    }
                    break;
                }
                case 2 :{
                    String jsonStr = sh.makeServiceCall(url);
                    Log.e(TAG,"result: " +jsonStr);

                    try{
                        JSONObject books = new JSONObject(jsonStr);
                        Log.e(TAG,""+books.getString("updateUserEtudiantInfoResult").equals("true"));
                        if (type.equals("Teacher")){
                            if (books.getString("updateUserEnseignantInfoResult").equals("true"))
                                ok = true;
                        }
                        else{
                            if (books.getString("updateUserEtudiantInfoResult").equals("true"))
                                ok = true;
                        }

                    }catch (JSONException e){

                    }
                    break;
                }
                default: break;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //adding the books
            switch (Do){
                case 1 : {
                    if (infoJson != null) {
                        try {
                            if (type.equals("Teacher")){
                                String ID = infoJson.getJSONArray(0).getString(0);
                                String name = infoJson.getJSONArray(0).getString(2);
                                String surname = infoJson.getJSONArray(0).getString(1);
                                String rank = infoJson.getJSONArray(0).getString(3);
                                specialtyET.setVisibility(View.GONE);
                                idET.setText(ID);
                                nameET.setText(name);
                                surnameET.setText(surname);
                                rankET.setText(rank);
                                idET.setInputType(InputType.TYPE_NULL);
                            }else{
                                String ID = infoJson.getJSONArray(0).getString(0);
                                String name = infoJson.getJSONArray(0).getString(2);
                                String surname = infoJson.getJSONArray(0).getString(1);
                                String specialty = infoJson.getJSONArray(0).getString(3);
                                String rank = infoJson.getJSONArray(0).getString(4);
                                specialtyET.setVisibility(View.VISIBLE);
                                idET.setText(ID);
                                nameET.setText(name);
                                surnameET.setText(surname);
                                specialtyET.setText(specialty);
                                rankET.setText(rank);
                                idET.setInputType(InputType.TYPE_NULL);
                            }

                        } catch (JSONException e) {
                        }
                    }
                    break;
                }
                case 2 : {
                    if (ok){
                        Toast.makeText(context,"Done!",Toast.LENGTH_LONG).show();
                        this_activity.finish();
                    }
                    else
                        Toast.makeText(context,"Failed! Retry again..",Toast.LENGTH_LONG).show();


                    break;
                }
                default: break;
            }

        }

    }
}
