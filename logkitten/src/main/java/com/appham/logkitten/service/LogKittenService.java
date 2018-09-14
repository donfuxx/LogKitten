package com.appham.logkitten.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.appham.logkitten.DeviceInfo;
import com.appham.logkitten.LogcatProcessor;
import com.appham.logkitten.R;
import com.appham.logkitten.SoundMachine;
import com.appham.logkitten.notifications.NotificationFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LogKittenService extends Service {

    public static final String STOP_SERVICE = "STOP_SERVICE";
    private static final int NOTIFICATION_ID = 7777777;
    private final int CRASH_ID = 41;
    private SoundMachine soundMachine;
    private Thread logThread;
    private Thread.UncaughtExceptionHandler defaultExceptionHandler;
    private SharedPreferences prefs;
    private String loglevel;
    private SharedPreferences.OnSharedPreferenceChangeListener prefsListener;

    @Override
    public void onCreate() {
        super.onCreate();
        soundMachine = new SoundMachine(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefsListener = (sharedPreferences, key) -> {
            if (getString(R.string.logkitten_pref_loglevel_key).equals(key)) {

                // set new log level and restart service
                loglevel = sharedPreferences.getString(getString(R.string.logkitten_pref_loglevel_key),
                        getString(R.string.logkitten_pref_loglevel_default));
                stopLogging();
                onStartCommand(new Intent(), 0, 1);
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefsListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // stop service
        if (STOP_SERVICE.equals(intent.getAction())) {
            stopLogging();
            stopSelf();
            return START_STICKY;
        } else if (logThread != null) {
            toastEvent(R.string.logkitten_already_started_service);
            return START_STICKY;
        }

        // start service
        startForeground(NOTIFICATION_ID, NotificationFactory.createServiceNotification(this));

        loglevel = prefs.getString(getString(R.string.logkitten_pref_loglevel_key),
                getString(R.string.logkitten_pref_loglevel_default));

        setCrashHandler();

        logThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader logReader = null;
                try {
                    logReader = LogcatProcessor.getLogcatReader(new String[]{"logcat", loglevel});

                    String line;
                    LogEntry prevEntry = new LogEntry("", "", "", "");

                    do {
                        line = logReader.readLine();

                        if (line == null) {
                            NotificationFactory.newNotification(
                                    Integer.parseInt(prevEntry.getPid().split("\\D")[0]),
                                    prevEntry, LogKittenService.this);
                            continue;
                        }

                        if (Thread.interrupted()) {
                            break;
                        }

                        prevEntry = LogcatProcessor.processLogLine(line,
                                prevEntry, soundMachine, LogKittenService.this);


                    } while (!Thread.interrupted() && line != null);

                    Log.d(this.getClass().getName(), "end reading logcat lines");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (logReader != null) {
                        try {
                            logReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        startLogging();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLogging();
        if (soundMachine != null) {
            soundMachine.release();
            soundMachine = null;
        }
        Thread.setDefaultUncaughtExceptionHandler(defaultExceptionHandler);
    }

    public void startLogging() {
        if (soundMachine == null) {
            soundMachine = new SoundMachine(this);
        }
        if (logThread != null) {
            logThread.start();
            toastEvent(R.string.logkitten_started_service);
        }
    }

    public void stopLogging() {
        if (logThread != null) {
            logThread.interrupt();
            logThread = null;
            toastEvent(R.string.logkitten_stopped_service);
        }
    }

    private void setCrashHandler() {
        if (defaultExceptionHandler == null) {
            defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

            Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> {

                StringWriter stringWriter = new StringWriter();
                paramThrowable.printStackTrace(new PrintWriter(stringWriter));
                String exceptionStr = stringWriter.toString() +
                        getString(R.string.logkitten_powered_by) +
                        new DeviceInfo().toString();

                LogEntry logEntry = new LogEntry(" ☠ CRASH ☠ : " + new Date().toString(),
                        Thread.currentThread().getId() + "",
                        "C",
                        exceptionStr);

                soundMachine.meow();
                NotificationFactory.newNotification(CRASH_ID, logEntry, this);

                SystemClock.sleep(1000);
                defaultExceptionHandler.uncaughtException(paramThread, paramThrowable);
            });
        }

    }

    private void toastEvent(@StringRes int stringRes) {
        Log.d(this.getClass().getName(), getString(stringRes));
        Log.d(this.getClass().getName(), getString(R.string.logkitten_powered_by));
        Toast.makeText(getApplicationContext(), stringRes, Toast.LENGTH_LONG).show();
    }

}
