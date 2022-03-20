package top.niunaijun.blackbox.app.configuration;

import android.app.Application;
import android.content.Context;

/**
 * Created by Milk on 5/5/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class AppLifecycleCallback {
    public static AppLifecycleCallback EMPTY = new AppLifecycleCallback() {

    };

    public void beforeCreateApplication(String packageName, String processName, Context context, int userId) {

    }

    public void beforeApplicationOnCreate(String packageName, String processName, Application application, int userId) {

    }

    public void afterApplicationOnCreate(String packageName, String processName, Application application, int userId) {

    }
}
