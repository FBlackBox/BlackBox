package top.niunaijun.blackboxa.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 *
 * @Description:
 * @Author: kotlinMiku
 * @CreateDate: 2022/4/17 16:32
 */
object ContextUtil {

    fun Context.openAppSystemSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", packageName, null)
        })
    }
}