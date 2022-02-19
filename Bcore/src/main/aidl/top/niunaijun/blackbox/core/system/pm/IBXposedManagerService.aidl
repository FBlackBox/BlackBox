// IBXposedManagerService.aidl

package top.niunaijun.blackbox.core.system.pm;

import java.util.List;
import top.niunaijun.blackbox.entity.pm.InstalledModule;

interface IBXposedManagerService {
    boolean isXPEnable();
    void setXPEnable(boolean enable);
    boolean isModuleEnable(String packageName);
    void setModuleEnable(String packageName, boolean enable);
    List<InstalledModule> getInstalledModules();
}