// IFakeLocationManager.aidl
package top.niunaijun.blackbox.core.system.location;

import top.niunaijun.blackbox.entity.BLocation;
import top.niunaijun.blackbox.entity.BCell;

import java.util.List;


interface IFakeLocationManager {
    int getPattern(int userId, String pkg);

    void setPattern(int userId, String pkg, int mode);

    void setCell(int userId, String pkg,in  BCell cell);

    void setAllCell(int userId, String pkg,in  List<BCell> cell);

    void setSurroundingCell(int userId, String pkg,in  List<BCell> cells);

    void setGlobalCell(in BCell cell);

    void setGlobalAllCell(in List<BCell> cell);

    void setGlobalSurroundingCell(in List<BCell> cell);

    BCell getCell(int userId, String pkg);

    List<BCell> getAllCell(int userId, String pkg);

    void setLocation(int userId, String pkg,in  BLocation location);

    BLocation getLocation(int userId, String pkg);

    void setGlobalLocation(in BLocation location);

    BLocation getGlobalLocation();
}