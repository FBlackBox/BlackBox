package top.niunaijun.blackboxa.app.rocker

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 *
 * @Description:
 * @Author: kotlinMiku
 * @CreateDate: 2022/3/19 20:08
 */
interface BaseActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }
    override fun onActivityStarted(activity: Activity) {

    }
    override fun onActivityResumed(activity: Activity) {

    }
    override fun onActivityPaused(activity: Activity) {

    }
    override fun onActivityStopped(activity: Activity) {

    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }
    override fun onActivityDestroyed(activity: Activity) {

    }
}