package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2022/3/6 19:26
 */
data class InstalledAppBean(val name:String, val icon: Drawable, val packageName:String, val sourceDir:String, val isInstall:Boolean)
