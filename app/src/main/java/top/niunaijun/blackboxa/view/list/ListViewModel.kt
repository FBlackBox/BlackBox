package top.niunaijun.blackboxa.view.list

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.data.AppsRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:36
 */
class ListViewModel(private val repo: AppsRepository) : BaseViewModel() {

    val appsLiveData = MutableLiveData<List<AppInfo>>()

    val previewingLiveData = MutableLiveData<Boolean>()

    fun previewInstalledList() {
        launchOnUI{
            repo.previewInstallList()
        }
    }

    fun getInstallAppList(){
        launchOnUI {
            repo.getInstalledAppList(previewingLiveData,appsLiveData)
        }
    }

    fun getInstalledModules(){
        launchOnUI {
            repo.getInstalledModuleList(previewingLiveData,appsLiveData)
        }
    }

}