package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:57
 */
data class AppInfo(val name:String,val icon:Drawable,val packageName:String,val sourceDir:String,val isXpModule:Boolean)