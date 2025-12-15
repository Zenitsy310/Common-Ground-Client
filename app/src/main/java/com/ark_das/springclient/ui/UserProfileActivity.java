package com.ark_das.springclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.R;
import com.ark_das.springclient.base_activity.BaseActivity;
import com.ark_das.springclient.data.UserDataLoader;
import com.ark_das.springclient.dto.UserRequest;
import com.ark_das.springclient.dto.UserResponse;
import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends BaseActivity {

    // ===== Toolbar =====
    private TextView tvTitle;
    private TextView tvSubtitle;
    private ImageButton btnHeaderEdit;

    // ===== Avatar =====
    private ShapeableImageView user_avatar;

    // ===== Profile info =====
    private TextView inputFirstName;
    private TextView inputLastName;
    private TextView inputEmail;
    private TextView inputBio;
    private TextView inputRegisterDate;

    //private Spinner spinner;
    // ===== Actions =====
    private MaterialButton btnEditProfile;

    // ===== Bottom menu =====
    private BottomMenuView bottomMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.activity_profile),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        initViews();
        setupListeners();
        setupBottomMenu();
        setupUserInfo();
    }

    private void initViews() {
        // Toolbar
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        btnHeaderEdit = findViewById(R.id.btnHeaderEdit);

        // Avatar
        user_avatar = findViewById(R.id.user_avatar);

        // Profile info
        inputFirstName    = findViewById(R.id.form_textFieldFirstName);
        inputLastName     = findViewById(R.id.form_textFieldLastName);
        inputEmail        = findViewById(R.id.form_textFieldEmail);
        inputBio          = findViewById(R.id.form_textFieldBio);
        inputRegisterDate = findViewById(R.id.form_textFieldRegisterDate);

        //spinner = findViewById(R.id.spinnerLanguage);
        // Actions
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // Bottom menu
        bottomMenuView = findViewById(R.id.bottomMenuView);
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );

        btnHeaderEdit.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );
    }


    private void setupBottomMenu() {
        bottomMenuView.setActive(R.id.nav_profile);
        bottomMenuView.setOnItemSelectedListener(id -> {
            if (id == R.id.nav_event) {
                startActivity(new Intent(this, EventListActivity.class));
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserListActivity.class));
            }/* else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatListActivity.class));
            } */else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
        });
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
                    Logger.getLogger(UserProfileActivity.class.getName()).log(Level.INFO,
                            "User data loaded and form filled");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Failed to found user", Toast.LENGTH_SHORT).show();
                Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, "Error occurred", t);
            }
        });
    }

    private int getCurentUsersId(){
        UserDataLoader loader = new UserDataLoader();
        return loader.loadUserId(this);
    }

    private void fillForm(User user) {
        inputFirstName.setText(user.getFirst_name());
        inputLastName.setText(user.getLast_name());
        inputEmail.setText(user.getEmail());
        inputBio.setText(user.getBio() != null ? user.getBio() : "Отсутствует");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = user.getCreated_at().format(formatter);
        inputRegisterDate.setText(formattedDate);
        // ivAvatar.setImage... // сюда загрузку аватара вставишь сам
    }
}