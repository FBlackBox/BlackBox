package top.niunaijun.blackbox.fake.provider;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.Uri;

import java.io.File;
import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.utils.compat.BuildCompat;

/**
 * Created by Milk on 4/18/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class FileProviderHandler {

    public static Uri convertFileUri(Context context, Uri uri) {
        if (BuildCompat.isN()) {
            List<ProviderInfo> providers = BActivityThread.getProviders();
            for (ProviderInfo provider : providers) {
                try {
                    File fileForUri = FileProvider.getFileForUri(context, provider.authority, uri);
                    if (fileForUri != null && fileForUri.exists()) {
                        return BlackBoxCore.getBStorageManager().getUriForFile(fileForUri.getAbsolutePath());
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return uri;
    }
}
