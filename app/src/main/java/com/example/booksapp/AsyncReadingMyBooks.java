package com.example.booksapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;

import com.example.booksapp.database.BookDatabase;
import com.example.booksapp.database.BookEntity;

import java.lang.ref.WeakReference;
import java.util.List;

public class AsyncReadingMyBooks extends AsyncTask<Void, Void, List<BookEntity>> {
    private final MainActivity.MyGridAdapter myGridAdapter;
    private final WeakReference<Context> contextWeakReference;

    public AsyncReadingMyBooks(MainActivity.MyGridAdapter myGridAdapter, Context context) {
        this.myGridAdapter = myGridAdapter;
        this.contextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    protected List<BookEntity> doInBackground(Void... voids) {
        if(contextWeakReference != null) {
            Context context = contextWeakReference.get();

            if(context != null ){
                BookDatabase db = Room.databaseBuilder(context,
                        BookDatabase.class, "BookDatabase")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();

                List<BookEntity> list = db.bookDAO().getAll();

                db.close();

                return list;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<BookEntity> bookEntities) {
        myGridAdapter.addAll(bookEntities);
        myGridAdapter.notifyDataSetChanged();
    }
}
