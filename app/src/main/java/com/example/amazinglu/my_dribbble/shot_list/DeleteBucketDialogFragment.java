package com.example.amazinglu.my_dribbble.shot_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.example.amazinglu.my_dribbble.R;

public class DeleteBucketDialogFragment extends DialogFragment {

    public static final String TAG = "DeleteBucketDialogFragment";
    private static final String KEY_BUCKET_ID = "bucket_id";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_bucket, null);
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent resultIntent = new Intent();
                        getTargetFragment().onActivityResult(ShotListFragment.REQ_CODE_COMFRIN_DELETE,
                                Activity.RESULT_OK, resultIntent);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.new_bucket_negative_button, null)
                .show();
    }

    public static DeleteBucketDialogFragment newInstance() {
        return new DeleteBucketDialogFragment();
    }
}
