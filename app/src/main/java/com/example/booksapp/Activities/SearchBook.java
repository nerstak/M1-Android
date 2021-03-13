package com.example.booksapp.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booksapp.R;

public class SearchBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_book);

        TextView searchQueryView = (TextView) findViewById(R.id.search_query);

        // Gets the search query from intent
        Intent searchIntent = getIntent();
        if (searchIntent.getAction().equals(Intent.ACTION_SEARCH)) {
            String bookQuery = searchIntent.getStringExtra(SearchManager.QUERY);
            searchQueryView.setText(bookQuery);
        }
    }
}
