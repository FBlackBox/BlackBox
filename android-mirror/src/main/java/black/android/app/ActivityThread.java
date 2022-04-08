package black.android.app;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;

import java.util.List;
import java.util.Map;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BParamClassName;
import top.niunaijun.blackreflection.annotation.BStaticField;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

@BClassName("android.app.ActivityThread")
public interface ActivityThread {
    @BField
    Object mAppThread();

    @BStaticField
    IInterface sPackageManager();

    @BStaticField
    IInterface sPermissionManager();

    @BField
    Map<IBinder, Object> mActivities();

    @BField
    Object mBoundApplication();

    @BField
    Handler mH();

    @BField
    Application mInitialApplication();

    @BField
    Instrumentation mInstrumentation();

    @BField
    Map<String, java.lang.ref.WeakReference<?>> mPackages();

    @BField
    Map<?, ?> mProviderMap();

    @BField
    Map<?, ?> mLocalProvidersByName();

    @BStaticMethod
    Object currentActivityThread();

    @BStaticMethod
    Application currentApplication();

    @BStaticMethod
    String currentPackageName();

    @BMethod
    IBinder getApplicationThread();

    @BMethod
    Handler getHandler();

    @BMethod
    String getProcessName();

    @BMethod
    Object getSystemContext();

    @BMethod
    Object getActivityClient(IBinder token);

    @BMethod
    Object getLaunchingActivity(IBinder token);

    @BMethod
    Object getPackageInfo(ApplicationInfo ai, @BParamClassName("android.content.res.CompatibilityInfo") Object compatInfo,
                          int flags);

    @BMethod
    void performNewIntents(IBinder IBinder0, List List1);

    @BMethod
    void sendActivityResult(IBinder IBinder0, String String1, int int2, int int3, Intent Intent4);

    @BClassName("android.app.ActivityThread$CreateServiceData")
    interface CreateServiceData {
        @BField
        Object compatInfo();

        @BField
        ServiceInfo info();

        @BField
        Intent intent();

        @BField
        IBinder token();
    }

    @BClassName("android.app.ActivityThread$H")
    interface H {
        @BStaticField
        int CREATE_SERVICE();

        @BStaticField
        int EXECUTE_TRANSACTION();

        @BStaticField
        int LAUNCH_ACTIVITY();

        @BStaticField
        int SCHEDULE_CRASH();
    }

    @BClassName("android.app.ActivityThread$AppBindData")
    interface AppBindData {
        @BField
        ApplicationInfo appInfo();

        @BField
        Object info();

        @BField
        ComponentName instrumentationName();

        @BField
        String processName();

        @BField
        List<android.content.pm.ProviderInfo> providers();
    }

    @BClassName("android.app.ActivityThread$ProviderKey")
    interface ProviderKeyJBMR1 {
        @BConstructor
        ProviderKeyJBMR1 _new(String String0, int int1);
    }

    @BClassName("android.app.ActivityThread$ProviderClientRecord")
    interface ProviderClientRecordJB {
        @BField
        Object mHolder();

        @BField
        IInterface mProvider();
    }

    @BClassName("android.app.ActivityThread$ProviderClientRecord")
    interface ProviderClientRecordP {
        @BConstructor
        ProviderClientRecordP _new();

        @BField
        String[] mNames();

        @BField
        IInterface mProvider();
    }

    @BClassName("android.app.ActivityThread$ProviderClientRecord")
    interface ProviderClientRecord {
        @BConstructor
        ProviderClientRecord _new();

        @BField
        String mName();

        @BField
        IInterface mProvider();
    }

    @BClassName("android.app.ActivityThread$ActivityClientRecord")
    interface ActivityClientRecord {
        @BField
        Activity activity();

        @BField
        ActivityInfo activityInfo();

        @BField
        Intent intent();

        @BField
        Boolean isTopResumedActivity();

        @BField
        IBinder token();

        @BField
        Object packageInfo();
    }
}
