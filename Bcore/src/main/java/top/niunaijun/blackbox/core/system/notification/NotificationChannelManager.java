package top.niunaijun.blackbox.core.system.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import top.niunaijun.blackbox.BlackBoxCore;

/**
 * Created by BlackBox on 2022/3/18.
 */
public class NotificationChannelManager {
    private final static NotificationChannelManager sManager = new NotificationChannelManager();

    public static NotificationChannel APP_CHANNEL;

    public static NotificationChannelManager get() {
        return sManager;
    }

    public NotificationChannelManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerAppChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerAppChannel() {
        NotificationManager nm = (NotificationManager) BlackBoxCore.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ONE_ID = BlackBoxCore.getContext().getPackageName();
        String CHANNEL_ONE_NAME = "black-box-app";
        APP_CHANNEL = new NotificationChannel(CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
        APP_CHANNEL.enableLights(true);
        APP_CHANNEL.setLightColor(Color.RED);
        APP_CHANNEL.setShowBadge(true);
        APP_CHANNEL.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        nm.createNotificationChannel(APP_CHANNEL);
    }
}
