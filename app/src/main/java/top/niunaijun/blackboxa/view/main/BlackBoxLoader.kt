package top.niunaijun.blackboxa.view.main

import android.app.Application
import android.content.Context
import android.util.Log
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.app.configuration.AppLifecycleCallback
import top.niunaijun.blackbox.app.configuration.ClientConfiguration
import top.niunaijun.blackbox.utils.FileUtils
import top.niunaijun.blackbox.utils.compat.BuildCompat
import top.niunaijun.blackboxa.app.App
import top.niunaijun.blackboxa.biz.cache.AppSharedPreferenceDelegate
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/6 23:38
 */
class BlackBoxLoader {


    private var mHideRoot by AppSharedPreferenceDelegate(App.getContext(), false)
    private var mHideXposed by AppSharedPreferenceDelegate(App.getContext(), false)

    private val mLogDir = getLogDir(App.getContext())

    fun hideRoot(): Boolean {
        return mHideRoot
    }

    fun invalidHideRoot(hideRoot: Boolean) {
        this.mHideRoot = hideRoot
    }

    fun invalidHideXposed(hideXposed: Boolean) {
        this.mHideXposed = hideXposed
    }

    fun hideXposed(): Boolean {
        return mHideXposed
    }

    fun getBlackBoxCore(): BlackBoxCore {
        return BlackBoxCore.get()
    }

    fun addLifecycleCallback() {
        BlackBoxCore.get().addAppLifecycleCallback(object : AppLifecycleCallback() {
            override fun beforeCreateApplication(
                packageName: String?,
                processName: String?,
                context: Context?
            ) {
                Log.d(TAG, "beforeCreateApplication: pkg $packageName, processName $processName")
            }

            override fun beforeApplicationOnCreate(
                packageName: String?,
                processName: String?,
                application: Application?
            ) {
                Log.d(TAG, "beforeApplicationOnCreate: pkg $packageName, processName $processName")
            }

            override fun afterApplicationOnCreate(
                packageName: String?,
                processName: String?,
                application: Application?
            ) {
                Log.d(TAG, "afterApplicationOnCreate: pkg $packageName, processName $processName")
            }
        })
    }

    fun attachBaseContext(context: Context) {
        BlackBoxCore.get().doAttachBaseContext(context, object : ClientConfiguration() {
            override fun getHostPackageName(): String {
                return context.packageName
            }

            override fun isHideRoot(): Boolean {
                return mHideRoot
            }

            override fun isHideXposed(): Boolean {
                return mHideXposed
            }
        })
    }

    fun doOnCreate(context: Context) {
        BlackBoxCore.get().doCreate()

        BlackBoxCore.get().setExceptionHandler { t, e ->
            val logFile = File(mLogDir, "${System.currentTimeMillis()}.log")
            PrintWriter(FileWriter(logFile)).use {
                e.printStackTrace(it)
            }
        }
    }


    companion object {

        val TAG: String = BlackBoxLoader::class.java.simpleName

        fun getLogDir(context: Context): String {
            return if (BuildCompat.isR()) {
                val log = File(
                    context.externalCacheDir?.parentFile?.parentFile?.parentFile?.parentFile,
                    "Download/BlackBoxLog"
                )
                FileUtils.mkdirs(log)
                log.absolutePath
            } else {
                val log = File(context.externalCacheDir?.parentFile, "BlackBoxLog")
                FileUtils.mkdirs(log)
                log.absolutePath
            }
        }
    }

}