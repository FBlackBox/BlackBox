// IBNotificationManagerService.aidl
package top.niunaijun.blackbox.core.system.notification;

// Declare any non-default types here with import statements
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;

interface IBNotificationManagerService {

    NotificationChannel getNotificationChannel(String channelId, int userId);

    List<NotificationChannel> getNotificationChannels(String packageName, int userId);

    List<NotificationChannelGroup> getNotificationChannelGroups(String packageName, int userId);

    void createNotificationChannel(in NotificationChannel notificationChannel, int userId);

    void deleteNotificationChannel(String channelId, int userId);

    void createNotificationChannelGroup(in NotificationChannelGroup notificationChannelGroup, int userId);

    void deleteNotificationChannelGroup(String groupId, int userId);

    void enqueueNotificationWithTag(int id, String tag, in Notification notification, int userId);

    void cancelNotificationWithTag(int id, String tag, int userId);
}