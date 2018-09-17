package com.appham.logkitten.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntryFactory {

    private static Pattern timePattern = Pattern.compile("^\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d");
    private static Pattern pidPattern = Pattern.compile("\\s+\\d{3,6}\\s+\\d{3,6}\\s+");
    private static Pattern levelPattern = Pattern.compile("\\s+[VDIWEAC]\\s+");
    private static Pattern urlPattern = Pattern.compile("(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    @NonNull
    public static LogEntry buildLogEntry(@NonNull String logLine) {

        // datetime
        String time = findTime(logLine);

        // PID-TID
        String pid = findPid(logLine);

        // level
        String level = findLevel(logLine);

        // content
        String content = findContent(logLine, time, pid, level);

        return new LogEntry(time, pid, level, content);
    }

    @NonNull
    public static String findTime(@NonNull String logLine) {
        Matcher timeMatcher = timePattern.matcher(logLine);
        return timeMatcher.find() ? timeMatcher.group() : "";
    }

    @NonNull
    public static String findPid(@NonNull String logLine) {
        Matcher pidMatcher = pidPattern.matcher(logLine);
        return pidMatcher.find() ? pidMatcher.group().trim() : "";
    }

    @NonNull
    public static String findLevel(@NonNull String logLine) {
        Matcher levelMatcher = levelPattern.matcher(logLine);
        return levelMatcher.find() ? levelMatcher.group().trim() : "";
    }

    @NonNull
    public static String findContent(@NonNull String logLine, String time, String pid, String level) {
        return logLine.replaceFirst(time, "")
                .replaceFirst(pid, "").replaceFirst(level, "").trim();
    }

    @Nullable
    public static URL findUrl(@NonNull String logLine) {
        Matcher urlMatcher = urlPattern.matcher(logLine);
        URL url = null;
        if (urlMatcher.find()) {
            try {
                url = new URL(urlMatcher.group().trim());
            } catch (MalformedURLException e) {
                Log.e(LogEntryFactory.class.getName(), e.getMessage());
            }
        }
        return url;
    }

}
