package com.ark_das.springclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.R;
import com.ark_das.springclient.data.UserDataLoader;
import com.ark_das.springclient.dto.LoginResponse;
import com.ark_das.springclient.dto.UserRequest;
import com.ark_das.springclient.dto.UserResponse;
import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity
        implements Validator.ValidationListener {

    /* -------- Layouts -------- */
    private TextInputLayout layoutFirstName;
    private TextInputLayout layoutLastName;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutBio;

   // private String currentUsersPassword;

    /* -------- Inputs -------- */
    @NotEmpty(message = "Введите имя")
    @Length(min = 2, message = "Имя должно быть не короче 2 символов")
    private TextInputEditText inputFirstName;

    @NotEmpty(message = "Введите фамилию")
    @Length(min = 2, message = "Фамилия должна быть не короче 2 символов")
    private TextInputEditText inputLastName;

    @NotEmpty(message = "Введите email")
    @Pattern(regex = ".*[@].*", message = "Email должен содержать @")
    private TextInputEditText inputEmail;

    @Length(min = 5, message = "Биография должна быть не короче 5 символов")
    private TextInputEditText inputBio;

    /* -------- Buttons -------- */
    private MaterialButton buttonSave;
    private MaterialButton buttonCancel;
    private ImageButton buttonMenu;

    /* -------- Validator -------- */
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user_profile);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.activity_edit_user_profile),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        initViews();
        setupUserInfo();
        setupValidator();
        setupActions();
    }
    private void initViews() {

        // Layouts
        layoutFirstName = findViewById(R.id.layout_form_textFieldFirstName);
        layoutLastName  = findViewById(R.id.layout_form_textFieldLastName);
        layoutEmail     = findViewById(R.id.layout_form_textFieldEmail);
        layoutBio       = findViewById(R.id.layout_form_textFieldBio);

        // Inputs
        inputFirstName = findViewById(R.id.form_textFieldFirstName);
        inputLastName  = findViewById(R.id.form_textFieldLastName);
        inputEmail     = findViewById(R.id.form_textFieldEmail);
        inputBio       = findViewById(R.id.form_textFieldBio);

        // Buttons
        buttonSave   = findViewById(R.id.form_buttonSave);
        buttonCancel = findViewById(R.id.form_buttonCancel);
        buttonMenu   = findViewById(R.id.btn_menu);
    }

    private void setupUserInfo() {
        int userId = getCurentUsersId();
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        UserRequest userRequest = new UserRequest();
        userRequest.setId(userId);

        userApi.getById(userRequest).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();

                    System.out.println(response.body().getUser().getId());
                    fillForm(user); // ← заполняем форму ПОСЛЕ получения данных
                    Logger.getLogger(EditProfileActivity.class.getName()).log(Level.INFO,
                            "User data loaded and form filled");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Failed to found user", Toast.LENGTH_SHORT).show();
                Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, "Error occurred", t);
            }
        });
    }

    private void fillForm(User user) {
        inputFirstName.setText(user.getFirst_name());
        inputLastName.setText((user.getLast_name()));
        inputEmail.setText(user.getEmail());
        inputBio.setText(user.getBio() != null ? user.getBio() : "Остутсвует");
        //currentUsersPassword = user.getPassword();
    }

    private int getCurentUsersId(){
        UserDataLoader loader = new UserDataLoader();
        return loader.loadUserId(this);
    }


    /* ================= INIT ================= */



    private void setupValidator() {
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    private void setupActions() {

        buttonSave.setOnClickListener(v -> validator.validate());

        buttonCancel.setOnClickListener(v -> {
            clearErrors();
            finish();
        });

        buttonMenu.setOnClickListener(v -> onBackPressed());
    }

    /* ================= VALIDATION ================= */

    @Override
    public void onValidationSucceeded() {
        clearErrors();

        // ⬇️ ТОЛЬКО пример, логику ты напишешь сам
        String firstName = inputFirstName.getText().toString().trim();
        String lastName  = inputLastName.getText().toString().trim();
        String email     = inputEmail.getText().toString().trim();
        String bio       = inputBio.getText().toString().trim();

        Toast.makeText(this,
                "Валидация пройдена ✔",
                Toast.LENGTH_SHORT).show();
        saveProfile(firstName, lastName, email, bio);
    }

    private void saveProfile(String firstName, String lastName, String email, String bio) {
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        User user = new User();
       //user.setPassword(currentUsersPassword);
        user.setFirst_name(firstName);
        user.setLast_name(lastName);
        user.setEmail(email);
        user.setBio(bio);
        user.setId(getCurentUsersId());

        userApi.save(user).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body().isSuccess()) {
                    Toast.makeText(EditProfileActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this,
                            response.body().getMessage() != null? response.body().getMessage(): "Save error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Server eror", Toast.LENGTH_SHORT).show();
                Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, "Error occured", t);
            }
        });


    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        clearErrors();

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view.getId() == R.id.form_textFieldFirstName) {
                layoutFirstName.setError(message);
            } else if (view.getId() == R.id.form_textFieldLastName) {
                layoutLastName.setError(message);
            } else if (view.getId() == R.id.form_textFieldEmail) {
                layoutEmail.setError(message);
            } else if (view.getId() == R.id.form_textFieldBio) {
                layoutBio.setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /* ================= UTILS ================= */

    private void clearErrors() {
        layoutFirstName.setError(null);
        layoutLastName.setError(null);
        layoutEmail.setError(null);
        layoutBio.setError(null);
    }
}
