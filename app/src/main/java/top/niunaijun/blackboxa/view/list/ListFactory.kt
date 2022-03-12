package top.niunaijun.blackboxa.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.niunaijun.blackboxa.data.AppsRepository

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:36
 */
@Suppress("UNCHECKED_CAST")
class ListFactory(private val appsRepository: AppsRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListViewModel(appsRepository) as T
    }
}