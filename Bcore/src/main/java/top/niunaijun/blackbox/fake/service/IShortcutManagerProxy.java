package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;

import black.android.content.pm.BRIShortcutServiceStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/5/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 * 未实现，全部拦截
 */
public class IShortcutManagerProxy extends BinderInvocationStub {

    public IShortcutManagerProxy() {
        super(BRServiceManager.get().getService(Context.SHORTCUT_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRIShortcutServiceStub.get().asInterface(BRServiceManager.get().getService(Context.SHORTCUT_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.SHORTCUT_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("requestPinShortcut")
    public static class RequestPinShortcut extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    @ProxyMethod("setDynamicShortcuts")
    public static class SetDynamicShortcuts extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    @ProxyMethod("addDynamicShortcuts")
    public static class AddDynamicShortcuts extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    @ProxyMethod("createShortcutResultIntent")
    public static class CreateShortcutResultIntent extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return new Intent();
        }
    }

    @ProxyMethod("pushDynamicShortcut")
    public static class pushDynamicShortcut extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return 0;
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceAllAppPkg(args);
        return super.invoke(proxy, method, args);
    }
}
