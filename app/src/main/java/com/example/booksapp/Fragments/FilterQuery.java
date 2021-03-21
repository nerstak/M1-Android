package com.example.booksapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.booksapp.R;

public class FilterQuery extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface FilterDialogListener {
        public void onFilterDialogClick(DialogFragment dialog, String selection);
    }

    // Use this instance of the interface to deliver action events
    FilterDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("FilterQuery must implement FilterDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.filter_title)
                .setItems(R.array.query_filter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onFilterDialogClick(FilterQuery.this
                                        , getResources().getStringArray(R.array.query_filter)[which]);
                            }
                        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}