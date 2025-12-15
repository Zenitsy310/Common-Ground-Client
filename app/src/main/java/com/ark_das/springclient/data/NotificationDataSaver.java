package com.ark_das.springclient.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ark_das.springclient.util.PrefsConstants;

public class NotificationDataSaver {
    private SharedPreferences getPrefs(Context context) {
        // Mode Private означает, что доступ к этому файлу будет иметь только ваше приложение.
        return context.getSharedPreferences(PrefsConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    // 2. Метод для сохранения данных
    public void saveNotificationResolve(Context context, boolean resolve) {
        SharedPreferences prefs = getPrefs(context);

        // Получаем объект Editor для начала изменений
        SharedPreferences.Editor editor = prefs.edit();

        // 3. Добавляем данные по ключам
        editor.putBoolean(PrefsConstants.KEY_NOTIFICATION, resolve);

        // 4. Применяем изменения (записываем на диск)
        // apply() - асинхронно (рекомендуется), commit() - синхронно
        editor.apply();
    }

}
