package top.niunaijun.blackboxa.data

import android.content.pm.ApplicationInfo
import android.net.Uri
import android.webkit.URLUtil
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.BlackBoxCore.getPackageManager
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackbox.utils.AbiUtils
import top.niunaijun.blackboxa.app.AppManager
import java.io.File


/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 23:05
 */

class AppsRepository {

    @Volatile
    private var mInstalledList = mutableListOf<AppInfo>()

    @Volatile
    private var isLoading = true


    fun previewInstallList() {
        isLoading = true
        val installedApplications: List<ApplicationInfo> = getPackageManager().getInstalledApplications(0)
        val installedList = mutableListOf<AppInfo>()

        for (installedApplication in installedApplications) {
            val file = File(installedApplication.sourceDir)

            if ((installedApplication.flags and  ApplicationInfo.FLAG_SYSTEM) != 0) continue

            if (!AbiUtils.isSupport(file)) continue

            val isXpModule = BlackBoxCore.get().isXposedModule(file)

            val info = AppInfo(
                    installedApplication.loadLabel(getPackageManager()).toString(),
                    installedApplication.loadIcon(getPackageManager()),
                    installedApplication.packageName,
                    installedApplication.sourceDir,
                    isXpModule
            )
            installedList.add(info)
        }

        this.mInstalledList = installedList
        isLoading = false
    }

    fun getInstalledAppList(previewingLiveData: MutableLiveData<Boolean>, appsLiveData: MutableLiveData<List<AppInfo>>) {
        previewingLiveData.postValue(isLoading)
        if (isLoading) {
            Thread.sleep(500)
            getInstalledAppList(previewingLiveData, appsLiveData)
        } else {
            previewingLiveData.postValue(isLoading)
            appsLiveData.postValue(ArrayList(mInstalledList))
        }
    }

    fun getInstalledModuleList(previewingLiveData: MutableLiveData<Boolean>, appsLiveData: MutableLiveData<List<AppInfo>>) {

        previewingLiveData.postValue(isLoading)
        if (isLoading) {
            Thread.sleep(500)
            getInstalledModuleList(previewingLiveData, appsLiveData)
        } else {
            previewingLiveData.postValue(isLoading)
            val moduleList = mInstalledList.filter {
                it.isXpModule
            }
            appsLiveData.postValue(moduleList)
        }
    }


    fun getVmInstallList(userId: Int, appsLiveData: MutableLiveData<List<AppInfo>>) {
        val applicationList = BlackBoxCore.get().getInstalledApplications(0, userId)

        val appInfoList = mutableListOf<AppInfo>()
        applicationList.forEach {
            val info = AppInfo(
                    it.loadLabel(getPackageManager()).toString(),
                    it.loadIcon(getPackageManager()),
                    it.packageName,
                    it.sourceDir,
                    isInstalledXpModule(it.packageName)
            )

            appInfoList.add(info)
        }
        appsLiveData.postValue(appInfoList)
    }

    private fun isInstalledXpModule(packageName: String): Boolean {
        BlackBoxCore.get().installedXPModules.forEach {
            if (packageName == it.packageName) {
                return@isInstalledXpModule true
            }
        }

        return false
    }

    fun installApk(source: String, userId: Int, resultLiveData: MutableLiveData<String>) {
        val blackBoxCore = BlackBoxCore.get()
        val installResult = if (URLUtil.isValidUrl(source)) {
            val uri = Uri.parse(source)
            blackBoxCore.installPackageAsUser(uri, userId)
        } else {
            //source == packageName
            blackBoxCore.installPackageAsUser(source, userId)
        }

        if (installResult.success) {
            resultLiveData.postValue("安装成功")
        } else {
            resultLiveData.postValue("安装失败：" + installResult.msg)
        }
        scanUser()
    }

    fun launchApk(packageName: String, userId: Int, launchLiveData: MutableLiveData<Boolean>) {
        val result = BlackBoxCore.get().launchApk(packageName, userId)
        launchLiveData.postValue(result)
    }

    fun unInstall(packageName: String, userID: Int, resultLiveData: MutableLiveData<String>) {
        BlackBoxCore.get().uninstallPackageAsUser(packageName, userID)
        scanUser()
        resultLiveData.postValue("卸载成功")
    }

    private fun scanUser() {
        val blackBoxCore = BlackBoxCore.get()
        val userList = blackBoxCore.users

        if (userList.isEmpty()) {
            return
        }

        val id = userList.last().id

        if (blackBoxCore.getInstalledApplications(0, id).isEmpty()) {
            blackBoxCore.deleteUser(id)
            AppManager.mRemarkSharedPreferences.edit {
                remove("Remark$id")
            }
            scanUser()
        }
    }

    fun clearApkData(packageName: String, userID: Int, resultLiveData: MutableLiveData<String>) {
        BlackBoxCore.get().clearPackage(packageName,userID)
        resultLiveData.postValue("清除成功")
    }

}
