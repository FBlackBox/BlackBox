package top.niunaijun.blackbox.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.proxy.record.ProxyBroadcastRecord;

/**
 * Created by Milk on 4/7/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ProxyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "StubBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ProxyBroadcastRecord record = ProxyBroadcastRecord.create(intent);
        Log.d(TAG, "onReceive: " + record);

        try {
            BlackBoxCore.getContext().sendBroadcast(record.mIntent);
        } catch (Exception ignored) {
        }
    }
}
