package com.ark_das.springclient.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ark_das.springclient.ui.RegisterForm;
import com.ark_das.springclient.util.PrefsConstants;

public class UserDataSaver {
    // 1. Получаем экземпляр SharedPreferences
    // Context нужен для доступа к файловой системе приложения.
    private SharedPreferences getPrefs(Context context) {
        // Mode Private означает, что доступ к этому файлу будет иметь только ваше приложение.
        return context.getSharedPreferences(PrefsConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    // 2. Метод для сохранения данных
    public void saveUser(Context context,  int userId, String role) {
        SharedPreferences prefs = getPrefs(context);

        // Получаем объект Editor для начала изменений
        SharedPreferences.Editor editor = prefs.edit();

        // 3. Добавляем данные по ключам
        editor.putInt(PrefsConstants.KEY_USER_ID, userId);               // Целое число
        editor.putString(PrefsConstants.KEY_ROLE, role); // Строка

        // 4. Применяем изменения (записываем на диск)
        // apply() - асинхронно (рекомендуется), commit() - синхронно
        editor.apply();
    }

    public void saveUserRoleName(Context context, String role) {
        SharedPreferences prefs = getPrefs(context);

        // Получаем объект Editor для начала изменений
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PrefsConstants.KEY_ROLE, role); // Строка
        editor.apply();
    }

    public void saveUserId(Context context, int id) {
        SharedPreferences prefs = getPrefs(context);

        // Получаем объект Editor для начала изменений
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PrefsConstants.KEY_USER_ID, id); // Строка
        editor.apply();
    }
}
