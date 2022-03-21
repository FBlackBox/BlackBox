package top.niunaijun.blackbox.core.system.location;

/**
 * Created by BlackBox on 2022/3/19.
 */
public class LocationRecord {
    public String packageName;
    public int userId;

    public LocationRecord(String packageName, int userId) {
        this.packageName = packageName;
        this.userId = userId;
    }
}
