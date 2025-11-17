package com.ark_das.springclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.dto.LoginResponse;
import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterForm extends AppCompatActivity implements Validator.ValidationListener{

    private Validator validator;

    TextInputLayout firstNameLayout, lastNameLayout, emailLayout, passwordLayout, confirmPasswordLayout;

    @NotEmpty(message = "Введите ваше имя")
    @Length(min = 2, message = "В имени должно быть не меньше 2 символов")
    @Order(1)
    TextInputEditText firstNameEditText;

    @NotEmpty(message = "Введите вашу фамиилю")
    @Length(min = 2, message = "В фамилии должно быть не меньше 2 символов")
    @Order(2)
    TextInputEditText lastNameEditText;

    @NotEmpty(message = "Введите почту")
    @Pattern(regex = ".*[@].*", message = "Укажите вашу полную почту, включая символ @")
    @Length(min = 3, message = "В почте должно быть не короче 3 символов")
    @Order(3)
    TextInputEditText emailEditText;

    @Password(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @Pattern(regex = ".*[A-Z].*", message = "Пароль должен содержать хотя бы одну заглавную букву")
    @Order(4)
    TextInputEditText passwordEditText;

    @ConfirmPassword(message = "Пароли не совпадают")
    @Order(5)
    TextInputEditText confirmPasswordEditText;

    AppCompatTextView signInLink;
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registration_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setComponents();
    }

    private void setComponents() {
        // for data validations
        validator = new Validator(this);
        validator.setValidationListener(this);

        //set layouts
        firstNameLayout = findViewById(R.id.firstNameLayout);
        lastNameLayout = findViewById(R.id.lastNameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        // set editText
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        //set buttons
        createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(view -> validator.validate());
        signInLink = findViewById(R.id.signUpLink);
        signInLink.setOnClickListener(view -> signInLink());


    }

    private void signInLink() {
        Intent intent = new Intent(this, LoginForm.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public void onValidationSucceeded() {

        clearEditTextLayouts();

        String first_name = String.valueOf(firstNameEditText.getText());
        String last_name = String.valueOf(lastNameEditText.getText());
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());

        User register_user = new User();

        register_user.setFirst_name(first_name);
        register_user.setLast_name(last_name);
        register_user.setEmail(email);
        register_user.setPassword(password);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        userApi.register(register_user)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(response.isSuccessful() && response.body().isSuccess()){
                            Toast.makeText(RegisterForm.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterForm.this, UserListActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(RegisterForm.this,
                                    response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(RegisterForm.this, "Server eror", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, "Error occured", t);
                    }
                });

    }

    private void clearEditTextLayouts() {
        firstNameLayout.setError(null);
        lastNameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        // Сначала очищаем старые ошибки
        clearEditTextLayouts();

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view.getId() == R.id.firstNameEditText) {
                firstNameLayout.setError(message);
            } else if (view.getId() == R.id.lastNameEditText) {
                lastNameLayout.setError(message);
            } else if (view.getId() == R.id.emailEditText) {
                emailLayout.setError(message);
            } else if (view.getId() == R.id.passwordEditText) {
                passwordLayout.setError(message);
            }else if (view.getId() == R.id.confirmPasswordEditText) {
                confirmPasswordLayout.setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }

    }
}