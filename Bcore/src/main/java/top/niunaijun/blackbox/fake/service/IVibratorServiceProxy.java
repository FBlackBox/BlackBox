package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.com.android.internal.os.BRIVibratorServiceStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by BlackBox on 2022/3/7.
 */
public class IVibratorServiceProxy extends BinderInvocationStub {

    public IVibratorServiceProxy() {
        super(BRServiceManager.get().getService(Context.VIBRATOR_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRIVibratorServiceStub.get().asInterface(BRServiceManager.get().getService(Context.VIBRATOR_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceFirstUid(args);
        MethodParameterUtils.replaceFirstAppPkg(args);
        return super.invoke(proxy, method, args);
    }
}
