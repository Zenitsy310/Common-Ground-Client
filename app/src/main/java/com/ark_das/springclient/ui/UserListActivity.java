package com.ark_das.springclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ark_das.springclient.R;
import com.ark_das.springclient.adapter.UserAdapter;
import com.ark_das.springclient.model.Role;
import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.RoleApi;
import com.ark_das.springclient.retrofit.UserApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private long backPressedTime = 0;
    private List<Role> roles = new ArrayList<>();
    private UserAdapter userAdapter;
    private TextView emptyState;
    private ProgressBar progressBar;
    private ImageButton btn_users, btn_events, btn_chats, btn_profile, btn_settings;
    private boolean rolesLoaded = false;
    private boolean usersLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_user_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupBackPressedCallback();
        setupBottomNavigation();



        // Показываем прогресс бар при старте
        showLoading(true);

        // Сначала создаем адаптер с пустыми списками
        userAdapter = new UserAdapter(new ArrayList<>(), new ArrayList<>());
        recyclerView.setAdapter(userAdapter);

        // Затем загружаем данные
        loadRoles();
        loadUsers();
    }

    private void setupBottomNavigation() {
        ImageButton[] buttons = {btn_users, btn_events, btn_chats, btn_profile, btn_settings};

        btn_users.setOnClickListener(v -> {
            setActive(buttons, btn_users);
            startActivity(new Intent(UserListActivity.this, UserListActivity.class));});
        btn_events.setOnClickListener(v -> {
            setActive(buttons, btn_events);
            startActivity(new Intent(UserListActivity.this, EventListActivity.class));});
        btn_chats.setOnClickListener(v -> setActive(buttons, btn_chats));
        btn_profile.setOnClickListener(v -> setActive(buttons, btn_profile));
        btn_settings.setOnClickListener(v -> setActive(buttons, btn_settings));

        // Значение по умолчанию
        setActive(buttons, btn_users);
    }
    private void setActive(ImageButton[] all, ImageButton selected) {
        for (ImageButton b : all) {
            b.setSelected(false);
        }
        selected.setSelected(true);
    }


    private void initializeViews() {
        recyclerView = findViewById(R.id.userList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyState = findViewById(R.id.emptyState_textView);
        progressBar = findViewById(R.id.progressBar);
        btn_users = findViewById(R.id.nav_users);
        btn_events = findViewById(R.id.nav_events);
        btn_chats = findViewById(R.id.nav_chats);
        btn_profile = findViewById(R.id.nav_profile);
        btn_settings = findViewById(R.id.nav_settings);


        emptyState.setVisibility(View.GONE);

        FloatingActionButton floatingActionButton = findViewById(R.id.userList_fab);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, UserForm.class);
            intent.putExtra("mode", "create");
            startActivity(intent);
        });
    }

    /*private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            switch (itemId) {
                case R.id.nav_home:
                    // Уже на главной
                    return true;

                case R.id.nav_events:
                    // startActivity(new Intent(UserListActivity.this, EventsActivity.class));
                    return true;

                case R.id.nav_create:
                    // startActivity(new Intent(UserListActivity.this, CreateEventActivity.class));
                    return true;

                case R.id.nav_chats:
                    // startActivity(new Intent(UserListActivity.this, ChatsActivity.class));
                    return true;

                case R.id.nav_profile:
                    // startActivity(new Intent(UserListActivity.this, ProfileActivity.class));
                    return true;

                default:
                    return false;
            }
        });
    }*/

    private void setupBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    finishAffinity();
                } else {
                    Toast.makeText(UserListActivity.this,
                            "Нажмите еще раз для выхода из приложения",
                            Toast.LENGTH_SHORT).show();
                    backPressedTime = System.currentTimeMillis();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void loadUsers() {
        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        userApi.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                usersLoaded = true;

                if (response.isSuccessful() && response.body() != null) {
                    // Создаем новый адаптер с обновленными данными
                    userAdapter = new UserAdapter(response.body(), roles);
                    recyclerView.setAdapter(userAdapter);

                    Logger.getLogger(UserListActivity.class.getName()).log(Level.INFO,
                            "Users loaded: " + response.body().size());

                    // Проверяем empty state после загрузки
                    checkEmptyState(response.body());
                } else {
                    Toast.makeText(UserListActivity.this,
                            "Failed to load users: " + response.code(), Toast.LENGTH_SHORT).show();
                    // Показываем empty state при ошибке
                    showEmptyState(true);
                }

                // Скрываем прогресс бар когда оба запроса завершены
                checkAndHideLoading();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                usersLoaded = true;
                Toast.makeText(UserListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                showEmptyState(true);
                checkAndHideLoading();
            }
        });
    }

    private void loadRoles() {
        RetrofitService retrofitService = new RetrofitService();
        RoleApi roleApi = retrofitService.getRetrofit().create(RoleApi.class);

        roleApi.getAllRoles().enqueue(new Callback<List<Role>>() {
            @Override
            public void onResponse(Call<List<Role>> call, Response<List<Role>> response) {
                rolesLoaded = true;

                if (response.isSuccessful() && response.body() != null) {
                    roles.clear();
                    roles.addAll(response.body());
                    Logger.getLogger(UserListActivity.class.getName()).log(Level.INFO,
                            "Roles loaded: " + roles.size());

                    // После загрузки ролей обновляем адаптер
                    if (userAdapter != null) {
                        userAdapter.setRoles(roles);
                    }
                } else {
                    Logger.getLogger(UserListActivity.class.getName()).log(Level.SEVERE,
                            "Failed to load roles: " + response.code());
                }

                // Скрываем прогресс бар когда оба запроса завершены
                checkAndHideLoading();
            }

            @Override
            public void onFailure(Call<List<Role>> call, Throwable t) {
                rolesLoaded = true;
                Toast.makeText(UserListActivity.this, "Failed to load roles", Toast.LENGTH_SHORT).show();
                Logger.getLogger(UserListActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
                checkAndHideLoading();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void checkAndHideLoading() {
        // Скрываем прогресс бар только когда оба запроса завершены
        if (rolesLoaded && usersLoaded) {
            showLoading(false);
        }
    }

    private void checkEmptyState(List<User> users) {
        if (users == null || users.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
        }
    }

    private void showEmptyState(boolean show) {
        if (show) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем только пользователей при возвращении
        // Не показываем прогресс бар при обычном обновлении
        loadUsers();
    }
}