package com.example.komputer.wifibarometer.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.komputer.wifibarometer.R;

public class ConfirmDialog extends AlertDialog.Builder{

    public ConfirmDialog(Context context, String title, String message) {
        super(context);
        setTitle(title)
                .setMessage(message)
//                .setCancelable(canDismiss)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onSelected(true);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSelected(false);
                    }
                })
                .setNeutralButton(R.string.cancel, null);
    }

    public interface ConfirmDialogListener{
        void onSelected(boolean button);
    }
    private ConfirmDialogListener listener;

    public ConfirmDialog setConfirmDialogListener(ConfirmDialogListener listener) {
        this.listener = listener;
        return this;
    }
}
