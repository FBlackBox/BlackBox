package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/2 21:03
 */
data class XpModuleInfo(
        val name: String,
        val desc: String,
        val packageName: String,
        val version: String,
        var enable:Boolean,
        val icon: Drawable
)
