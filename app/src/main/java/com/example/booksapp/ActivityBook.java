package com.example.booksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;

/**
 * Activity to display information on book
 */
public class ActivityBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        String bookID = extras.getString("bookID");

        BookDatabase db = Room.databaseBuilder(getApplicationContext(),
                BookDatabase.class, "BookDatabase")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        BookEntity bookEntity = db.bookDAO().findByID(bookID);
    }

    /**
     * Function to go back to the previous activity
     * @param item Menu Item
     * @return Boolean of success
     */
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}