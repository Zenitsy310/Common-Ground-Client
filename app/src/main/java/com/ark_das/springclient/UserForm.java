package com.ark_das.springclient;

import android.os.Bundle;
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

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         initializeComponents();
        }

    private void initializeComponents(){
        TextInputEditText inpuntEditTextFirstName = findViewById(R.id.form_textFieldFirstName);
        TextInputEditText inpuntEditTextLastName = findViewById(R.id.form_textFieldLastName);
        TextInputEditText inpuntEditTextEmail = findViewById(R.id.form_textFieldEmail);
        TextInputEditText inpuntEditTextLogin = findViewById(R.id.form_textFieldLogin);
        TextInputEditText inpuntEditTextPassword = findViewById(R.id.form_textFieldPasswordHash);

        MaterialButton buttonSave = findViewById(R.id.form_buttonSave);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        buttonSave.setOnClickListener(view -> {

            String first_name = String.valueOf(inpuntEditTextFirstName.getText());
            String last_name = String.valueOf(inpuntEditTextLastName.getText());
            String email = String.valueOf(inpuntEditTextEmail.getText());
            String login = String.valueOf(inpuntEditTextLogin.getText());
            String password = String.valueOf(inpuntEditTextPassword.getText());

            User user = new User();
            user.setFirst_name(first_name);
            user.setLast_name(last_name);
            user.setEmail(email);
            user.setLogin(login);
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
        });
    }
}