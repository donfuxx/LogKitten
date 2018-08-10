package com.appham.logkitten;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogKittenService extends Service {

    private static final int NOTIFICATION_ID = 7777777;
    private Pattern timePattern = Pattern.compile("^\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d");
    private Pattern pidPattern = Pattern.compile("\\s+\\d{3,6}\\s+\\d{3,6}\\s+");
    private Pattern levelPattern = Pattern.compile("\\s+[VDIWEA]\\s+");

    enum LogKittenChannel {
        LOG_KITTEN_SERVICE("Log Kitten Service", "Log Kitten monitoring service status"),
        LOG_KITTEN_ENTRIES("Log Kitten Entries", "Log Kitten log entries show in this channel");

        private final CharSequence channelName;
        private final String channelDesc;

        LogKittenChannel(CharSequence channelName, String channelDesc) {
            this.channelName = channelName;
            this.channelDesc = channelDesc;
        }
    }

    class LogEntry {
        private String time, pid, level, content;

        LogEntry(String time, String pid, String level, String content) {
            this.time = time;
            this.pid = pid;
            this.level = level;
            this.content = content;
        }

        String getPidLevel() {
            return pid + " - " + level;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startInForeground();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Process logcat;
                BufferedReader br = null;
                try {
                    logcat = Runtime.getRuntime().exec(new String[]{"logcat", "*:W"});
                    br = new BufferedReader(new InputStreamReader(logcat.getInputStream()),4*1024);
                    String line;
                    LogEntry prevEntry = new LogEntry("", "", "", "");


                    do {
                        line = br.readLine();

                        if (line == null) {
                            newNotification(Integer.parseInt(prevEntry.pid.split("\\D")[0]), prevEntry);
                            continue;
                        }

                        LogEntry logEntry = buildLogEntry(line);

                        if ((!TextUtils.isEmpty(logEntry.pid) && !TextUtils.isEmpty(prevEntry.pid)) &&
                                !prevEntry.getPidLevel().equals(logEntry.getPidLevel())) {
                            newNotification(Integer.parseInt(prevEntry.pid.split("\\D")[0]), prevEntry);
                            prevEntry = logEntry;
                        } if (!TextUtils.isEmpty(logEntry.pid) && TextUtils.isEmpty(prevEntry.pid)) {
                            prevEntry = logEntry;
                        } else if ((TextUtils.isEmpty(logEntry.pid) && !TextUtils.isEmpty(prevEntry.content)) ||
                                prevEntry.pid.equals(logEntry.pid)) {
                            prevEntry.pid = logEntry.pid;
                            prevEntry.level = logEntry.level;
                            prevEntry.content = prevEntry.content + "\n" + logEntry.content;
                            if (prevEntry.pid.matches("\\d+\\s+\\d+")) {
                                newNotification(Integer.parseInt(prevEntry.pid.split("\\D")[0]), prevEntry);
                            }
                        }


                    } while (line != null);

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
        }).start();

        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startInForeground() {
        Intent notificationIntent = new Intent(this, LogDetailActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, LogKittenChannel.LOG_KITTEN_SERVICE.name())
                .setSmallIcon(R.drawable.ic_kitten_notification)
                .setContentTitle("Log Kitten")
                .setContentText("Log Kitten Service is running!")
                .setTicker("Log Kitten TICKER")
                .setColor(Color.GREEN)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(LogKittenChannel.LOG_KITTEN_SERVICE.name())
                .addAction(R.drawable.ic_kitten_notification, "Stop", null)
                .addAction(R.drawable.ic_kitten_notification, "Clear Logcat", null)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        createNotificationChannel(LogKittenChannel.LOG_KITTEN_SERVICE);
        createNotificationChannel(LogKittenChannel.LOG_KITTEN_ENTRIES);
        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel(LogKittenChannel channelType ) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelType.name(),
                    channelType.channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelType.channelDesc);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private synchronized void newNotification(int id, LogEntry logEntry) {
        if (!logEntry.level.matches("E|W")) return;

        Intent notificationIntent = new Intent(this, LogDetailActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, logEntry.content);
        shareIntent.setType("text/plain");
        Intent chooserIntent = Intent.createChooser(shareIntent, getResources().getText(R.string.app_name));

        PendingIntent pendingShareIntent = PendingIntent.getActivity(this,
                0,
                chooserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        boolean isError = "E".equals(logEntry.level);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                LogKittenChannel.LOG_KITTEN_ENTRIES.name())
                .setSmallIcon(R.drawable.ic_kitten_notification)
                .setContentTitle(logEntry.time + " - " + logEntry.pid + " - " + logEntry.level)
                .setContentText(logEntry.content)
                .setTicker("Log Kitten TICKER")
                .setColor(isError ? Color.RED : ContextCompat.getColor(this, R.color.orange))
                .setPriority(isError ? NotificationCompat.PRIORITY_MAX : NotificationCompat.PRIORITY_DEFAULT)
                .setNumber(id)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(logEntry.content))
                .addAction(R.drawable.ic_kitten_notification, "Share", pendingShareIntent)
                .addAction(R.drawable.ic_kitten_notification, "Details", null)
                .addAction(R.drawable.ic_kitten_notification, "Google it", null)
                .setContentIntent(pendingIntent);

        if (isError) {
            builder.setGroup("Log Kitten Entries: " + logEntry.level);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(id, builder.build());
    }

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
