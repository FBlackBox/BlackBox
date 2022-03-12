package top.niunaijun.blackboxa.app

import android.content.Context
import android.content.SharedPreferences
import top.niunaijun.blackboxa.view.main.BlackBoxLoader

object AppManager {
    @JvmStatic
    val mBlackBoxLoader by lazy {
        BlackBoxLoader()
    }

    @JvmStatic
    val mBlackBoxCore by lazy {
        mBlackBoxLoader.getBlackBoxCore()
    }

    @JvmStatic
    val mRemarkSharedPreferences: SharedPreferences by lazy {
        App.getContext().getSharedPreferences("UserRemark",Context.MODE_PRIVATE)
    }

    fun doAttachBaseContext(context: Context) {
        try {
            mBlackBoxLoader.attachBaseContext(context)
            mBlackBoxLoader.addLifecycleCallback()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doOnCreate(context: Context) {
        mBlackBoxLoader.doOnCreate(context)
        initThirdService(context)
    }

    private fun initThirdService(context: Context) {}
}
