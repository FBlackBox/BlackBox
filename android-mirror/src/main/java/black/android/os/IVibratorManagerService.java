package black.android.os;

import android.os.IBinder;
import android.os.IInterface;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Created by BlackBox on 2022/3/19.
 */
@BClassName("android.os.IVibratorManagerService")
public interface IVibratorManagerService {

    @BClassName("android.os.IVibratorManagerService$Stub")
    interface Stub {
        @BStaticMethod
        IInterface asInterface(IBinder IBinder0);
    }
}
