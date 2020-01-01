package com.example.phoenix.library;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phoenix.library.Helper.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailsPage extends AppCompatActivity {
    private String TAG = DetailsPage.class.getSimpleName();
    String title;
    String author;
    String theme;
    String book_count;
    String description;
    String ID;
    String email;
    String url;
    View detailsView;
    String ok;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_page);
        context = getApplicationContext();
        title = getIntent().getStringExtra("TITLE");
        ID = getIntent().getStringExtra("ID");
        author = getIntent().getStringExtra("AUTHOR");
        theme = getIntent().getStringExtra("THEME");
        book_count = getIntent().getStringExtra("BOOK_COUNT");
        description = getIntent().getStringExtra("DESCRIPTION");
        detailsView = findViewById(R.id.details_view);
        TextView titleTV = findViewById(R.id.details_title);
        titleTV.setText(title);
        TextView idTV = findViewById(R.id.details_ID);
        idTV.setText(ID);
        TextView authorTV = findViewById(R.id.details_author);
        authorTV.setText(author);
        TextView themeTV = findViewById(R.id.details_theme);
        themeTV.setText(theme);
        TextView countTV = findViewById(R.id.details_count);
        countTV.setText(book_count);
        TextView descriptionTV = findViewById(R.id.details_description);
        descriptionTV.setText(description);
        String ok = getIntent().getStringExtra("OK");
        Button book = findViewById(R.id.bookButton);
        if (ok.equals("true"))
            book.setVisibility(View.GONE);
        else {
            if (Integer.parseInt(book_count) == 0){
                book.setText("Sign up to the wait-list");
            }
            }
            book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email = Login.sharedpreferences.getString("LOGIN",null);
                    Log.e(TAG,email);
                    url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/reserver/"+ID+"/"+email;
                    new BookingProcess().execute();
                }
            });


    }

    private class BookingProcess extends AsyncTask<Void, Void, Void> {


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
                    ok = books.getString("reserverResult");
                } catch (final JSONException e) {
                    Log.e(TAG, "Response from url: " + e);
                }
            } else {
                Snackbar.make(detailsView, "Check your network connection then retry.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.e(TAG, "Response from url: " + jsonStr);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            switch (ok){
                case "0":{
                    //getDialog("You're in the blacklist, you can't perform this operation.").show();
                    Toast.makeText(context,"You're in the blacklist, you can't perform this operation.",Toast.LENGTH_LONG).show();
                    break;
                }
                case "1":{
                    //getDialog("You already took this book.").show();
                    Toast.makeText(context,"You already took this book.",Toast.LENGTH_LONG).show();
                    break;
                }
                case "2":{
                    //getDialog("The book isn't available, you've been added to the wait-list.").show();
                    Toast.makeText(context,"You have been added to the wait-list.",Toast.LENGTH_LONG).show();
                    break;
                }
                case "3":{
                    //getDialog("Please retry again something happened.");
                    Toast.makeText(context,"Please retry again something happened.",Toast.LENGTH_LONG).show();
                    break;
                }
                case "4":{
                    //getDialog("Done!!").show();
                    Toast.makeText(context,"Done!!",Toast.LENGTH_LONG).show();
                    break;
                }
                case "5":{
                    //getDialog("Done!!").show();
                    Toast.makeText(context,"You are already in the wait-list.",Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }

    }



    private Dialog getDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false);
            builder.setPositiveButton("OK", null);
        return builder.create();
    }
}
