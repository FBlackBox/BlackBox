package black.android.content;

import android.os.Bundle;
import android.os.IBinder;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;
import top.niunaijun.blackreflection.annotation.BParamClassName;

@BClassName("android.content.BroadcastReceiver")
public interface BroadcastReceiver {
    @BMethod
    android.content.BroadcastReceiver.PendingResult getPendingResult();

    @BMethod
    void setPendingResult(@BParamClassName("android.content.BroadcastReceiver$PendingResult") Object pendingResult);

    @BClassName("android.content.BroadcastReceiver$PendingResult")
    interface PendingResultM {
        @BConstructor
        android.content.BroadcastReceiver.PendingResult _new(int resultCode, String resultData, Bundle resultExtras, int type,
                              boolean ordered, boolean sticky, IBinder token, int userId, int flags);

        @BField
        boolean mAbortBroadcast();

        @BField
        boolean mFinished();

        @BField
        int mFlags();

        @BField
        boolean mInitialStickyHint();

        @BField
        boolean mOrderedHint();

        @BField
        int mResultCode();

        @BField
        String mResultData();

        @BField
        Bundle mResultExtras();

        @BField
        int mSendingUser();

        @BField
        IBinder mToken();

        @BField
        int mType();
    }

    @BClassName("android.content.BroadcastReceiver$PendingResult")
    interface PendingResult {
        @BConstructor
        android.content.BroadcastReceiver.PendingResult _new(int resultCode, String resultData, Bundle resultExtras, int type,
                                                             boolean ordered, boolean sticky, IBinder token, int userId);

        @BField
        boolean mAbortBroadcast();

        @BField
        boolean mFinished();

        @BField
        boolean mInitialStickyHint();

        @BField
        boolean mOrderedHint();

        @BField
        int mResultCode();

        @BField
        String mResultData();

        @BField
        Bundle mResultExtras();

        @BField
        int mSendingUser();

        @BField
        IBinder mToken();

        @BField
        int mType();
    }
}
