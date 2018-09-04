package com.appham.logkitten;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.appham.logkitten.service.LogEntry;
import com.appham.logkitten.service.LogKittenService;
import com.appham.logkitten.view.LogDetailActivity;
import com.appham.logkitten.view.SettingsActivity;

import static com.appham.logkitten.service.LogKittenService.STOP_SERVICE;

public class IntentFactory {

    @NonNull
    public static PendingIntent getPendingDetailsIntent(@NonNull LogEntry logEntry,
                                                        @NonNull Context context) {
        Intent notificationIntent = getIntentShareExtras(logEntry,
                new Intent(context, LogDetailActivity.class), context);

        return getPendingLogsIntent(context, notificationIntent);
    }

    @NonNull
    public static PendingIntent getPendingLogsIntent(@NonNull Context context,
                                                     @NonNull Intent notificationIntent) {
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

        return PendingIntent.getActivity(context,
                uniqueInt,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    public static PendingIntent getPendingGoogleIntent(@NonNull LogEntry logEntry,
                                                       @NonNull Context context) {
        Intent googleIntent = getBrowserIntent("https://www.google.com/search?q=" + logEntry.getContent());

        return PendingIntent.getActivity(context,
                0,
                googleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    public static PendingIntent getPendingSettingsIntent(@NonNull Context context) {
        Intent settingsIntent = SettingsActivity.getLaunchIntent(context);

        return PendingIntent.getActivity(context,
                0,
                settingsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    public static Intent getBrowserIntent(@NonNull String url) {
        Intent googleIntent = new Intent(Intent.ACTION_VIEW);
        googleIntent.setData(Uri.parse(url));
        return googleIntent;
    }

    @NonNull
    public static PendingIntent getPendingShareIntent(@NonNull LogEntry logEntry,
                                                      @NonNull Context context) {
        Intent chooserIntent = getChooserIntent(logEntry, context);

        return PendingIntent.getActivity(context,
                0,
                chooserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    public static PendingIntent getPendingStopIntent(@NonNull Context context) {
        Intent stopIntent = new Intent(context, LogKittenService.class);
        stopIntent.setAction(STOP_SERVICE);

        return PendingIntent.getService(context,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    public static Intent getChooserIntent(@NonNull LogEntry logEntry, @NonNull Context context) {
        Intent shareIntent = getIntentShareExtras(logEntry, new Intent(), context);
        return Intent.createChooser(shareIntent, context.getResources().getText(R.string.logkitten_lib_name));
    }

    @NonNull
    private static Intent getIntentShareExtras(@NonNull LogEntry logEntry,
                                               @NonNull Intent shareIntent,
                                               @NonNull Context context) {
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, logEntry.getContent() +
                context.getString(R.string.logkitten_powered_by));
        shareIntent.setType("text/plain");
        return shareIntent;
    }
}
