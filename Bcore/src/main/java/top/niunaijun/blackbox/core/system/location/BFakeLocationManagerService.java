package top.niunaijun.blackbox.core.system.location;

import android.os.Parcel;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.SparseArray;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.app.BFakeLocationManager;
import top.niunaijun.blackbox.core.env.BEnvironment;
import top.niunaijun.blackbox.entity.BCell;
import top.niunaijun.blackbox.entity.BLocation;
import top.niunaijun.blackbox.utils.CloseUtils;
import top.niunaijun.blackbox.utils.FileUtils;

/**
 * Fake location
 * plan1: only GPS invocation is valid and other methods like addressed by cells are intercepted at all.
 * plan2: mock fake neighboring cells from LBS database and modify the result of GPS invocation.
 * the final testing condition requires UI demo.
 * Created by BlackBoxing on 3/8/22.
 **/
public class BFakeLocationManagerService extends IFakeLocationManager.Stub {
    private static final BFakeLocationManagerService sService = new BFakeLocationManagerService();
    private final SparseArray<ArrayMap<String, BLocationConfig>> mLocationConfigs = new SparseArray<>();
    private BLocationConfig mGlobalConfig = new BLocationConfig();

    public static BFakeLocationManagerService get() {
        return sService;
    }

    public static boolean isFakeLocationEnable(){
        return get().getPattern(BActivityThread.getUserId(), BActivityThread.getAppPackageName()) !=0;
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
    // Serialize Fake Location Configuration to Disk(not tested)
    public boolean save() {
        synchronized (this) {
            Parcel parcel = Parcel.obtain();
            AtomicFile atomicFile = new AtomicFile(BEnvironment.getFakeLocationConf());
            FileOutputStream fileOutputStream = null;
            try {
                mGlobalConfig.writeToParcel(parcel, 0);
                for(int i=0; i< mLocationConfigs.size(); i++){
                    int tmpUserId = mLocationConfigs.keyAt(i);
                    ArrayMap<String, BLocationConfig> pkgs = mLocationConfigs.valueAt(i);
                    parcel.writeInt(tmpUserId);
                    parcel.writeMap(pkgs);
                }
                parcel.setDataPosition(0);
                fileOutputStream = atomicFile.startWrite();
                FileUtils.writeParcelToOutput(parcel, fileOutputStream);
                atomicFile.finishWrite(fileOutputStream);
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
                atomicFile.failWrite(fileOutputStream);
                return false;
            } finally {
                parcel.recycle();
                CloseUtils.close(fileOutputStream);
            }
        }
    }

    @Override
    public void setPattern(int userId, String pkg, int pattern){
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).pattern = pattern;
            save();
        }
    }

    @Override
    public void setCell(int userId, String pkg,BCell cell) {
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).cell = cell;
            save();
        }
    }

    @Override
    public void setAllCell(int userId, String pkg, List<BCell> cells){
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).allCell = cells;
            save();
        }

    }

    @Override
    public void setSurroundingCell(int userId, String pkg,List<BCell> cells){
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).allCell = cells;
            save();
        }

    }

    @Override
    public void setGlobalCell(BCell cell) {
        synchronized (mGlobalConfig) {
            mGlobalConfig.cell = cell;
            save();
        }
    }

    @Override
    public void setGlobalAllCell(List<BCell> cells) {
        synchronized (mGlobalConfig) {
            mGlobalConfig.allCell = cells;
            save();
        }
    }

    @Override
    public void setGlobalSurroundingCell(List<BCell> cells){
        synchronized (mGlobalConfig) {
            mGlobalConfig.neighboringCellInfo = cells;
            save();
        }
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
        synchronized (mLocationConfigs) {
            getOrCreateConfig(userId, pkg).location = location;
            save();
        }
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
        synchronized (mGlobalConfig) {
            mGlobalConfig.location = location;
            save();
        }
    }

    @Override
    public BLocation getGlobalLocation() {
        return mGlobalConfig.location;
    }

}
