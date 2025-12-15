package com.ark_das.springclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.R;
import com.ark_das.springclient.base_activity.BaseActivity;

public class ChatListActivity extends BaseActivity {

    private BottomMenuView bottomMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_chat_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupBottomMenu();
    }

    private void setupBottomMenu() {
        bottomMenuView.setActive(R.id.nav_settings);

        bottomMenuView.setOnItemSelectedListener(id -> {
            if (id == R.id.nav_event) {
                startActivity(new Intent(this, EventListActivity.class));
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserListActivity.class));
            } /*else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatListActivity.class));
            }*/ else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
            }
        });
    }
}
