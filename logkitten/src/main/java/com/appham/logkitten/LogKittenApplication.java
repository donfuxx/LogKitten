package com.appham.logkitten;

import android.app.Application;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

public class LogKittenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextCompat.startForegroundService(this,
                new Intent(this, LogKittenService.class));
    }
}
