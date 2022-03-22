package top.niunaijun.blackbox.core.system;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.core.env.AppSystemEnv;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.core.system.accounts.BAccountManagerService;
import top.niunaijun.blackbox.core.system.am.BActivityManagerService;
import top.niunaijun.blackbox.core.system.am.BJobManagerService;
import top.niunaijun.blackbox.core.system.location.BLocationManagerService;
import top.niunaijun.blackbox.core.system.notification.BNotificationManagerService;
import top.niunaijun.blackbox.core.system.os.BStorageManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageInstallerService;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.pm.BXposedManagerService;
import top.niunaijun.blackbox.core.system.user.BUserHandle;
import top.niunaijun.blackbox.core.system.user.BUserManagerService;
import top.niunaijun.blackbox.entity.pm.InstallOption;
import top.niunaijun.blackbox.utils.FileUtils;

import static top.niunaijun.blackbox.core.env.BEnvironment.EMPTY_JAR;
import static top.niunaijun.blackbox.core.env.BEnvironment.JUNIT_JAR;

/**
 * Created by Milk on 4/22/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class BlackBoxSystem {
    private static BlackBoxSystem sBlackBoxSystem;
    private final List<ISystemService> mServices = new ArrayList<>();
    private final static AtomicBoolean isStartup = new AtomicBoolean(false);

    public static BlackBoxSystem getSystem() {
        if (sBlackBoxSystem == null) {
            synchronized (BlackBoxSystem.class) {
                if (sBlackBoxSystem == null) {
                    sBlackBoxSystem = new BlackBoxSystem();
                }
            }
        }
        return sBlackBoxSystem;
    }

    public void startup() {
        if (isStartup.getAndSet(true))
            return;
        BEnvironment.load();

        mServices.add(BPackageManagerService.get());
        mServices.add(BUserManagerService.get());
        mServices.add(BActivityManagerService.get());
        mServices.add(BJobManagerService.get());
        mServices.add(BStorageManagerService.get());
        mServices.add(BPackageInstallerService.get());
        mServices.add(BXposedManagerService.get());
        mServices.add(BProcessManagerService.get());
        mServices.add(BAccountManagerService.get());
        mServices.add(BLocationManagerService.get());
        mServices.add(BNotificationManagerService.get());

        for (ISystemService service : mServices) {
            service.systemReady();
        }

        List<String> preInstallPackages = AppSystemEnv.getPreInstallPackages();
        for (String preInstallPackage : preInstallPackages) {
            try {
                if (!BPackageManagerService.get().isInstalled(preInstallPackage, BUserHandle.USER_ALL)) {
                    PackageInfo packageInfo = BlackBoxCore.getPackageManager().getPackageInfo(preInstallPackage, 0);
                    BPackageManagerService.get().installPackageAsUser(packageInfo.applicationInfo.sourceDir, InstallOption.installBySystem(), BUserHandle.USER_ALL);
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        initJarEnv();
    }

    private void initJarEnv() {
        try {
            InputStream junit = BlackBoxCore.getContext().getAssets().open("junit.jar");
            FileUtils.copyFile(junit, JUNIT_JAR);

            InputStream empty = BlackBoxCore.getContext().getAssets().open("empty.jar");
            FileUtils.copyFile(empty, EMPTY_JAR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
