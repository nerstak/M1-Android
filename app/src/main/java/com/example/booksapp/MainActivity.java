package com.example.booksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;

import com.example.booksapp.database.BookDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookDatabase db = Room.databaseBuilder(getApplicationContext(),
                BookDatabase.class, "BookDatabase")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }
}