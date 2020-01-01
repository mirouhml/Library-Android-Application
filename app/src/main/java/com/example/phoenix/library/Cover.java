package com.example.phoenix.library;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Cover extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the SlidePane-Activity. */
                Intent mainIntent = new Intent(Cover.this,Login.class);
                startActivity(mainIntent);
                finish();
            }
        }, 1800);
    }
}
