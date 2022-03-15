package top.niunaijun.blackboxa.bean

import android.graphics.drawable.Drawable
import top.niunaijun.blackbox.entity.location.BLocation

data class FakeLocationBean(
    val userID: Int,
    val name: String,
    val icon: Drawable,
    val packageName: String,
    var fakeLocationPattern: Int,
    var fakeLocation: BLocation?
)

data class FakeLocationBeanInstallBean(val userID: Int, val success: Boolean, val msg: String)