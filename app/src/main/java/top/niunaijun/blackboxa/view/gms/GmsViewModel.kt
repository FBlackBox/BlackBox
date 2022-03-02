package top.niunaijun.blackboxa.view.gms

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.data.GmsRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

/**
 *
 * @Description: gms viewModel
 * @Author: BlackBox
 * @CreateDate: 2022/3/2 21:11
 */
class GmsViewModel(private val mRepo: GmsRepository) : BaseViewModel() {

    val mResultLiveData = MutableLiveData<String>()

    val mInstalledLiveData = MutableLiveData<List<GmsBean>>()

    val mUpdateInstalledLiveData = MutableLiveData<Pair<Int,Boolean>>()

    fun getInstalledUser() {
        launchOnUI {
            mRepo.getGmsInstalledList(mInstalledLiveData)
        }
    }

    fun installGms(userID: Int) {
        launchOnUI {
            mRepo.installGms(userID, mResultLiveData,mUpdateInstalledLiveData)
        }
    }

    fun uninstallGms(userID: Int) {
        launchOnUI {
            mRepo.uninstallGms(userID, mResultLiveData,mUpdateInstalledLiveData)
        }
    }
}