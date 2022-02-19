package top.niunaijun.blackboxa.view.xp

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackboxa.bean.XpModuleInfo
import top.niunaijun.blackboxa.data.XpRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel
import java.io.File

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/2 20:55
 */
class XpViewModel(private val repo:XpRepository):BaseViewModel() {

    val appsLiveData = MutableLiveData<List<XpModuleInfo>>()

    val resultLiveData = MutableLiveData<String>()

    val launchLiveData = MutableLiveData<Boolean>()

    fun getInstalledModule() {
        launchOnUI {
            repo.getInstallModules(appsLiveData)
        }
    }

    fun installModule(source:String) {
        launchOnUI {
            repo.installModule(source,resultLiveData)
        }
    }


    fun unInstallModule(packageName: String){
        launchOnUI {
            repo.unInstallModule(packageName,resultLiveData)
        }
    }
}