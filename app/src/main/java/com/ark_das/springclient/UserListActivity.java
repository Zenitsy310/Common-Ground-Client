package com.ark_das.springclient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<Role> roles = new ArrayList<>(); // ← ИНИЦИАЛИЗИРОВАН ПУСТОЙ СПИСОК
    private UserAdapter userAdapter; // ← ДОБАВЛЕНО

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

        // Сначала создаем адаптер с пустыми списками
        userAdapter = new UserAdapter(new ArrayList<>(), new ArrayList<>());
        recyclerView.setAdapter(userAdapter);

        // Затем загружаем данные
        loadRoles();
        loadUsers();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.userList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton floatingActionButton = findViewById(R.id.userList_fab);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, UserForm.class);
            intent.putExtra("mode", "create");
            startActivity(intent);
        });
    }

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
                if (response.isSuccessful() && response.body() != null) {
                    // Создаем новый адаптер с обновленными данными
                    userAdapter = new UserAdapter(response.body(), roles);
                    recyclerView.setAdapter(userAdapter);

                    Logger.getLogger(UserListActivity.class.getName()).log(Level.INFO,
                            "Users loaded: " + response.body().size());
                } else {
                    Toast.makeText(UserListActivity.this,
                            "Failed to load users: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(UserListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onFailure(Call<List<Role>> call, Throwable t) {
                Toast.makeText(UserListActivity.this, "Failed to load roles", Toast.LENGTH_SHORT).show();
                Logger.getLogger(UserListActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем только пользователей при возвращении
        loadUsers();
    }
}