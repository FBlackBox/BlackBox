// IFakeLocationManager.aidl
package top.niunaijun.blackbox.core.system.location;

import top.niunaijun.blackbox.entity.location.BLocation;
import top.niunaijun.blackbox.entity.location.BCell;

import java.util.List;


interface IBLocationManagerService {
    int getPattern(int userId, String pkg);

    void setPattern(int userId, String pkg, int mode);

    void setCell(int userId, String pkg,in  BCell cell);

    void setAllCell(int userId, String pkg,in  List<BCell> cell);

    void setNeighboringCell(int userId, String pkg,in  List<BCell> cells);
    List<BCell> getNeighboringCell(int userId, String pkg);

    void setGlobalCell(in BCell cell);

    void setGlobalAllCell(in List<BCell> cell);

    void setGlobalNeighboringCell(in List<BCell> cell);

    List<BCell> getGlobalNeighboringCell();

    BCell getCell(int userId, String pkg);

    List<BCell> getAllCell(int userId, String pkg);

    void setLocation(int userId, String pkg,in  BLocation location);

    BLocation getLocation(int userId, String pkg);

    void setGlobalLocation(in BLocation location);

    BLocation getGlobalLocation();

    void requestLocationUpdates(in IBinder listener, String packageName, int userId);

    void removeUpdates(in IBinder listener);
}