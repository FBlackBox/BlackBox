package top.niunaijun.blackbox.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import black.android.content.BRBroadcastReceiver;
import top.niunaijun.blackbox.proxy.record.ProxyBroadcastRecord;

/**
 * Created by BlackBox on 2022/2/25.
 */
public class ProxyBroadcastReceiver extends BroadcastReceiver {
    private final BroadcastReceiver mReceiver;

    public ProxyBroadcastReceiver(BroadcastReceiver receiver) {
        mReceiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Object pendingResult = BRBroadcastReceiver.get(this).getPendingResult();
        BRBroadcastReceiver.get(mReceiver).setPendingResult(pendingResult);

        intent.setExtrasClassLoader(context.getClassLoader());
        ProxyBroadcastRecord proxyBroadcastRecord = ProxyBroadcastRecord.create(intent);
        if (mReceiver != null) {
            Intent finalIntent;
            if (proxyBroadcastRecord.mIntent != null) {
                proxyBroadcastRecord.mIntent.setExtrasClassLoader(context.getClassLoader());
                finalIntent = proxyBroadcastRecord.mIntent;
            } else {
                finalIntent = intent;
            }
            mReceiver.onReceive(context, finalIntent);
        }
    }
}
