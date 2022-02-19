package black.com.android.internal.app;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

@BClassName("com.android.internal.app.ISmtOpsService")
public interface ISmtOpsService {
    @BClassName("com.android.internal.app.ISmtOpsService$Stub")
    interface Stub {
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
