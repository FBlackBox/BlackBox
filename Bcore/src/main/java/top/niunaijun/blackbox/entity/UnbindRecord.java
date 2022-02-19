package top.niunaijun.blackbox.entity;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Milk on 4/7/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class UnbindRecord implements Parcelable {
    private int mBindCount;
    private int mStartId;
    private ComponentName mComponentName;

    public int getStartId() {
        return mStartId;
    }

    public void setStartId(int startId) {
        mStartId = startId;
    }

    public int getBindCount() {
        return mBindCount;
    }

    public void setBindCount(int bindCount) {
        mBindCount = bindCount;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public void setComponentName(ComponentName componentName) {
        mComponentName = componentName;
    }

    public static Creator<UnbindRecord> getCREATOR() {
        return CREATOR;
    }

    public UnbindRecord() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mBindCount);
        dest.writeInt(this.mStartId);
        dest.writeParcelable(this.mComponentName, flags);
    }

    protected UnbindRecord(Parcel in) {
        this.mBindCount = in.readInt();
        this.mStartId = in.readInt();
        this.mComponentName = in.readParcelable(ComponentName.class.getClassLoader());
    }

    public static final Creator<UnbindRecord> CREATOR = new Creator<UnbindRecord>() {
        @Override
        public UnbindRecord createFromParcel(Parcel source) {
            return new UnbindRecord(source);
        }

        @Override
        public UnbindRecord[] newArray(int size) {
            return new UnbindRecord[size];
        }
    };
}
