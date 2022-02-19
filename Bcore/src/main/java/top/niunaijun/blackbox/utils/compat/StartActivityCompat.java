package top.niunaijun.blackbox.utils.compat;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by Milk on 4/9/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class StartActivityCompat {
    private static int index = 0;
    private static int appThreadIndex;
    private static int callingPageIndex;
    private static int callingFeatureIdIndex;
    private static int intentIndex;
    private static int resolvedTypeIndex;
    private static int resultToIndex;
    private static int resultWhoIndex;
    private static int requestCodeIndex;
    private static int flagsIndex;
    private static int profilerInfoIndex;
    private static int optionsIndex;

    static {
        if (BuildCompat.isR()) {
            appThreadIndex = index++;
            callingPageIndex = index++;
            callingFeatureIdIndex = index++;
            intentIndex = index++;
            resolvedTypeIndex = index++;
            resultToIndex = index++;
            resultWhoIndex = index++;
            requestCodeIndex = index++;
            flagsIndex = index++;
            profilerInfoIndex = index++;
            optionsIndex = index++;
        } else {
            appThreadIndex = index++;
            callingPageIndex = index++;
            intentIndex = index++;
            resolvedTypeIndex = index++;
            resultToIndex = index++;
            resultWhoIndex = index++;
            requestCodeIndex = index++;
            flagsIndex = index++;
            profilerInfoIndex = index++;
            optionsIndex = index++;
        }
    }

    public static Object getIApplicationThread(Object[] args) {
        if (args == null || args.length < appThreadIndex) {
            return null;
        }
        return args[appThreadIndex];
    }

    public static String getCallingPackage(Object[] args) {
        if (args == null || args.length < callingPageIndex) {
            return null;
        }
        return (String) args[callingPageIndex];
    }

    public static Intent getIntent(Object[] args) {
        if (args == null || args.length < intentIndex) {
            return null;
        }
        return (Intent) args[intentIndex];
    }

    public static String getResolvedType(Object[] args) {
        if (args == null || args.length < resolvedTypeIndex) {
            return null;
        }
        return (String) args[resolvedTypeIndex];
    }

    public static IBinder getResultTo(Object[] args) {
        if (args == null || args.length < resultToIndex) {
            return null;
        }
        return (IBinder) args[resultToIndex];
    }

    public static String getResultWho(Object[] args) {
        if (args == null || args.length < resultWhoIndex) {
            return null;
        }
        return (String) args[resultWhoIndex];
    }

    public static int getRequestCode(Object[] args) {
        if (args == null || args.length < requestCodeIndex) {
            return -1;
        }
        return (int) args[requestCodeIndex];
    }

    public static int getFlags(Object[] args) {
        if (args == null || args.length < flagsIndex) {
            return -1;
        }
        return (int) args[flagsIndex];
    }

    public static Object getProfilerInfo(Object[] args) {
        if (args == null || args.length < profilerInfoIndex) {
            return null;
        }
        return args[profilerInfoIndex];
    }

    public static Bundle getOptions(Object[] args) {
        if (args == null || args.length < optionsIndex) {
            return null;
        }
        return (Bundle) args[optionsIndex];
    }


    public static int getAppThreadIndex() {
        return appThreadIndex;
    }

    public static void setAppThreadIndex(int appThreadIndex) {
        StartActivityCompat.appThreadIndex = appThreadIndex;
    }

    public static int getCallingPageIndex() {
        return callingPageIndex;
    }

    public static void setCallingPageIndex(int callingPageIndex) {
        StartActivityCompat.callingPageIndex = callingPageIndex;
    }

    public static int getIntentIndex() {
        return intentIndex;
    }

    public static void setIntentIndex(int intentIndex) {
        StartActivityCompat.intentIndex = intentIndex;
    }

    public static int getResolvedTypeIndex() {
        return resolvedTypeIndex;
    }

    public static void setResolvedTypeIndex(int resolvedTypeIndex) {
        StartActivityCompat.resolvedTypeIndex = resolvedTypeIndex;
    }

    public static int getResultToIndex() {
        return resultToIndex;
    }

    public static void setResultToIndex(int resultToIndex) {
        StartActivityCompat.resultToIndex = resultToIndex;
    }

    public static int getResultWhoIndex() {
        return resultWhoIndex;
    }

    public static void setResultWhoIndex(int resultWhoIndex) {
        StartActivityCompat.resultWhoIndex = resultWhoIndex;
    }

    public static int getRequestCodeIndex() {
        return requestCodeIndex;
    }

    public static void setRequestCodeIndex(int requestCodeIndex) {
        StartActivityCompat.requestCodeIndex = requestCodeIndex;
    }

    public static int getFlagsIndex() {
        return flagsIndex;
    }

    public static void setFlagsIndex(int flagsIndex) {
        StartActivityCompat.flagsIndex = flagsIndex;
    }

    public static int getProfilerInfoIndex() {
        return profilerInfoIndex;
    }

    public static void setProfilerInfoIndex(int profilerInfoIndex) {
        StartActivityCompat.profilerInfoIndex = profilerInfoIndex;
    }

    public static int getOptionsIndex() {
        return optionsIndex;
    }

    public static void setOptionsIndex(int optionsIndex) {
        StartActivityCompat.optionsIndex = optionsIndex;
    }
}
