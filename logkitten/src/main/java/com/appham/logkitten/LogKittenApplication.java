package com.appham.logkitten;

import android.app.Application;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;

import com.appham.logkitten.notifications.NotificationFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LogKittenApplication extends Application {

    private final int CRASH_ID = 41;
    private SoundMachine soundMachine;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextCompat.startForegroundService(this,
                new Intent(this, LogKittenService.class));

        soundMachine = new SoundMachine(this);

        Thread.UncaughtExceptionHandler defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {

            StringWriter stringWriter = new StringWriter();
            paramThrowable.printStackTrace(new PrintWriter(stringWriter));
            String exceptionStr = stringWriter.toString();

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
    public void onTerminate() {
        if (soundMachine != null) {
            soundMachine.release();
            soundMachine = null;
        }
        super.onTerminate();
    }
}
