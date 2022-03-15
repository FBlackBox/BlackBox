package top.niunaijun.blackbox.entity.location;

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
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    public static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    public static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    public static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B*/
    public static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0*/
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A*/
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT*/
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * No phone module
     *
     */
    public static final int PHONE_TYPE_NONE = 0;
    /**
     * GSM phone
     */
    public static final int PHONE_TYPE_GSM = 1;
    /**
     * CDMA phone
     */
    public static final int PHONE_TYPE_CDMA = 2;

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

    public  BCell(){}
    public BCell(int MCC, int MNC, int LAC, int CID) {
        this.TYPE = this.PHONE_TYPE_GSM;
        this.MCC = MCC;
        this.CID = CID;
        this.MNC = MNC;
        this.LAC = LAC;
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

