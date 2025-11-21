package com.ark_das.springclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.dto.LoginResponse;
import com.ark_das.springclient.dto.UserRequest;
import com.ark_das.springclient.dto.UserResponse;
import com.ark_das.springclient.model.Role;
import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.RoleApi;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserForm extends AppCompatActivity implements Validator.ValidationListener {

    private Bundle arguments;
    private int userId;
    private String mode;

    //Layouts for inputEditText
    TextInputLayout layout_form_textFieldFirstName, layout_form_textFieldLastName,
            layout_form_textFieldEmail, layout_form_textFieldLogin, layout_form_textFieldPassword,
            layout_form_textFieldBio, layout_form_spinnerRole;

    //InputEditText from form
    @NotEmpty(message = "Введите ваше имя")
    @Length(min = 2, message = "В имени должно быть не меньше 2 символов")
    TextInputEditText inpuntEditTextFirstName;

    @NotEmpty(message = "Введите вашу фамилию")
    @Length(min = 2, message = "В фамилии должно быть не меньше 2 символов")
    TextInputEditText inpuntEditTextLastName;

    @NotEmpty(message = "Введите почту")
    @Pattern(regex = ".*[@].*", message = "Укажите вашу полную почту, включая символ @")
    @Length(min = 3, message = "В почте должно быть не короче 3 символов")
    TextInputEditText inpuntEditTextEmail;

    @Password(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @Pattern(regex = ".*[A-Z].*", message = "Пароль должен содержать хотя бы одну заглавную букву")
    TextInputEditText inpuntEditTextPassword;

    @NotEmpty(message = "Введите логин")
    @Length(min = 3, message = "В лоигне должно быть не короче 3 символов")
    TextInputEditText inpuntEditTextLogin;

    TextInputEditText inputBio;

    Spinner form_spinnerRole;

    private List<Role> roles;
    private Validator validator;
    private int selectedRoleId;


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
        Intent intent = getIntent();
        if (intent != null) {
            arguments = intent.getExtras();
            if (arguments != null) {
                userId = arguments.getInt("userId");
                mode = arguments.getString("mode");
                Logger.getLogger(UserForm.class.getName()).log(Level.INFO,
                        "Received arguments - userId: " + userId + ", mode: " + mode);
            }
        }
        initializeComponents();
    }

    private void initializeComponents() {

        // Инициализация layout
        layout_form_textFieldFirstName = findViewById(R.id.layout_form_textFieldFirstName);
        layout_form_textFieldLastName = findViewById(R.id.layout_form_textFieldLastName);
        layout_form_textFieldEmail = findViewById(R.id.layout_form_textFieldEmail);
        layout_form_textFieldLogin = findViewById(R.id.layout_form_textFieldLogin);
        layout_form_textFieldPassword = findViewById(R.id.layout_form_textFieldPassword);
        layout_form_textFieldBio = findViewById(R.id.layout_form_textFieldBio);
        layout_form_spinnerRole = findViewById(R.id.layout_form_spinnerRole);

        // Инициализация полей ввода
        inpuntEditTextFirstName = findViewById(R.id.form_textFieldFirstName);
        inpuntEditTextLastName = findViewById(R.id.form_textFieldLastName);
        inpuntEditTextEmail = findViewById(R.id.form_textFieldEmail);
        inpuntEditTextPassword = findViewById(R.id.form_textFieldPassword);
        inpuntEditTextLogin = findViewById(R.id.form_textFieldLogin);
        inputBio = findViewById(R.id.form_textFieldBio);
        form_spinnerRole = findViewById(R.id.form_spinnerRole);

        // Инициализация валидатора
        validator = new Validator(this);
        validator.setValidationListener(this);

        MaterialButton buttonSave = findViewById(R.id.form_buttonSave);
        MaterialButton buttonCancel = findViewById(R.id.form_buttonCancel);
        MaterialButton buttonDelete = findViewById(R.id.form_buttonDelete);
        MaterialButton buttonMenu = findViewById(R.id.btn_menu);

        // Запуск валидации по клику
        buttonSave.setOnClickListener(view -> {

            validator.validate();

        });

        buttonCancel.setOnClickListener(view -> {
            putBack();
        });

        buttonDelete.setOnClickListener(view -> {

            deleteUser();
        });

        buttonMenu.setOnClickListener(view -> {
            putBack();
        });

        // Инициализация списка ролей
        roles = new ArrayList<>();

        // Загрузка ролей с сервера
        loadRoles();
        if (arguments.get("mode") != null && arguments.get("mode").equals("update")) {
            setupEditMode();
        }

    }

    private void fillForm(User user) {
        inpuntEditTextFirstName.setText(user.getFirst_name());
        inpuntEditTextLastName.setText((user.getLast_name()));
        inpuntEditTextEmail.setText(user.getEmail());
        inpuntEditTextPassword.setText(user.getPassword());
        inpuntEditTextLogin.setText(user.getLogin());
        inputBio.setText(user.getBio() != null ? user.getBio() : "Остутсвует");
        form_spinnerRole.setSelection(user.getRole_id() - 1);

    }

    private void setUserInfoById() {
        int userId = arguments.getInt("userId");


        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        UserRequest userRequest = new UserRequest();
        userRequest.setId(userId);

        userApi.getById(userRequest).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();
                    System.out.println(user);
                    fillForm(user); // ← заполняем форму ПОСЛЕ получения данных
                    Logger.getLogger(UserForm.class.getName()).log(Level.INFO,
                            "User data loaded and form filled");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(UserForm.this, "Failed to found user", Toast.LENGTH_SHORT).show();
                Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, "Error occurred", t);
            }
        });


    }

    private void loadRoles() {
        RetrofitService retrofitService = new RetrofitService();
        RoleApi roleApi = retrofitService.getRetrofit().create(RoleApi.class);

        roleApi.getAllRoles().enqueue(new Callback<List<Role>>() {
            @Override
            public void onResponse(Call<List<Role>> call, Response<List<Role>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    roles.clear();
                    roles.addAll(response.body());
                    setupRoleSpinner();
                    Logger.getLogger(UserForm.class.getName()).log(Level.INFO, "Roles loaded: " + roles.size());
                } else {
                    Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, "Failed to load roles");
                }
            }

            @Override
            public void onFailure(Call<List<Role>> call, Throwable t) {
                Toast.makeText(UserForm.this, "Failed to load roles", Toast.LENGTH_SHORT).show();
                Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, "Error occurred", t);
            }
        });
    }

    private void setupRoleSpinner() {
        // Создаем список названий ролей для отображения
        List<String> roleNames = new ArrayList<>();
        for (Role role : roles) {
            roleNames.add(role.getName()); // предполагая, что у Role есть метод getName()
        }

        // Создаем адаптер для Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roleNames
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        form_spinnerRole.setAdapter(adapter);

        // Обработчик выбора роли
        form_spinnerRole.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedRoleId = roles.get(position).getId(); // сохраняем ID выбранной роли
                layout_form_spinnerRole.setError(null); // убираем ошибку при выборе
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedRoleId = 0;
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        clearAllErrors();

        String first_name = inpuntEditTextFirstName.getText().toString().trim();
        String last_name = inpuntEditTextLastName.getText().toString().trim();
        String email = inpuntEditTextEmail.getText().toString().trim();
        String login = inpuntEditTextLogin.getText().toString().trim();
        String password = inpuntEditTextPassword.getText().toString().trim();
        String bio = inputBio.getText().toString().trim();

        User user = new User();
        user.setFirst_name(first_name);
        user.setLast_name(last_name);
        user.setEmail(email);
        user.setPassword(password);
        user.setLogin(login);
        user.setBio(bio);
        user.setRole_id(selectedRoleId); // устанавливаем выбранную роль

        registerUser(user);
        clearFields();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        // Сначала убираем все ошибки
        clearAllErrors();

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Устанавливаем ошибки для соответствующих полей
            if (view.getId() == R.id.form_textFieldFirstName) {
                layout_form_textFieldFirstName.setError(message);
            } else if (view.getId() == R.id.form_textFieldLastName) {
                layout_form_textFieldLastName.setError(message);
            } else if (view.getId() == R.id.form_textFieldEmail) {
                layout_form_textFieldEmail.setError(message); // ← исправлено было layout_form_textFieldLogin
            } else if (view.getId() == R.id.form_textFieldPassword) {
                layout_form_textFieldPassword.setError(message);
            } else if (view.getId() == R.id.form_textFieldLogin) {
                layout_form_textFieldPassword.setError(message);
            } else if (view.getId() == R.id.form_textFieldBio) {
                layout_form_textFieldBio.setError(message);
            } else if (view.getId() == R.id.form_spinnerRole) {
                layout_form_spinnerRole.setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void clearFields(){
        inpuntEditTextFirstName.setText("");
        inpuntEditTextLastName.setText("");
        inpuntEditTextEmail.setText("");
        inpuntEditTextPassword.setText("");
        inpuntEditTextLogin.setText("");
        inputBio.setText("");
        form_spinnerRole.setSelection(1);
    }
    private void clearAllErrors() {
        layout_form_textFieldFirstName.setError(null);
        layout_form_textFieldLastName.setError(null);
        layout_form_textFieldEmail.setError(null);
        layout_form_textFieldPassword.setError(null);
        layout_form_textFieldLogin.setError(null);
        layout_form_textFieldBio.setError(null);
        layout_form_spinnerRole.setError(null);
    }

    private void clearForm(){
        clearAllErrors();
        clearFields();
    }

    public void deleteUser(){
        //действия удаления
    }

    private void setupEditMode(){
        setUserInfoById();
    }

    private void setupCreateMode(){

    }
    private void registerUser(User user){
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
        userApi.register(user)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(response.isSuccessful() && response.body().isSuccess()){
                            Toast.makeText(UserForm.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            clearForm();
                        }else{
                            Toast.makeText(UserForm.this,
                                    response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(UserForm.this, "Server eror", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, "Error occured", t);
                    }
                });
    }

    private void putBack() {
        Intent intent = new Intent(UserForm.this, UserListActivity.class);
        startActivity(intent);
    }
}