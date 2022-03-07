package top.niunaijun.blackbox.core.system.location;

import android.util.ArrayMap;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import top.niunaijun.blackbox.app.BFakeLocationManager;
import top.niunaijun.blackbox.entity.BCell;
import top.niunaijun.blackbox.entity.BLocation;

public class BFakeLocationManagerService extends IFakeLocationManager.Stub {
    private static final BFakeLocationManagerService sService = new BFakeLocationManagerService();
    private final SparseArray<ArrayMap<String, BLocationConfig>> mLocationConfigs = new SparseArray<>();
    private BLocationConfig mGlobalConfig = new BLocationConfig();

    public static BFakeLocationManagerService get() {
        return sService;
    }
    private BLocationConfig getOrCreateConfig(int userId, String pkg) {
        ArrayMap<String, BLocationConfig> pkgs = mLocationConfigs.get(userId);
        if (pkgs == null) {
            pkgs = new ArrayMap<>();
            mLocationConfigs.put(userId, pkgs);
        }
        BLocationConfig mBLocationConfig = pkgs.get(pkg);
        if (mBLocationConfig == null) {
            mBLocationConfig = new BLocationConfig();
            mBLocationConfig.pattern = BFakeLocationManager.CLOSE_MODE;
            pkgs.put(pkg, mBLocationConfig);
        }
        return mBLocationConfig;
    }
    public int getPattern(int userId, String pkg){
        synchronized (mLocationConfigs) {
            BLocationConfig mBLocationConfig = getOrCreateConfig(userId, pkg);
            return mBLocationConfig.pattern;
        }
    }
    @Override
    public void setPattern(int userId, String pkg, int pattern){
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).pattern = pattern;
        }
    }
    @Override
    public void setCell(int userId, String pkg,BCell cell) {
        getOrCreateConfig(userId, pkg).cell = cell;
    }
    @Override
    public void setAllCell(int userId, String pkg, List<BCell> cells){
        getOrCreateConfig(userId, pkg).allCell = cells;
    }

    @Override
    public void setGlobalCell(BCell cell) {
        mGlobalConfig.cell = cell;
    }
    @Override
    public void setGlobalAllCell(List<BCell> cells) {
        mGlobalConfig.allCell = cells;
    }

    @Override
    public BCell getCell(int userId, String pkg) {
        BLocationConfig mBLocationConfig = getOrCreateConfig(userId, pkg);
        switch (mBLocationConfig.pattern) {
            case BFakeLocationManager.OWN_MODE:
                return mBLocationConfig.cell;
            case BFakeLocationManager.GLOBAL_MODE:
                return mGlobalConfig.cell;
            case BFakeLocationManager.CLOSE_MODE:
            default:
                return null;
        }
    }
    @Override
    public List<BCell> getAllCell(int userId, String pkg){
        BLocationConfig mBLocationConfig = getOrCreateConfig(userId, pkg);
        switch (mBLocationConfig.pattern) {
            case BFakeLocationManager.OWN_MODE:
                return mBLocationConfig.allCell;
            case BFakeLocationManager.GLOBAL_MODE:
                return mGlobalConfig.allCell;
            case BFakeLocationManager.CLOSE_MODE:
            default:
                return null;
        }
    }
    @Override
    public void setLocation(int userId, String pkg,BLocation location){
        getOrCreateConfig(userId, pkg).location = location;
    }
    @Override
    public BLocation getLocation(int userId, String pkg){
        BLocationConfig mBLocationConfig = getOrCreateConfig(userId, pkg);
        switch (mBLocationConfig.pattern) {
            case BFakeLocationManager.OWN_MODE:
                return mBLocationConfig.location;
            case BFakeLocationManager.GLOBAL_MODE:
                return mGlobalConfig.location;
            case BFakeLocationManager.CLOSE_MODE:
            default:
                return null;
        }
    }
    @Override
    public void setGlobalLocation(BLocation location){
        mGlobalConfig.location = location;
    }
    @Override
    public BLocation getGlobalLocation() {
        return mGlobalConfig.location;
    }

}
