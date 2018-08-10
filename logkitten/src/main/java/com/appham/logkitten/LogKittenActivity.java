package com.appham.logkitten;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class LogKittenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContextCompat.startForegroundService(getApplicationContext(),
                new Intent(getApplicationContext(), LogKittenService.class));

        finish();
    }
}
