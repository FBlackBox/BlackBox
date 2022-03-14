package top.niunaijun.blackboxa.bean

data class FakeLocationBean(val userID:Int,val userName:String,var isInstalledGms:Boolean)


data class FakeLocationBeanInstallBean(val userID: Int,val success:Boolean,val msg:String)