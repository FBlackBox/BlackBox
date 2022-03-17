package top.niunaijun.blackbox.core.system.accounts;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;

import black.android.content.res.BRAssetManager;
import top.niunaijun.blackbox.core.system.pm.BPackageManagerService;
import top.niunaijun.blackbox.core.system.pm.BPackageSettings;
import top.niunaijun.blackbox.core.system.pm.PackageManagerCompat;

public class RegisteredServicesParser {

    public XmlResourceParser getParser(Context context, ServiceInfo serviceInfo, String name) {
        Bundle meta = serviceInfo.metaData;
        if (meta != null) {
            int xmlId = meta.getInt(name);
            if (xmlId != 0) {
                try {
                    Resources resources = getResources(context, serviceInfo.applicationInfo);
                    if (resources == null)
                        return null;
                    return resources.getXml(xmlId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Resources getResources(Context context, ApplicationInfo appInfo) {
        return PackageManagerCompat.getResources(context, appInfo);
    }
}