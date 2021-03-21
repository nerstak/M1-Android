package com.example.booksapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Activity to display information on book
 */
public class ActivityBook extends AppCompatActivity {
    public BookEntity bookEntity;
    private String currentFragment = "info";
    private static final int PICK_IMAGE = 100;
    private String bookID;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        setFragment(BookInfo.newInstance());

        Bundle extras = getIntent().getExtras();
        bookID = extras.getString("bookID");

        BookDatabase db = DatabaseUtilities.getBookDatabase(getApplicationContext());
        bookEntity = db.bookDAO().findByID(bookID);
        db.close();

        getSupportActionBar().setTitle(String.valueOf(bookEntity.getTitle()));

        setPageCount();
        setStatusSpinner();
        setRatingSpinner();
        setButtons();
        setPageViewEdit();
        setFragmentListener();

    }

    /**
     * Setting up buttons and their listener
     */
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

        Button coverButton = findViewById(R.id.add_cover);
        coverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
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
        } else if(item.getItemId() == R.id.action_delete_book) {
            //Delete book from db and custom cover from files
            BookDatabase db = DatabaseUtilities.getBookDatabase(getApplicationContext());
            db.bookDAO().delete(bookEntity);
            db.close();
            deleteFile(bookID);
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
    private void setStatusSpinner() {
        Spinner spinner = findViewById(R.id.spinner_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt("Status?");
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
     * Set spinner values and default position
     */
    private void setRatingSpinner() {

        Spinner spinner = findViewById(R.id.spinner_rating);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rating_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt("Rating?");
        spinner.setAdapter(adapter);

        String[] ratings = getResources().getStringArray(R.array.rating_array);
        spinner.setSelection(
            IntStream.range(0, ratings.length).filter(i -> ratings[i].equals(bookEntity.getRating())).findFirst().orElse(0)
        );


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bookEntity.setRating(ratings[position]);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //If we are picking an image from the gallery get the image and save it to internal app storage
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            try {
                File f = new File(getFilesDir(), bookID);
                FileOutputStream fos = new FileOutputStream(f);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                //Rotate and scale the image correctly before saving it
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = rotateImage(bitmap, inputStream);
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 600, true);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

                ImageView imageView = findViewById(R.id.cover_book_info);

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Rotates an image based on Exif information
     * Images uploaded from gallery may be incorrectly rotated by bitmapFactory since their Exif data is ignored
     * @param bitmap image to rotate
     * @return rotated image
     * @throws IOException error in file access
     */
    public Bitmap rotateImage(Bitmap bitmap, InputStream inputStream) throws IOException {
        int rotate = 0;
        ExifInterface exif;
        exif = new ExifInterface(inputStream);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }
}