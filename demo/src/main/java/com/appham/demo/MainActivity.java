package com.appham.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w("Demo", "Test Warning Demo");

        new Handler().postDelayed(() -> {
            try {
                throw new RuntimeException("Test Exception Demo");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 2000);
    }

    public void crash(View view) {
        throw new RuntimeException("Test Crash Demo");
    }
}
