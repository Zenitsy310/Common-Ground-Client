package com.ark_das.springclient.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ark_das.springclient.util.PrefsConstants;

public class NotificationDataLoader {

    private SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PrefsConstants.PREF_NAME, Context.MODE_PRIVATE);
    }


    public boolean isResolve(Context context) {
        SharedPreferences prefs = getPrefs(context);
        return prefs.getBoolean(PrefsConstants.KEY_NOTIFICATION, true);
    }


}

