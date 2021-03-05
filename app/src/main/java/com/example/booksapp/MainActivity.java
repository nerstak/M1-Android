package com.example.booksapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyGridAdapter myGridAdapter = new MyGridAdapter(this);
        GridView gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(myGridAdapter);

        AsyncReadingMyBooks asyncReadingMyBooks = new AsyncReadingMyBooks(myGridAdapter, getApplicationContext());


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

            String data = (String) ((BookEntity) getItem(position)).id;
            ImageView imageView = convertView.findViewById(R.id.bitmap_image_view);

            File file = new File(context.getCacheDir(), data);
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

            return convertView;
        }

        public void addAll(List<BookEntity> list) {
            vector.addAll(list);
        }
    }
}