package com.ark_das.springclient.ui;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.ark_das.springclient.R;
import com.ark_das.springclient.base_activity.BaseActivity;
import com.ark_das.springclient.data.UserDataLoader;
import com.ark_das.springclient.data.UserDataSaver;
import com.ark_das.springclient.notification.NotificationWorker;
import com.ark_das.springclient.util.PrefsConstants;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {

    // Константы времени
    private static final int SPLASH_DURATION = 2500; // 2.5 секунды
    private static final int DOTS_ANIMATION_INTERVAL = 500; // 0.5 секунды
    private static final int LOGO_ANIMATION_DURATION = 800;
    private static final int TEXT_APPEAR_DELAY = 400;


    //данные юзера
    private int savedId = -1;
    private String savedRole = "User";

    // UI элементы
    private MaterialCardView logoCard;
    private ShapeableImageView logoImage;
    private TextView appTitle;
    private TextView appSubtitle;
    private CircularProgressIndicator loadingProgress;
    private TextView loadingText;
    private TextView versionText;
    private View decorBackground;

    // Анимационные обработчики
    private Handler animationHandler;
    private int loadingDotsCount = 0;
    private Runnable dotsAnimationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Включаем EdgeToEdge для полноэкранного режима
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Настройка отступов системных баров
        setupSystemBarsPadding();

        // Инициализация всех View элементов
        initializeViews();

        // Настройка статических данных
        setupStaticContent();

        // Запуск анимационной последовательности
        startUiAnimations();

        // Запланировать переход на следующий экран
        scheduleNextScreenNavigation();

        //sendNotificationAfterTime(this);

        //attachBaseContext();
    }


    /**
     * Настройка отступов для системных баров
     */
    private void setupSystemBarsPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.activity_main),
                (view, windowInsets) -> {
                    Insets systemBars = windowInsets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );
                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );
                    return windowInsets;
                }
        );
    }

    /**
     * Инициализация всех View элементов из разметки
     */
    private void initializeViews() {
        logoCard = findViewById(R.id.logo_card);
        logoImage = findViewById(R.id.logo_image);
        appTitle = findViewById(R.id.app_title);
        appSubtitle = findViewById(R.id.app_subtitle);
        loadingProgress = findViewById(R.id.loading_progress);
        loadingText = findViewById(R.id.loading_text);
        versionText = findViewById(R.id.version_text);
        decorBackground = findViewById(R.id.decor_background);

        // Инициализируем Handler для анимаций
        animationHandler = new Handler();
    }

    private void loadUser(){
        UserDataLoader loader = new UserDataLoader();

        savedId = loader.loadUserId(this);
        savedRole = loader.getUserRole(this);

        /*String message = String.format("Пользователь: %d, Роль: %s",
                savedId, savedRole);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();*/
    }

    private void setupStaticContent() {
        try {
            // Установка версии приложения из манифеста
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .versionName;
            versionText.setText(String.format("v%s", versionName));
        } catch (Exception e) {
            // Fallback версия
            versionText.setText("v1.0.0");
        }
    }


    /**
     * Запуск всех UI анимаций
     */
    private void startUiAnimations() {
        // 1. Анимация фонового декоративного элемента
        animateBackgroundElement();

        // 2. Анимация логотипа (масштаб + прозрачность)
        animateLogoReveal();

        // 3. Анимация появления текстов
        animateTextReveal();

        // 4. Анимация точек загрузки
        startLoadingDotsAnimation();

        // 5. Случайные микро-анимации для живости
        addRandomMicroAnimations();
    }

    /**
     * Анимация фонового элемента
     */
    private void animateBackgroundElement() {
        AlphaAnimation backgroundFadeIn = new AlphaAnimation(0f, 0.05f);
        backgroundFadeIn.setDuration(1000);
        backgroundFadeIn.setStartOffset(200);
        decorBackground.startAnimation(backgroundFadeIn);
    }

    /**
     * Анимация появления логотипа
     */
    private void animateLogoReveal() {
        // Начальное состояние (скрыто и уменьшено)
        logoCard.setScaleX(0.7f);
        logoCard.setScaleY(0.7f);
        logoCard.setAlpha(0f);
        logoCard.setRotation(-10f);

        // Комплексная анимация
        logoCard.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .rotation(0f)
                .setDuration(LOGO_ANIMATION_DURATION)
                .setStartDelay(300)
                .withEndAction(() -> {
                    // Легкая пульсация после появления
                    startLogoPulseAnimation();
                })
                .start();
    }

    /**
     * Легкая пульсация логотипа
     */
    private void startLogoPulseAnimation() {
        ScaleAnimation pulse = new ScaleAnimation(
                1f, 1.03f, // X scale
                1f, 1.03f, // Y scale
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        pulse.setDuration(1000);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(Animation.INFINITE);
        logoCard.startAnimation(pulse);
    }

    /**
     * Анимация появления текстовых элементов
     */
    private void animateTextReveal() {
        // Анимация заголовка
        AlphaAnimation titleAnimation = new AlphaAnimation(0f, 1f);
        titleAnimation.setDuration(600);
        titleAnimation.setStartOffset(TEXT_APPEAR_DELAY);
        appTitle.startAnimation(titleAnimation);

        // Анимация подзаголовка с задержкой
        AlphaAnimation subtitleAnimation = new AlphaAnimation(0f, 1f);
        subtitleAnimation.setDuration(600);
        subtitleAnimation.setStartOffset(TEXT_APPEAR_DELAY + 200);
        appSubtitle.startAnimation(subtitleAnimation);

        // Анимация версии (самая последняя)
        AlphaAnimation versionAnimation = new AlphaAnimation(0f, 0.7f);
        versionAnimation.setDuration(400);
        versionAnimation.setStartOffset(TEXT_APPEAR_DELAY + 400);
        versionText.startAnimation(versionAnimation);
    }

    /**
     * Анимация точек в тексте "Загрузка..."
     */
    private void startLoadingDotsAnimation() {
        dotsAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                loadingDotsCount = (loadingDotsCount + 1) % 4;

                // Создаем строку с точками
                StringBuilder dotsBuilder = new StringBuilder(getString(R.string.loading));
                for (int i = 0; i < loadingDotsCount; i++) {
                    dotsBuilder.append(".");
                }

                loadingText.setText(dotsBuilder.toString());

                // Планируем следующий кадр анимации
                animationHandler.postDelayed(this, DOTS_ANIMATION_INTERVAL);
            }
        };

        // Запускаем анимацию
        animationHandler.postDelayed(dotsAnimationRunnable, DOTS_ANIMATION_INTERVAL);
    }

    /**
     * Случайные микро-анимации для оживления интерфейса
     */
    private void addRandomMicroAnimations() {
        // Легкое изменение прозрачности подзаголовка
        Animation subtitlePulse = new AlphaAnimation(0.8f, 1f);
        subtitlePulse.setDuration(2000);
        subtitlePulse.setRepeatMode(Animation.REVERSE);
        subtitlePulse.setRepeatCount(Animation.INFINITE);
        appSubtitle.startAnimation(subtitlePulse);

        // Изменение цвета прогресс-бара
        new Handler().postDelayed(() -> {
            // Можно добавить изменение цвета через 1 секунду
            // loadingProgress.setIndicatorColor(getColor(R.color.lightPurple));
        }, 1000);
    }

    /**
     * Планирование перехода на следующий экран
     */
    private void scheduleNextScreenNavigation() {
        new Handler().postDelayed(() -> {
            // Здесь будет ваша логика навигации
            navigateToNextScreen();
        }, SPLASH_DURATION);
    }

    /**
     * Переход на следующий экран (заглушка для вашей реализации)
     */
    private void navigateToNextScreen() {
        // TODO: Реализуйте вашу логику навигации
        //saveUser();
        loadUser();
        if(isNewUser()){
            //Toast.makeText(this, "UserId: " + savedId, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UserListActivity.class);
            startActivity(intent);
        }else{
            //Toast.makeText(this, "New user", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, RegisterForm.class);
            startActivity(intent);
        }
        // Пример структуры:
        // 1. Проверить авторизацию
        // 2. Определить роль пользователя
        // 3. Перейти на соответствующий экран
        // 4. Применить анимацию перехода
    }
    private boolean isNewUser(){
        return savedId != -1;
    }

    /**
     * Очистка ресурсов при уничтожении Activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Останавливаем все анимации
        if (animationHandler != null && dotsAnimationRunnable != null) {
            animationHandler.removeCallbacks(dotsAnimationRunnable);
        }

        // Очищаем анимации с View
        logoCard.clearAnimation();
        appTitle.clearAnimation();
        appSubtitle.clearAnimation();
        loadingText.clearAnimation();
        decorBackground.clearAnimation();
    }
    /*private void saveUser(){
        UserDataSaver saver = new UserDataSaver();
        saver.saveUser(this,-1, "role");
    }*/
}