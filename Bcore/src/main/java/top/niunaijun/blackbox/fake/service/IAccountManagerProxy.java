package top.niunaijun.blackbox.fake.service;

import android.content.Context;

import black.android.accounts.BRIAccountManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.service.base.ValueMethodProxy;

/**
 * Created by Milk on 2022/2/14.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IAccountManagerProxy extends BinderInvocationStub {
    public IAccountManagerProxy() {
        super(BRServiceManager.get().getService(Context.ACCOUNT_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRIAccountManagerStub.get().asInterface(BRServiceManager.get().getService(Context.ACCOUNT_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.ACCOUNT_SERVICE);
    }

    @Override
    protected void onBindMethod() {
        super.onBindMethod();
        addMethodHook(new ValueMethodProxy("getUserData", null));
        addMethodHook(new ValueMethodProxy("peekAuthToken", null));
        addMethodHook(new ValueMethodProxy("setUserData", 0));
        addMethodHook(new ValueMethodProxy("addAccountExplicitly", true));
        addMethodHook(new ValueMethodProxy("setAuthToken", 0));
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
