// IBActivityThread.aidl
package top.niunaijun.blackbox.core;

// Declare any non-default types here with import statements

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import java.util.List;
import android.content.pm.ResolveInfo;

interface IBActivityThread {
    IBinder getActivityThread();
    void bindApplication();
    void stopService(in ComponentName componentName);
    void restartJobService(String selfId);
    IBinder acquireContentProviderClient(in ProviderInfo providerInfo);

    IBinder peekService(in Intent intent);
    void finishActivity(IBinder token);
    void handleNewIntent(IBinder token, in Intent intent);
}
