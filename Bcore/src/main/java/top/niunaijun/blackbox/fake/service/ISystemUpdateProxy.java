package top.niunaijun.blackbox.fake.service;


import black.android.os.BRServiceManager;
import black.android.view.BRIAutoFillManagerStub;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;

/**
 * @author Findger
 * @function
 * @date :2022/4/2 21:59
 **/
public class ISystemUpdateProxy extends BinderInvocationStub {
    public ISystemUpdateProxy() {
        super(BRServiceManager.get().getService("system_update"));
    }

    @Override
    protected Object getWho() {
        return BRIAutoFillManagerStub.get().asInterface(BRServiceManager.get().getService("system_update"));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("system_update");
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
