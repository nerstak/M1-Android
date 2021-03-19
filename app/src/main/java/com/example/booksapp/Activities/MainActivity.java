package com.example.booksapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.example.booksapp.AsyncTasks.AsyncBitmapDownloader;
import com.example.booksapp.AsyncTasks.AsyncReadingMyBooks;
import com.example.booksapp.R;
import com.example.booksapp.database.BookEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Vector;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT;

public class MainActivity extends AppCompatActivity {
    private MyGridAdapter myGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myGridAdapter = new MyGridAdapter(this);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(myGridAdapter);

        FloatingActionButton newBook = findViewById(R.id.add_button);
        newBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearchRequested();
            }
        });
    }

    /**
     * Adapter for GridView
     */
    public class MyGridAdapter extends BaseAdapter {
        final Context context;
        final Vector<BookEntity> vector = new Vector<>();

        public MyGridAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return vector.size();
        }

        @Override
        public Object getItem(int position) {
            return vector.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_personal_book_layout, parent, false);
            }

            // Init of variables
            BookEntity bookEntity = (BookEntity) (getItem(position));
            ImageView imageView = convertView.findViewById(R.id.bitmap_image_view);
            TextView textView = convertView.findViewById(R.id.basic_book_info);

            // Loading and displaying data
            loadCover(bookEntity, imageView);
            loadInfo(bookEntity, textView);

            // Action
            convertView.setClickable(true);
            convertView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent bookActivity = new Intent(getApplicationContext(), ActivityBook.class);
                            bookActivity.putExtra("bookID", bookEntity.getId());
                            startActivity(bookActivity);
                        }
                    }
            );

            return convertView;
        }

        /**
         * Set elements as new inner vectors
         *
         * @param list Elements
         */
        public void setVector(List<BookEntity> list) {
            vector.clear();
            vector.addAll(list);
        }

        /**
         * Load correct cover
         *
         * @param book      Book to load cover
         * @param imageView ImageView for cover
         */
        private void loadCover(BookEntity book, ImageView imageView) {
            Bitmap bitmap = book.loadImage(getApplicationContext());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else if (isNetworkAvailable()) {
                // We download the cover if it was missing
                AsyncBitmapDownloader downloader = new AsyncBitmapDownloader(new WeakReference<>(context), book.getId());
                downloader.execute();
            }
        }

        /**
         * Load information (title + author)
         *
         * @param book     Book
         * @param textView TextView
         */
        private void loadInfo(BookEntity book, TextView textView) {
            String info = context.getResources().getString(
                    R.string.basic_book_info, book.getTitle(), book.getAuthor());
            textView.setText(HtmlCompat.fromHtml(info, FROM_HTML_MODE_COMPACT));
        }
    }

    /**
     * Check if network is available
     *
     * @return Boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myGridAdapter != null) {
            AsyncReadingMyBooks asyncReadingMyBooks = new AsyncReadingMyBooks(myGridAdapter, getApplicationContext());
            asyncReadingMyBooks.execute();
        }
    }
}