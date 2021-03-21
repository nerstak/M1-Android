package com.example.booksapp.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.booksapp.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Schedule notifications
 */
public class TimerService extends Service {
    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        scheduleNotificationTimer();
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

    /**
     * Creates a notification channel, since this is needed from API 26 onwards
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // Create channel
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Schedules frequency of sending the notification intent
     */
    private void scheduleNotificationTimer() {
        Intent timerIntent = new Intent(this, NotificationReceiver.class);

        TimerTask task = new TimerTask() {
            public void run() {
                sendBroadcast(timerIntent);
            }};
        //Sends a notification every 2 minute for testing purposes
        timer.schedule(task,5000,120000);
        //Use below for a notification every 3 days
        // timer.schedule(task,259200000,259200000);
    }
}
