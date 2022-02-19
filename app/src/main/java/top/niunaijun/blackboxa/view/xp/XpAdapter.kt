package top.niunaijun.blackboxa.view.xp

import android.view.ViewGroup
import top.niunaijun.blackboxa.bean.XpModuleInfo
import top.niunaijun.blackboxa.databinding.ItemXpBinding
import top.niunaijun.blackboxa.util.inflateBinding
import top.niunaijun.blackboxa.util.newBindingViewHolder
import top.niunaijun.blackboxa.view.base.BaseAdapter


/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/2 21:32
 */
class XpAdapter: BaseAdapter<ItemXpBinding, XpModuleInfo>() {

    private var onCheckChange : ((data:XpModuleInfo,isChecked:Boolean)->Unit)? =null

    override fun getViewBinding(parent: ViewGroup): ItemXpBinding {
        return newBindingViewHolder(parent,false)
    }

    override fun initView(binding: ItemXpBinding, position: Int, data: XpModuleInfo) {
        binding.icon.setImageDrawable(data.icon)
        binding.name.text = data.name
        binding.desc.text = data.desc
        binding.enable.isChecked = data.enable
        binding.enable.setOnCheckedChangeListener { _, isChecked ->
            if(onCheckChange!=null){
                onCheckChange!!(data,isChecked)
                data.enable = isChecked
            }
        }
    }

    fun setOnCheckChangeListener(func :(data:XpModuleInfo,isChecked:Boolean)->Unit){
        this.onCheckChange = func
    }
}