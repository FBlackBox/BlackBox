package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;

import black.android.location.BRILocationManagerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
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
            Log.d(TAG, "PackageName: "+BActivityThread.getAppPackageName());
            Log.d(TAG, "UserId: "+String.valueOf(BActivityThread.getUserId()));

            BLocation bLocation = new BLocation(30.263214, 120.159073);
            return bLocation.convert2SystemLocation();
//            MethodParameterUtils.replaceFirstAppPkg(args);
//            return method.invoke(who, args);
        }
    }
    @ProxyMethod("getLastKnownLocation")
    public static class GetLastKnownLocation extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getLastKnownLocation");
            Log.d(TAG, "PackageName: "+BActivityThread.getAppPackageName());
            Log.d(TAG, "UserId: "+String.valueOf(BActivityThread.getUserId()));
            Log.d(TAG,BlackBoxCore.getHostPkg());
//            BLocation bLocation = new BLocation(30.263214, 120.159073);
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
            Log.d(TAG, "PackageName: "+BActivityThread.getAppPackageName());
            Log.d(TAG, "UserId: "+ BActivityThread.getUserId());
//            BLocation bLocation = new BLocation(30.263214, 120.159073);
//            return bLocation.convert2SystemLocation();
//            Location location = new Location();
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getProviderProperties")
    public static class GetProviderProperties extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getProviderProperties");
//            BLocation bLocation = new BLocation(30.263214, 120.159073);
//            return bLocation.convert2SystemLocation();
//            Location location = new Location();
//            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("removeGpsStatusListener")
    public static class RemoveGpsStatusListener extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "removeGpsStatusListener");
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getBestProviderr")
    public static class GetBestProvider extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getBestProvider");
            return LocationManager.GPS_PROVIDER;
        }
    }

    @ProxyMethod("getAllProviders")
    public static class GetAllProviders extends MethodHook {

        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getBestProvider");
            return Arrays.asList(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER);
        }
    }
}
