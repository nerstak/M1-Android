package com.example.booksapp.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseUtilities {
    public static BookDatabase getBookDatabase(Context context) {
        return Room.databaseBuilder(context,
                BookDatabase.class, "BookDatabase")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }
}
