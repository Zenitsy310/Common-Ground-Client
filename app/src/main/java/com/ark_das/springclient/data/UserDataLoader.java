package com.ark_das.springclient.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ark_das.springclient.util.PrefsConstants;

public class UserDataLoader {

    private SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PrefsConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    // Метод для загрузки данных
    public int loadUserId(Context context) {
        SharedPreferences prefs = getPrefs(context);

        return prefs.getInt(PrefsConstants.KEY_USER_ID, 0);
    }

    public String getUserRole(Context context) {
        SharedPreferences prefs = getPrefs(context);
        return prefs.getString(PrefsConstants.KEY_ROLE, "admin");
    }
}