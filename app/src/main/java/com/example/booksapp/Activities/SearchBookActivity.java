package com.example.booksapp.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booksapp.AsyncTasks.AsyncFindBooks;
import com.example.booksapp.R;

public class SearchBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.search_header);

        TextView searchQueryView = (TextView) findViewById(R.id.search_query);

        // Gets the search query from intent
        Intent searchIntent = getIntent();
        if (searchIntent.getAction().equals(Intent.ACTION_SEARCH)) {
            String bookQuery = searchIntent.getStringExtra(SearchManager.QUERY);
            searchQueryView.setText(bookQuery);
            AsyncFindBooks findBooks = new AsyncFindBooks(getResources().getString(R.string.CONSUMER_KEY));
            findBooks.execute(bookQuery);
        }
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
