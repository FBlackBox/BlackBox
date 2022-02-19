package black.android.telephony;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BField;

@BClassName("android.telephony.CellSignalStrengthCdma")
public interface CellSignalStrengthCdma {
    @BConstructor
    CellSignalStrengthCdma _new();

    @BField
    int mCdmaDbm();

    @BField
    int mCdmaEcio();

    @BField
    int mEvdoDbm();

    @BField
    int mEvdoEcio();

    @BField
    int mEvdoSnr();
}
