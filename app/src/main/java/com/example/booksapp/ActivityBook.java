package com.example.booksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;
import com.example.booksapp.database.DatabaseUtilities;
import com.example.booksapp.database.StatusBook;

/**
 * Activity to display information on book
 */
public class ActivityBook extends AppCompatActivity {
    public BookEntity bookEntity;
    private String currentFragment = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setFragment(BookInfo.newInstance());

        Bundle extras = getIntent().getExtras();
        String bookID = extras.getString("bookID");

        BookDatabase db = DatabaseUtilities.getBookDatabase(getApplicationContext());

        bookEntity = db.bookDAO().findByID(bookID);
        db.close();


        setPageCount();
        setSpinner();
        setFragmentListener();

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

    /**
     * Setup fragment
     * @param fragment Fragment
     */
    public void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_book, fragment);
        transaction.commit();
    }

    /**
     * Set actual page count
     */
    private void setPageCount() {
        TextView title = findViewById(R.id.input_page);
        title.setText(String.valueOf(bookEntity.pageRead));
    }

    /**
     * Set spinner values and default position
     */
    private void setSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(StatusBook.toStatus(bookEntity.status).ordinal());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bookEntity.status = StatusBook.values()[position].toString();
                BookDatabase db = DatabaseUtilities.getBookDatabase(getApplicationContext());
                db.bookDAO().update(bookEntity);
                db.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Listener to switch between fragments
     */
    private void setFragmentListener() {
        FrameLayout frame = findViewById(R.id.container_book);
        frame.setClickable(true);
        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFragment.equals("info")) {
                    setFragment(BookResume.newInstance());
                    currentFragment = "resume";
                } else if (currentFragment.equals("resume")) {
                    setFragment(BookInfo.newInstance());
                    currentFragment = "info";
                }
            }
        });
    }
}