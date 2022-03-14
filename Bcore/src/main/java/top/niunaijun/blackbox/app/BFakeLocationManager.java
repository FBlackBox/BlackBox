package top.niunaijun.blackbox.app;

import java.util.List;

import top.niunaijun.blackbox.core.system.location.BFakeLocationManagerService;
import top.niunaijun.blackbox.entity.BCell;
import top.niunaijun.blackbox.entity.BLocation;

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

    public static boolean isFakeLocationEnable() {
        return BFakeLocationManagerService.get().getPattern(BActivityThread.getUserId(), BActivityThread.getAppPackageName()) != 0;
    }

    public void setPattern(int userId, String pkg, int pattern) {
        BFakeLocationManagerService.get().setPattern(userId, pkg, pattern);
    }

    public void getPattern(int userId, String pkg) {
        BFakeLocationManagerService.get().getPattern(userId, pkg);
    }

    public void setCell(int userId, String pkg, BCell cell) {
        BFakeLocationManagerService.get().setCell(userId, pkg, cell);
    }

    public void setAllCell(int userId, String pkg, List<BCell> cells) {
        BFakeLocationManagerService.get().setAllCell(userId, pkg, cells);
    }

    public void setSurroundingCell(int userId, String pkg, List<BCell> cells) {
        BFakeLocationManagerService.get().setSurroundingCell(userId, pkg, cells);
    }

    public void setGlobalCell(BCell cell) {
        BFakeLocationManagerService.get().setGlobalCell(cell);
    }

    public void setGlobalAllCell(List<BCell> cells) {
        BFakeLocationManagerService.get().setGlobalAllCell(cells);
    }

    public void setGlobalSurroundingCell(List<BCell> cells) {
        BFakeLocationManagerService.get().setGlobalSurroundingCell(cells);
    }

    public BCell getCell(int userId, String pkg) {
        return BFakeLocationManagerService.get().getCell(userId, pkg);
    }

    public List<BCell> getAllCell(int userId, String pkg) {
        return BFakeLocationManagerService.get().getAllCell(userId, pkg);
    }

    public void setLocation(int userId, String pkg, BLocation location) {
        BFakeLocationManagerService.get().setLocation(userId, pkg, location);
    }

    public BLocation getLocation(int userId, String pkg) {
        return BFakeLocationManagerService.get().getLocation(userId, pkg);
    }

    public void setGlobalLocation(BLocation location) {
        BFakeLocationManagerService.get().setGlobalLocation(location);
    }

    public BLocation getGlobalLocation() {
        return BFakeLocationManagerService.get().getGlobalLocation();
    }
}
