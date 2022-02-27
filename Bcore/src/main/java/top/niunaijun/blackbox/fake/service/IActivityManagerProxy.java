package top.niunaijun.blackbox.fake.service;

import android.app.IServiceConnection;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import black.android.app.BRActivityManagerNative;
import black.android.app.BRActivityManagerOreo;
import black.android.content.BRContentProviderNative;
import black.android.os.BRUserHandle;
import black.android.util.BRSingleton;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.core.env.AppSystemEnv;
import top.niunaijun.blackbox.entity.AppConfig;
import top.niunaijun.blackbox.entity.am.RunningAppProcessInfo;
import top.niunaijun.blackbox.entity.am.RunningServiceInfo;
import top.niunaijun.blackbox.fake.delegate.ContentProviderDelegate;
import top.niunaijun.blackbox.fake.delegate.InnerReceiverDelegate;
import top.niunaijun.blackbox.fake.delegate.ServiceConnectionDelegate;
import top.niunaijun.blackbox.fake.frameworks.BActivityManager;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.fake.hook.ScanClass;
import top.niunaijun.blackbox.fake.service.context.providers.ContentProviderStub;
import top.niunaijun.blackbox.proxy.ProxyActivity;
import top.niunaijun.blackbox.proxy.ProxyManifest;
import top.niunaijun.blackbox.proxy.record.ProxyBroadcastRecord;
import top.niunaijun.blackbox.utils.ComponentUtils;
import top.niunaijun.blackbox.utils.MethodParameterUtils;
import top.niunaijun.blackbox.utils.Reflector;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Created by Milk on 3/30/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
@ScanClass(ActivityManagerCommonProxy.class)
public class IActivityManagerProxy extends ClassInvocationStub {
    public static final String TAG = "ActivityManagerStub";

    @Override
    protected Object getWho() {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = BRActivityManagerOreo.get().IActivityManagerSingleton();
        } else if (BuildCompat.isL()) {
            iActivityManager = BRActivityManagerNative.get().getDefault();
        }
        return BRSingleton.get(iActivityManager).get();
    }

    @Override
    protected void inject(Object base, Object proxy) {
        Object iActivityManager = null;
        if (BuildCompat.isOreo()) {
            iActivityManager = BRActivityManagerOreo.get().IActivityManagerSingleton();
        } else if (BuildCompat.isL()) {
            iActivityManager = BRActivityManagerNative.get().getDefault();
        }
        BRSingleton.get(iActivityManager)._set_mInstance(proxy);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod(name = "getContentProvider")
    public static class GetContentProvider extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Exception {
            int authIndex = getAuthIndex();
            Object auth = args[authIndex];
            Object content = null;

            if (auth instanceof String) {
                if (ProxyManifest.isProxy((String) auth)) {
                    return method.invoke(who, args);
                }

                if (BuildCompat.isQ()) {
                    args[1] = BlackBoxCore.getHostPkg();
                }

                if (auth.equals("settings") || auth.equals("media") || auth.equals("telephony")) {
                    content = method.invoke(who, args);
                    ContentProviderDelegate.update(content, (String) auth);
                    return content;
                } else {
                    Log.d(TAG, "hook getContentProvider: " + auth);

                    ProviderInfo providerInfo = BlackBoxCore.getBPackageManager().resolveContentProvider((String) auth, GET_META_DATA, BActivityThread.getUserId());
                    if (providerInfo == null) {
                        Object invoke = method.invoke(who, args);
                        if (invoke != null) {
                            Object provider = Reflector.with(invoke)
                                    .field("provider")
                                    .get();
                            if (provider != null && !(provider instanceof Proxy)) {
                                Reflector.with(invoke)
                                        .field("provider")
                                        .set(new ContentProviderStub().wrapper((IInterface) provider, BlackBoxCore.getHostPkg()));
                            }
                        }
                        return invoke;
                    }

                    IBinder providerBinder = null;
                    if (BActivityThread.getAppPid() != -1) {
                        AppConfig appConfig = BlackBoxCore.getBActivityManager().initProcess(providerInfo.packageName, providerInfo.processName, BActivityThread.getUserId());
                        if (appConfig.bpid != BActivityThread.getAppPid()) {
                            providerBinder = BlackBoxCore.getBActivityManager().acquireContentProviderClient(providerInfo);
                        }
                        args[authIndex] = ProxyManifest.getProxyAuthorities(appConfig.bpid);
                        args[getUserIndex()] = BRUserHandle.get().myUserId();
                    }
                    content = method.invoke(who, args);

                    Reflector.with(content)
                            .field("info")
                            .set(providerInfo);

                    if (providerBinder != null) {
                        Reflector.with(content)
                                .field("provider")
                                .set(BRContentProviderNative.get().asInterface(providerBinder));

                    }
                }

                return content;
            }
            return method.invoke(who, args);
        }

        private int getAuthIndex() {
            // 10.0
            if (BuildCompat.isQ()) {
                return 2;
            } else {
                return 1;
            }
        }

        private int getUserIndex() {
            return getAuthIndex() + 1;
        }
    }

    @ProxyMethod(name = "startService")
    public static class StartService extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, BActivityThread.getUserId());
            if (resolveInfo == null) {
                return method.invoke(who, args);
            }

            int requireForegroundIndex = getRequireForeground();
            boolean requireForeground = false;
            if (requireForegroundIndex != -1) {
                requireForeground = (boolean) args[requireForegroundIndex];
            }
            return BlackBoxCore.getBActivityManager().startService(intent, resolvedType, requireForeground, BActivityThread.getUserId());
        }

        public int getRequireForeground() {
            if (BuildCompat.isOreo()) {
                return 3;
            }
            return -1;
        }
    }

    @ProxyMethod(name = "stopService")
    public static class StopService extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[1];
            String resolvedType = (String) args[2];
            return BlackBoxCore.getBActivityManager().stopService(intent, resolvedType, BActivityThread.getUserId());
        }
    }

    @ProxyMethod(name = "stopServiceToken")
    public static class StopServiceToken extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ComponentName componentName = (ComponentName) args[0];
            IBinder token = (IBinder) args[1];
            BlackBoxCore.getBActivityManager().stopServiceToken(componentName, token, BActivityThread.getUserId());
            return true;
        }
    }

    @ProxyMethod(name = "bindService")
    public static class BindService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Intent intent = (Intent) args[2];
            String resolvedType = (String) args[3];
            IServiceConnection connection = (IServiceConnection) args[4];
            // 暂不支持gms
            if (ComponentUtils.isGmsService(intent)) {
                return 0;
            }
            ResolveInfo resolveInfo = BlackBoxCore.getBPackageManager().resolveService(intent, 0, resolvedType, BActivityThread.getUserId());
            if (resolveInfo != null || AppSystemEnv.isOpenPackage(intent.getComponent())) {
                Intent proxyIntent = BlackBoxCore.getBActivityManager().bindService(intent,
                        connection == null ? null : connection.asBinder(),
                        resolvedType,
                        BActivityThread.getUserId());
                if (connection != null) {
                    args[4] = ServiceConnectionDelegate.createProxy(connection, intent);
                }
                if (proxyIntent != null) {
                    args[2] = proxyIntent;
                    return method.invoke(who, args);
                }
            }
            return 0;
        }
    }

    // 10.0
    @ProxyMethod(name = "bindIsolatedService")
    public static class BindIsolatedService extends BindService {
        @Override
        protected Object beforeHook(Object who, Method method, Object[] args) throws Throwable {
            // instanceName
            args[6] = null;
            return super.beforeHook(who, method, args);
        }
    }

    @ProxyMethod(name = "unbindService")
    public static class UnbindService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IServiceConnection iServiceConnection = (IServiceConnection) args[0];
            if (iServiceConnection == null) {
                return method.invoke(who, args);
            }
            BlackBoxCore.getBActivityManager().unbindService(iServiceConnection.asBinder(), BActivityThread.getUserId());
            ServiceConnectionDelegate delegate = ServiceConnectionDelegate.getDelegate(iServiceConnection.asBinder());
            if (delegate != null) {
                args[0] = delegate;
            }
            return method.invoke(who, args);
        }
    }

    @ProxyMethod(name = "getRunningAppProcesses")
    public static class GetRunningAppProcesses extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            RunningAppProcessInfo runningAppProcesses = BActivityManager.get().getRunningAppProcesses(BActivityThread.getAppPackageName(), BActivityThread.getUserId());
            if (runningAppProcesses == null) {
                return new ArrayList<>();
            }
            return runningAppProcesses.mAppProcessInfoList;
        }
    }

    @ProxyMethod(name = "getServices")
    public static class GetServices extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            RunningServiceInfo runningServices = BActivityManager.get().getRunningServices(BActivityThread.getAppPackageName(), BActivityThread.getUserId());
            if (runningServices == null) {
                return new ArrayList<>();
            }
            return runningServices.mRunningServiceInfoList;
        }
    }

    @ProxyMethod(name = "getIntentSender")
    public static class GetIntentSender extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            Intent[] intents = (Intent[]) args[getIntentsIndex()];

            // todo
            for (Intent intent : intents) {
                intent.setComponent(new ComponentName(BlackBoxCore.getHostPkg(), ProxyActivity.P0.class.getName()));
            }
            return method.invoke(who, args);
        }

        private int getIntentsIndex() {
            if (BuildCompat.isR()) {
                return 6;
            } else {
                return 5;
            }
        }
    }

    @ProxyMethod(name = "getIntentSenderWithFeature")
    public static class GetIntentSenderWithFeature extends GetIntentSender {
    }

    @ProxyMethod(name = "broadcastIntentWithFeature")
    public static class BroadcastIntentWithFeature extends BroadcastIntent {
    }

    @ProxyMethod(name = "broadcastIntent")
    public static class BroadcastIntent extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int intentIndex = getIntentIndex(args);
            Intent intent = (Intent) args[intentIndex];
            String resolvedType = (String) args[intentIndex + 1];
            Intent proxyIntent = BlackBoxCore.getBActivityManager().sendBroadcast(intent, resolvedType, BActivityThread.getUserId());
            if (proxyIntent != null) {
                proxyIntent.setExtrasClassLoader(BActivityThread.getApplication().getClassLoader());
                ProxyBroadcastRecord.saveStub(proxyIntent, intent, BActivityThread.getUserId());
                args[intentIndex] = proxyIntent;
            }
            // ignore permission
            for (int i = 0; i < args.length; i++) {
                Object o = args[i];
                if (o instanceof String[]) {
                    args[i] = null;
                }
            }
            return method.invoke(who, args);
        }

        int getIntentIndex(Object[] args) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Intent) {
                    return i;
                }
            }
            return 1;
        }
    }

    @ProxyMethod(name = "unregisterReceiver")
    public static class unregisterReceiver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            InnerReceiverDelegate delegate = InnerReceiverDelegate.getDelegate((IBinder) args[0]);
            if (delegate == null) {
                return method.invoke(who, args);
            }
            args[0] = delegate;
            return method.invoke(who, args);
        }
    }

    @ProxyMethod(name = "finishReceiver")
    public static class finishReceiver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            InnerReceiverDelegate delegate = InnerReceiverDelegate.getDelegate((IBinder) args[0]);
            if (delegate == null) {
                return method.invoke(who, args);
            }
            args[0] = delegate;
            return method.invoke(who, args);
        }
    }

    @ProxyMethod(name = "publishService")
    public static class PublishService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }

    @ProxyMethod(name = "peekService")
    public static class PeekService extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(args);
            Intent intent = (Intent) args[0];
            String resolvedType = (String) args[1];
            IBinder peek = BlackBoxCore.getBActivityManager().peekService(intent, resolvedType, BActivityThread.getUserId());
            return peek;
        }
    }

    // todo
    @ProxyMethod(name = "sendIntentSender")
    public static class SendIntentSender extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    // android 10
    @ProxyMethod(name = "registerReceiverWithFeature")
    public static class RegisterReceiverWithFeature extends RegisterReceiver {
    }

    @ProxyMethod(name = "registerReceiver")
    public static class RegisterReceiver extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            int receiverIndex = getReceiverIndex();
            if (args[receiverIndex] != null) {
                IIntentReceiver proxy = InnerReceiverDelegate.createProxy((IIntentReceiver) args[receiverIndex]);
                args[receiverIndex] = proxy;
            }
            // ignore permission
            if (args[getPermissionIndex()] != null) {
                args[getPermissionIndex()] = null;
            }
            return method.invoke(who, args);
        }

        public int getReceiverIndex() {
            if (BuildCompat.isS()) {
                return 4;
            } else if (BuildCompat.isR()) {
                return 3;
            }
            return 2;
        }

        public int getPermissionIndex() {
            if (BuildCompat.isS()) {
                return 6;
            } else if (BuildCompat.isR()) {
                return 5;
            }
            return 4;
        }
    }

    @ProxyMethod(name = "grantUriPermission")
    public static class GrantUriPermission extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            MethodParameterUtils.replaceLastUserId(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod(name = "setServiceForeground")
    public static class setServiceForeground extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args[0] instanceof ComponentName) {
                args[0] = new ComponentName(BlackBoxCore.getHostPkg(), ProxyManifest.getProxyService(BActivityThread.getAppPid()));
            }
            return method.invoke(who, args);
        }
    }
}
