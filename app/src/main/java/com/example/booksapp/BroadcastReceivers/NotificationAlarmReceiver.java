package com.example.booksapp.BroadcastReceivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.booksapp.Activities.MainActivity;

public class NotificationAlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent to invoke app when click on notification.
        //In this sample, we want to start/launch this sample app when user clicks on notification
        Intent intentToRepeat = new Intent(context, MainActivity.class);
        //set flag to restart/relaunch the app
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Pending intent to handle launch of Activity in intent above
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, NOTIFICATION_ID, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build notification
        Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, repeatedNotification);
    }

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context, "CHANNEL_ID")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.arrow_up_float)
                        .setContentTitle("Hey read a book, loser")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder;
    }
}
