package top.niunaijun.blackbox.entity.location;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by BlackBoxing on 3/8/22.
 **/
public class BLocationConfig implements Parcelable {

    public int pattern;
    public BCell cell;
    public List<BCell> allCell;
    public List<BCell> neighboringCellInfo;
    public BLocation location;

    @Override
    public int describeContents() {
        return 0;
    }

    public BLocationConfig() {
    }

    public BLocationConfig(Parcel in) {
        refresh(in);
    }

    public void refresh(Parcel in) {
        this.pattern = in.readInt();
        this.cell = in.readParcelable(BCell.class.getClassLoader());
        this.allCell = in.createTypedArrayList(BCell.CREATOR);
        this.neighboringCellInfo = in.createTypedArrayList(BCell.CREATOR);
        this.location = in.readParcelable(BLocation.class.getClassLoader());
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pattern);
        dest.writeParcelable(this.cell, flags);
        dest.writeTypedList(this.allCell);
        dest.writeTypedList(this.neighboringCellInfo);
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
