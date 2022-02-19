package top.niunaijun.blackbox.proxy.record;

import android.content.Intent;
import android.content.pm.ServiceInfo;

/**
 * Created by Milk on 4/1/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class ProxyServiceRecord {
    public Intent mServiceIntent;
    public ServiceInfo mServiceInfo;
    public int mUserId;
    public int mStartId;

    public ProxyServiceRecord(Intent serviceIntent, ServiceInfo serviceInfo, int userId, int startId) {
        mServiceIntent = serviceIntent;
        mServiceInfo = serviceInfo;
        mUserId = userId;
        mStartId = startId;
    }

    public static void saveStub(Intent shadow, Intent target, ServiceInfo serviceInfo, int userId, int startId) {
        shadow.putExtra("_VM_|_target_", target);
        shadow.putExtra("_VM_|_service_info_", serviceInfo);
        shadow.putExtra("_VM_|_user_id_", userId);
        shadow.putExtra("_VM_|_start_id_", startId);
    }

    public static ProxyServiceRecord create(Intent intent) {
        Intent target = intent.getParcelableExtra("_VM_|_target_");
        ServiceInfo serviceInfo = intent.getParcelableExtra("_VM_|_service_info_");
        int userId = intent.getIntExtra("_VM_|_user_id_", 0);
        int startId = intent.getIntExtra("_VM_|_start_id_", 0);
        return new ProxyServiceRecord(target, serviceInfo, userId, startId);
    }
}
