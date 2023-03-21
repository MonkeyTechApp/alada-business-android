package com.poupock.feussom.aladabusiness.core;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.poupock.feussom.aladabusiness.R;

public class CoreApp extends Application {
    public static final String CHANNEL_ID = CoreApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
