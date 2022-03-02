package top.niunaijun.blackboxa.view.gms

import android.view.ViewGroup
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.databinding.ItemGmsBinding
import top.niunaijun.blackboxa.view.base.BaseAdapter

/**
 *
 * @Description:
 * @Author: BlackBox
 * @CreateDate: 2022/3/2 21:13
 */
class GmsAdapter : BaseAdapter<ItemGmsBinding, GmsBean>() {
    override fun getViewBinding(parent: ViewGroup): ItemGmsBinding {
        return ItemGmsBinding.inflate(getLayoutInflater(parent), parent, false)
    }

    override fun initView(binding: ItemGmsBinding, position: Int, data: GmsBean) {
        binding.tvTitle.text = data.userName
        binding.checkbox.isChecked = data.isInstalledGms
        binding.checkbox.setOnCheckedChangeListener  { buttonView, isChecked ->
            if(buttonView.isPressed){
                binding.root.performClick()
            }
        }
    }
}