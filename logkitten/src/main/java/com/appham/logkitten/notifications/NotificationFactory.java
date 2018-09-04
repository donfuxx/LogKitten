package com.appham.logkitten.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.appham.logkitten.IntentFactory;
import com.appham.logkitten.R;
import com.appham.logkitten.service.LogEntry;
import com.appham.logkitten.view.LogDetailActivity;

public class NotificationFactory {

    @NonNull
    public static Notification createServiceNotification(@NonNull Context context) {
        PendingIntent pendingStopIntent = IntentFactory.getPendingStopIntent(context);
        PendingIntent pendingLogsIntent = IntentFactory.getPendingLogsIntent(context,
                new Intent(context, LogDetailActivity.class));
        PendingIntent pendingSettingsIntent = IntentFactory.getPendingSettingsIntent(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, LogKittenChannel.LOG_KITTEN_SERVICE.name())
                .setSmallIcon(R.drawable.logkitten_ic_notification)
                .setContentTitle(context.getString(R.string.logkitten_lib_name))
                .setContentText(context.getString(R.string.logkitten_service_running))
                .setTicker(context.getString(R.string.logkitten_ticker))
                .setColor(Color.GREEN)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setGroup(LogKittenChannel.LOG_KITTEN_SERVICE.name())
                .addAction(R.drawable.logkitten_ic_notification, context.getString(R.string.logkitten_action_stop), pendingStopIntent)
                .addAction(R.drawable.logkitten_ic_notification, context.getString(R.string.logkitten_action_show_logs), pendingLogsIntent)
                .addAction(R.drawable.logkitten_ic_notification, context.getString(R.string.logkitten_action_settings), pendingSettingsIntent)
                .setContentIntent(pendingLogsIntent);

        Notification notification = builder.build();
        createNotificationChannel(LogKittenChannel.LOG_KITTEN_SERVICE, context);
        createNotificationChannel(LogKittenChannel.LOG_KITTEN_ENTRIES, context);
        return notification;
    }

    private static void createNotificationChannel(@NonNull LogKittenChannel channelType, @NonNull Context context) {
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

    public static void newNotification(int id, @NonNull LogEntry logEntry, @NonNull Context context) {
        if (!logEntry.getLevel().matches("[EWC]")) return;

        PendingIntent pendingDetailsIntent = IntentFactory.getPendingDetailsIntent(logEntry, context);
        PendingIntent pendingShareIntent = IntentFactory.getPendingShareIntent(logEntry, context);
        PendingIntent pendingGoogleIntent = IntentFactory.getPendingGoogleIntent(logEntry, context);

        boolean isError = logEntry.getLevel().matches("[EC]");

        @ColorRes int colorRes = getColorRes(logEntry.getLevel());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                LogKittenChannel.LOG_KITTEN_ENTRIES.name())
                .setSmallIcon(R.drawable.logkitten_ic_notification)
                .setContentTitle(logEntry.getTitle())
                .setContentText(logEntry.getContent())
                .setTicker(context.getString(R.string.logkitten_ticker))
                .setColor(ContextCompat.getColor(context, colorRes))
                .setPriority(isError ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT)
                .setNumber(id)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(logEntry.getContent()))
                .setAutoCancel(true)
                .addAction(R.drawable.logkitten_ic_notification, context.getString(R.string.logkitten_action_share), pendingShareIntent)
                .addAction(R.drawable.logkitten_ic_notification, context.getString(R.string.logkitten_google_it), pendingGoogleIntent)
                .setContentIntent(pendingDetailsIntent);

        if (isError) {
            builder.setGroup(context.getString(R.string.logkitten_entries) + logEntry.getLevel());
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }

    @NonNull
    private static @ColorRes int getColorRes(@NonNull String level) {
        if (level.equals("C")) {
            return R.color.logkitten_magenta;
        } else if (level.equals("E")) {
            return R.color.logkitten_red;
        } else {
            return R.color.logkitten_orange;
        }
    }

}
