package com.sap.uncolor.equalizer.auth_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.sap.uncolor.equalizer.Apis.Api;
import com.sap.uncolor.equalizer.Apis.ApiResponse;
import com.sap.uncolor.equalizer.Apis.response_models.AuthResponseModel;
import com.sap.uncolor.equalizer.R;
import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.main_activity.MainActivity;
import com.sap.uncolor.equalizer.utils.LoadingDialog;
import com.sap.uncolor.equalizer.utils.MessageReporter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AuthActivity extends AppCompatActivity implements ApiResponse.ApiFailureListener{

    @BindView(R.id.editTextLogin)
    EditText editTextLogin;

    @BindView(R.id.editTextPassword)
    EditText editTextPassword;

    private AlertDialog loadingDialog;

    public static Intent getInstance(Context context){
        return new Intent(context, MainActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        if(App.isAuth()){
            startActivity(MainActivity.getInstance(this));
            finish();
            return;
        }
        ButterKnife.bind(this);
        loadingDialog = LoadingDialog.newInstance(this, LoadingDialog.LABEL_PROCESSING);
    }

    @OnClick(R.id.buttonSignIn)
    void onButtonSignInClick(){
        loadingDialog.show();
        if(editTextLogin.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty()){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
            return;
        }
        Api.getSource().login(editTextLogin.getText().toString(), editTextPassword.getText().toString())
                .enqueue(ApiResponse.getCallback(getLoginResponseListener(), this));
    }

    private ApiResponse.ApiResponseListener<AuthResponseModel> getLoginResponseListener() {
        return new ApiResponse.ApiResponseListener<AuthResponseModel>() {
            @Override
            public void onResponse(AuthResponseModel result) {
                loadingDialog.cancel();
                if(result.getToken() == null){
                    MessageReporter.showMessage(AuthActivity.this,
                            "Ошибка", "Ошибка авторизации. Попробуйте пройти авторизацию позже");
                }
                else {
                    String token = result.getToken();
                    App.saveToken(token);
                    finish();
                    startActivity(MainActivity.getInstance(AuthActivity.this));
                }
            }
        };
    }


    @Override
    public void onFailure(int code, String message) {
        loadingDialog.cancel();
        MessageReporter.showMessage(AuthActivity.this,
                "Ошибка", "Ошибка авторизации. Попробуйте пройти авторизацию позже");
    }
}
