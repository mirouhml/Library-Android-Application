package com.example.phoenix.library;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.phoenix.library.Helper.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class book_list extends AppCompatActivity {
    private String TAG = book_list.class.getSimpleName();
    private static String url ;
    JSONArray booksJson = null;
    int typeOfSearch = 1;
    ListView listView;
    boolean ok =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_list);
        listView = findViewById(R.id.list);
        final TextView titleView = findViewById(R.id.titleView);
        final TextView authorView = findViewById(R.id.authorView);
        final TextView themeView = findViewById(R.id.themeView);
        final TextView keywordsView = findViewById(R.id.keywordsView);
        final TextView note = findViewById(R.id.note);

        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfSearch = 1;
                titleView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                titleView.setTextColor(getResources().getColor(R.color.gray));

                authorView.setBackgroundColor(getResources().getColor(R.color.gray));
                authorView.setTextColor(getResources().getColor(R.color.colorAccent));

                themeView.setBackgroundColor(getResources().getColor(R.color.gray));
                themeView.setTextColor(getResources().getColor(R.color.colorAccent));

                keywordsView.setBackgroundColor(getResources().getColor(R.color.gray));
                keywordsView.setTextColor(getResources().getColor(R.color.colorAccent));

                note.setVisibility(View.INVISIBLE);
            }
        });
        authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfSearch = 2;
                authorView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                authorView.setTextColor(getResources().getColor(R.color.gray));

                titleView.setBackgroundColor(getResources().getColor(R.color.gray));
                titleView.setTextColor(getResources().getColor(R.color.colorAccent));

                themeView.setBackgroundColor(getResources().getColor(R.color.gray));
                themeView.setTextColor(getResources().getColor(R.color.colorAccent));

                keywordsView.setBackgroundColor(getResources().getColor(R.color.gray));
                keywordsView.setTextColor(getResources().getColor(R.color.colorAccent));

                note.setVisibility(View.INVISIBLE);
            }
        });
        themeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfSearch = 3;
                themeView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                themeView.setTextColor(getResources().getColor(R.color.gray));

                titleView.setBackgroundColor(getResources().getColor(R.color.gray));
                titleView.setTextColor(getResources().getColor(R.color.colorAccent));

                authorView.setBackgroundColor(getResources().getColor(R.color.gray));
                authorView.setTextColor(getResources().getColor(R.color.colorAccent));

                keywordsView.setBackgroundColor(getResources().getColor(R.color.gray));
                keywordsView.setTextColor(getResources().getColor(R.color.colorAccent));

                note.setVisibility(View.INVISIBLE);
            }
        });
        keywordsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeOfSearch = 4;
                keywordsView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                keywordsView.setTextColor(getResources().getColor(R.color.gray));

                titleView.setBackgroundColor(getResources().getColor(R.color.gray));
                titleView.setTextColor(getResources().getColor(R.color.colorAccent));

                authorView.setBackgroundColor(getResources().getColor(R.color.gray));
                authorView.setTextColor(getResources().getColor(R.color.colorAccent));

                themeView.setBackgroundColor(getResources().getColor(R.color.gray));
                themeView.setTextColor(getResources().getColor(R.color.colorAccent));

                note.setVisibility(View.VISIBLE);
            }
        });
        final EditText searchText = findViewById(R.id.searchText);
        ImageView search = findViewById(R.id.search);
        url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/bookListAllDisponible";
        ok = false;
        new GetBooks().execute();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchWord = searchText.getText().toString();
                searchWord = searchWord.replaceAll("\\s+","%20");
                Log.e(TAG,searchWord);
                ok = true;
                url ="http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/bookListSearch/"+typeOfSearch+"/"+searchWord;
                new GetBooks().execute();
            }
        });



    }

    private class GetBooks extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {


            // Making a request to url and getting response
            if (!ok){
                HttpHandler sh = new HttpHandler();

                String jsonStr = sh.makeServiceCall(url);
                Log.e(TAG, "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        // Getting JSON Array node
                        JSONObject books = new JSONObject(jsonStr);
                        booksJson = books.getJSONArray("bookListAllDisponibleResult");
                    } catch (final JSONException e) {
                        Log.e(TAG, "Response from url: " + e);
                    }
                } else {
                    Snackbar.make(listView, "Check your network connection then retry.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.e(TAG, "Response from url: " + jsonStr);
                }
            }else {
                HttpHandler sh = new HttpHandler();
                String jsonStr = sh.makeServiceCall(url);
                //Log.e(TAG, "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        // Getting JSON Array node
                        JSONObject books = new JSONObject(jsonStr);
                        booksJson = books.getJSONArray("bookListSearchResult");
                    } catch (final JSONException e) {
                        //Log.e(TAG, "Response from url: " + e);
                    }
                } else {
                    Snackbar.make(listView, "Check your network connection then retry.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //Log.e(TAG, "Response from url: " + jsonStr);
                }
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //adding the books
            if (booksJson != null){
                final ArrayList<book> books = new ArrayList<>();
                try{
                    for (int i=0;i<booksJson.length();i++){
                        String ID = booksJson.getJSONArray(i).getString(0);
                        String title = booksJson.getJSONArray(i).getString(1);
                        String author = booksJson.getJSONArray(i).getString(2);
                        String theme = booksJson.getJSONArray(i).getString(3);
                        int bookCount = Integer.parseInt(booksJson.getJSONArray(i).getString(4)) - Integer.parseInt(booksJson.getJSONArray(i).getString(5));
                        String description = booksJson.getJSONArray(i).getString(6);
                        books.add(new book(title,author,theme,bookCount,description,ID));
                    }
                }catch(JSONException e){
                }
                bookAdapter itemsAdapter = new bookAdapter(book_list.this, books,true);

                listView.setAdapter(itemsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                book currentBook=books.get(position);
                Intent intent = new Intent(book_list.this,DetailsPage.class);
                intent.putExtra("TITLE", ""+currentBook.getTitle());
                intent.putExtra("ID", ""+currentBook.getID());
                intent.putExtra("AUTHOR", ""+currentBook.getAuthor());
                intent.putExtra("THEME", ""+currentBook.getTheme());
                intent.putExtra("BOOK_COUNT", ""+currentBook.getBookCount());
                intent.putExtra("DESCRIPTION", ""+currentBook.getDescription());
                intent.putExtra("OK","false");
                startActivity(intent);
            }
            });
            }

        }

    }
}
