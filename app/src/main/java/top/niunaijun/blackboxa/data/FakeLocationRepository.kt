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
 * mode and location configuration are respectively concept.
 * Location config just store the location but mode decides whether turns on it
 * each application has three pattern.Global: use global fake location,
 * self: use own config, close: use real config
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

    fun setLocation(userId: Int, pkg: String, location: BLocation) {
        BLocationManager.get().setLocation(userId, pkg, location)
    }

    fun getInstalledAppList(
        userID: Int,
        appsFakeLiveData: MutableLiveData<List<FakeLocationBean>>
    ) {
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
    }
}