package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import black.android.os.BRIPowerManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;

/**
 * Created by BlackBox on 2022/3/1.
 */
public class IPowerManagerProxy extends BinderInvocationStub {
    public IPowerManagerProxy() {
        super(BRServiceManager.get().getService(Context.POWER_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRIPowerManagerStub.get().asInterface(BRServiceManager.get().getService(Context.POWER_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.POWER_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("acquireWakeLock", 0));
        addMethodHook(new ValueMethodProxy("acquireWakeLockWithUid", 0));
        addMethodHook(new ValueMethodProxy("releaseWakeLock", 0));
        addMethodHook(new ValueMethodProxy("updateWakeLockWorkSource", 0));
        addMethodHook(new ValueMethodProxy("isWakeLockLevelSupported", true));
    }
}
