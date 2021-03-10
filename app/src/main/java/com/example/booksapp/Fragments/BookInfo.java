package com.example.booksapp.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.booksapp.Activities.ActivityBook;
import com.example.booksapp.R;

import java.io.File;

/**
 *  Fragment holding most information on book
 */
public class BookInfo extends Fragment {
    private ActivityBook activity;

    public BookInfo() {
    }


    public static BookInfo newInstance() {
        BookInfo fragment = new BookInfo();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (ActivityBook) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle();
        setAuthor();
        setPage();
        setDate();
        setCover();
    }

    private void setDate() {
        TextView date = activity.findViewById(R.id.date_book);
        date.setText(activity.getResources().getString(R.string.date_format,activity.bookEntity.getPublishDate()));
    }

    /**
     * Set cover of book
     */
    private void setCover() {
        ImageView cover = activity.findViewById(R.id.cover_book_info);

        File file = new File(activity.getCacheDir(), activity.bookEntity.getId());
        if (file.exists()) {
            Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());

            // Upscaling image
            cover.setImageBitmap(
                    Bitmap.createScaledBitmap(b, b.getWidth() * 3, b.getHeight() * 3, true));
        }
    }

    /**
     * Set title of book
     */
    private void setTitle() {
        TextView title = activity.findViewById(R.id.title_book);
        title.setText(activity.bookEntity.getTitle());
    }

    /**
     * Set author of book
     */
    private void setAuthor() {
        TextView author = activity.findViewById(R.id.author_book);
        author.setText(activity.bookEntity.getAuthor());
    }

    /**
     * Set author of book
     */
    private void setPage() {
        TextView page = activity.findViewById(R.id.page_book);
        page.setText(activity.getResources().getString(R.string.pages_numbering, activity.bookEntity.getPageCount()));
    }
}