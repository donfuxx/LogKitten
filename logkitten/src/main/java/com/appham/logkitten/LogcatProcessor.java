package com.appham.logkitten;

import android.support.annotation.NonNull;

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
    public static BufferedReader getLogcatReader(String[] progArray) throws IOException {
        Process process = Runtime.getRuntime().exec(progArray);
        return new BufferedReader(
                new InputStreamReader(process.getInputStream()));
    }
}
