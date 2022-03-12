package black.android.content.pm;

import android.content.pm.PackageParser.Package;

import java.io.File;

import top.niunaijun.blackreflection.annotation.BClassName;
import top.niunaijun.blackreflection.annotation.BConstructor;
import top.niunaijun.blackreflection.annotation.BMethod;

@BClassName("android.content.pm.PackageParser")
public interface PackageParserLollipop {
    @BConstructor
    android.content.pm.PackageParser _new();

//    @BStaticMethod
//    ActivityInfo generateActivityInfo(PackageParser.Activity service, int flag, PackageUserState state, int userId);
//
//    @BStaticMethod
//    ApplicationInfo generateApplicationInfo(Package p, int flags, PackageUserState state);
//
//    @BStaticMethod
//    PackageInfo generatePackageInfo(Package p, int[] ints, int int1, long long1, long );
//
//    @BStaticMethod
//    ProviderInfo generateProviderInfo(PackageParser.Provider service, int flag, PackageUserState state, int userId);
//
//    @BStaticMethod
//    ServiceInfo generateServiceInfo(PackageParser.Service service, int flag, PackageUserState state, int userId);

    @BMethod
    void collectCertificates(Package p, int flags);

    @BMethod
    Package parsePackage(File File0, int flags);
}
