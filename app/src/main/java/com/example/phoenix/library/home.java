package com.example.phoenix.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.phoenix.library.Helper.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String email;
    private String TAG = home.class.getSimpleName();
    private String url;
    JSONArray booksJson = null;
    ListView listView;
    Context context;
    TextView navEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listView = findViewById(R.id.list_reservation);
        email = getIntent().getStringExtra("EMAIL");
        context = getApplicationContext();
        navEmail = findViewById(R.id.nav_email);
        url = "http://"+Login.ipAddress+":9999/LibraryBackgroundService.csp/reservationList/"+email;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && firstVisibleItem > 0) {

                    fab.setVisibility(View.INVISIBLE);
                }else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this,book_list.class);
                startActivity(intent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        navEmail = headerView.findViewById(R.id.nav_email);
        navEmail.setText(email);
        new GetBooks().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            navEmail.setText(email);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_book) {
            Intent intent = new Intent(home.this,book_list.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(home.this,EditInfo.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = Login.sharedpreferences.edit();
            editor.remove("LOGIN");
            editor.remove("TYPE");
            editor.apply();
            Intent intent = new Intent(home.this,Login.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }
    private class GetBooks extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {

            //if (isOnline()){
                HttpHandler sh = new HttpHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url);
                Log.e(TAG, "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        // Getting JSON Array node
                        JSONObject books = new JSONObject(jsonStr);
                        booksJson = books.getJSONArray("reservationListResult");
                    } catch (final JSONException e) {
                        Log.e(TAG, "Response from url: " + e);
                    }
                } else {
                    Snackbar.make(listView, "There was a problem retrieving the data.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
           /* }
            else
                Snackbar.make(listView, "Check your network connection.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
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
                        Log.e(TAG,author);
                        String theme = booksJson.getJSONArray(i).getString(3);
                        Log.e(TAG,theme);
                        int bookCount = Integer.parseInt(booksJson.getJSONArray(i).getString(4)) - Integer.parseInt(booksJson.getJSONArray(i).getString(5));
                        String description = booksJson.getJSONArray(i).getString(6);
                        books.add(new book(title,author,theme,bookCount,description,ID));
                    }
                }catch(JSONException e){
                }
                bookAdapter itemsAdapter = new bookAdapter(home.this, books,false);

                listView.setAdapter(itemsAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        book currentBook=books.get(position);
                        Intent intent = new Intent(home.this,DetailsPage.class);
                        intent.putExtra("TITLE", ""+currentBook.getTitle());
                        intent.putExtra("ID", ""+currentBook.getID());
                        intent.putExtra("AUTHOR", ""+currentBook.getAuthor());
                        intent.putExtra("THEME", ""+currentBook.getTheme());
                        intent.putExtra("BOOK_COUNT", ""+currentBook.getBookCount());
                        intent.putExtra("DESCRIPTION", ""+currentBook.getDescription());
                        intent.putExtra("OK","true");
                        startActivity(intent);
                    }
                });
            }



            /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                book Word=books.get(position);
            }
            });*/
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        new GetBooks().execute();
    }


}
