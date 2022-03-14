package top.niunaijun.blackboxa.data

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.app.BFakeLocationManager
import top.niunaijun.blackbox.utils.AbiUtils
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.bean.InstalledAppBean
import java.io.File

/**
 * getInstalledApplications and query fake location of each of them.
 * @Description:
 * @Author: BlackBoxing
 * @CreateDate: 2022/3/12 21:14
 */
class FakeLocationRepository {
    val TAG: String = "FakeLocationRepository"

    fun setPattern(userId: Int, pkg: String, pattern: Int) {
        BFakeLocationManager.get().setPattern(userId, pkg, pattern)
    }

    fun getPattern(userId: Int, pkg: String) {
        return BFakeLocationManager.get().getPattern(userId, pkg)
    }


    fun getInstalledAppList(
        userID: Int,
        loadingLiveData: MutableLiveData<Boolean>,
        appsLiveData: MutableLiveData<List<InstalledAppBean>>
    ) {
        loadingLiveData.postValue(true)
        val installedList = mutableListOf<AppInfo>()
        val installedApplications: List<ApplicationInfo> =
            BlackBoxCore.get().getInstalledApplications(0, userID)
        for (installedApplication in installedApplications) {
            val file = File(installedApplication.sourceDir)

            if ((installedApplication.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue

            if (!AbiUtils.isSupport(file)) continue

            val isXpModule = BlackBoxCore.get().isXposedModule(file)

            val info = AppInfo(
                installedApplication.loadLabel(BlackBoxCore.getPackageManager()).toString(),
                installedApplication.loadIcon(BlackBoxCore.getPackageManager()),
                installedApplication.packageName,
                installedApplication.sourceDir,
                isXpModule
            )
            installedList.add(info)
        }

        val blackBoxCore = BlackBoxCore.get()
        val newInstalledList = installedList.map {
            InstalledAppBean(
                it.name,
                it.icon,
                it.packageName,
                it.sourceDir,
                blackBoxCore.isInstalled(it.packageName, userID)
            )
        }
        Log.d(TAG, newInstalledList.joinToString(","))
        appsLiveData.postValue(newInstalledList)
        loadingLiveData.postValue(false)

    }
    
}