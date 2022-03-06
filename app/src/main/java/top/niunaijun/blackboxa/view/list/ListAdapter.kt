package top.niunaijun.blackboxa.view.list

import android.view.View
import android.view.ViewGroup
import top.niunaijun.blackboxa.bean.InstalledAppBean
import top.niunaijun.blackboxa.databinding.ItemPackageBinding
import top.niunaijun.blackboxa.util.newBindingViewHolder
import top.niunaijun.blackboxa.view.base.BaseAdapter

/**
 *
 * @Description: 软件显示界面适配器
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:52
 */

class ListAdapter : BaseAdapter<ItemPackageBinding, InstalledAppBean>() {
    override fun getViewBinding(parent: ViewGroup): ItemPackageBinding {
        return newBindingViewHolder(parent, false)

    }

    override fun initView(binding: ItemPackageBinding, position: Int, data: InstalledAppBean) {
        binding.icon.setImageDrawable(data.icon)
        binding.name.text = data.name
        binding.packageName.text = data.packageName
        binding.cornerLabel.visibility = if (data.isInstall) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}