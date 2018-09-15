package com.appham.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w("Demo", "Test Warning Demo");

    }

    public void warning(View view) {
        Log.w("Demo", "Test Warning Log Demo");
    }

    public void error(View view) {
        Log.e("Demo", "Test Error Log Demo");
    }

    public void crash(View view) {
        throw new RuntimeException("Test Crash Demo");
    }
}
