package com.ark_das.springclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.ark_das.springclient.R;
import com.ark_das.springclient.base_activity.BaseActivity;
import com.ark_das.springclient.data.NotificationDataLoader;
import com.ark_das.springclient.data.UserDataLoader;
import com.ark_das.springclient.dto.ChangePasswordRequest;
import com.ark_das.springclient.dto.LoginResponse;
import com.ark_das.springclient.notification.NotificationWorker;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity
        implements Validator.ValidationListener {

    private Validator validator;

    private TextInputLayout layoutOldPassword;
    private TextInputLayout layoutNewPassword;
    private TextInputLayout layoutConfirmPassword;

    @NotEmpty(message = "Введите старый пароль")
    private TextInputEditText fieldOldPassword;

    @NotEmpty(message = "Введите новый пароль")
    @Password(min = 8, message = "Минимум 8 символов")
    private TextInputEditText fieldNewPassword;

    @NotEmpty(message = "Подтвердите пароль")
    private TextInputEditText fieldConfirmPassword;

    private MaterialButton btnResetPassword;
    private MaterialButton btnExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        bindViews();
        setupValidator();
        setupListeners();
    }

    private void bindViews() {
        layoutOldPassword = findViewById(R.id.layout_old_password);
        layoutNewPassword = findViewById(R.id.layout_new_password);
        layoutConfirmPassword = findViewById(R.id.layout_confirm_new_password);

        fieldOldPassword = findViewById(R.id.field_old_password);
        fieldNewPassword = findViewById(R.id.field_new_password);
        fieldConfirmPassword = findViewById(R.id.field_confirm_new_password);

        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnExit = findViewById(R.id.btnExit);
    }

    private void setupValidator() {
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    private void setupListeners() {
        btnResetPassword.setOnClickListener(v -> validator.validate());
        btnExit.setOnClickListener(v -> finish());
    }

    // ================= VALIDATION =================

    @Override
    public void onValidationSucceeded() {
        clearErrors();

        String oldPass = fieldOldPassword.getText().toString().trim();
        String newPass = fieldNewPassword.getText().toString().trim();
        String confirm = fieldConfirmPassword.getText().toString().trim();

        if (!newPass.equals(confirm)) {
            layoutConfirmPassword.setError("Пароли не совпадают");
            return;
        }

        changePassword(oldPass, newPass);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        clearErrors();

        for (ValidationError error : errors) {
            int id = error.getView().getId();
            String msg = error.getCollatedErrorMessage(this);

            if (id == R.id.field_old_password) {
                layoutOldPassword.setError(msg);
            } else if (id == R.id.field_new_password) {
                layoutNewPassword.setError(msg);
            } else if (id == R.id.field_confirm_new_password) {
                layoutConfirmPassword.setError(msg);
            }
        }
    }

    private void clearErrors() {
        layoutOldPassword.setError(null);
        layoutNewPassword.setError(null);
        layoutConfirmPassword.setError(null);
    }

    // ================= API =================

    private int getCurrentUserId() {
        return new UserDataLoader().loadUserId(this);
    }

    private void changePassword(String oldPassword, String newPassword) {
        UserApi api = new RetrofitService()
                .getRetrofit()
                .create(UserApi.class);
        int id = getCurrentUserId();

        ChangePasswordRequest request = new ChangePasswordRequest(id, oldPassword, newPassword);
        api.changePassword(request)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call,
                                           Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            successChange();
                            Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(
                                    ChangePasswordActivity.this,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(
                                ChangePasswordActivity.this,
                                "Ошибка сервера",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void successChange() {
        if(isResolve()){
            sendNotificationAfterTime(this);
        }
    }

    private void sendNotificationAfterTime(Context context ) {
        Data data = new Data.Builder()
                .putString("title", "Пароль")
                .putString("content", "Недавно вы меняли пароль")
                .build();

        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(NotificationWorker.class)
                        .setInputData(data)
                        .setInitialDelay(4, TimeUnit.SECONDS) // ⏰ ВРЕМЯ
                        .build();

        WorkManager.getInstance(context).enqueue(request);
    }
    private boolean isResolve(){
        NotificationDataLoader loader = new NotificationDataLoader();
        return loader.isResolve(this);
    }
}

