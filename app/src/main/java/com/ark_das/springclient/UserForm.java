package com.ark_das.springclient;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserForm extends AppCompatActivity implements Validator.ValidationListener {

    //Layouts for inputEditText
    //TextInputLayout


    //InputEditText from form
    @NotEmpty(message = "Введите ваше имя")
    @Length(min = 2, message = "В имени должно быть не меньше 2 символов")
    TextInputEditText inpuntEditTextFirstName;

    @NotEmpty(message = "Введите вашу фамиилю")
    @Length(min = 2, message = "В фамилии должно быть не меньше 2 символов")
    TextInputEditText inpuntEditTextLastName;

    @NotEmpty(message = "Введите почту")
    @Pattern(regex = ".*[@].*", message = "Укажите вашу полную почту, включая символ @")
    @Length(min = 3, message = "В почте должно быть не короче 3 символов")
    TextInputEditText inpuntEditTextEmail;

    @Password(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @Pattern(regex = ".*[A-Z].*", message = "Пароль должен содержать хотя бы одну заглавную букву")
    TextInputEditText inpuntEditTextPassword;


    TextInputEditText inputBio;

    @NotEmpty(message = "Выберете роль")
    AutoCompleteTextView form_spinnerRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_add_user_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         initializeComponents();
        }

    private void initializeComponents(){
        inpuntEditTextFirstName = findViewById(R.id.form_textFieldFirstName);
        inpuntEditTextLastName = findViewById(R.id.form_textFieldLastName);
        inpuntEditTextEmail = findViewById(R.id.form_textFieldEmail);
        //inpuntEditTextLogin = findViewById(R.id.form_textFieldLogin);
        inpuntEditTextPassword = findViewById(R.id.form_textFieldPassword);

        MaterialButton buttonSave = findViewById(R.id.form_buttonSave);


        //adduser()
        buttonSave.setOnClickListener(view -> onValidationSucceeded());


    }

    private void adduser(){

    }

    @Override
    public void onValidationSucceeded() {
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        String first_name = String.valueOf(inpuntEditTextFirstName.getText());
        String last_name = String.valueOf(inpuntEditTextLastName.getText());
        String email = String.valueOf(inpuntEditTextEmail.getText());
        //String login = String.valueOf(inpuntEditTextLogin.getText());
        String password = String.valueOf(inpuntEditTextPassword.getText());

        User user = new User();
        user.setFirst_name(first_name);
        user.setLast_name(last_name);
        user.setEmail(email);
        //user.setLogin(login);
        user.setPassword(password);

        //user.setCreated_at(LocalDateTime.now());

        userApi.save(user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(UserForm.this, "Save successful!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(UserForm.this, "Save failed!!!", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, "Error occured", t);
                    }
                });
        }


    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        /*for (ValidationError error : errors) {
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
            }*/
    }
}