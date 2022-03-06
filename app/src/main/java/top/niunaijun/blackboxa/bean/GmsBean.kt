package top.niunaijun.blackboxa.bean

/**
 *
 * @Description:
 * @Author: BlackBox
 * @CreateDate: 2022/3/2 21:30
 */
data class GmsBean(val userID:Int,val userName:String,var isInstalledGms:Boolean)


data class GmsInstallBean(val userID: Int,val success:Boolean,val msg:String)