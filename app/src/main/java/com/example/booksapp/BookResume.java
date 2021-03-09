package com.example.booksapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Fragment for book resume
 */
public class BookResume extends Fragment {
    private ActivityBook activity;

    public BookResume() {
    }


    public static BookResume newInstance() {
        BookResume fragment = new BookResume();
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
        return inflater.inflate(R.layout.fragment_book_resume, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setResume();
    }

    private void setResume() {
        TextView resume = activity.findViewById(R.id.resume_book);

        resume.setText(Html.fromHtml(activity.bookEntity.resume));
    }
}