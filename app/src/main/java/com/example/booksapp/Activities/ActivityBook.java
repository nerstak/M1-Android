package com.example.booksapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booksapp.Fragments.BookInfo;
import com.example.booksapp.Fragments.BookResume;
import com.example.booksapp.R;
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
    private Boolean pageEditFocus = false;

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
        setButtons();
        setPageViewEdit();
        setFragmentListener();
    }

    private void setButtons() {
        ImageButton minusButton = findViewById(R.id.book_minus_page);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookEntity.setPageRead(bookEntity.getPageRead() - 1);
                updateBook();
                setPageCount();
            }
        });

        ImageButton plusButton = findViewById(R.id.book_plus_page);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookEntity.setPageRead(bookEntity.getPageRead() + 1);
                updateBook();
                setPageCount();
            }
        });
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
        TextView page = findViewById(R.id.input_page);
        page.setText(String.valueOf(bookEntity.getPageRead()));
    }

    /**
     * Set spinner values and default position
     */
    private void setSpinner() {
        Spinner spinner = findViewById(R.id.spinner_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(StatusBook.toStatus(bookEntity.getStatus()).ordinal());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bookEntity.setStatus(StatusBook.values()[position].toString());
                updateBook();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

    /**
     * Update book in database
     */
    private void updateBook() {
        BookDatabase db = DatabaseUtilities.getBookDatabase(getApplicationContext());
        db.bookDAO().update(bookEntity);
        db.close();
    }

    /**
     * Set up text view edition of page read
     */
    private void setPageViewEdit() {
        TextView page = findViewById(R.id.input_page);
        page.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Only fire action when the user consider that he is done
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (event == null || !event.isShiftPressed()) {
                        String nbPage = page.getText().toString();
                        String message = "Given number is not possible";
                        try {
                            if(bookEntity.setPageRead(Integer.parseInt(nbPage))) {
                                updateBook();
                                message = "Page number updated";
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),
                                message,
                                Toast.LENGTH_SHORT).show();
                        setPageCount();

                        return true;
                    }
                }
                return false;
            }
        });
    }
}