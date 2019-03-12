package com.sap.uncolor.equalizer.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sap.uncolor.equalizer.Apis.response_models.CaptchaErrorResponse;
import com.sap.uncolor.equalizer.R;

/**
 * Created by Uncolor on 13.09.2018.
 */

public class CaptchaDialog {

    private ImageView imageViewCaptcha;
    private Button buttonSend;
    private EditText editTextCaptcha;
    private AlertDialog alertDialog;

    private CaptchaErrorResponse captchaErrorResponse;

    public CaptchaDialog(Context context, CaptchaErrorResponse captchaErrorResponse){
        this.captchaErrorResponse = captchaErrorResponse;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_send_captcha, null);
        editTextCaptcha = dialogView.findViewById(R.id.editTextCaptcha);
        buttonSend = dialogView.findViewById(R.id.buttonSend);
        imageViewCaptcha = dialogView.findViewById(R.id.imageViewCaptcha);
        dialogBuilder.setView(dialogView);
        loadCaptchaImage(context);
        alertDialog = dialogBuilder.create();
    }

    public void show(){
        alertDialog.show();
    }

    public void dismiss(){
        alertDialog.dismiss();
    }

    public String getCaptcha(){
        return editTextCaptcha.getText().toString();
    }

    public void setOnSendClickListener(View.OnClickListener listener){
        buttonSend.setOnClickListener(listener);
    }

    private void loadCaptchaImage(Context context){
        Glide
                .with(context)
                .load(captchaErrorResponse.getCaptchaImage())
                .into(imageViewCaptcha);
    }

}
