package black.android.app;

import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.IBinder;

import java.util.List;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BMethod;

@BClassName("android.app.IApplicationThread")
public interface IApplicationThread {
    @BMethod
    void scheduleBindService(IBinder IBinder0, Intent Intent1, boolean boolean2);

    @BMethod
    void scheduleCreateService(IBinder IBinder0, ServiceInfo ServiceInfo1);

    @BMethod
    void scheduleNewIntent(List List0, IBinder IBinder1);

    @BMethod
    void scheduleServiceArgs(IBinder IBinder0, int int1, int int2, Intent Intent3);

    @BMethod
    void scheduleStopService(IBinder IBinder0);

    @BMethod
    void scheduleUnbindService(IBinder IBinder0, Intent Intent1);
}
