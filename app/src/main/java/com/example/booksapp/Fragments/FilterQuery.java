package com.example.booksapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.booksapp.R;

public class FilterQuery extends DialogFragment {
    public interface FilterDialogListener {
        public void onFilterDialogClick(DialogFragment dialog, String selection);
    }

    // Use this instance of the interface to deliver action events
    FilterDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the FilterDialogListener so we can send events to the host
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
        //Sets the parameters for the dialog builder
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