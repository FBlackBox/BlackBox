package top.niunaijun.blackbox.fake.service;

import android.accounts.Account;
import android.accounts.IAccountManagerResponse;
import android.content.Context;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.Map;

import black.android.accounts.BRIAccountManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.fake.frameworks.BAccountManager;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.Slog;

/**
 * Created by Milk on 2022/2/14.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IAccountManagerProxy extends BinderInvocationStub {
    public static final String TAG = "IAccountManagerProxy";

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
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Slog.d(TAG, "call " + method.getName());
        return super.invoke(proxy, method, args);
    }

    @ProxyMethod("getPassword")
    public static class getPassword extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getPassword((Account) args[0]);
        }
    }

    @ProxyMethod("getUserData")
    public static class getUserData extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getUserData((Account) args[0], (String) args[1]);
        }
    }

    @ProxyMethod("getAuthenticatorTypes")
    public static class getAuthenticatorTypes extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAuthenticatorTypes();
        }
    }

    @ProxyMethod("getAccountsForPackage")
    public static class getAccountsForPackage extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountsForPackage((String) args[0], (int) args[1]);
        }
    }

    @ProxyMethod("getAccountsByTypeForPackage")
    public static class getAccountsByTypeForPackage extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountsByTypeForPackage((String) args[0], (String) args[1]);
        }
    }

    @ProxyMethod("getAccountByTypeAndFeatures")
    public static class getAccountByTypeAndFeatures extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().getAccountByTypeAndFeatures((IAccountManagerResponse) args[0], (String) args[1], (String[]) args[2]);
            return 0;
        }
    }

    @ProxyMethod("getAccountsByFeatures")
    public static class getAccountsByFeatures extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().getAccountsByFeatures((IAccountManagerResponse) args[0], (String) args[1], (String[]) args[2]);
            return 0;
        }
    }

    @ProxyMethod("getAccountsAsUser")
    public static class getAccountsAsUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountsAsUser((String) args[0]);
        }
    }

    @ProxyMethod("addAccountExplicitly")
    public static class addAccountExplicitly extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().addAccountExplicitly((Account) args[0], (String) args[1], (Bundle) args[2]);
        }
    }

    @ProxyMethod("removeAccountAsUser")
    public static class removeAccountAsUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().removeAccountAsUser((IAccountManagerResponse) args[0], (Account) args[1], (boolean) args[2]);
            return 0;
        }
    }

    @ProxyMethod("removeAccountExplicitly")
    public static class removeAccountExplicitly extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().removeAccountExplicitly((Account) args[0]);
        }
    }

    @ProxyMethod("copyAccountToUser")
    public static class copyAccountToUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().copyAccountToUser((IAccountManagerResponse) args[0], (Account) args[1], (int) args[2], (int) args[3]);
            return 0;
        }
    }

    @ProxyMethod("invalidateAuthToken")
    public static class invalidateAuthToken extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().invalidateAuthToken((String) args[0], (String) args[1]);
            return 0;
        }
    }

    @ProxyMethod("peekAuthToken")
    public static class peekAuthToken extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().peekAuthToken((Account) args[0], (String) args[1]);
        }
    }

    @ProxyMethod("setAuthToken")
    public static class setAuthToken extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().setAuthToken((Account) args[0], (String) args[1], (String) args[2]);
            return 0;
        }
    }

    @ProxyMethod("setPassword")
    public static class setPassword extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().setPassword((Account) args[0], (String) args[1]);
            return 0;
        }
    }

    @ProxyMethod("clearPassword")
    public static class clearPassword extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().clearPassword((Account) args[0]);
            return 0;
        }
    }

    @ProxyMethod("setUserData")
    public static class setUserData extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().setUserData((Account) args[0], (String) args[1], (String) args[2]);
            return 0;
        }
    }

    @ProxyMethod("updateAppPermission")
    public static class updateAppPermission extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().updateAppPermission((Account) args[0], (String) args[1], (int) args[2], (boolean) args[3]);
            return 0;
        }
    }

    @ProxyMethod("getAuthToken")
    public static class getAuthToken extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().getAuthToken((IAccountManagerResponse) args[0],
                    (Account) args[1],
                    (String) args[2],
                    (boolean) args[3],
                    (boolean) args[4],
                    (Bundle) args[5]);
            return 0;
        }
    }

    @ProxyMethod("addAccount")
    public static class addAccount extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().addAccount((IAccountManagerResponse) args[0],
                    (String) args[1],
                    (String) args[2],
                    (String[]) args[3],
                    (boolean) args[4],
                    (Bundle) args[5]);
            return 0;
        }
    }

    @ProxyMethod("addAccountAsUser")
    public static class addAccountAsUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().addAccountAsUser((IAccountManagerResponse) args[0],
                    (String) args[1],
                    (String) args[2],
                    (String[]) args[3],
                    (boolean) args[4],
                    (Bundle) args[5]);
            return 0;
        }
    }

    @ProxyMethod("updateCredentials")
    public static class updateCredentials extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().updateCredentials((IAccountManagerResponse) args[0],
                    (Account) args[1],
                    (String) args[2],
                    (boolean) args[3],
                    (Bundle) args[4]);
            return 0;
        }
    }

    @ProxyMethod("editProperties")
    public static class editProperties extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().editProperties((IAccountManagerResponse) args[0],
                    (String) args[1],
                    (boolean) args[2]);
            return 0;
        }
    }

    @ProxyMethod("confirmCredentialsAsUser")
    public static class confirmCredentialsAsUser extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().confirmCredentialsAsUser((IAccountManagerResponse) args[0],
                    (Account) args[1],
                    (Bundle) args[2],
                    (boolean) args[3]);
            return 0;
        }
    }

    @ProxyMethod("accountAuthenticated")
    public static class accountAuthenticated extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().accountAuthenticated((Account) args[0]);
            return 0;
        }
    }

    @ProxyMethod("getAuthTokenLabel")
    public static class getAuthTokenLabel extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().getAuthTokenLabel((IAccountManagerResponse) args[0],
                    (String) args[1],
                    (String) args[2]);
            return 0;
        }
    }

    @ProxyMethod("getPackagesAndVisibilityForAccount")
    public static class getPackagesAndVisibilityForAccount extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getPackagesAndVisibilityForAccount((Account) args[0]);
        }
    }

    @ProxyMethod("addAccountExplicitlyWithVisibility")
    public static class addAccountExplicitlyWithVisibility extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().addAccountExplicitlyWithVisibility((Account) args[0],
                    (String) args[1],
                    (Bundle) args[2],
                    (Map) args[3]
            );
        }
    }

    @ProxyMethod("setAccountVisibility")
    public static class setAccountVisibility extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().setAccountVisibility((Account) args[0],
                    (String) args[1],
                    (int) args[2]
            );
        }
    }

    @ProxyMethod("getAccountVisibility")
    public static class getAccountVisibility extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountVisibility((Account) args[0],
                    (String) args[1]
            );
        }
    }

    @ProxyMethod("getAccountsAndVisibilityForPackage")
    public static class getAccountsAndVisibilityForPackage extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return BAccountManager.get().getAccountsAndVisibilityForPackage((String) args[0],
                    (String) args[1]
            );
        }
    }

    @ProxyMethod("registerAccountListener")
    public static class registerAccountListener extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().registerAccountListener((String[]) args[0],
                    (String) args[1]
            );
            return 0;
        }
    }

    @ProxyMethod("unregisterAccountListener")
    public static class unregisterAccountListener extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BAccountManager.get().unregisterAccountListener((String[]) args[0],
                    (String) args[1]
            );
            return 0;
        }
    }
}
