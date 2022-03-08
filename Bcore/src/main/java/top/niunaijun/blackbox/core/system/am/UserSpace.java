package top.niunaijun.blackbox.core.system.am;

import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Milk on 4/25/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class UserSpace {
    public final ActiveServices mActiveServices = new ActiveServices();
    public final ActivityStack mStack = new ActivityStack();
    public final Map<IBinder, PendingIntentRecord> mIntentSenderRecords = new HashMap<>();
}
