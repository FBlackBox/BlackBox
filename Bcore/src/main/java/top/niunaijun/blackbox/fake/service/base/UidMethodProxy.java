package top.niunaijun.blackbox.fake.service.base;

import java.lang.reflect.Method;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.MethodHook;

/**
 * Created by BlackBox on 2022/3/5.
 */
public class UidMethodProxy extends MethodHook {
    private final int index;
    private final String name;

    public UidMethodProxy(String name, int index) {
        this.index = index;
        this.name = name;
    }

    @Override
    protected String getMethodName() {
        return name;
    }

    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
        int uid = (int) args[index];
        if (uid == BActivityThread.getBUid()) {
            args[index] = BlackBoxCore.getHostUid();
        }
        return method.invoke(who, args);
    }
}
