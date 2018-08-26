package com.appham.logkitten;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

public class LaunchServiceActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextCompat.startForegroundService(this,
                new Intent(this, LogKittenService.class));
        finish();
    }
}