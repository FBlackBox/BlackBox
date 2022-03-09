package top.niunaijun.blackbox.fake.service;

import android.app.ActivityManager;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.app.BRActivityClient;
import black.android.util.BRSingleton;
import top.niunaijun.blackbox.fake.frameworks.BActivityManager;
import top.niunaijun.blackbox.fake.hook.ClassInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.compat.TaskDescriptionCompat;

/**
 * Created by BlackBox on 2022/2/22.
 */
public class IActivityClientProxy extends ClassInvocationStub {
    public static final String TAG = "IActivityClientProxy";
    private final Object who;

    public IActivityClientProxy(Object who) {
        this.who = who;
    }

    @Override
    protected Object getWho() {
        if (who != null) {
            return who;
        }
        Object instance = BRActivityClient.get().getInstance();
        Object singleton = BRActivityClient.get(instance).INTERFACE_SINGLETON();
        return BRSingleton.get(singleton).get();
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        Object instance = BRActivityClient.get().getInstance();
        Object singleton = BRActivityClient.get(instance).INTERFACE_SINGLETON();
        BRSingleton.get(singleton)._set_mInstance(proxyInvocation);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object getProxyInvocation() {
        return super.getProxyInvocation();
    }

    @Override
    public void onlyProxy(boolean o) {
        super.onlyProxy(o);
    }

    @ProxyMethod("finishActivity")
    public static class FinishActivity extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onFinishActivity(token);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("activityResumed")
    public static class ActivityResumed extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onActivityResumed(token);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("activityDestroyed")
    public static class ActivityDestroyed extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onActivityDestroyed(token);
            return method.invoke(who, args);
        }
    }

    // for >= Android 12
    @ProxyMethod("setTaskDescription")
    public static class SetTaskDescription extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ActivityManager.TaskDescription td = (ActivityManager.TaskDescription) args[1];
            args[1] = TaskDescriptionCompat.fix(td);
            return method.invoke(who, args);
        }
    }
}
