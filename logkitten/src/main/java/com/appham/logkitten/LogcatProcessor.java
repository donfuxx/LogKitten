package com.appham.logkitten;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.appham.logkitten.notifications.NotificationFactory;
import com.appham.logkitten.service.LogEntry;
import com.appham.logkitten.service.LogEntryFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class LogcatProcessor {

    @NonNull
    public static SpannableStringBuilder dumpLogcat(@NonNull Context context) {
        final SpannableStringBuilder log = new SpannableStringBuilder();
        try {
            BufferedReader bufferedReader = getLogcatReader(new String[]{"logcat", "-d"});
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                SpannableString spanLine = new SpannableString(line);
                colorizeLog(context, spanLine);
                makeUrlsClickable(context, spanLine);
                log.append(spanLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return log;
        }
    }

    private static void colorizeLog(@NonNull Context context, @NonNull SpannableString spanLine) {
        if (LogEntryFactory.findLevel(spanLine.toString()).equals("W")) {
            spanLine.setSpan(new ForegroundColorSpan(
                            ContextCompat.getColor(context, R.color.logkitten_orange_red)),
                    0, spanLine.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (LogEntryFactory.findLevel(spanLine.toString()).equals("E")) {
            spanLine.setSpan(new ForegroundColorSpan(
                            ContextCompat.getColor(context, R.color.logkitten_red)),
                    0, spanLine.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static void makeUrlsClickable(@NonNull Context context, SpannableString spanLine) {
        URL url = LogEntryFactory.findUrl(spanLine.toString());
        if (url != null) {
            int urlStart = spanLine.toString().indexOf(url.toString());
            int urlEnd = urlStart + url.toString().length();
            spanLine.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    context.startActivity(IntentFactory.getBrowserIntent(url.toString()));
                }
            }, urlStart, urlEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    @NonNull
    public static String poweredByString(@NonNull Context context) {
        return new DeviceInfo().toString() + context.getString(R.string.logkitten_powered_by);
    }
}
