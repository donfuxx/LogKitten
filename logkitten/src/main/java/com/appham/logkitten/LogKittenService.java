package com.appham.logkitten;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.appham.logkitten.notifications.NotificationFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogKittenService extends Service {

    public static final String STOP_SERVICE = "STOP_SERVICE";
    private static final int NOTIFICATION_ID = 7777777;
    private Pattern timePattern = Pattern.compile("^\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d");
    private Pattern pidPattern = Pattern.compile("\\s+\\d{3,6}\\s+\\d{3,6}\\s+");
    private Pattern levelPattern = Pattern.compile("\\s+[VDIWEA]\\s+");
    private SoundMachine soundMachine;
    private Thread logThread;

    @Override
    public void onCreate() {
        super.onCreate();
        soundMachine = new SoundMachine(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // stop service
        if (STOP_SERVICE.equals(intent.getAction())) {
            stopLogging();
            stopSelf();
            return START_STICKY;
        } else if (logThread != null) {
            return START_STICKY;
        }

        // start service
        startForeground(NOTIFICATION_ID, NotificationFactory.createServiceNotification(this));

        logThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process logcat;
                BufferedReader br = null;
                try {
//                    Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
                    logcat = Runtime.getRuntime().exec(new String[]{"logcat", "*:W"});
                    br = new BufferedReader(new InputStreamReader(logcat.getInputStream()),4*1024);
                    String line;
                    LogEntry prevEntry = new LogEntry("", "", "", "");


                    do {
                        line = br.readLine();

                        if (line == null) {
                            NotificationFactory.newNotification(
                                    Integer.parseInt(prevEntry.getPid().split("\\D")[0]),
                                    prevEntry, LogKittenService.this);
                            continue;
                        }

                        if (Thread.interrupted()) {
                            break;
                        }

                        LogEntry logEntry = buildLogEntry(line);

                        if ((!TextUtils.isEmpty(logEntry.getPid()) && !TextUtils.isEmpty(prevEntry.getPid())) &&
                                !prevEntry.getPidLevel().equals(logEntry.getPidLevel())) {
                            prevEntry.setContent(prevEntry.getContent() + getString(R.string.logkitten_powered_by));
                            NotificationFactory.newNotification(Integer.parseInt(
                                    prevEntry.getPid().split("\\D")[0]), prevEntry, LogKittenService.this);
                            prevEntry = logEntry;
                            soundMachine.meow();
                        }
                        if (!TextUtils.isEmpty(logEntry.getPid()) && TextUtils.isEmpty(prevEntry.getPid())) {
                            prevEntry = logEntry;
                        } else if ((TextUtils.isEmpty(logEntry.getPid()) && !TextUtils.isEmpty(prevEntry.getContent())) ||
                                prevEntry.getPid().equals(logEntry.getPid())) {
                            prevEntry.setPid(logEntry.getPid());
                            prevEntry.setLevel(logEntry.getLevel());
                            prevEntry.setContent(prevEntry.getContent() + "\n" + logEntry.getContent());
                            if (prevEntry.getPid().matches("\\d+\\s+\\d+")) {
                                NotificationFactory.newNotification(
                                        Integer.parseInt(prevEntry.getPid().split("\\D")[0]),
                                        prevEntry, LogKittenService.this);
                            }
                        }


                    } while (!Thread.interrupted() && line != null);

                    Log.d(this.getClass().getName(), "end reading lines");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
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

    private void toastEvent(@StringRes int stringRes) {
        Log.d(this.getClass().getName(), getString(stringRes));
        Log.d(this.getClass().getName(), getString(R.string.logkitten_powered_by));
        Toast.makeText(getApplicationContext(), stringRes, Toast.LENGTH_LONG).show();
    }

    @NonNull
    private LogEntry buildLogEntry(@NonNull String logLine) {

        // datetime
        Matcher timeMatcher = timePattern.matcher(logLine);
        String time = timeMatcher.find() ? timeMatcher.group() : "";

        // PID-TID
        Matcher pidMatcher = pidPattern.matcher(logLine);
        String pid = pidMatcher.find() ? pidMatcher.group().trim() : "";

        // level
        Matcher levelMatcher = levelPattern.matcher(logLine);
        String level = levelMatcher.find() ? levelMatcher.group().trim() : "";

        // content
        String content = logLine.replaceFirst(time, "")
                .replaceFirst(pid, "").replaceFirst(level, "").trim();

        return new LogEntry(time, pid, level, content);

    }
}
