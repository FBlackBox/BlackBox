package black.android.app;

import android.app.Application;
import android.app.IServiceConnection;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.IInterface;

import java.io.File;
import java.lang.ref.WeakReference;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

@BClassName("android.app.LoadedApk")
public interface LoadedApk {
    @BField
    Application mApplication();

    @BField
    ApplicationInfo mApplicationInfo();

    @BField
    File mCredentialProtectedDataDirFile();

    @BField
    String mDataDir();

    @BField
    File mDataDirFile();

    @BField
    File mDeviceProtectedDataDirFile();

    @BField
    String mLibDir();

    @BField
    boolean mSecurityViolation();

    @BField
    boolean mPackageName();

    @BMethod
    Object getResources();

    @BMethod
    IServiceConnection forgetServiceDispatcher(Context Context0, ServiceConnection ServiceConnection1);

    @BMethod
    ClassLoader getClassLoader();

    @BMethod
    IServiceConnection getServiceDispatcher(ServiceConnection ServiceConnection0, Context Context1, Handler Handler2, int int3);

    @BMethod
    Application makeApplication(boolean boolean0, Instrumentation Instrumentation1);

    @BClassName("android.app.LoadedApk$ServiceDispatcher")
    interface ServiceDispatcher {
        @BField
        ServiceConnection mConnection();

        @BField
        Context mContext();

        @BClassName("android.app.LoadedApk$ServiceDispatcher$InnerConnection")
        interface InnerConnection {
            @BField
            WeakReference<?> mDispatcher();
        }
    }

    @BClassName("android.app.LoadedApk$ReceiverDispatcher")
    interface ReceiverDispatcher {
        @BField
        IIntentReceiver mIIntentReceiver();

        @BField
        BroadcastReceiver mReceiver();

        @BMethod
        IInterface getIIntentReceiver();

        @BClassName("android.app.LoadedApk$ReceiverDispatcher$InnerReceiver")
        interface InnerReceiver {
            @BField
            WeakReference<?> mDispatcher();
        }
    }
}
