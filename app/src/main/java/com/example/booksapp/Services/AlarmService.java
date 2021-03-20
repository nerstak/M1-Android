package com.example.booksapp.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.booksapp.BroadcastReceivers.NotificationAlarmReceiver;
import com.example.booksapp.R;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends Service {
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        scheduleNotificationAlarm();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scheduleNotificationAlarm() {
        Intent alarmIntent = new Intent(this, NotificationAlarmReceiver.class);

        TimerTask task = new TimerTask() {
            public void run() {
                sendBroadcast(alarmIntent);
            }};
        //Sends a notification every 2 minute for testing purposes
        timer.schedule(task,5000,120000);
        //Use below for a notification every 3 days
        // timer.schedule(task,259200000,259200000);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
