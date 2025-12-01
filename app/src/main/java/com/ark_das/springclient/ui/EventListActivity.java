package com.ark_das.springclient.ui;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.R;

public class EventListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_event_list);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_event_list), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });
        //Вынести в отдельную функции для инициализации компонентов
        BottomMenuView bottomMenuView = findViewById(R.id.bottomMenuView);
        bottomMenuView.setActive(R.id.nav_event);
        bottomMenuView.setOnItemSelectedListener(id -> {
            if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserListActivity.class));
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, UserListActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, UserListActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, UserListActivity.class));
            }
        });
    }
}
