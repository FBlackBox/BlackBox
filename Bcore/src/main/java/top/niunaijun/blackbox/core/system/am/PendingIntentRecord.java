package top.niunaijun.blackbox.core.system.am;

import java.util.Objects;

/**
 * Created by BlackBox on 2022/3/8.
 */
public class PendingIntentRecord {
    public int uid;
    public String packageName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PendingIntentRecord)) return false;
        PendingIntentRecord that = (PendingIntentRecord) o;
        return uid == that.uid &&
                Objects.equals(packageName, that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, packageName);
    }
}
