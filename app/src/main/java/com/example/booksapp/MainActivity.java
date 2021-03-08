package com.example.booksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Remove this once we can add book through app
        AsyncAddSingleBook a = new AsyncAddSingleBook(new WeakReference<>(getApplicationContext()), "oGeiDwAAQBAJ", getResources().getString(R.string.CONSUMER_KEY));
        //a.execute();

        MyGridAdapter myGridAdapter = new MyGridAdapter(this);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(myGridAdapter);

        AsyncReadingMyBooks asyncReadingMyBooks = new AsyncReadingMyBooks(myGridAdapter, getApplicationContext());
        asyncReadingMyBooks.execute();

    }

    public class MyGridAdapter extends BaseAdapter {
        Context context;
        Vector<BookEntity> vector = new Vector<>();

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
            if(convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_personal_book_layout, parent, false);
            }

            BookEntity bookEntity = (BookEntity) (getItem(position));
            ImageView imageView = convertView.findViewById(R.id.bitmap_image_view);
            TextView textView = convertView.findViewById(R.id.basic_book_info);

            File file = new File(context.getCacheDir(), bookEntity.id);
            if(file.exists()) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            } else if(isNetworkAvailable()){
                AsyncBitmapDownloader downloader = new AsyncBitmapDownloader(new WeakReference<>(context), bookEntity.id);
                downloader.execute();
                notifyDataSetChanged();
            }

            textView.setText(
                    context.getResources().getString(
                            R.string.basic_book_info, bookEntity.title,bookEntity.author));

            convertView.setClickable(true);
            convertView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent bookActivity = new Intent(getApplicationContext(), ActivityBook.class);
                            bookActivity.putExtra("bookID", bookEntity.id);
                            startActivity(bookActivity);
                        }
                    }
            );

            return convertView;
        }

        public void addAll(List<BookEntity> list) {
            vector.addAll(list);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}