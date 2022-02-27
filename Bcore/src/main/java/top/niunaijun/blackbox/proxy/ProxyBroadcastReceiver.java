package top.niunaijun.blackbox.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import black.android.app.BRActivityManagerNative;
import black.android.app.BRIActivityManager;
import black.android.content.BRBroadcastReceiver;
import black.android.content.BRBroadcastReceiverPendingResult;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.utils.Slog;

/**
 * Created by BlackBox on 2022/2/25.
 */
public class ProxyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "ProxyBroadcastReceiver";

    private final BroadcastReceiver mReceiver;
    private PendingResult mPendingResult;
    private Intent mIntent;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mPendingResult != null) {
                Slog.d(TAG, "call auto finish: " + mIntent);
                mPendingResult.finish();
            }
        }
    };

    public ProxyBroadcastReceiver(BroadcastReceiver receiver) {
        mReceiver = receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        mPendingResult = goAsync();
//        mIntent = intent;
//        BRBroadcastReceiver.get(mReceiver).setPendingResult(mPendingResult);
//        intent.setExtrasClassLoader(context.getClassLoader());
//        if (mReceiver != null) {
//            mHandler.sendEmptyMessageDelayed(0, 8000);
//            Slog.d(TAG, "call onReceive: " + intent);
//            mReceiver.onReceive(context, intent);
//            Slog.d(TAG, "call finish: " + intent);
//            mHandler.removeMessages(0);
//        }
//        mPendingResult.finish();
    }
}