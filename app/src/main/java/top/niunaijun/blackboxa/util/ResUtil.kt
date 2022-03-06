package top.niunaijun.blackboxa.util

import androidx.annotation.StringRes
import top.niunaijun.blackboxa.app.App


fun getString(@StringRes id:Int,vararg arg:String):String{
    if(arg.isEmpty()){
        return App.getContext().getString(id)
    }
    return App.getContext().getString(id,*arg)
}

