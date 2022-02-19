package black.android.app;

import android.content.Intent;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

@BClassName("android.app.IActivityManager")
public interface IActivityManagerN {
    @BMethod
    Boolean finishActivity(IBinder IBinder0, int int1, Intent Intent2, int int3);
}
