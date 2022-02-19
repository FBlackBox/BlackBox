package black.android.content.pm;


import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BField;

@BClassName("android.content.pm.ApplicationInfo")
public interface ApplicationInfoL {
    @BField
    String primaryCpuAbi();

    @BField
    Integer privateFlags();

    @BField
    String scanPublicSourceDir();

    @BField
    String scanSourceDir();

    @BField
    String secondaryCpuAbi();

    @BField
    String secondaryNativeLibraryDir();

    @BField
    String[] splitPublicSourceDirs();

    @BField
    String[] splitSourceDirs();
}
