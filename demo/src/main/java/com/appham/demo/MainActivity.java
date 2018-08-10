package com.appham.demo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appham.logkitten.StartLogKittenActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, StartLogKittenActivity.class));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Test crash");
            }
        }, 2000);
    }
}
