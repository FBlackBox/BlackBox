package top.niunaijun.blackboxa.util

import android.content.Context
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import top.niunaijun.blackboxa.app.App

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/2 0:13
 */

fun Context.toast(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
}

fun toast(msg: String){
    App.getContext().toast(msg)
}