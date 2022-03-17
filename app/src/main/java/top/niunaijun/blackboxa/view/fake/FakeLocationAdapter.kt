package top.niunaijun.blackboxa.view.fake

import android.view.View
import android.view.ViewGroup
import top.niunaijun.blackbox.fake.frameworks.BLocationManager
import top.niunaijun.blackboxa.bean.FakeLocationBean
import top.niunaijun.blackboxa.databinding.ItemFakeBinding
import top.niunaijun.blackboxa.util.newBindingViewHolder
import top.niunaijun.blackboxa.view.base.BaseAdapter

/**
 *
 * @Description: 软件显示界面适配器
 * @Author: BlackBoxing
 * @CreateDate: 2022/3/14
 */

class FakeLocationAdapter : BaseAdapter<ItemFakeBinding, FakeLocationBean>() {
    override fun getViewBinding(parent: ViewGroup): ItemFakeBinding {
        return newBindingViewHolder(parent, false)

    }

    override fun initView(binding: ItemFakeBinding, position: Int, data: FakeLocationBean) {
        binding.icon.setImageDrawable(data.icon)
        binding.name.text = data.name
        if (data.fakeLocation == null || data.fakeLocationPattern == BLocationManager.CLOSE_MODE) {
            binding.fakeLocation.text = "Real Location"
        } else {
            binding.fakeLocation.text =
                String.format("%f, %f", data.fakeLocation!!.latitude, data.fakeLocation!!.longitude)
        }
//        if(data.fakeLocationPattern != 0){
//            binding.fakeLocation.text = String.format("%f, %f", data.fakeLocation.latitude, data.fakeLocation.longitude)
//        }
        binding.cornerLabel.visibility = View.VISIBLE
    }
}