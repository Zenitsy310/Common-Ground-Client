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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private long backPressedTime = 0;

    private List<Role> roles;

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

        recyclerView = findViewById(R.id.userList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton floatingActionButton = findViewById(R.id.userList_fab);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, UserForm.class);
            startActivity(intent);
        });
        setupBackPressedCallback();
        loadRoles();
        loadUsers();

    }
    private void setupBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Двойное нажатие для выхода из приложения
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    // Выйти из приложения
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
        userApi.getAllUsers()
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        populateListView(response.body(), roles);
                        Logger.getLogger(UserListActivity.class.getName()).log(Level.SEVERE, "Error occured");
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Toast.makeText(UserListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(UserListActivity.class.getName()).log(Level.SEVERE, "Error occured", t);
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
                    try {
                        roles.clear();
                    }catch (Exception e){
                        Logger.getLogger(UserListActivity.class.getName()).log(Level.INFO, "null");
                    }
                    roles.addAll(response.body());
                    Logger.getLogger(UserListActivity.class.getName()).log(Level.INFO, "Roles loaded: " + roles.size());
                } else {
                    Logger.getLogger(UserListActivity.class.getName()).log(Level.SEVERE, "Failed to load roles");
                }
            }

            @Override
            public void onFailure(Call<List<Role>> call, Throwable t) {
                Toast.makeText(UserListActivity.this, "Failed to load roles", Toast.LENGTH_SHORT).show();
                Logger.getLogger(UserForm.class.getName()).log(Level.SEVERE, "Error occurred", t);
            }
        });
    }

    private void populateListView(List<User> userList, List<Role> roles) {
        UserAdapter userAdapter = new UserAdapter(userList, roles);
        recyclerView.setAdapter(userAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadUsers();
    }


}