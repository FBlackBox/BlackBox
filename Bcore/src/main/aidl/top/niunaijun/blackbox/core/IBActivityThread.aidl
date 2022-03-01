// IBActivityThread.aidl
package top.niunaijun.blackbox.core;

// Declare any non-default types here with import statements

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.content.pm.ActivityInfo;
import top.niunaijun.blackbox.entity.am.ReceiverData;

interface IBActivityThread {
    IBinder getActivityThread();
    void bindApplication();
    void restartJobService(String selfId);
    IBinder acquireContentProviderClient(in ProviderInfo providerInfo);

    IBinder peekService(in Intent intent);
    void stopService(in Intent componentName);

    void finishActivity(IBinder token);
    void handleNewIntent(IBinder token, in Intent intent);

    void scheduleReceiver(in ReceiverData data);
}
