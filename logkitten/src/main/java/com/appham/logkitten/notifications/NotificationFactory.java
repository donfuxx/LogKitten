package com.appham.logkitten.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.appham.logkitten.LogDetailActivity;
import com.appham.logkitten.LogEntry;
import com.appham.logkitten.R;

public class NotificationFactory {

    public static Notification createServiceNotification(Context context) {
        PendingIntent pendingStopIntent = IntentFactory.getPendingStopIntent(context);
        PendingIntent pendingLogsIntent = IntentFactory.getPendingLogsIntent(context,
                new Intent(context, LogDetailActivity.class));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, LogKittenChannel.LOG_KITTEN_SERVICE.name())
                .setSmallIcon(R.drawable.logkitten_ic_notification)
                .setContentTitle("Log Kitten")
                .setContentText("Log Kitten Service is running!")
                .setTicker("Log Kitten TICKER")
                .setColor(Color.GREEN)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setGroup(LogKittenChannel.LOG_KITTEN_SERVICE.name())
                .addAction(R.drawable.logkitten_ic_notification, "Stop", pendingStopIntent)
                .addAction(R.drawable.logkitten_ic_notification, "Show Logs", pendingLogsIntent)
                .setContentIntent(pendingLogsIntent);

        Notification notification = builder.build();
        createNotificationChannel(LogKittenChannel.LOG_KITTEN_SERVICE, context);
        createNotificationChannel(LogKittenChannel.LOG_KITTEN_ENTRIES, context);
        return notification;
    }

    private static void createNotificationChannel(LogKittenChannel channelType, Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelType.name(),
                    channelType.getChannelName(),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(channelType.getChannelDesc());
            channel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void newNotification(int id, LogEntry logEntry, Context context) {
        if (!logEntry.getLevel().matches("E|W")) return;

        PendingIntent pendingDetailsIntent = IntentFactory.getPendingDetailsIntent(logEntry, context);
        PendingIntent pendingShareIntent = IntentFactory.getPendingShareIntent(logEntry, context);
        PendingIntent pendingGoogleIntent = IntentFactory.getPendingGoogleIntent(logEntry, context);

        boolean isError = "E".equals(logEntry.getLevel());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                LogKittenChannel.LOG_KITTEN_ENTRIES.name())
                .setSmallIcon(R.drawable.logkitten_ic_notification)
                .setContentTitle(logEntry.getTitle())
                .setContentText(logEntry.getContent())
                .setTicker("Log Kitten TICKER")
                .setColor(isError ? Color.RED : ContextCompat.getColor(context, R.color.logkitten_orange))
                .setPriority(isError ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT)
                .setNumber(id)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(logEntry.getContent()))
                .setAutoCancel(true)
                .addAction(R.drawable.logkitten_ic_notification, "Share", pendingShareIntent)
                .addAction(R.drawable.logkitten_ic_notification, "Google it", pendingGoogleIntent)
                .setContentIntent(pendingDetailsIntent);

        if (isError) {
            builder.setGroup("Log Kitten Entries: " + logEntry.getLevel());
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }

}
