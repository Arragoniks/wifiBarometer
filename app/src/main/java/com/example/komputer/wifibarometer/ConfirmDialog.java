package com.example.komputer.wifibarometer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmDialog extends AlertDialog.Builder{

    //private String message = "";

    public ConfirmDialog(Context context) {
        super(context);
        setTitle("Confirm Your Choose!")
                //.setMessage(message)
                //.setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.OnSelected(true);
                    }
                })
                .setNegativeButton(R.string.no, null);
    }

    public void setText(String message){
        //this.message = message;
        this.setMessage(message);
    }

    public interface OpenDialogListener{
        public void OnSelected(boolean button);
    }
    private OpenDialogListener listener;

    public ConfirmDialog setOpenDialogListener(OpenDialogListener listener) {
        this.listener = listener;
        return this;
    }
}
