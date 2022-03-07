package top.niunaijun.blackbox.entity;

import android.os.Parcel;
import android.os.Parcelable;
/*
 * created by BlackBoxing at 2022/03/06
 * */
public class BCell implements Parcelable {
    /**
     * mnc : 1
     * lac : 41093
     * ci : 3865320
     * acc : 1177
     * location : {"lon":116.343278,"lat":39.531734}
     * reference blog: https://liuschen.top/2016/09/15/BLocation.html
     * MCC，Mobile Country Code，移动国家代码（中国的为460）；
     * MNC，Mobile Network Code，移动网络号码（00移动 01联通 11电信4G）；
     * LAC/TAC(1~65535)，Location Area Code，位置区域码；
     * CID/CI( 2G(1~65535), 3G/4G(1~268435455))，Cell Identity，基站编号；
     * TYPE: Cdma/Lte/Gsm/Wcdma
     */

    public int MCC;
    public int MNC;
    public int LAC;
    public int CID;
    public int TYPE;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.MCC);
        dest.writeInt(this.MNC);
        dest.writeInt(this.LAC);
        dest.writeInt(this.CID);
        dest.writeInt(this.TYPE);
    }

    public BCell() {
    }

    public BCell(Parcel in) {
        this.MCC = in.readInt();
        this.MNC = in.readInt();
        this.LAC = in.readInt();
        this.CID = in.readInt();
        this.TYPE = in.readInt();
    }

    public static final Parcelable.Creator<BCell> CREATOR = new Parcelable.Creator<BCell>() {
        @Override
        public BCell createFromParcel(Parcel source) {
            return new BCell(source);
        }

        @Override
        public BCell[] newArray(int size) {
            return new BCell[size];
        }
    };
}

