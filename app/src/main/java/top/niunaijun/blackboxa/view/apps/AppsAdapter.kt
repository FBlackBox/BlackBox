package top.niunaijun.blackboxa.view.apps

import android.view.View
import android.view.ViewGroup
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.databinding.ItemAppBinding
import top.niunaijun.blackboxa.util.inflateBinding
import top.niunaijun.blackboxa.view.base.BaseAdapter

/**
 *
 * @Description: 软件显示界面适配器
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:52
 */

class AppsAdapter : BaseAdapter<ItemAppBinding, AppInfo>() {
    override fun getViewBinding(parent: ViewGroup): ItemAppBinding {
        return inflateBinding(getLayoutInflater(parent))

    }

    override fun initView(binding: ItemAppBinding, position: Int, data: AppInfo) {
        binding.icon.setImageDrawable(data.icon)
        binding.name.text = data.name
        if(data.isXpModule){
            binding.cornerLabel.visibility = View.VISIBLE
        }else{
            binding.cornerLabel.visibility = View.INVISIBLE
        }
    }
}