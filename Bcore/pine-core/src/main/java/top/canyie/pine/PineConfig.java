package top.canyie.pine;

import android.os.Build;

/**
 * A class to stores some configures.
 * @author canyie
 */
@SuppressWarnings("WeakerAccess") public final class PineConfig {
    public static int sdkLevel;
    /**
     * Whether we need to print more detailed logs.
     */
    public static boolean debug = true;

    /**
     * Whether the current process is debuggable.
     */
    public static boolean debuggable;

    /**
     * Whether all Pine hooks won't take effect.
     */
    public static boolean disableHooks;

    /**
     * Internal API. Whether we should use fast-native to speedup jni method calling.
     */
    public static boolean useFastNative;
    /** Set to true will try to hide certain features. Some information used for debugging may be erased.  */
    public static boolean antiChecks;
    /** Set to true will disable the hidden api policy for application domain */
    public static boolean disableHiddenApiPolicy = true;
    /** Set to true will disable the hidden api policy for platform domain */
    public static boolean disableHiddenApiPolicyForPlatformDomain = true;

    /**
     * A function to load our native library (libpine.so)
     * @see Pine.LibLoader
     */
    public static Pine.LibLoader libLoader = new Pine.LibLoader() {
        @Override public void loadLib() {
            System.loadLibrary("pine");
        }
    };

    static {
        sdkLevel = Build.VERSION.SDK_INT;
        if (sdkLevel == 30 && Build.VERSION.PREVIEW_SDK_INT > 0) {
            // Android S Preview
            sdkLevel = 31;
        }
    }

    private PineConfig() {
        throw new RuntimeException();
    }
}
