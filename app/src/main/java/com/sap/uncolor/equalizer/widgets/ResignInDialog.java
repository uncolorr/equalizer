package com.sap.uncolor.equalizer.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sap.uncolor.equalizer.R;

/**
 * Created by Uncolor on 04.11.2018.
 */

public class ResignInDialog {

    private Button buttonSignIn;
    private EditText editTextLogin;
    private EditText editTextPassword;
    private AlertDialog alertDialog;

    public ResignInDialog(Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_resign, null);
        editTextLogin = dialogView.findViewById(R.id.editTextLogin);
        editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        buttonSignIn = dialogView.findViewById(R.id.buttonSignIn);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
    }

    public void show() {
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }

    public String getLogin() {
        return editTextLogin.getText().toString();
    }

    public String getPassword(){
        return editTextPassword.getText().toString();
    }

    public void setOnSignInClickListener(View.OnClickListener listener) {
        buttonSignIn.setOnClickListener(listener);
    }

    public void clear(){
        editTextLogin.getText().clear();
        editTextPassword.getText().clear();
    }

}
