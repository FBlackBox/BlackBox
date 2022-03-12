package top.niunaijun.blackbox.app;

import top.niunaijun.blackbox.core.system.location.IFakeLocationManager;
/**
 * not useful now and BFakeLocationManagerService maybe would take the function of this perfectly
 * or destroy the safety of BlackBox.
 * Created by BlackBoxing on 3/8/22.
 **/
public class BFakeLocationManager {
    private static final BFakeLocationManager sBFakeLocationManager = new BFakeLocationManager();

    public static final int CLOSE_MODE = 0;
    public static final int GLOBAL_MODE = 1;
    public static final int OWN_MODE = 2;
    public static BFakeLocationManager get() {
        return sBFakeLocationManager;
    }

    private IFakeLocationManager mService;
}
