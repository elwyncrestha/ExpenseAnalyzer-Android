package com.elvin.expense_analyzer.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
public class NotificationChannelUtils {
    public static final String CHANNEL_1 = "Channel 1";
    Context context;

    public NotificationChannelUtils(Context context) {
        this.context = context;
    }

    public void create() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1,
                    CHANNEL_1,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is channel 1");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
