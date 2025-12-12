package com.ark_das.springclient.ui;

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

import com.ark_das.springclient.R;
import com.ark_das.springclient.data.UserDataLoader;
import com.ark_das.springclient.data.UserDataSaver;
import com.ark_das.springclient.dto.RoleResponse;
import com.ark_das.springclient.retrofit.RoleApi;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;


import com.ark_das.springclient.dto.LoginRequest;
import com.ark_das.springclient.dto.LoginResponse;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginForm extends AppCompatActivity implements Validator.ValidationListener{

    private Validator validator;

    @NotEmpty(message = "Введите почту")
    @Pattern(regex = ".*[@].*", message = "Укажите вашу полную почту, включая символ @")
    @Length(min = 3, message = "В почте должно быть не короче 3 символов")
    @Order(1)
    TextInputEditText emailEditText;

    @NotEmpty(message = "Введите пароль")
    @Order(2)
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
                            saveUserRoleName(response.body().getUser().getRole_id());
                            saveUserIdLocal(response.body().getUser().getId());
                            loadUser();
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

    private void saveUserRoleName(int id){
        RetrofitService retrofitService = new RetrofitService();
        RoleApi roleApi = retrofitService.getRetrofit().create(RoleApi.class);
        roleApi.getRoleById(id).enqueue(new Callback<RoleResponse>() {
            @Override
            public void onResponse(Call<RoleResponse> call, Response<RoleResponse> response) {
                if(response.isSuccessful() && response.body().isSuccess()){
                    saveUserRoleNameLocal(response.body().getRole().getName());
                }else{
                    Toast.makeText(LoginForm.this,
                            response.body().getMessage() != null? response.body().getMessage() : "Server error", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RoleResponse> call, Throwable t) {
                Toast.makeText(LoginForm.this, "Server eror", Toast.LENGTH_SHORT).show();
                Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, "Error occured", t);
            }
        });
    }

    private void loadUser(){
        UserDataLoader loader = new UserDataLoader();

        int savedId = loader.loadUserId(this);
        String savedRole = loader.getUserRole(this);

        String message = String.format("Пользователь: %d, Роль: %s",
                savedId, savedRole);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private void saveUserLocal(int userId, String role){
        UserDataSaver saver = new UserDataSaver();
        saver.saveUser(this,userId, role);
    }
    private void saveUserRoleNameLocal(String role){
        UserDataSaver saver = new UserDataSaver();
        saver.saveUserRoleName(this, role);
    }

    private void saveUserIdLocal(int id){
        UserDataSaver saver = new UserDataSaver();
        saver.saveUserId(this, id);
    }

}