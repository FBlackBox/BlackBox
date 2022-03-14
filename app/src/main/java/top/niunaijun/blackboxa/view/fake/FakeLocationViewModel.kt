package top.niunaijun.blackboxa.view.fake

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.bean.GmsInstallBean
import top.niunaijun.blackboxa.data.FakeLocationRepository
import top.niunaijun.blackboxa.data.GmsRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

class FakeLocationViewModel (private val mRepo: FakeLocationRepository) : BaseViewModel() {

    val mInstalledLiveData = MutableLiveData<List<GmsBean>>()

    val mUpdateInstalledLiveData = MutableLiveData<GmsInstallBean>()

    fun previewInstalledList() {
        launchOnUI{
            mRepo.previewInstallList()
        }
    }

    fun getInstallAppList(userID:Int){
        launchOnUI {
            mRepo.getInstalledAppList(userID,loadingLiveData,appsLiveData)
        }
    }

}