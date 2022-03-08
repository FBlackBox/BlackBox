// IBActivityManagerService.aidl
package top.niunaijun.blackbox.core.system.am;

import android.content.Intent;
import android.content.ComponentName;
import android.content.pm.ServiceInfo;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import java.lang.String;
import android.app.IServiceConnection;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.UnbindRecord;
import android.os.Bundle;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.PendingResultData;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;

// Declare any non-default types here with import statements

interface IBActivityManagerService {
    AppConfig initProcess(String packageName, String processName, int userId);
    void restartProcess(String packageName, String processName, int userId);

    void startActivity(in Intent intent, int userId);
    int startActivityAms(int userId, in Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, in Bundle options);
    int startActivities(int userId, in Intent[] intent, in String[] resolvedType, IBinder resultTo, in Bundle options);

    ComponentName startService(in Intent intent, String resolvedType, boolean requireForeground, int userId);
    int stopService(in Intent intent,in String resolvedType, int userId);

    Intent bindService(in Intent service, in IBinder binder, String resolvedType, int userId);
    void unbindService(in IBinder binder, int userId);

    void stopServiceToken(in ComponentName className, in IBinder token, int userId);

    void onStartCommand(in Intent proxyIntent, int userId);
    UnbindRecord onServiceUnbind(in Intent proxyIntent, int userId);
    void onServiceDestroy(in Intent proxyIntent, int userId);

    IBinder acquireContentProviderClient(in ProviderInfo providerInfo);
    Intent sendBroadcast(in Intent intent, String resolvedType, int userId);
    IBinder peekService(in Intent intent, String resolvedType, int userId);

    void onActivityCreated(int taskId, IBinder token, IBinder activityRecord);
    void onActivityResumed(IBinder token);
    void onActivityDestroyed(IBinder token);
    void onFinishActivity(IBinder token);

    RunningAppProcessInfo getRunningAppProcesses(String callerPackage, int userId);
    RunningServiceInfo getRunningServices(String callerPackage, int userId);

    void scheduleBroadcastReceiver(in Intent intent, in PendingResultData pendingResultData, int userId);
    void finishBroadcast(in PendingResultData data);

    String getCallingPackage(in IBinder token, int userId);
    ComponentName getCallingActivity(in IBinder token, int userId);

    void getIntentSender(in IBinder target, String packageName, int uid, int userId);
    String getPackageForIntentSender(in IBinder target, int userId);
    int getUidForIntentSender(in IBinder target, int userId);
}
