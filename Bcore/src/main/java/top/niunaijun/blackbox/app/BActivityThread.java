package top.niunaijun.blackbox.app;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.Service;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.os.StrictMode;
import android.text.TextUtils;
import android.webkit.WebView;

import java.io.File;
import java.lang.reflect.Method;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import black.android.app.ActivityThreadAppBindDataContext;
import black.android.app.BRActivity;
import black.android.app.BRActivityManagerNative;
import black.android.app.BRActivityThread;
import black.android.app.BRActivityThreadActivityClientRecord;
import black.android.app.BRActivityThreadAppBindData;
import black.android.app.BRActivityThreadNMR1;
import black.android.app.BRActivityThreadQ;
import black.android.app.BRContextImpl;
import black.android.app.BRLoadedApk;
import black.android.app.BRService;
import black.android.content.BRBroadcastReceiver;
import black.android.content.BRContentProviderClient;
import black.android.graphics.BRCompatibility;
import black.android.security.net.config.BRNetworkSecurityConfigProvider;
import black.com.android.internal.content.BRReferrerIntent;
import black.dalvik.system.BRVMRuntime;
import top.canyie.pine.xposed.PineXposed;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback;
import top.niunaijun.blackbox.app.dispatcher.AppServiceDispatcher;
import top.niunaijun.blackbox.core.CrashHandler;
import top.niunaijun.blackbox.core.IBActivityThread;
import top.niunaijun.blackbox.core.IOCore;
import top.niunaijun.blackbox.core.NativeCore;
import top.niunaijun.blackbox.core.env.VirtualRuntime;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.am.ReceiverData;
import top.niunaijun.blackbox.entity.pm.InstalledModule;
import top.niunaijun.blackbox.fake.delegate.AppInstrumentation;
import top.niunaijun.blackbox.fake.delegate.ContentProviderDelegate;
import top.niunaijun.blackbox.fake.frameworks.BXposedManager;
import top.niunaijun.blackbox.fake.hook.HookManager;
import top.niunaijun.blackbox.fake.service.HCallbackProxy;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.Slog;
import top.niunaijun.blackbox.utils.compat.ActivityManagerCompat;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import top.niunaijun.blackbox.utils.compat.ContextCompat;
import top.niunaijun.blackbox.utils.compat.StrictModeCompat;

/**
 * Created by Milk on 3/31/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BActivityThread extends IBActivityThread.Stub {
    public static final String TAG = "BActivityThread";

    private static BActivityThread sBActivityThread;
    private AppBindData mBoundApplication;
    private Application mInitialApplication;
    private AppConfig mAppConfig;
    private final List<ProviderInfo> mProviders = new ArrayList<>();
    private final Handler mH = BlackBoxCore.get().getHandler();
    private static final Object mConfigLock = new Object();

    public static boolean isThreadInit() {
        return sBActivityThread != null;
    }

    public static BActivityThread currentActivityThread() {
        if (sBActivityThread == null) {
            synchronized (BActivityThread.class) {
                if (sBActivityThread == null) {
                    sBActivityThread = new BActivityThread();
                }
            }
        }
        return sBActivityThread;
    }

    public static AppConfig getAppConfig() {
        synchronized (mConfigLock) {
            return currentActivityThread().mAppConfig;
        }
    }

    public static List<ProviderInfo> getProviders() {
        return currentActivityThread().mProviders;
    }

    public static String getAppProcessName() {
        if (getAppConfig() != null) {
            return getAppConfig().processName;
        } else if (currentActivityThread().mBoundApplication != null) {
            return currentActivityThread().mBoundApplication.processName;
        } else {
            return null;
        }
    }

    public static String getAppPackageName() {
        if (getAppConfig() != null) {
            return getAppConfig().packageName;
        } else if (currentActivityThread().mInitialApplication != null) {
            return currentActivityThread().mInitialApplication.getPackageName();
        } else {
            return null;
        }
    }

    public static Application getApplication() {
        return currentActivityThread().mInitialApplication;
    }

    public static int getAppPid() {
        return getAppConfig() == null ? -1 : getAppConfig().bpid;
    }

    public static int getBUid() {
        return getAppConfig() == null ? BUserHandle.AID_APP_START : getAppConfig().buid;
    }

    public static int getBAppId() {
        return BUserHandle.getAppId(getBUid());
    }

    public static int getCallingBUid() {
        return getAppConfig() == null ? BlackBoxCore.getHostUid() : getAppConfig().callingBUid;
    }

    public static int getUid() {
        return getAppConfig() == null ? -1 : getAppConfig().uid;
    }

    public static int getUserId() {
        return getAppConfig() == null ? 0 : getAppConfig().userId;
    }

    public void initProcess(AppConfig appConfig) {
        synchronized (mConfigLock) {
            if (this.mAppConfig != null && !this.mAppConfig.packageName.equals(appConfig.packageName)) {
                // 该进程已被attach
                throw new RuntimeException("reject init process: " + appConfig.processName + ", this process is : " + this.mAppConfig.processName);
            }
            this.mAppConfig = appConfig;
            IBinder iBinder = asBinder();
            try {
                iBinder.linkToDeath(new DeathRecipient() {
                    @Override
                    public void binderDied() {
                        synchronized (mConfigLock) {
                            try {
                                iBinder.linkToDeath(this, 0);
                            } catch (RemoteException ignored) {
                            }
                            mAppConfig = null;
                        }
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isInit() {
        return mBoundApplication != null;
    }

    public Service createService(ServiceInfo serviceInfo, IBinder token) {
        if (!BActivityThread.currentActivityThread().isInit()) {
            BActivityThread.currentActivityThread().bindApplication(serviceInfo.packageName, serviceInfo.processName);
        }
        ClassLoader classLoader = BRLoadedApk.get(mBoundApplication.info).getClassLoader();
        Service service;
        try {
            service = (Service) classLoader.loadClass(serviceInfo.name).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Slog.e(TAG, "Unable to instantiate service " + serviceInfo.name
                    + ": " + e.toString());
            return null;
        }

        try {
            Context context = BlackBoxCore.getContext().createPackageContext(
                    serviceInfo.packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
            );
            BRContextImpl.get(context).setOuterContext(service);
            BRService.get(service).attach(
                    context,
                    BlackBoxCore.mainThread(),
                    serviceInfo.name,
                    token,
                    mInitialApplication,
                    BRActivityManagerNative.get().getDefault()
            );
            ContextCompat.fix(context);
            service.onCreate();
            return service;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to create service " + serviceInfo.name
                            + ": " + e.toString(), e);
        }
    }

    public JobService createJobService(ServiceInfo serviceInfo) {
        if (!BActivityThread.currentActivityThread().isInit()) {
            BActivityThread.currentActivityThread().bindApplication(serviceInfo.packageName, serviceInfo.processName);
        }
        ClassLoader classLoader = BRLoadedApk.get(mBoundApplication.info).getClassLoader();
        JobService service;
        try {
            service = (JobService) classLoader.loadClass(serviceInfo.name).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Slog.e(TAG, "Unable to create JobService " + serviceInfo.name
                    + ": " + e.toString());
            return null;
        }

        try {
            Context context = BlackBoxCore.getContext().createPackageContext(
                    serviceInfo.packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY
            );
            BRContextImpl.get(context).setOuterContext(service);
            BRService.get(service).attach(
                    context,
                    BlackBoxCore.mainThread(),
                    serviceInfo.name,
                    BActivityThread.currentActivityThread().getActivityThread(),
                    mInitialApplication,
                    BRActivityManagerNative.get().getDefault()
            );
            ContextCompat.fix(context);
            service.onCreate();
            service.onBind(null);
            return service;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to create JobService " + serviceInfo.name
                            + ": " + e.toString(), e);
        }
    }

    public void bindApplication(final String packageName, final String processName) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            final ConditionVariable conditionVariable = new ConditionVariable();
            BlackBoxCore.get().getHandler().post(() -> {
                handleBindApplication(packageName, processName);
                conditionVariable.open();
            });
            conditionVariable.block();
        } else {
            handleBindApplication(packageName, processName);
        }
    }

    public synchronized void handleBindApplication(String packageName, String processName) {
        if (isInit())
            return;
        try {
            CrashHandler.create();
        } catch (Throwable ignored) {
        }

        PackageInfo packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, PackageManager.GET_PROVIDERS, BActivityThread.getUserId());
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        if (packageInfo.providers == null) {
            packageInfo.providers = new ProviderInfo[]{};
        }
        mProviders.addAll(Arrays.asList(packageInfo.providers));

        Object boundApplication = BRActivityThread.get(BlackBoxCore.mainThread()).mBoundApplication();

        Context packageContext = createPackageContext(applicationInfo);
        Object loadedApk = BRContextImpl.get(packageContext).mPackageInfo();
        BRLoadedApk.get(loadedApk)._set_mSecurityViolation(false);
        // fix applicationInfo
        BRLoadedApk.get(loadedApk)._set_mApplicationInfo(applicationInfo);

        int targetSdkVersion = applicationInfo.targetSdkVersion;
        if (targetSdkVersion < Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy newPolicy = new StrictMode.ThreadPolicy.Builder(StrictMode.getThreadPolicy()).permitNetwork().build();
            StrictMode.setThreadPolicy(newPolicy);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (targetSdkVersion < Build.VERSION_CODES.N) {
                StrictModeCompat.disableDeathOnFileUriExposure();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix(getUserId() + ":" + packageName + ":" + processName);
        }

        VirtualRuntime.setupRuntime(processName, applicationInfo);

        BRVMRuntime.get(BRVMRuntime.get().getRuntime()).setTargetSdkVersion(applicationInfo.targetSdkVersion);
        if (BuildCompat.isS()) {
            BRCompatibility.get().setTargetSdkVersion(applicationInfo.targetSdkVersion);
        }

        NativeCore.init(Build.VERSION.SDK_INT);
        assert packageContext != null;
        IOCore.get().enableRedirect(packageContext);

        AppBindData bindData = new AppBindData();
        bindData.appInfo = applicationInfo;
        bindData.processName = processName;
        bindData.info = loadedApk;
        bindData.providers = mProviders;

        ActivityThreadAppBindDataContext activityThreadAppBindData = BRActivityThreadAppBindData.get(boundApplication);
        activityThreadAppBindData._set_instrumentationName(new ComponentName(bindData.appInfo.packageName, Instrumentation.class.getName()));
        activityThreadAppBindData._set_appInfo(bindData.appInfo);
        activityThreadAppBindData._set_info(bindData.info);
        activityThreadAppBindData._set_processName(bindData.processName);
        activityThreadAppBindData._set_providers(bindData.providers);

        mBoundApplication = bindData;

        //ssl适配
        if (BRNetworkSecurityConfigProvider.getRealClass() != null) {
            Security.removeProvider("AndroidNSSP");
            BRNetworkSecurityConfigProvider.get().install(packageContext);
        }
        Application application;
        try {
            onBeforeCreateApplication(packageName, processName, packageContext);
            application = BRLoadedApk.get(loadedApk).makeApplication(false, null);
            mInitialApplication = application;
            BRActivityThread.get(BlackBoxCore.mainThread())._set_mInitialApplication(mInitialApplication);
            ContextCompat.fix((Context) BRActivityThread.get(BlackBoxCore.mainThread()).getSystemContext());
            ContextCompat.fix(mInitialApplication);
            installProviders(mInitialApplication, bindData.processName, bindData.providers);

            onBeforeApplicationOnCreate(packageName, processName, application);
            AppInstrumentation.get().callApplicationOnCreate(application);
            onAfterApplicationOnCreate(packageName, processName, application);

            HookManager.get().checkEnv(HCallbackProxy.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to makeApplication", e);
        }
    }

    public static Context createPackageContext(ApplicationInfo info) {
        try {
            return BlackBoxCore.getContext().createPackageContext(info.packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void installProviders(Context context, String processName, List<ProviderInfo> provider) {
        long origId = Binder.clearCallingIdentity();
        try {
            for (ProviderInfo providerInfo : provider) {
                try {
                    if (processName.equals(providerInfo.processName) ||
                            providerInfo.processName.equals(context.getPackageName()) || providerInfo.multiprocess) {
                        installProvider(BlackBoxCore.mainThread(), context, providerInfo, null);
                    }
                } catch (Throwable ignored) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(origId);
            ContentProviderDelegate.init();
        }
    }

    public Object getPackageInfo() {
        return mBoundApplication.info;
    }

    public static void installProvider(Object mainThread, Context context, ProviderInfo providerInfo, Object holder) throws Throwable {
        Method installProvider = Reflector.findMethodByFirstName(mainThread.getClass(), "installProvider");
        if (installProvider != null) {
            installProvider.setAccessible(true);
            installProvider.invoke(mainThread, context, holder, providerInfo, false, true, true);
        }
    }

    public void loadXposed(Context context) {
        String vPackageName = getAppPackageName();
        String vProcessName = getAppProcessName();
        if (!TextUtils.isEmpty(vPackageName) && !TextUtils.isEmpty(vProcessName) && BXposedManager.get().isXPEnable()) {
            assert vPackageName != null;
            assert vProcessName != null;

            boolean isFirstApplication = vPackageName.equals(vProcessName);

            List<InstalledModule> installedModules = BXposedManager.get().getInstalledModules();
            for (InstalledModule installedModule : installedModules) {
                if (!installedModule.enable) {
                    continue;
                }
                try {
                    PineXposed.loadModule(new File(installedModule.getApplication().sourceDir));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            try {
                PineXposed.onPackageLoad(vPackageName, vProcessName, context.getApplicationInfo(), isFirstApplication, context.getClassLoader());
            } catch (Throwable ignored) {
            }
        }
        if (BlackBoxCore.get().isHideXposed()) {
            NativeCore.hideXposed();
        }
    }

    @Override
    public IBinder getActivityThread() {
        return BRActivityThread.get(BlackBoxCore.mainThread()).getApplicationThread();
    }

    @Override
    public void bindApplication() {
        if (!isInit()) {
            bindApplication(getAppPackageName(), getAppProcessName());
        }
    }

    @Override
    public void stopService(Intent intent) {
        AppServiceDispatcher.get().stopService(intent);
    }

    @Override
    public void restartJobService(String selfId) throws RemoteException {

    }

    @Override
    public IBinder acquireContentProviderClient(ProviderInfo providerInfo) throws RemoteException {
        if (!isInit()) {
            bindApplication(BActivityThread.getAppConfig().packageName, BActivityThread.getAppConfig().processName);
        }
        String[] split = providerInfo.authority.split(";");
        for (String auth : split) {
            ContentProviderClient contentProviderClient = BlackBoxCore.getContext()
                    .getContentResolver().acquireContentProviderClient(auth);
            IInterface iInterface = BRContentProviderClient.get(contentProviderClient).mContentProvider();
            if (iInterface == null)
                continue;
            return iInterface.asBinder();
        }
        return null;
    }

    @Override
    public IBinder peekService(Intent intent) {
        return AppServiceDispatcher.get().peekService(intent);
    }

    @Override
    public void finishActivity(final IBinder token) {
        mH.post(() -> {
            Map<IBinder, Object> activities = BRActivityThread.get(BlackBoxCore.mainThread()).mActivities();
            if (activities.isEmpty())
                return;
            Object clientRecord = activities.get(token);
            if (clientRecord == null)
                return;
            Activity activity = getActivityByToken(token);

            while (activity.getParent() != null) {
                activity = activity.getParent();
            }

            int resultCode = BRActivity.get(activity).mResultCode();
            Intent resultData = BRActivity.get(activity).mResultData();
            ActivityManagerCompat.finishActivity(token, resultCode, resultData);
            BRActivity.get(activity)._set_mFinished(true);
        });
    }

    @Override
    public void handleNewIntent(final IBinder token, final Intent intent) {
        mH.post(() -> {
            Intent newIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                newIntent = BRReferrerIntent.get()._new(intent, BlackBoxCore.getHostPkg());
            } else {
                newIntent = intent;
            }
            Object mainThread = BlackBoxCore.mainThread();
            if (BRActivityThread.get(BlackBoxCore.mainThread())._check_performNewIntents(null, null) != null) {
                BRActivityThread.get(mainThread).performNewIntents(
                        token,
                        Collections.singletonList(newIntent)
                );
            } else if (BRActivityThreadNMR1.get(mainThread)._check_performNewIntents(null, null, false) != null) {
                BRActivityThreadNMR1.get(mainThread).performNewIntents(
                        token,
                        Collections.singletonList(newIntent),
                        true);
            } else if (BRActivityThreadQ.get(mainThread)._check_handleNewIntent(null, null) != null) {
                BRActivityThreadQ.get(mainThread).handleNewIntent(token, Collections.singletonList(newIntent));
            }
        });
    }

    @Override
    public void scheduleReceiver(ReceiverData data) throws RemoteException {
        if (!isInit()) {
            bindApplication();
        }
        mH.post(() -> {
            BroadcastReceiver mReceiver = null;
            Intent intent = data.intent;
            ActivityInfo activityInfo = data.activityInfo;
            BroadcastReceiver.PendingResult pendingResult = data.data.build();

            try {
                Context baseContext = mInitialApplication.getBaseContext();
                ClassLoader classLoader = baseContext.getClassLoader();
                intent.setExtrasClassLoader(classLoader);

                mReceiver = (BroadcastReceiver) classLoader.loadClass(activityInfo.name).newInstance();
                BRBroadcastReceiver.get(mReceiver).setPendingResult(pendingResult);
                mReceiver.onReceive(baseContext, intent);
                BroadcastReceiver.PendingResult finish = BRBroadcastReceiver.get(mReceiver).getPendingResult();
                if (finish != null) {
                    finish.finish();
                }
                BlackBoxCore.getBActivityManager().finishBroadcast(data.data);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                Slog.e(TAG,
                        "Error receiving broadcast " + intent
                                + " in " + mReceiver);
            }
        });
    }

    public static Activity getActivityByToken(IBinder token) {
        Map<IBinder, Object> iBinderObjectMap =
                BRActivityThread.get(BlackBoxCore.mainThread()).mActivities();
        return BRActivityThreadActivityClientRecord.get(iBinderObjectMap.get(token)).activity();
    }

    private void onBeforeCreateApplication(String packageName, String processName, Context context) {
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.beforeCreateApplication(packageName, processName, context, BActivityThread.getUserId());
        }
    }

    private void onBeforeApplicationOnCreate(String packageName, String processName, Application application) {
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.beforeApplicationOnCreate(packageName, processName, application, BActivityThread.getUserId());
        }
    }

    private void onAfterApplicationOnCreate(String packageName, String processName, Application application) {
        for (AppLifecycleCallback appLifecycleCallback : BlackBoxCore.get().getAppLifecycleCallbacks()) {
            appLifecycleCallback.afterApplicationOnCreate(packageName, processName, application, BActivityThread.getUserId());
        }
    }

    public static class AppBindData {
        String processName;
        ApplicationInfo appInfo;
        List<ProviderInfo> providers;
        Object info;
    }
}
