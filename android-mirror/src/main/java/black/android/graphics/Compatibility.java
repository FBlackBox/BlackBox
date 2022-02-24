package black.android.graphics;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BStaticMethod;

/**
 * Created by BlackBox on 2022/2/24.
 */
@BClassName("android.graphics.Compatibility")
public interface Compatibility {
    @BStaticMethod
    void setTargetSdkVersion(int targetSdkVersion);
}
