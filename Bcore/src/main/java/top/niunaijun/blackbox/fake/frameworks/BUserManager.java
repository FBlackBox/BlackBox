package top.niunaijun.blackbox.fake.frameworks;

import android.os.RemoteException;

import java.util.Collections;
import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.system.ServiceManager;
import top.niunaijun.blackbox.core.system.user.BUserInfo;
import top.niunaijun.blackbox.core.system.user.IBUserManagerService;

/**
 * Created by Milk on 4/28/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BUserManager {
    private static final BUserManager sUserManager = new BUserManager();
    private IBUserManagerService mService;

    public static BUserManager get() {
        return sUserManager;
    }

    public BUserInfo createUser(int userId) {
        try {
            return getService().createUser(userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(int userId) {
        try {
            getService().deleteUser(userId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<BUserInfo> getUsers() {
        try {
            return getService().getUsers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private IBUserManagerService getService() {
        if (mService != null && mService.asBinder().isBinderAlive()) {
            return mService;
        }
        mService = IBUserManagerService.Stub.asInterface(BlackBoxCore.get().getService(ServiceManager.USER_MANAGER));
        return getService();
    }
}
