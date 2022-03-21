package top.niunaijun.blackbox.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.proxy.record.ProxyPendingRecord;
import top.niunaijun.blackbox.utils.Slog;

/**
 * Created by Milk on 3/28/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ProxyPendingActivity extends Activity {
    public static final String TAG = "ProxyPendingActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        ProxyPendingRecord pendingActivityRecord = ProxyPendingRecord.create(getIntent());
        Slog.d(TAG, "ProxyPendingActivity: " + pendingActivityRecord);
        if (pendingActivityRecord.mTarget == null)
            return;
        pendingActivityRecord.mTarget.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingActivityRecord.mTarget.setExtrasClassLoader(BActivityThread.getApplication().getClassLoader());
        startActivity(pendingActivityRecord.mTarget);
    }

    public static class P0 extends ProxyPendingActivity {

    }

    public static class P1 extends ProxyPendingActivity {

    }

    public static class P2 extends ProxyPendingActivity {

    }

    public static class P3 extends ProxyPendingActivity {

    }

    public static class P4 extends ProxyPendingActivity {

    }

    public static class P5 extends ProxyPendingActivity {

    }

    public static class P6 extends ProxyPendingActivity {

    }

    public static class P7 extends ProxyPendingActivity {

    }

    public static class P8 extends ProxyPendingActivity {

    }

    public static class P9 extends ProxyPendingActivity {

    }

    public static class P10 extends ProxyPendingActivity {

    }

    public static class P11 extends ProxyPendingActivity {

    }

    public static class P12 extends ProxyPendingActivity {

    }

    public static class P13 extends ProxyPendingActivity {

    }

    public static class P14 extends ProxyPendingActivity {

    }

    public static class P15 extends ProxyPendingActivity {

    }

    public static class P16 extends ProxyPendingActivity {

    }

    public static class P17 extends ProxyPendingActivity {

    }

    public static class P18 extends ProxyPendingActivity {

    }

    public static class P19 extends ProxyPendingActivity {

    }

    public static class P20 extends ProxyPendingActivity {

    }

    public static class P21 extends ProxyPendingActivity {

    }

    public static class P22 extends ProxyPendingActivity {

    }

    public static class P23 extends ProxyPendingActivity {

    }

    public static class P24 extends ProxyPendingActivity {

    }

    public static class P25 extends ProxyPendingActivity {

    }

    public static class P26 extends ProxyPendingActivity {

    }

    public static class P27 extends ProxyPendingActivity {

    }

    public static class P28 extends ProxyPendingActivity {

    }

    public static class P29 extends ProxyPendingActivity {

    }

    public static class P30 extends ProxyPendingActivity {

    }

    public static class P31 extends ProxyPendingActivity {

    }

    public static class P32 extends ProxyPendingActivity {

    }

    public static class P33 extends ProxyPendingActivity {

    }

    public static class P34 extends ProxyPendingActivity {

    }

    public static class P35 extends ProxyPendingActivity {

    }

    public static class P36 extends ProxyPendingActivity {

    }

    public static class P37 extends ProxyPendingActivity {

    }

    public static class P38 extends ProxyPendingActivity {

    }

    public static class P39 extends ProxyPendingActivity {

    }

    public static class P40 extends ProxyPendingActivity {

    }

    public static class P41 extends ProxyPendingActivity {

    }

    public static class P42 extends ProxyPendingActivity {

    }

    public static class P43 extends ProxyPendingActivity {

    }

    public static class P44 extends ProxyPendingActivity {

    }

    public static class P45 extends ProxyPendingActivity {

    }

    public static class P46 extends ProxyPendingActivity {

    }

    public static class P47 extends ProxyPendingActivity {

    }

    public static class P48 extends ProxyPendingActivity {

    }

    public static class P49 extends ProxyPendingActivity {

    }
}
