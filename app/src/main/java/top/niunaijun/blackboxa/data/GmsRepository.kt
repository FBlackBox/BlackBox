package top.niunaijun.blackboxa.data

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.app.AppManager
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.util.getString

/**
 *
 * @Description:
 * @Author: BlackBox
 * @CreateDate: 2022/3/2 21:14
 */
class GmsRepository {


    fun getGmsInstalledList(mInstalledLiveData: MutableLiveData<List<GmsBean>>) {
        val userList = arrayListOf<GmsBean>()

        BlackBoxCore.get().users.forEach {
            val userId = it.id
            val userName =
                AppManager.mRemarkSharedPreferences.getString("Remark$userId", "User $userId") ?: ""
            val isInstalled = BlackBoxCore.get().isInstallGms(userId)
            val bean = GmsBean(userId, userName, isInstalled)
            userList.add(bean)
        }

        mInstalledLiveData.postValue(userList)
    }

    fun installGms(
        userID: Int,
        mResultLiveData: MutableLiveData<String>,
        mUpdateInstalledLiveData: MutableLiveData<Pair<Int, Boolean>>
    ) {
        var isSuccess = false
        if (!BlackBoxCore.get().isInstallGms(userID)) {
            isSuccess = BlackBoxCore.get().installGms(userID)
        }

        val result = if (isSuccess) {
            getString(R.string.install_success)
        } else {
            getString(R.string.install_fail_no_msg)
        }

        mResultLiveData.postValue(result)
        mUpdateInstalledLiveData.postValue(userID to isSuccess)
    }

    fun uninstallGms(
        userID: Int,
        mResultLiveData: MutableLiveData<String>,
        mUpdateInstalledLiveData: MutableLiveData<Pair<Int, Boolean>>
    ) {
        var isSuccess = false
        if (BlackBoxCore.get().isInstallGms(userID)) {
            isSuccess = BlackBoxCore.get().uninstallGms(userID)
        }

        val result = if (isSuccess) {
            getString(R.string.uninstall_success)
        } else {
            getString(R.string.uninstall_fail)
        }

        mResultLiveData.postValue(result)

        mUpdateInstalledLiveData.postValue(userID to isSuccess)
    }
}