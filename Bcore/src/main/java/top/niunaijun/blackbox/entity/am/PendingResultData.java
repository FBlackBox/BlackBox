package top.niunaijun.blackbox.entity.am;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

import black.android.content.BRBroadcastReceiverPendingResult;
import black.android.content.BRBroadcastReceiverPendingResultM;
import black.android.content.BroadcastReceiverPendingResultContext;
import black.android.content.BroadcastReceiverPendingResultMContext;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Created by BlackBox on 2022/2/28.
 */
public class PendingResultData implements Parcelable {
    public int mType;
    public boolean mOrderedHint;
    public boolean mInitialStickyHint;
    public IBinder mToken;
    public int mSendingUser;
    public int mFlags;
    public int mResultCode;
    public String mResultData;
    public Bundle mResultExtras;
    public boolean mAbortBroadcast;
    public boolean mFinished;
    public String mBToken;

    public PendingResultData(BroadcastReceiver.PendingResult pendingResult) {
        mBToken = UUID.randomUUID().toString();
        if (BuildCompat.isM()) {
            BroadcastReceiverPendingResultMContext resultMContext = BRBroadcastReceiverPendingResultM.get(pendingResult);
            mType = resultMContext.mType();
            mOrderedHint = resultMContext.mOrderedHint();
            mInitialStickyHint = resultMContext.mInitialStickyHint();
            mToken = resultMContext.mToken();
            mSendingUser = resultMContext.mSendingUser();
            mFlags = resultMContext.mFlags();
            mResultData = resultMContext.mResultData();
            mResultExtras = resultMContext.mResultExtras();
            mAbortBroadcast = resultMContext.mAbortBroadcast();
            mFinished = resultMContext.mFinished();
        } else {
            BroadcastReceiverPendingResultContext resultContext = BRBroadcastReceiverPendingResult.get(pendingResult);
            mType = resultContext.mType();
            mOrderedHint = resultContext.mOrderedHint();
            mInitialStickyHint = resultContext.mInitialStickyHint();
            mToken = resultContext.mToken();
            mSendingUser = resultContext.mSendingUser();
            mResultData = resultContext.mResultData();
            mResultExtras = resultContext.mResultExtras();
            mAbortBroadcast = resultContext.mAbortBroadcast();
            mFinished = resultContext.mFinished();
        }
    }

    public BroadcastReceiver.PendingResult build() {
        if (BuildCompat.isM()) {
            return BRBroadcastReceiverPendingResultM.get()._new(mResultCode, mResultData, mResultExtras, mType, mOrderedHint, mInitialStickyHint, mToken, mSendingUser, mFlags);
        } else {
            return BRBroadcastReceiverPendingResult.get()._new(mResultCode, mResultData, mResultExtras, mType, mOrderedHint, mInitialStickyHint, mToken, mSendingUser);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeByte(this.mOrderedHint ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mInitialStickyHint ? (byte) 1 : (byte) 0);
        dest.writeStrongBinder(this.mToken);
        dest.writeInt(this.mSendingUser);
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mResultCode);
        dest.writeString(this.mResultData);
        dest.writeBundle(this.mResultExtras);
        dest.writeByte(this.mAbortBroadcast ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mFinished ? (byte) 1 : (byte) 0);
        dest.writeString(this.mBToken);
    }

    public void readFromParcel(Parcel source) {
        this.mType = source.readInt();
        this.mOrderedHint = source.readByte() != 0;
        this.mInitialStickyHint = source.readByte() != 0;
        this.mToken = source.readStrongBinder();
        this.mSendingUser = source.readInt();
        this.mFlags = source.readInt();
        this.mResultCode = source.readInt();
        this.mResultData = source.readString();
        this.mResultExtras = source.readBundle();
        this.mAbortBroadcast = source.readByte() != 0;
        this.mFinished = source.readByte() != 0;
        this.mBToken = source.readString();
    }

    protected PendingResultData(Parcel in) {
        this.mType = in.readInt();
        this.mOrderedHint = in.readByte() != 0;
        this.mInitialStickyHint = in.readByte() != 0;
        this.mToken = in.readStrongBinder();
        this.mSendingUser = in.readInt();
        this.mFlags = in.readInt();
        this.mResultCode = in.readInt();
        this.mResultData = in.readString();
        this.mResultExtras = in.readBundle();
        this.mAbortBroadcast = in.readByte() != 0;
        this.mFinished = in.readByte() != 0;
        this.mBToken = in.readString();
    }

    public static final Parcelable.Creator<PendingResultData> CREATOR = new Parcelable.Creator<PendingResultData>() {
        @Override
        public PendingResultData createFromParcel(Parcel source) {
            return new PendingResultData(source);
        }

        @Override
        public PendingResultData[] newArray(int size) {
            return new PendingResultData[size];
        }
    };

    @Override
    public String toString() {
        return "PendingResultData{" +
                "mType=" + mType +
                ", mOrderedHint=" + mOrderedHint +
                ", mInitialStickyHint=" + mInitialStickyHint +
                ", mToken=" + mToken +
                ", mSendingUser=" + mSendingUser +
                ", mFlags=" + mFlags +
                ", mResultCode=" + mResultCode +
                ", mResultData='" + mResultData + '\'' +
                ", mResultExtras=" + mResultExtras +
                ", mAbortBroadcast=" + mAbortBroadcast +
                ", mFinished=" + mFinished +
                '}';
    }
}
