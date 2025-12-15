package com.ark_das.springclient.base_activity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ark_das.springclient.util.LocaleHelper;
import com.ark_das.springclient.util.ThemeHelper;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocaleHelper.getLanguage(newBase);
        Context context = LocaleHelper.updateResources(newBase, lang);
        super.attachBaseContext(context);
    }
}
