package black.com.android.internal.telephony;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

@BClassName("com.android.internal.telephony.IPhoneSubInfo")
public interface IPhoneSubInfo {
    @BClassName("com.android.internal.telephony.IPhoneSubInfo$Stub")
    interface Stub {
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
