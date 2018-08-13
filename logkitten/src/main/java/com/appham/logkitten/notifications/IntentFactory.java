package com.appham.logkitten.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.appham.logkitten.LogDetailActivity;
import com.appham.logkitten.LogEntry;
import com.appham.logkitten.R;

public class IntentFactory {

    public static PendingIntent getPendingDetailsIntent(Context context) {
        Intent notificationIntent = new Intent(context, LogDetailActivity.class);
        return PendingIntent.getActivity(context,
                0, notificationIntent, 0);
    }

    public static PendingIntent getPendingShareIntent(LogEntry logEntry, Context context) {
        Intent chooserIntent = getChooserIntent(logEntry, context);

        return PendingIntent.getActivity(context,
                0,
                chooserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static Intent getChooserIntent(LogEntry logEntry, Context context) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, logEntry.getContent());
        shareIntent.setType("text/plain");
        return Intent.createChooser(shareIntent, context.getResources().getText(R.string.app_name));
    }
}
