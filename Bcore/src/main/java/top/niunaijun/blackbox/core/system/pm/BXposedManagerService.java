package top.niunaijun.blackbox.core.system.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;

import androidx.core.util.AtomicFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.ISystemService;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.entity.pm.InstalledModule;
import top.niunaijun.blackbox.entity.pm.XposedConfig;
import top.niunaijun.blackbox.utils.CloseUtils;
import top.niunaijun.blackbox.utils.FileUtils;
import top.niunaijun.blackbox.utils.compat.XposedParserCompat;

/**
 * Created by Milk on 5/2/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BXposedManagerService extends IBXposedManagerService.Stub implements ISystemService, PackageMonitor {
    private static final BXposedManagerService sService = new BXposedManagerService();

    private XposedConfig mXposedConfig;
    private final Object mLock = new Object();
    private BPackageManagerService mPms;
    private final Map<String, InstalledModule> mCacheModule = new HashMap<>();

    public static BXposedManagerService get() {
        return sService;
    }

    public BXposedManagerService() {
    }

    @Override
    public void systemReady() {
        loadModuleStateLr();
        mPms = BPackageManagerService.get();
        mPms.addPackageMonitor(this);
    }

    @Override
    public boolean isXPEnable() {
        synchronized (mLock) {
            return mXposedConfig.enable;
        }
    }

    @Override
    public void setXPEnable(boolean enable) {
        synchronized (mLock) {
            mXposedConfig.enable = enable;
            saveModuleStateLw();
        }
    }

    @Override
    public boolean isModuleEnable(String packageName) {
        synchronized (mLock) {
            Boolean enable = mXposedConfig.moduleState.get(packageName);
            return enable != null && enable;
        }
    }

    @Override
    public void setModuleEnable(String packageName, boolean enable) {
        synchronized (mLock) {
            if (!mPms.isInstalled(packageName, BUserHandle.USER_XPOSED)) {
                return;
            }
            mXposedConfig.moduleState.put(packageName, enable);
            saveModuleStateLw();
        }
    }

    @Override
    public List<InstalledModule> getInstalledModules() {
        List<ApplicationInfo> installedApplications = mPms.getInstalledApplications(PackageManager.GET_META_DATA, BUserHandle.USER_XPOSED);
        synchronized (mCacheModule) {
            for (ApplicationInfo installedApplication : installedApplications) {
                if (mCacheModule.containsKey(installedApplication.packageName))
                    continue;
                InstalledModule installedModule = XposedParserCompat.parseModule(installedApplication);
                if (installedModule != null) {
                    mCacheModule.put(installedApplication.packageName, installedModule);
                }
            }
            ArrayList<InstalledModule> installedModules = new ArrayList<>(mCacheModule.values());
            for (InstalledModule installedModule : installedModules) {
                installedModule.enable = isModuleEnable(installedModule.packageName);
            }
            return installedModules;
        }
    }

    private void loadModuleStateLr() {
        File xpModuleConf = BEnvironment.getXPModuleConf();
        if (!xpModuleConf.exists()) {
            mXposedConfig = new XposedConfig();
            saveModuleStateLw();
            return;
        }
        Parcel parcel = null;
        try {
            parcel = FileUtils.readToParcel(xpModuleConf);
            mXposedConfig = new XposedConfig(parcel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
    }

    private void saveModuleStateLw() {
        Parcel parcel = Parcel.obtain();
        AtomicFile atomicFile = new AtomicFile(BEnvironment.getXPModuleConf());
        FileOutputStream fileOutputStream = null;
        try {
            mXposedConfig.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            fileOutputStream = atomicFile.startWrite();
            FileUtils.writeParcelToOutput(parcel, fileOutputStream);
            atomicFile.finishWrite(fileOutputStream);
        } catch (Exception ignored) {
            atomicFile.failWrite(fileOutputStream);
        } finally {
            parcel.recycle();
            CloseUtils.close(fileOutputStream);
        }
    }

    @Override
    public void onPackageUninstalled(String packageName, boolean removeApp, int userId) {
        if (userId != BUserHandle.USER_XPOSED && userId != BUserHandle.USER_ALL) {
            return;
        }
        synchronized (mCacheModule) {
            mCacheModule.remove(packageName);
        }
        synchronized (mLock) {
            mXposedConfig.moduleState.remove(packageName);
            saveModuleStateLw();
        }
    }

    @Override
    public void onPackageInstalled(String packageName, int userId) {
        if (userId != BUserHandle.USER_XPOSED && userId != BUserHandle.USER_ALL) {
            return;
        }
        synchronized (mCacheModule) {
            mCacheModule.remove(packageName);
        }
        synchronized (mLock) {
            mXposedConfig.moduleState.put(packageName, false);
            saveModuleStateLw();
        }
    }
}
