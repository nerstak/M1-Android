package com.example.booksapp.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.booksapp.Activities.ActivityBook;
import com.example.booksapp.R;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT;

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

        resume.setText(HtmlCompat.fromHtml(activity.bookEntity.getResume(), FROM_HTML_MODE_COMPACT));
    }
}