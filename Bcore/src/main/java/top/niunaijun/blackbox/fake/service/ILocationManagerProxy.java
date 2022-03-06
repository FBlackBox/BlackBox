package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import black.android.location.BRILocationManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.entity.BLocation;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.MethodParameterUtils;

/**
 * Created by Milk on 4/8/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ILocationManagerProxy extends BinderInvocationStub {
    public static final String TAG = "ILocationManagerProxy";
    public ILocationManagerProxy() {
        super(BRServiceManager.get().getService(Context.LOCATION_SERVICE));
    }

    @Override
    protected Object getWho() {
        return BRILocationManagerStub.get().asInterface(BRServiceManager.get().getService(Context.LOCATION_SERVICE));
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodParameterUtils.replaceLastAppPkg(args);
        return super.invoke(proxy, method, args);
    }

    @ProxyMethod("registerGnssStatusCallback")
    public static class RegisterGnssStatusCallback extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "RegisterGnssStatusCallback");
//            MethodParameterUtils.replaceLastAppPkg(args);
//            return method.invoke(who, args);
            return true;
        }
    }
    @ProxyMethod("getLastLocation")
    public static class GetLastLocation extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getLastLocation");

//            BLocation bLocation = new BLocation(20.15154, 45.58584);
//            return bLocation.convert2SystemLocation();
//            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
    @ProxyMethod("getLastKnownLocation")
    public static class GetLastKnownLocation extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getLastKnownLocation");
//            BLocation bLocation = new BLocation(20.15154, 45.58584);
//            return bLocation.convert2SystemLocation();
//            Location location = new Location();
//            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
    @ProxyMethod("requestLocationUpdates")
    public static class RequestLocationUpdates extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "requestLocationUpdates");
//            BLocation bLocation = new BLocation();
//            return bLocation.convert2SystemLocation();
//            Location location = new Location();
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }
}
