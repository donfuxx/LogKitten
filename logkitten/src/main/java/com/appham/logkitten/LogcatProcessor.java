package com.appham.logkitten;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.appham.logkitten.notifications.NotificationFactory;
import com.appham.logkitten.service.LogEntry;
import com.appham.logkitten.service.LogEntryFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogcatProcessor {

    @NonNull
    public static String dumpLogcat() {
        final StringBuilder log = new StringBuilder();
        try {
            BufferedReader bufferedReader = getLogcatReader(new String[]{"logcat", "-d"});

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return log.toString();
        }
    }

    @NonNull
    public static LogEntry processLogLine(@NonNull String line,
                                          @NonNull LogEntry prevEntry,
                                          @NonNull SoundMachine soundMachine,
                                          @NonNull Context context) {
        LogEntry logEntry = LogEntryFactory.buildLogEntry(line);

        if ((!TextUtils.isEmpty(logEntry.getPid()) && !TextUtils.isEmpty(prevEntry.getPid())) &&
                !prevEntry.getPidLevel().equals(logEntry.getPidLevel())) {
            prevEntry.setContent(prevEntry.getContent());
            NotificationFactory.newNotification(Integer.parseInt(
                    prevEntry.getPid().split("\\D")[0]), prevEntry, context);
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
                        prevEntry, context);
            }
        }
        return prevEntry;
    }

    @NonNull
    public static BufferedReader getLogcatReader(@NonNull String[] progArray) throws IOException {
        Process process = Runtime.getRuntime().exec(progArray);
        return new BufferedReader(
                new InputStreamReader(process.getInputStream()));
    }
}
