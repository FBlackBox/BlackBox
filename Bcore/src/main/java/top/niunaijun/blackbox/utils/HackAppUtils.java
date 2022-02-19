package top.niunaijun.blackbox.utils;

public class HackAppUtils {

    /**
     * Enable the Log output of QQ.
     *
     * @param packageName package name
     * @param classLoader class loader
     */
    public static void enableQQLogOutput(String packageName, ClassLoader classLoader) {
        if ("com.tencent.mobileqq".equals(packageName)) {
            try {
                Reflector.on("com.tencent.qphone.base.util.QLog", true, classLoader)
                        .field("UIN_REPORTLOG_LEVEL")
                        .set(100);
            } catch (Exception e) {
                e.printStackTrace();
                // ignore
            }
        }
    }
}
