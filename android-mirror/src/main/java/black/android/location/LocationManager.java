package black.android.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IInterface;

import java.util.HashMap;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;
import top.niunaijun.blackreflection.annotation.BMethod;

@BClassName("android.location.LocationManager")
public interface LocationManager {
    @BField
    HashMap mGnssNmeaListeners();

    @BField
    HashMap mGnssStatusListeners();

    @BField
    HashMap mGpsNmeaListeners();

    @BField
    HashMap mGpsStatusListeners();

    @BField
    HashMap mListeners();

    @BField
    HashMap mNmeaListeners();

    @BField
    IInterface mService();

    @BClassName("android.location.LocationManager$GnssStatusListenerTransport")
    interface ListenerTransport {
        @BField
        LocationListener mListener();

        @BField
        Object this$0();

        @BMethod
        void onLocationChanged(Location Location0);

        @BMethod
        void onProviderDisabled(String String0);

        @BMethod
        void onProviderEnabled(String String0);

        @BMethod
        void onStatusChanged(String String0, int int1, Bundle Bundle2);
    }

    @BClassName("android.location.LocationManager$GnssStatusListenerTransport")
    interface GpsStatusListenerTransportVIVO {
        @BMethod
        void onSvStatusChanged(int int0, int[] ints1, float[] floats2, float[] floats3, float[] floats4, int int5, int int6, int int7,  long[] longs8);
    }

    @BClassName("android.location.LocationManager$GpsStatusListenerTransport")
    interface GpsStatusListenerTransportSumsungS5 {
//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4, int int5, int int6, int int7, [I int[]8);
    }

    @BClassName("android.location.LocationManager$GpsStatusListenerTransport")
    interface GpsStatusListenerTransportOPPO_R815T {
//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4, [I int[]5, [I int[]6, [I int[]7, int int8);
    }

    @BClassName("android.location.LocationManager$GpsStatusListenerTransport")
    interface GpsStatusListenerTransport {
        @BField
        Object mListener();

        @BField
        Object mNmeaListener();

        @BField
        Object this$0();

        @BMethod
        void onFirstFix(int int0);

        @BMethod
        void onGpsStarted();

        @BMethod
        void onNmeaReceived(long long0, String String1);

//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4, int int5, int int6, int int7);
    }

    @BClassName("android.location.LocationManager$GnssStatusListenerTransport")
    interface GnssStatusListenerTransportO {
//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4, [F float[]5);
    }

    @BClassName("android.location.LocationManager$GnssStatusListenerTransport")
    interface GnssStatusListenerTransport {
        @BField
        Object mGpsListener();

        @BField
        Object mGpsNmeaListener();

        @BField
        Object this$0();

        @BMethod
        void onFirstFix(int int0);

        @BMethod
        void onGnssStarted();

        @BMethod
        void onNmeaReceived(long long0, String String1);

//        @BMethod
//        void onSvStatusChanged(int int0, [I int[]1, [F float[]2, [F float[]3, [F float[]4);
    }
}
