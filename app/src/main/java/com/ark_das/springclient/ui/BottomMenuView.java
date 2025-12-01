package com.ark_das.springclient.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ark_das.springclient.R;

public class BottomMenuView extends LinearLayout {

    private FrameLayout navUser, navEvent, navChat, navProfile, navSettings;
    private int activeId = -1; // текущая активная кнопка
    private OnItemSelectedListener listener;

    public BottomMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //if (isInEditMode()) return; // Preview пропустит код

        inflate(context, R.layout.bottom_nav_menu, this);

        navUser = findViewById(R.id.nav_user);
        navEvent = findViewById(R.id.nav_event);
        navChat = findViewById(R.id.nav_chat);
        navProfile = findViewById(R.id.nav_profile);
        navSettings = findViewById(R.id.nav_settings);

        FrameLayout[] buttons = {navUser, navEvent, navChat, navProfile, navSettings};

        for (FrameLayout btn : buttons) {
            btn.setOnClickListener(v -> {
                setActive(v.getId()); // подсветка
                if (listener != null) {
                    listener.onItemSelected(v.getId()); // внешний callback
                }
            });
        }
    }

    public void setActive(int id) {
        // Сброс цвета всех кнопок
        FrameLayout[] buttons = {navUser, navEvent, navChat, navProfile, navSettings};
        for (FrameLayout btn : buttons) {
            ImageView icon = (ImageView) btn.getChildAt(0);
            icon.setColorFilter(getResources().getColor(R.color.black));
        }

        // Подсветка активной кнопки
        FrameLayout activeBtn = findViewById(id);
        ImageView activeIcon = (ImageView) activeBtn.getChildAt(0);
        activeIcon.setColorFilter(getResources().getColor(R.color.mainPurple));

        activeId = id;
    }


    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int id);
    }
}
