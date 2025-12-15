package com.ark_das.springclient.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ark_das.springclient.R;

public class NotificationWorker extends Worker {

    public static final String CHANNEL_ID = "notify_channel";

    public NotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params
    ) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        String title = getInputData().getString("title");
        String content = getInputData().getString("content");

        if (title == null) title = "Уведомление";
        if (content == null) content = "Сообщение";

        createNotificationChannel();
        showNotification(title, content);

        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Основные уведомления",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Уведомления приложения");

            NotificationManager manager =
                    getApplicationContext().getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String content) {
        Context context = getApplicationContext();

        Notification notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify((int) System.currentTimeMillis(), notification);
    }
}
