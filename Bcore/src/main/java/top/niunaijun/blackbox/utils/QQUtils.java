package top.niunaijun.blackbox.utils;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by BlackBox on 2022/2/20.
 */
public class QQUtils {
    public static void hackLog(Context context) {
        try {
            Class<?> aClass = context.getClassLoader().loadClass("com.tencent.qphone.base.util.QLog");
            Field uin_reportlog_level = aClass.getDeclaredField("UIN_REPORTLOG_LEVEL");
            uin_reportlog_level.setAccessible(true);
            uin_reportlog_level.set(null, 1000);
            Log.d("QQUtils", "hackLog: success");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
