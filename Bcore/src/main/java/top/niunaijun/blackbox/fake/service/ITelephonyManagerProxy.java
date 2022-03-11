package top.niunaijun.blackbox.fake.service;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

import black.android.os.BRServiceManager;
import black.com.android.internal.telephony.BRITelephonyStub;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.Md5Utils;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ITelephonyManagerProxy extends BinderInvocationStub {
    public static final String TAG = "ITelephonyManagerProxy";
    public ITelephonyManagerProxy() {
        super(BRServiceManager.get().getService(Context.TELEPHONY_SERVICE));
    }

    @Override
    protected Object getWho() {
        IBinder telephony = BRServiceManager.get().getService(Context.TELEPHONY_SERVICE);
        return BRITelephonyStub.get().asInterface(telephony);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("getDeviceId")
    public static class GetDeviceId extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    @ProxyMethod("getImeiForSlot")
    public static class getImeiForSlot extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    @ProxyMethod("getMeidForSlot")
    public static class GetMeidForSlot extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
//                MethodParameterUtils.replaceFirstAppPkg(args);
//                return method.invoke(who, args);
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    @ProxyMethod("isUserDataEnabled")
    public static class IsUserDataEnabled extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return true;
        }
    }

    @ProxyMethod("getSubscriberId")
    public static class GetSubscriberId extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    @ProxyMethod("getDeviceIdWithFeature")
    public static class GetDeviceIdWithFeature extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return Md5Utils.md5(BlackBoxCore.getHostPkg());
        }
    }

    @ProxyMethod("getCellLocation")
    public static class GetCellLocation extends MethodHook{
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getCellLocation");
//            BCell bCell= new BCell(460, 1, 13850, 4098);
//            return bCell;
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getAllCellInfo")
    public static class GetAllCellInfo extends MethodHook{
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getAllCellInfo");
//            List<BCell> allCellInfo = new ArrayList<BCell>();
//            BCell bCell= new BCell(460, 1, 13850, 4098);
//            allCellInfo.add(bCell);
//            return allCellInfo;
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getNetworkOperator")
    public static class GetNetworkOperator extends MethodHook{
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getNetworkOperator");
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("getNeighboringCellInfo")
    public static class GetNeighboringCellInfo extends MethodHook{
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Log.d(TAG, "getNetworkOperator");
//            List<BCell> allCellInfo = new ArrayList<BCell>();
//            BCell bCell= new BCell(460, 1, 13850, 4098);
//
//            BCell bCell1= new BCell(460, 0, 22547, 79893377);
//            allCellInfo.add(bCell1);

//
//            return allCellInfo;
            return method.invoke(who, args);
        }
    }
}
