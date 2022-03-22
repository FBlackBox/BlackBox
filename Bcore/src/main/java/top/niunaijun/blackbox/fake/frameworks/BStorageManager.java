package top.niunaijun.blackbox.fake.frameworks;

import android.net.Uri;
import android.os.RemoteException;
import android.os.storage.StorageVolume;

import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.os.IBStorageManagerService;

/**
 * Created by Milk on 4/14/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BStorageManager extends BlackManager<IBStorageManagerService> {
    private static final BStorageManager sStorageManager = new BStorageManager();

    public static BStorageManager get() {
        return sStorageManager;
    }

    @Override
    protected String getServiceName() {
        return ServiceManager.STORAGE_MANAGER;
    }

    public StorageVolume[] getVolumeList(int uid, String packageName, int flags, int userId) {
        try {
            return getService().getVolumeList(uid, packageName, flags, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new StorageVolume[]{};
    }

    public Uri getUriForFile(String file) {
        try {
            return getService().getUriForFile(file);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
