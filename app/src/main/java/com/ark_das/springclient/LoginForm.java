package com.ark_das.springclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;


import com.ark_das.springclient.adapter.LoginRequest;
import com.ark_das.springclient.adapter.LoginResponse;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginForm extends AppCompatActivity implements Validator.ValidationListener{

    private Validator validator;

    @NotEmpty(message = "Введите почту")
    @Length(min = 3, message = "В почте должно быть не короче 3 символов")
    TextInputEditText emailEditText;

    @NotEmpty(message = "Введите пароль")
    TextInputEditText passwordEditText;

    TextInputLayout layoutEmail, layoutPassword;
    TextView signUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_auth_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setComponents();
    }

    public void setComponents(){

        validator = new Validator(this);
        validator.setValidationListener(this);

        layoutEmail = findViewById(R.id.emailLayout);
        layoutPassword = findViewById(R.id.passwordLayout);

        //components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Button buttonLogin = findViewById(R.id.loginButton);
        buttonLogin.setOnClickListener(view -> validator.validate());

        signUpLink = findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(view -> SignUpLink());




    }

    private void SignUpLink() {
        Intent intent = new Intent(LoginForm.this, RegisterForm.class);
        startActivity(intent);
        //aнимация
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onValidationSucceeded() {
        layoutEmail.setError(null);
        layoutPassword.setError(null);

        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());

        LoginRequest login_data = new LoginRequest();
        login_data.setEmail(email);
        login_data.setPassword(password);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        userApi.login(login_data)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(response.isSuccessful() && response.body().isSuccess()){ //&& response.body().getId() != 0
                            Toast.makeText(LoginForm.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginForm.this, UserListActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginForm.this,
                                    response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginForm.this, "Server eror", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, "Error occured", t);
                    }
                });

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        // Сначала очищаем старые ошибки
        layoutEmail.setError(null);
        layoutPassword.setError(null);

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view.getId() == R.id.emailEditText) {
                layoutEmail.setError(message);
            } else if (view.getId() == R.id.passwordEditText) {
                layoutPassword.setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}