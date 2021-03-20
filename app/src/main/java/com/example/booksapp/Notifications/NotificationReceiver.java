package com.example.booksapp.Notifications;

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


public class NotificationReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 100;
    private Context context;
    private String bookID;
    private String title;
    private String text;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //Intent we broadcast when the user clicks the notification
        Intent clickedIntent;

        generateMessage();

        // Go to the activity of the notification book if the notification was about a specific book
        if(bookID != null) {
            clickedIntent = new Intent(context, ActivityBook.class);
            clickedIntent.putExtra("bookID", bookID);
        } else {
            // main activity otherwise
            clickedIntent = new Intent(context, MainActivity.class);
        }

        //set flag to restart/relaunch the app
        clickedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Pending intent to handle launch of Activity in intent above
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, NOTIFICATION_ID, clickedIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Build notification
        Notification repeatedNotification = buildNotification(context, pendingIntent);

        //Puts the notification in a manager so we can lock & send it
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Finally send notification
        notificationManager.notify(NOTIFICATION_ID, repeatedNotification);
    }

    /**
     * Builds notification given pending intent and context
     * @param context current context
     * @param pendingIntent pending intent of the intent we want to broadcast with notification tap
     * @return built notification
     */
    private Notification buildNotification(Context context, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context, "CHANNEL_ID")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_icon_app)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    /**
     * Gather notification title and text as well as possible book id to direct to
     */
    private void generateMessage() {
        BookDatabase db = DatabaseUtilities.getBookDatabase(context);
        // Gets all books that have been finished/dropped but not rated
        List<BookEntity> unrateds = db.bookDAO().getAllUnrated();
        // Gets all books that have been planned to read or put on hold
        List<BookEntity> unreads = db.bookDAO().getAllUnread();

        // If there are no books matching the above criteria, send a generic notification not targeting
        // a singular book
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
            // If both criteria are available choose a notification type at random
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

    /**
     * Generates notification title and text & book id to direct to for a rating type notif
     * @param books list of all unrated books
     */
    private void generateRatingMessage(List<BookEntity> books) {
        BookEntity book = pickRandomBook(books);

        title = "What did you think about " + book.getTitle();
        text = "Come give it a rating!";
        bookID = book.getId();
    }

    /**
     * Generates notification title and text & book id to direct to for a reading type notif
     * @param books list of all unread books
     */
    private void generateReadingMessage(List<BookEntity> books) {
        BookEntity book = pickRandomBook(books);

        title = "Looking for a book to read?";
        text = "Why not try " + book.getTitle() + "?";
        bookID = book.getId();
    }

    /**
     * Picks a random book from a list of books
     * @param books list of books
     * @return chosen book
     */
    private BookEntity pickRandomBook(List<BookEntity> books) {
        return books.get(new Random().nextInt(books.size()));
    }
}
