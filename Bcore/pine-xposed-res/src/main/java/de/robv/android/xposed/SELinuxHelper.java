package de.robv.android.xposed;

import android.os.SELinux;
import android.system.OsConstants;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

import de.robv.android.xposed.services.BaseService;
import de.robv.android.xposed.services.DirectAccessService;
import top.canyie.dreamland.utils.IOUtils;


/**
 * A helper to work with (or without) SELinux, abstracting much of its big complexity.
 */
public final class SELinuxHelper {
    public static final String TAG = "SELinuxHelper";

    private static boolean sIsSELinuxEnabled = false;
    // Dreamland changed: Only supports DirectAccessService
    private static BaseService sServiceAppDataFile = new DirectAccessService();

    // Dreamland changed: sIsSELinuxEnabled will be initialized in static block
    static {
        try {
            sIsSELinuxEnabled = SELinux.isSELinuxEnabled();
        } catch (NoClassDefFoundError ignored) {
        }
    }

    private SELinuxHelper() {}

    // Dreamland changed: Don't use SELinuxHelper.initOnce(), sIsSELinuxEnabled will be initialized in static block
//    public static void initOnce() {
//        try {
//            sIsSELinuxEnabled = SELinux.isSELinuxEnabled();
//        } catch (NoClassDefFoundError ignored) {}
//    }


    /**
     * Determines whether SELinux is permissive or enforcing.
     *
     * @return A boolean indicating whether SELinux is enforcing.
     */
    public static boolean isSELinuxEnforced() {
        // Dreamland changed because SELinux.isSELinuxEnforced() may return incorrect value
//        return sIsSELinuxEnabled && SELinux.isSELinuxEnforced();

        if (!sIsSELinuxEnabled) return false;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/sys/fs/selinux/enforce");
            int status = fis.read();
            switch (status) {
                case '1':
                    return true;
                case '0':
                    return false;
                default:
                    Log.e(TAG, "Unexpected byte " + status + " in /sys/fs/selinux/enforce");
                    break;
            }
        } catch (IOException e) {
            int errno = IOUtils.getErrno(e);
            if (errno == OsConstants.EACCES || errno == OsConstants.EPERM) {
                // Status file is existing but cannot read, blocked by SELinux?
                Log.w(TAG, "Read /sys/fs/selinux/enforce failed: permission denied.");
                return true;
            }
            Log.e(TAG, "Read SELinux status file failed", e);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        return SELinux.isSELinuxEnforced();
    }

    /**
     * Gets the security context of the current process.
     *
     * @return A String representing the security context of the current process.
     */
    public static String getContext() {
        return sIsSELinuxEnabled ? SELinux.getContext() : null;
    }

    /**
     * Retrieve the service to be used when accessing files in {@code /data/data/*}.
     *
     * <p class="caution"><strong>IMPORTANT:</strong> If you call this from the Zygote process,
     * don't re-use the result in different process!
     *
     * @return An instance of the service.
     */
    public static BaseService getAppDataFileService() {
        // Dreamland changed: Only supports DirectAccessService
//        if (sServiceAppDataFile != null)
//            return sServiceAppDataFile;
//        throw new UnsupportedOperationException();
        return sServiceAppDataFile;
    }


    // Dreamland changed: Only supports DirectAccessService
//    /*package*/ static void initForProcess(String packageName) {
//        if (sIsSELinuxEnabled) {
//            if (packageName == null) {  // Zygote
//                sServiceAppDataFile = new ZygoteService();
//            } else if (packageName.equals("android")) {  //system_server
//                sServiceAppDataFile = BinderService.getService(BinderService.TARGET_APP);
//            } else {  // app
//                sServiceAppDataFile = new DirectAccessService();
//            }
//        } else {
//            sServiceAppDataFile = new DirectAccessService();
//        }
//    }
}
