package top.niunaijun.blackboxa.data

import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.entity.location.BLocation
import top.niunaijun.blackbox.fake.frameworks.BLocationManager
import top.niunaijun.blackboxa.bean.FakeLocationBean

/**
 * getInstalledApplications and query fake location of each of them.
 * @Description:
 * @Author: BlackBoxing
 * @CreateDate: 2022/3/12 21:14
 */
class FakeLocationRepository {
    val TAG: String = "FakeLocationRepository"

    fun setPattern(userId: Int, pkg: String, pattern: Int) {
        BLocationManager.get().setPattern(userId, pkg, pattern)
    }

    private fun getPattern(userId: Int, pkg: String): Int {
        return BLocationManager.get().getPattern(userId, pkg)
    }

    private fun getLocation(userId: Int, pkg: String): BLocation? {
        return BLocationManager.get().getLocation(userId, pkg)
    }

    fun getInstalledAppList(
        userID: Int,
        loadingLiveData: MutableLiveData<Boolean>,
        appsFakeLiveData: MutableLiveData<List<FakeLocationBean>>
    ) {
        loadingLiveData.postValue(true)
        val installedList = mutableListOf<FakeLocationBean>()
        val installedApplications: List<ApplicationInfo> =
            BlackBoxCore.get().getInstalledApplications(0, userID)
        // List<ApplicationInfo> -> List<FakeLocationBean>
        for (installedApplication in installedApplications) {
//            val file = File(installedApplication.sourceDir)
//
//            if ((installedApplication.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue
//
//            if (!AbiUtils.isSupport(file)) continue
//
//            val isXpModule = BlackBoxCore.get().isXposedModule(file)

            val info = FakeLocationBean(
                userID,
                installedApplication.loadLabel(BlackBoxCore.getPackageManager()).toString(),
                installedApplication.loadIcon(BlackBoxCore.getPackageManager()),
                installedApplication.packageName,
                getPattern(userID, installedApplication.packageName),
                getLocation(userID, installedApplication.packageName)
            )
            installedList.add(info)
        }

        Log.d(TAG, installedList.joinToString(","))
        appsFakeLiveData.postValue(installedList)
        loadingLiveData.postValue(false)
    }
}