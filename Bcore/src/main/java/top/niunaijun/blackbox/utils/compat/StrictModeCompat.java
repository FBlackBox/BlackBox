package top.niunaijun.blackbox.utils.compat;

import black.android.os.BRStrictMode;

public class StrictModeCompat {
    public static int DETECT_VM_FILE_URI_EXPOSURE = BRStrictMode.get().DETECT_VM_FILE_URI_EXPOSURE() == null ?
            (0x20 << 8) : BRStrictMode.get().DETECT_VM_FILE_URI_EXPOSURE();

    public static int PENALTY_DEATH_ON_FILE_URI_EXPOSURE = BRStrictMode.get().PENALTY_DEATH_ON_FILE_URI_EXPOSURE() == null ?
            (0x04 << 24) : BRStrictMode.get().PENALTY_DEATH_ON_FILE_URI_EXPOSURE();

    public static boolean disableDeathOnFileUriExposure(){
        try {
            BRStrictMode.get().disableDeathOnFileUriExposure();
            return true;
        } catch (Throwable e) {
            try {
                int sVmPolicyMask = BRStrictMode.get().sVmPolicyMask();
                sVmPolicyMask &= ~(DETECT_VM_FILE_URI_EXPOSURE | PENALTY_DEATH_ON_FILE_URI_EXPOSURE);
                BRStrictMode.get()._set_sVmPolicyMask(sVmPolicyMask);
                return true;
            } catch (Throwable e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }
}
