package com.appham.logkitten;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.appham.logkitten.notifications.NotificationFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LaunchServiceActivity extends Activity {

    private final int CRASH_ID = 41;
    private SoundMachine soundMachine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService();
        setCrashHandler();
        finish();
    }

    private void startService() {
        ContextCompat.startForegroundService(this,
                new Intent(this, LogKittenService.class));
    }

    private void setCrashHandler() {
        soundMachine = new SoundMachine(this);

        Thread.UncaughtExceptionHandler defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {

            StringWriter stringWriter = new StringWriter();
            paramThrowable.printStackTrace(new PrintWriter(stringWriter));
            String exceptionStr = stringWriter.toString() + getString(R.string.logkitten_powered_by);

            LogEntry logEntry = new LogEntry("CRASH: " + new Date().toString(),
                    Thread.currentThread().getId() + "",
                    "E",
                    exceptionStr);

            soundMachine.meow();
            NotificationFactory.newNotification(CRASH_ID, logEntry, this);

            SystemClock.sleep(2000);
            defaultExceptionHandler.uncaughtException(paramThread, paramThrowable);
        });
    }

    @Override
    protected void onDestroy() {
        if (soundMachine != null) {
            soundMachine.release();
            soundMachine = null;
        }
        super.onDestroy();
    }
}
