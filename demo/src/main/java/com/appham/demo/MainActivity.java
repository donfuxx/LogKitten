package com.appham.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.appham.logkitten.LogKittenActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, LogKittenActivity.class));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.w("Demo", "Test Warning");

                throw new RuntimeException("Test Error Demo");
            }
        }, 2000);
    }
}
