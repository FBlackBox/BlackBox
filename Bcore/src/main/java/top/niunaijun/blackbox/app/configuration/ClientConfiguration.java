package top.niunaijun.blackbox.app.configuration;

import java.io.File;

/**
 * Created by Milk on 5/4/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public abstract class ClientConfiguration {

    public boolean isHideRoot() {
        return false;
    }

    public boolean isHideXposed() {
        return false;
    }

    public abstract String getHostPackageName();

    public boolean isEnableDaemonService() {
        return true;
    }

    public boolean isEnableLauncherActivity() {
        return true;
    }

    /**
     * This method is called when an internal application requests to install a new application.
     *
     * @return Is it handled?
     */
    public boolean requestInstallPackage(File file) {
        return false;
    }
}
