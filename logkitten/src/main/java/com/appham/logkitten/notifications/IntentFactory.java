package com.appham.logkitten.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.appham.logkitten.LogDetailActivity;
import com.appham.logkitten.LogEntry;
import com.appham.logkitten.LogKittenService;
import com.appham.logkitten.R;

import static com.appham.logkitten.LogKittenService.STOP_SERVICE;

public class IntentFactory {

    public static PendingIntent getPendingDetailsIntent(LogEntry logEntry, Context context) {
        Intent notificationIntent = getIntentShareExtras(logEntry,
                new Intent(context, LogDetailActivity.class));

        return getPendingLogsIntent(context, notificationIntent);
    }


    public static PendingIntent getPendingLogsIntent(Context context, Intent notificationIntent) {
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

        return PendingIntent.getActivity(context,
                uniqueInt,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPendingGoogleIntent(LogEntry logEntry, Context context) {
        Intent googleIntent = new Intent(Intent.ACTION_VIEW);
        googleIntent.setData(Uri.parse("https://www.google.com/search?q=" + logEntry.getContent()));

        return PendingIntent.getActivity(context,
                0,
                googleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPendingShareIntent(LogEntry logEntry, Context context) {
        Intent chooserIntent = getChooserIntent(logEntry, context);

        return PendingIntent.getActivity(context,
                0,
                chooserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPendingStopIntent(Context context) {
        Intent stopIntent = new Intent(context, LogKittenService.class);
        stopIntent.setAction(STOP_SERVICE);

        return PendingIntent.getService(context,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static Intent getChooserIntent(LogEntry logEntry, Context context) {
        Intent shareIntent = getIntentShareExtras(logEntry, new Intent());
        return Intent.createChooser(shareIntent, context.getResources().getText(R.string.app_name));
    }

    private static Intent getIntentShareExtras(LogEntry logEntry, Intent shareIntent) {
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, logEntry.getContent());
        shareIntent.setType("text/plain");
        return shareIntent;
    }
}
