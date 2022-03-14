package top.niunaijun.blackboxa.bean

import top.niunaijun.blackbox.entity.BLocation

data class FakeLocationBean(
    val userID: Int,
    val packageName: String,
    var fakeLocationPattern: Int,
    var fakeLocation: BLocation
)

data class FakeLocationBeanInstallBean(val userID: Int, val success: Boolean, val msg: String)