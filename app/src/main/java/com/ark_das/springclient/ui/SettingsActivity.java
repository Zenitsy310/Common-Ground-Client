package com.ark_das.springclient.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ark_das.springclient.data.NotificationDataLoader;
import com.ark_das.springclient.data.NotificationDataSaver;
import com.ark_das.springclient.data.UserDataLoader;
import com.ark_das.springclient.data.UserDataSaver;
import com.ark_das.springclient.dto.UserRequest;
import com.ark_das.springclient.dto.UserResponse;
import com.ark_das.springclient.model.User;
import com.ark_das.springclient.retrofit.RetrofitService;
import com.ark_das.springclient.retrofit.UserApi;
import com.ark_das.springclient.util.LocaleHelper;
import com.ark_das.springclient.R;
import com.ark_das.springclient.base_activity.BaseActivity;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends BaseActivity {

    private ImageView settingsIcon;

    private LinearLayout layoutSwitchLanguage, layout_app_info,
            layout_logout, layout_data_reset, layout_change_password;
    private Spinner spinnerLanguage;
    private TextView currentLanguage;

    private Switch switchNotifications;

    private BottomMenuView bottomMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Обработка системных панелей (статус-бар, навигационная панель)
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.activity_settings),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top,
                            systemBars.right, systemBars.bottom);
                    return insets;
                });

        initViews();
        setupListenets();
        setupLanguageSpinner();
        setupSwitches();
        setupBottomMenu();
    }


    private void initViews() {
        settingsIcon = findViewById(R.id.settings_icon);
        layoutSwitchLanguage = findViewById(R.id.layout_switch_language);
        layout_app_info = findViewById(R.id.layout_app_info);
        layout_logout = findViewById(R.id.layout_logout);
        layout_data_reset = findViewById(R.id.layout_data_reset);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        currentLanguage = findViewById(R.id.current_language);
        switchNotifications = findViewById(R.id.switch_notifications);
        layout_change_password = findViewById(R.id.layout_change_password);

        bottomMenuView = findViewById(R.id.bottomMenuView);
    }

    //Обработчики нажатий
    private void setupListenets() {
        layout_app_info.setOnClickListener(v -> showAppInfoDialog());
        layout_logout.setOnClickListener(v -> confirmLogout());
        layout_data_reset.setOnClickListener(v -> confirmDataReset());
        layout_change_password.setOnClickListener(v -> openChangePasswordDialog());

    }


    public void dataReset(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().clear().apply();
        logout();

    }
    public void confirmDataReset(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выполнить?").setMessage("Вы действительно хотите выполнить это действие?").setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dataReset();
                openLoginForm();
            }
        }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Действие при отмене
                dialog.dismiss(); // Закрываем диалог
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void confirmLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выполнить?").setMessage("Вы действительно хотите выполнить это действие?").setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                logout();
                openLoginForm();
            }
        }).setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Действие при отмене
                dialog.dismiss(); // Закрываем диалог
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void logout() {
        UserDataSaver saver = new UserDataSaver();
        saver.saveUserId(this,-1);
    }

    private void openLoginForm() {
        Intent intent = new Intent(SettingsActivity.this, RegisterForm.class);
        startActivity(intent);
        //aнимация
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void openChangePasswordDialog() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void showAppInfoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_app_info); // твой кастомный layout
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // прозрачные края
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Настройка кнопки закрытия
        Button btnClose = dialog.findViewById(R.id.btnCloseDialog);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }
        // Настройка размера
        int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.85); // 85% ширины экрана
        int height = ViewGroup.LayoutParams.WRAP_CONTENT; // высота по содержимому
        dialog.getWindow().setLayout(width, height);
        dialog.show();
    }


    private void setupLanguageSpinner() {
        String[] languages = {"Русский", "English"};
        String[] codes = {"ru", "en"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                languages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        String currentLang = LocaleHelper.getLanguage(this);
        currentLanguage.setText(currentLang.toUpperCase());

        int selectedIndex = currentLang.equals("en") ? 1 : 0;
        spinnerLanguage.setSelection(selectedIndex, false);

        layoutSwitchLanguage.setOnClickListener(v -> spinnerLanguage.setVisibility(View.VISIBLE));

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCode = codes[position];

                if (!selectedCode.equals(LocaleHelper.getLanguage(SettingsActivity.this))) {
                    // Устанавливаем новый язык
                    LocaleHelper.setLocale(SettingsActivity.this, selectedCode);

                    // Перезапускаем активити без анимации
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    overridePendingTransition(0, 0); // убираем мерцание
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void setupSwitches() {
        switchNotifications.setChecked(getNotificationResolve());
        // Уведомления
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // включено
                setupNotificationResolve(true);
            } else {
                // выключено
                setupNotificationResolve(false);
            }
        });
    }
    private void setupNotificationResolve(boolean resolve){
        NotificationDataSaver saver = new NotificationDataSaver();
        saver.saveNotificationResolve(this,resolve);
    }

    private boolean getNotificationResolve(){
        NotificationDataLoader loader = new NotificationDataLoader();
        return loader.isResolve(this);
    }

    private void setupBottomMenu() {
        bottomMenuView.setActive(R.id.nav_settings);

        bottomMenuView.setOnItemSelectedListener(id -> {
            if (id == R.id.nav_event) {
                startActivity(new Intent(this, EventListActivity.class));
            } else if (id == R.id.nav_user) {
                startActivity(new Intent(this, UserListActivity.class));
            }/* else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatListActivity.class));
            } */else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
            }
        });
    }
    @Override
    public void onConfigurationChanged(@NonNull android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Перерисовываем layout БЕЗ уничтожения Activity
        setContentView(R.layout.activity_settings);

        // заново инициализируем всё
        initViews();
        setupListenets();
        setupLanguageSpinner();
        setupSwitches();
        setupBottomMenu();
    }
}
