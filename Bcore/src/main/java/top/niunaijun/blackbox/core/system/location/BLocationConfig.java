package top.niunaijun.blackbox.core.system.location;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import top.niunaijun.blackbox.entity.BCell;
import top.niunaijun.blackbox.entity.BLocation;

public class BLocationConfig implements Parcelable {
    int pattern;
    BCell cell;
    List<BCell> allCell;
    List<BCell> surroundingCell;
    BLocation location;
    @Override
    public int describeContents() {
        return 0;
    }
    BLocationConfig(){}
    BLocationConfig(Parcel in){
        this.pattern = in.readInt();
        this.cell = in.readParcelable(BCell.class.getClassLoader());
        this.allCell = in.createTypedArrayList(BCell.CREATOR);
        this.surroundingCell = in.createTypedArrayList(BCell.CREATOR);
        this.location = in.readParcelable(BLocation.class.getClassLoader());
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pattern);
        dest.writeParcelable(this.cell, flags);
        dest.writeTypedList(this.allCell);
        dest.writeTypedList(this.surroundingCell);
        dest.writeParcelable(this.location, flags);
    }
    public static final Creator<BLocationConfig> CREATOR = new Creator<BLocationConfig>() {
        @Override
        public BLocationConfig createFromParcel(Parcel source) {
            return new BLocationConfig(source);
        }

        @Override
        public BLocationConfig[] newArray(int size) {
            return new BLocationConfig[size];
        }
    };
}
