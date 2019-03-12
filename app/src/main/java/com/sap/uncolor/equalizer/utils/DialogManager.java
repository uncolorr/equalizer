package com.sap.uncolor.equalizer.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Uncolor on 16.09.2018.
 */

public class DialogManager {

    public static void showDialog(Context context, String message, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("CANCEL", getNegativeButtonListener());
                builder.setPositiveButton("OK", listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static DialogInterface.OnClickListener getNegativeButtonListener(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
    }
}
