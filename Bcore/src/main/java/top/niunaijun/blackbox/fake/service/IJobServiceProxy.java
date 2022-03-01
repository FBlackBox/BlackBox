package top.niunaijun.blackbox.fake.service;

import android.app.job.JobInfo;
import android.content.Context;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.app.job.BRIJobSchedulerStub;
import black.android.os.BRServiceManager;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;

/**
 * Created by Milk on 4/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class IJobServiceProxy extends BinderInvocationStub {
    public static final String TAG = "JobServiceStub";

    public IJobServiceProxy() {
        super(BRServiceManager.get().getService(Context.JOB_SCHEDULER_SERVICE));
    }

    @Override
    protected Object getWho() {
        IBinder jobScheduler = BRServiceManager.get().getService("jobscheduler");
        return BRIJobSchedulerStub.get().asInterface(jobScheduler);
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    @ProxyMethod("schedule")
    public static class Schedule extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            JobInfo jobInfo = (JobInfo) args[0];
            JobInfo proxyJobInfo = BlackBoxCore.getBJobManager()
                    .schedule(jobInfo);
            args[0] = proxyJobInfo;
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("cancel")
    public static class Cancel extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            args[0] = BlackBoxCore.getBJobManager()
                    .cancel(BActivityThread.getAppConfig().processName, (Integer) args[0]);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("cancelAll")
    public static class CancelAll extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            BlackBoxCore.getBJobManager().cancelAll(BActivityThread.getAppConfig().processName);
            return method.invoke(who, args);
        }
    }


    @ProxyMethod("enqueue")
    public static class Enqueue extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            JobInfo jobInfo = (JobInfo) args[0];
            JobInfo proxyJobInfo = BlackBoxCore.getBJobManager()
                    .schedule(jobInfo);
            args[0] = proxyJobInfo;
            return method.invoke(who, args);
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }
}
