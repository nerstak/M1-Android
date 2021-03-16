package com.example.booksapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = { BookEntity.class }, version = 4)
public abstract class BookDatabase extends RoomDatabase {
    public abstract BookDAO bookDAO();
}
