package com.appham.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w("Demo", "Test Warning Demo");

        new Handler().postDelayed(() -> {
            throw new RuntimeException("Test Error Demo");
        }, 2000);
    }
}
