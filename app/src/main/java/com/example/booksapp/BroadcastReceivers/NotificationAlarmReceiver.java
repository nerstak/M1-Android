package com.example.booksapp.BroadcastReceivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.booksapp.Activities.ActivityBook;
import com.example.booksapp.Activities.MainActivity;
import com.example.booksapp.R;
import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;
import com.example.booksapp.database.DatabaseUtilities;

import java.util.List;
import java.util.Random;

public class NotificationAlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 100;
    private Context context;
    private String bookID;
    private String title;
    private String text;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Intent to invoke app when click on notification.
        //In this sample, we want to start/launch this sample app when user clicks on notification
        Intent intentToRepeat;

        setMessage();

        if(bookID != null) {
            intentToRepeat = new Intent(context, ActivityBook.class);
            intentToRepeat.putExtra("bookID", bookID);
        } else {
            intentToRepeat = new Intent(context, MainActivity.class);
        }

        //set flag to restart/relaunch the app
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Pending intent to handle launch of Activity in intent above
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, NOTIFICATION_ID, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build notification
        NotificationCompat.Builder builder= buildLocalNotification(context, pendingIntent);
        Notification repeatedNotification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, repeatedNotification);
    }

    private NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context, "CHANNEL_ID")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_icon_app)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder;
    }

    private void setMessage() {
        BookDatabase db = DatabaseUtilities.getBookDatabase(context);
        List<BookEntity> unrateds = db.bookDAO().getAllUnrated();
        List<BookEntity> unreads = db.bookDAO().getAllUnread();

        if(unrateds.isEmpty() && unreads.isEmpty())
        {
            title = "Any good books lately?";
            text = "Come add them to your Library!";
        }
        else if(unrateds.isEmpty()){
            generateReadingMessage(unreads);
        } else if(unreads.isEmpty()) {
            generateRatingMessage(unrateds);
        } else {
            int choice = new Random().nextInt(2);
            switch (choice) {
                case 0:
                    generateReadingMessage(unreads);
                    break;
                case 1:
                    generateRatingMessage(unrateds);
                    break;
            }
        }
    }

    private void generateRatingMessage(List<BookEntity> books) {
        BookEntity book = pickRandomBook(books);

        title = "What did you think about " + book.getTitle();
        text = "Come give it a rating!";
        bookID = book.getId();
    }

    private void generateReadingMessage(List<BookEntity> books) {
        BookEntity book = pickRandomBook(books);

        title = "Looking for a book to read?";
        text = "Why not try " + book.getTitle() + "?";
        bookID = book.getId();
    }

    private BookEntity pickRandomBook(List<BookEntity> books) {
        return books.get(new Random().nextInt(books.size()));
    }
}
