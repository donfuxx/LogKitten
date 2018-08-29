package com.appham.logkitten;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntryFactory {

    private static Pattern timePattern = Pattern.compile("^\\d\\d-\\d\\d\\s\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d");
    private static Pattern pidPattern = Pattern.compile("\\s+\\d{3,6}\\s+\\d{3,6}\\s+");
    private static Pattern levelPattern = Pattern.compile("\\s+[VDIWEAC]\\s+");

    @NonNull
    public static LogEntry buildLogEntry(@NonNull String logLine) {

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
