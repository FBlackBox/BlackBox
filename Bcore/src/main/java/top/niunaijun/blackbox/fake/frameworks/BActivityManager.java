package top.niunaijun.blackbox.fake.frameworks;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.am.IBActivityManagerService;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.UnbindRecord;
import top.niunaijun.blackbox.entity.am.PendingResultData;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;

/**
 * Created by Milk on 4/14/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BActivityManager extends BlackManager<IBActivityManagerService> {
    private static final BActivityManager sActivityManager = new BActivityManager();

    public static BActivityManager get() {
        return sActivityManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.ACTIVITY_MANAGER;
    }

    public AppConfig initProcess(String packageName, String processName, int userId) {
        try {
            return getService().initProcess(packageName, processName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void restartProcess(String packageName, String processName, int userId) {
        try {
            getService().restartProcess(packageName, processName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Intent intent, int userId) {
        try {
            getService().startActivity(intent, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public int startActivityAms(int userId, Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags, Bundle options) {
        try {
            return getService().startActivityAms(userId, intent, resolvedType, resultTo, resultWho, requestCode, flags, options);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int startActivities(int userId, Intent[] intent, String[] resolvedType, IBinder resultTo, Bundle options) {
        try {
            return getService().startActivities(userId, intent, resolvedType, resultTo, options);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ComponentName startService(Intent intent, String resolvedType, boolean requireForeground, int userId) {
        try {
            return getService().startService(intent, resolvedType, requireForeground, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int stopService(Intent intent, String resolvedType, int userId) {
        try {
            return getService().stopService(intent, resolvedType, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Intent bindService(Intent service, IBinder binder, String resolvedType, int userId) {
        try {
            return getService().bindService(service, binder, resolvedType, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unbindService(IBinder binder, int userId) {
        try {
            getService().unbindService(binder, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopServiceToken(ComponentName componentName, IBinder token, int userId) {
        try {
            getService().stopServiceToken(componentName, token, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onStartCommand(Intent proxyIntent, int userId) {
        try {
            getService().onStartCommand(proxyIntent, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public UnbindRecord onServiceUnbind(Intent proxyIntent, int userId) {
        try {
            return getService().onServiceUnbind(proxyIntent, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onServiceDestroy(Intent proxyIntent, int userId) {
        try {
            getService().onServiceDestroy(proxyIntent, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public IBinder acquireContentProviderClient(ProviderInfo providerInfo) {
        try {
            return getService().acquireContentProviderClient(providerInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Intent sendBroadcast(Intent intent, String resolvedType, int userId) {
        try {
            return getService().sendBroadcast(intent, resolvedType, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IBinder peekService(Intent intent, String resolvedType, int userId) {
        try {
            return getService().peekService(intent, resolvedType, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onActivityCreated(int taskId, IBinder token, IBinder activityRecord) {
        try {
            getService().onActivityCreated(taskId, token, activityRecord);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResumed(IBinder token) {
        try {
            // Fix https://github.com/FBlackBox/BlackBox/issues/28
            if ("com.tencent.mm".equals(BActivityThread.getAppPackageName())) {
                Activity activityByToken = BActivityThread.getActivityByToken(token);
                if (activityByToken != null) {
                    activityByToken.getWindow().getDecorView().clearFocus();
                }
            }
        } catch (Throwable ignored) {
        }
        try {
            getService().onActivityResumed(token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onActivityDestroyed(IBinder token) {
        try {
            getService().onActivityDestroyed(token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onFinishActivity(IBinder token) {
        try {
            getService().onFinishActivity(token);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public RunningAppProcessInfo getRunningAppProcesses(String callerPackage, int userId) throws RemoteException {
        try {
            return getService().getRunningAppProcesses(callerPackage, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RunningServiceInfo getRunningServices(String callerPackage, int userId) throws RemoteException {
        try {
            return getService().getRunningServices(callerPackage, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void scheduleBroadcastReceiver(Intent intent, PendingResultData pendingResultData, int userId) throws RemoteException {
        getService().scheduleBroadcastReceiver(intent, pendingResultData, userId);
    }

    public void finishBroadcast(PendingResultData data) {
        try {
            getService().finishBroadcast(data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getCallingPackage(IBinder token, int userId) {
        try {
            return getService().getCallingPackage(token, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ComponentName getCallingActivity(IBinder token, int userId) {
        try {
            return getService().getCallingActivity(token, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getIntentSender(IBinder target, String packageName, int uid) {
        try {
            getService().getIntentSender(target, packageName, uid, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getPackageForIntentSender(IBinder target) {
        try {
            return getService().getPackageForIntentSender(target, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUidForIntentSender(IBinder target) {
        try {
            return getService().getUidForIntentSender(target, BActivityThread.getUserId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
