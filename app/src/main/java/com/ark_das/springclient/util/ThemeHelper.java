package com.ark_das.springclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {

    private static final String SELECTED_THEME = "selected_theme";

    // Режимы для ночной темы
    public static final int MODE_NIGHT_YES = 1;
    public static final int MODE_NIGHT_NO = 2;
    public static final int MODE_NIGHT_FOLLOW_SYSTEM = 3;

    // Сохраняем выбор темы в SharedPreferences
    public static void setSelectedTheme(Context context, int mode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt(SELECTED_THEME, mode).apply();

        // Применяем выбранный режим
        switch (mode) {
            case MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_NIGHT_FOLLOW_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    // Получаем сохраненный режим темы
    public static int getSelectedTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(SELECTED_THEME, MODE_NIGHT_FOLLOW_SYSTEM); // по умолчанию используем системную тему
    }
}
