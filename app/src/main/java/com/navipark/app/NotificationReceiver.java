package com.navipark.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "NaviParkChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        int notificationId = intent.getIntExtra("notificationId", 1);
        boolean isExtendable = intent.getBooleanExtra("isExtendable", false);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Parking Reminders", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent actionIntent = new Intent(context, isExtendable ? ExtendTimeActivity.class : HomeActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, notificationId, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("NaviPark Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(actionPendingIntent);

        if (isExtendable) {
            builder.addAction(android.R.drawable.ic_menu_add, "Extend Time", actionPendingIntent);
        }

        notificationManager.notify(notificationId, builder.build());
    }
}
