package top.canyie.pine;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import top.canyie.pine.callback.MethodHook;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The bridge class provides main APIs for you.
 * @author canyie
 */
@SuppressWarnings("WeakerAccess")
public final class Pine {
    private static final String TAG = "Pine";
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final int ARCH_ARM = 1;
    private static final int ARCH_ARM64 = 2;
    private static final int ARCH_X86 = 3;
    private static volatile boolean initialized;
    private static final Map<String, Method> sBridgeMethods = new HashMap<>(8, 2f);
    private static final Map<Long, HookRecord> sHookRecords = new ConcurrentHashMap<>();
    private static final Object sHookLock = new Object();
    private static int arch;
    private static volatile int hookMode = HookMode.AUTO;
    private static HookHandler sHookHandler = new HookHandler() {
        @Override
        public MethodHook.Unhook handleHook(HookRecord hookRecord, MethodHook hook, int modifiers,
                                            boolean newMethod, boolean canInitDeclaringClass) {
            if (newMethod)
                hookNewMethod(hookRecord, modifiers, canInitDeclaringClass);

            if (hook == null) {
                // This can only happens when the up handler pass null manually,
                // just return null and let the up to do remaining everything
                return null;
            }
            hookRecord.addCallback(hook);
            return hook.new Unhook(hookRecord);
        }

        @Override public void handleUnhook(HookRecord hookRecord, MethodHook hook) {
            hookRecord.removeCallback(hook);
        }
    };

    private static HookListener sHookListener;

    private Pine() {
        throw new RuntimeException("Use static methods");
    }

    /**
     * Initialize the Pine library if not initialized.
     */
    public static void ensureInitialized() {
        if (initialized) return;
        synchronized (Pine.class) {
            if (initialized) return;
            initialize();
            initialized = true;
        }
    }

    /**
     * Check whether Pine library is initialized.
     * @return {@code true} If Pine is initialized, {@code false} otherwise.
     */
    public static boolean isInitialized() {
        return initialized;
    }

    // https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:core/core/src/main/java/androidx/core/os/BuildCompat.java;l=49;drc=f8ab4c3030c3fbadca32a9593c522c89a9f2cadf
    private static boolean isAtLeastPreReleaseCodename(String codename) {
        final String buildCodename = Build.VERSION.CODENAME.toUpperCase(Locale.ROOT);

        // Special case "REL", which means the build is not a pre-release build.
        if ("REL".equals(buildCodename)) {
            return false;
        }

        return buildCodename.compareTo(codename.toUpperCase(Locale.ROOT)) >= 0;
    }

    @SuppressLint("ObsoleteSdkInt") private static void initialize() {
        int sdkLevel = PineConfig.sdkLevel;
        if (sdkLevel < Build.VERSION_CODES.KITKAT)
            throw new RuntimeException("Unsupported android sdk level " + sdkLevel);
        else if (sdkLevel > Build.VERSION_CODES.R) {
            Log.w(TAG, "Android version too high, not tested now...");
            if (sdkLevel >= Build.VERSION_CODES.S_V2 && isAtLeastPreReleaseCodename("Tiramisu")) {
                // Android 13 (Tiramisu) Preview
                sdkLevel = Build.VERSION_CODES.S_V2 + 1;
            } else if (sdkLevel == Build.VERSION_CODES.S && isAtLeastPreReleaseCodename("Sv2")) {
                // Android 12.1 (SL) Preview
                sdkLevel = Build.VERSION_CODES.S_V2;
            }
        }

        String vmVersion = System.getProperty("java.vm.version");
        if (vmVersion == null || !vmVersion.startsWith("2"))
            throw new RuntimeException("Only supports ART runtime");

        try {
            LibLoader libLoader = PineConfig.libLoader;
            if (libLoader != null) libLoader.loadLib();

            init0(sdkLevel, PineConfig.debug, PineConfig.debuggable, PineConfig.antiChecks,
                    PineConfig.disableHiddenApiPolicy, PineConfig.disableHiddenApiPolicyForPlatformDomain);
            initBridgeMethods();

            if (PineConfig.useFastNative && sdkLevel >= Build.VERSION_CODES.LOLLIPOP)
                enableFastNative();
        } catch (Exception e) {
            throw new RuntimeException("Pine init error", e);
        }
    }

    private static void initBridgeMethods() {
        try {
            String entryClassName;
            Class<?>[] paramTypes;

            if (arch == ARCH_ARM64) {
                entryClassName = "top.canyie.pine.entry.Arm64Entry";
                paramTypes = new Class<?>[] {long.class, long.class, long.class,
                        long.class, long.class, long.class, long.class};
            } else if (arch == ARCH_ARM) {
                entryClassName = "top.canyie.pine.entry.Arm32Entry";
                paramTypes = new Class<?>[] {int.class, int.class, int.class};
            } else if (arch == ARCH_X86) {
                entryClassName = "top.canyie.pine.entry.X86Entry";
                paramTypes = new Class<?>[] {int.class, int.class, int.class};
            } else throw new RuntimeException("Unexpected arch " + arch);

            // Use Class.forName() to ensure entry class is initialized.
            Class<?> entryClass = Class.forName(entryClassName, true, Pine.class.getClassLoader());

            String[] bridgeMethodNames = {"voidBridge", "intBridge", "longBridge", "doubleBridge", "floatBridge",
                    "booleanBridge", "byteBridge", "charBridge", "shortBridge", "objectBridge"};

            for (String bridgeMethodName : bridgeMethodNames) {
                Method bridge = entryClass.getDeclaredMethod(bridgeMethodName, paramTypes);
                bridge.setAccessible(true);
                sBridgeMethods.put(bridgeMethodName, bridge);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to init bridge methods", e);
        }
    }

    /**
     * Set how Pine to hook method.
     * @param newHookMode One of {@code Pine.HookMode.AUTO}, {@code Pine.HookMode.INLINE} or
     *                    {@code Pine.HookMode.REPLACEMENT}.
     * @throws IllegalArgumentException If the {@code newHookMode} is not one of
     *                     {@code Pine.HookMode.AUTO}, {@code Pine.HookMode.INLINE} or
     *                     {@code Pine.HookMode.REPLACEMENT}.
     * @see Pine.HookMode
     */
    public static void setHookMode(int newHookMode) {
        if (newHookMode < HookMode.AUTO || newHookMode > HookMode.REPLACEMENT)
            throw new IllegalArgumentException("Illegal hookMode " + newHookMode);
        hookMode = newHookMode;
    }

    /**
     * Set a handler that will be used when Pine hooking a method.
     * Internal API, use {@code HookListener} instead if you just want to be notified when hooking.
     * Note that only one handler can exist at a time, so setting a new handler will override
     * the old one. Save the old handler if yours don't know how to properly hook a method!
     * @param h The handler you want to register. Cannot be null.
     * @throws NullPointerException If the handler you given is null.
     * @see HookHandler
     */
    public static void setHookHandler(HookHandler h) {
        if (h == null) throw new NullPointerException("h == null");
        sHookHandler = h;
    }

    /**
     * Get the handler that will be used when Pine hooking a method.
     * Internal API, use the returned handler only when you are implementing your own handler
     * and want to continue hooking. Do NOT use the returned hook handler if you just want to hook.
     * @return The handler that will be used when hooking. Will never be null.
     * @see HookHandler
     */
    public static HookHandler getHookHandler() {
        return sHookHandler;
    }

    /**
     * Set a listener that will be called before/after hooking a method.
     * @param l The listener you want to set. Can be null.
     * @see HookListener
     */
    public static void setHookListener(HookListener l) {
        sHookListener = l;
    }

    /**
     * Get the listener that will be called before/after hooking method.
     * @return The listener that will be called before/after hooking method, or {@code null} if not set.
     * @see HookListener
     */
    public static HookListener getHookListener() {
        return sHookListener;
    }

    /**
     * Return whether the system is 64-bit.
     * Note that this method will initialize Pine library if uninitialized.
     * @return {@code true} if the system is 64-bit, {@code false} otherwise.
     */
    public static boolean is64Bit() {
        ensureInitialized();
        return arch == ARCH_ARM64;
    }

    /**
     * Register a hook that will be invoked when the {@code method} is invoked.
     * Note that this will initialize Pine library if uninitialized.
     * @param method The method want to control.
     * @param callback The callback that will be invoked when the {@code method} is invoked.
     * @return The unhook for this hook. Call {@code unhook.unhook()} to unregister the hook.
     * @throws NullPointerException If {@code method} or {@code callback} is null.
     * @throws IllegalArgumentException If {@code method} cannot be hooked, such as abstract method.
     */
    public static MethodHook.Unhook hook(Member method, MethodHook callback) {
        return hook(method, callback, true);
    }

    /**
     * Register a hook that will be invoked when the {@code method} is invoked.
     * Note that this will initialize Pine library if uninitialized.
     * @param method The method want to control.
     * @param callback The callback that will be invoked when the {@code method} is invoked.
     * @param canInitDeclaringClass {@code true} if initializing the class is allowed, {@code false} otherwise.
     * @return The unhook for this hook. Call {@code unhook.unhook()} to unregister the hook.
     * @throws NullPointerException If {@code method} or {@code callback} is null.
     * @throws IllegalArgumentException If {@code method} cannot be hooked, such as abstract method.
     */
    public static MethodHook.Unhook hook(Member method, MethodHook callback, boolean canInitDeclaringClass) {
        if (PineConfig.debug)
            Log.d(TAG, "Hooking method " + method + " with callback " + callback);

        if (method == null) throw new NullPointerException("method == null");
        if (callback == null) throw new NullPointerException("callback == null");

        int modifiers = method.getModifiers();
        if (method instanceof Method) {
            if (Modifier.isAbstract(modifiers))
                throw new IllegalArgumentException("Cannot hook abstract methods: " + method);
            ((Method) method).setAccessible(true);
        } else if (method instanceof Constructor) {
            if (Modifier.isStatic(modifiers)) // TODO: We really cannot hook this?
                throw new IllegalArgumentException("Cannot hook class initializer: " + method);
            ((Constructor<?>) method).setAccessible(true);
        } else {
            throw new IllegalArgumentException("Only methods and constructors can be hooked: " + method);
        }

        ensureInitialized();

        HookListener hookListener = sHookListener;

        if (hookListener != null)
            hookListener.beforeHook(method, callback);

        long artMethod = getArtMethod(method);
        HookRecord hookRecord;
        boolean newMethod = false;

        synchronized (sHookLock) {
            hookRecord = sHookRecords.get(artMethod);
            if (hookRecord == null) {
                newMethod = true;
                hookRecord = new HookRecord(method, artMethod);
                sHookRecords.put(artMethod, hookRecord);
            }
        }

        MethodHook.Unhook unhook = sHookHandler.handleHook(hookRecord, callback, modifiers,
                newMethod, canInitDeclaringClass);

        if (hookListener != null)
            hookListener.afterHook(method, unhook);

        return unhook;
    }

    static void hookNewMethod(HookRecord hookRecord, int modifiers, boolean canInitDeclaringClass) {
        Member method = hookRecord.target;
        boolean isInlineHook;
        if (hookMode == HookMode.AUTO) {
            // On Android N or lower, entry_point_from_compiled_code_ may be hard-coded in the machine code
            // (sharpening optimization), entry replacement will most likely not take effect,
            // so we prefer to use inline hook; And on Android O+, this optimization is not performed,
            // so we prefer a more stable entry replacement mode.

            isInlineHook = PineConfig.sdkLevel < Build.VERSION_CODES.O;
        } else {
            isInlineHook = hookMode == HookMode.INLINE;
        }

        long thread = currentArtThread0();
        if ((hookRecord.isStatic = Modifier.isStatic(modifiers)) && canInitDeclaringClass) {
            resolve((Method) method);
            if (PineConfig.sdkLevel >= Build.VERSION_CODES.Q) {
                // Android R has a new class state called "visibly initialized",
                // and FixupStaticTrampolines will be called after class was initialized.
                // The entry point will be reset. Make this class be visibly initialized before hook
                // Note: this feature does not exist on official Android Q,
                // but some weird ROMs cherry-pick this commit to these Android Q ROMs
                // https://github.com/crdroidandroid/android_art/commit/ef76ced9d2856ac988377ad99288a357697c4fa2
                makeClassesVisiblyInitialized(thread);
            }
        }

        Class<?> declaring = method.getDeclaringClass();

        final boolean jni = Modifier.isNative(modifiers);
        final boolean proxy = Proxy.isProxyClass(declaring);

        // Only try compile target method when trying inline hook.
        if (isInlineHook) {
            // Cannot compile native or proxy methods.
            if (!(jni || proxy)) {
                boolean compiled = compile0(thread, method);
                if (!compiled) {
                    Log.w(TAG, "Cannot compile the target method, force replacement mode.");
                    isInlineHook = false;
                }
            } else {
                isInlineHook = false;
            }
        }

        String bridgeMethodName;
        if (method instanceof Method) {
            hookRecord.paramTypes = ((Method) method).getParameterTypes();
            Class<?> returnType = ((Method) method).getReturnType();
            bridgeMethodName = returnType.isPrimitive() ? returnType.getName() + "Bridge" : "objectBridge";
        } else {
            hookRecord.paramTypes = ((Constructor<?>) method).getParameterTypes();
            // Constructor is actually a method named <init> and its return type is void.
            bridgeMethodName = "voidBridge";
        }

        hookRecord.paramNumber = hookRecord.paramTypes.length;

        Method bridge = sBridgeMethods.get(bridgeMethodName);
        if (bridge == null)
            throw new AssertionError("Cannot find bridge method for " + method);

        Method backup = hook0(thread, declaring, method, bridge, isInlineHook, jni, proxy);

        if (backup == null)
            throw new RuntimeException("Failed to hook method " + method);

        backup.setAccessible(true);
        hookRecord.backup = backup;
    }

    private static void resolve(Method method) {
        Object[] badArgs;
        if (method.getParameterTypes().length > 0) {
            badArgs = null;
        } else {
            badArgs = new Object[1];
        }
        try {
            method.invoke(null, badArgs);
        } catch (IllegalArgumentException e) {
            // Only should happen. We used the unmatched parameter array.
            return;
        } catch (Exception e) {
            throw new RuntimeException("Unknown exception thrown when resolve static method.", e);
        }
        throw new RuntimeException("No IllegalArgumentException thrown when resolve static method.");
    }

    /**
     * Return whether the given method has been hooked before.
     * Note that once the method is hooked, this will return {@code true}, even it has been unhooked.
     * @param method the method you want to check.
     * @return {@code true} if the method has been hooked before, {@code false} otherwise.
     */
    public static boolean isHooked(Member method) {
        if (!(method instanceof Method || method instanceof Constructor))
            throw new IllegalArgumentException("Only methods and constructors can be hooked: " + method);
        return sHookRecords.containsKey(getArtMethod(method));
    }

    public static HookRecord getHookRecord(long artMethod) {
        HookRecord result = sHookRecords.get(artMethod);
        if (result == null) {
            throw new AssertionError("No HookRecord found for ArtMethod pointer 0x" + Long.toHexString(artMethod));
        }
        return result;
    }

    public static Object getObject(long thread, long address) {
        if (address == 0) return null;
        return getObject0(thread, address);
    }

    public static long getAddress(long thread, Object o) {
        if (o == null) return 0;
        return getAddress0(thread, o);
    }

    static Object callBackupMethod(Member origin, Method backup, Object thisObject, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (PineConfig.sdkLevel >= Build.VERSION_CODES.N) {
            // On Android 7.0+, java.lang.Class object is movable and may cause crash when
            // invoke backup method, so we update declaring_class when invoke backup method.
            Class<?> declaring = origin.getDeclaringClass();
            updateDeclaringClass(origin, backup);
            //Runtime.getRuntime().gc();
            Object result = backup.invoke(thisObject, args);

            // Explicit use declaring_class object to ensure it has reference on stack
            // and avoid being moved by gc.
            declaring.getClass();
            return result;
        } else {
            return backup.invoke(thisObject, args);
        }
    }

    /**
     * Invoke the original implementation of the given method.
     * If the method is not hooked, just invoke it directly.
     * @param method The method you want to invoke its original implementation.
     * @param thisObject  The object the underlying method is invoked from
     * @param args The arguments used for the method call
     * @return The result of the original method
     * @throws NullPointerException If the given method is null.
     * @throws IllegalAccessException Should never happen
     * @throws InvocationTargetException If the underlying method throws an exception.
     * @throws IllegalArgumentException If the method cannot be invoked with the given args.
     * @see CallFrame#invokeOriginalMethod()
     * @see CallFrame#invokeOriginalMethod(Object, Object...)
     * @see Method#invoke(Object, Object...)
     */
    public static Object invokeOriginalMethod(Member method, Object thisObject, Object... args) throws IllegalAccessException, InvocationTargetException {
        if (method == null) throw new NullPointerException("method == null");
        if (method instanceof Method) {
            ((Method) method).setAccessible(true);
        } else if (method instanceof Constructor) {
            ((Constructor<?>) method).setAccessible(true);
        } else {
            throw new IllegalArgumentException("method must be of type Method or Constructor");
        }

        HookRecord hookRecord = sHookRecords.get(getArtMethod(method));
        if (hookRecord == null) {
            // Not hooked
            if (method instanceof Constructor) {
                if (thisObject != null)
                    throw new IllegalArgumentException(
                            "Cannot invoke a not hooked Constructor with a non-null receiver");
                try {
                    ((Constructor<?>) method).newInstance(args);
                    return null;
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException("invalid Constructor", e);
                }
            } else {
                return ((Method) method).invoke(thisObject, args);
            }
        }

        if (hookRecord.backup == null) {
            // Pending, we need to make the declaring class initialized
            // the backup will be set in FixupStaticTrampolines or MarkClassInitialized
            // I think we don't need makeClassesVisiblyInitialized here
            assert method instanceof Method;
            resolve((Method) method);
//            if (PineConfig.sdkLevel >= 30) {
//                makeClassesVisiblyInitialized(thread);
//            }
        }

        return callBackupMethod(hookRecord.target, hookRecord.backup, thisObject, args);
    }

    /**
     * Compile the given method. Note that this will always do nothing on Android R+,
     * and may crash if JIT compilation is not allowed the current process,
     * like some system process. Make sure you really need this before use!
     * @param method The method you want to compile.
     * @return {@code true} if successfully compile the method, {@code false} otherwise.
     * @throws NullPointerException If the given method is null.
     * @throws IllegalArgumentException If the given method cannot be compiled.
     */
    public static boolean compile(Member method) {
        int modifiers = method.getModifiers();
        Class<?> declaring = method.getDeclaringClass();

        if (!(method instanceof Method || method instanceof Constructor))
            throw new IllegalArgumentException("Only methods and constructors can be compiled: " + method);

        if (Modifier.isAbstract(modifiers))
            throw new IllegalArgumentException("Cannot compile abstract methods: " + method);

        if (Modifier.isNative(modifiers) || Proxy.isProxyClass(declaring)) {
            // Cannot compile native methods and proxy methods
            return false;
        }

        ensureInitialized();
        return compile0(currentArtThread0(), method);
    }

    /**
     * Decompile the given method. Force the given method executes by interpreter.
     * This may be very useful when hooking a method that is inlined into caller.
     * Note that in that case, you should decompile the caller rather than the callee.
     * @param method The method you want to decompile.
     * @param disableJit {@code true} if you want to prevent the method gets JIT compiled again
     * @return {@code true} If successfully decompiled this method, {@code false} otherwise.
     */
    public static boolean decompile(Member method, boolean disableJit) {
        int modifiers = method.getModifiers();
        Class<?> declaring = method.getDeclaringClass();

        if (!(method instanceof Method || method instanceof Constructor))
            throw new IllegalArgumentException("Only methods and constructors can be decompiled: " + method);

        if (Modifier.isAbstract(modifiers))
            throw new IllegalArgumentException("Cannot decompile abstract methods: " + method);

        if (Proxy.isProxyClass(declaring)) {
            // Proxy methods entry is fixed at art_quick_proxy_invoke_handler.
            return false;
        }
        ensureInitialized();
        return decompile0(method, disableJit);
    }

    /**
     * Prevent any JIT inlining in the current process. DOES NOT WORK FOR NOW.
     * @return {@code true} if successfully disabled jit inlining, {@code false} otherwise.
     */
    public static boolean disableJitInline() {
        if (PineConfig.sdkLevel < Build.VERSION_CODES.N) {
            // No JIT.
            return false;
        }
        ensureInitialized();
        return disableJitInline0();
    }

    /**
     * Set whether we can manually JIT compile a method.
     * @param allowed {@code true} if allowed, {@code false} otherwise.
     */
    public static void setJitCompilationAllowed(boolean allowed) {
        if (PineConfig.sdkLevel < Build.VERSION_CODES.N) {
            // No JIT.
            return;
        }
        ensureInitialized();
        setJitCompilationAllowed0(allowed);
    }

    /**
     * Prevent art recording our method call profile. This can prevent methods being AOT-compiled,
     * so can avoid some hook invalidation caused by optimization, but will cause performance problems.
     * @return {@code true} if we successfully disabled profile saver, {@code false} otherwise.
     */
    public static boolean disableProfileSaver() {
        if (PineConfig.sdkLevel < Build.VERSION_CODES.N) return false;
        ensureInitialized();
        return disableProfileSaver0();
    }

    /**
     * Set whether the current process is debuggable, like {@code PineConfig#debuggable} but allows
     * you set the value after Pine library is initialized.
     * @param debuggable whether the current process is debuggable.
     * @see PineConfig#debuggable
     */
    public static void setDebuggable(boolean debuggable) {
        if (!initialized) {
            synchronized (Pine.class) {
                if (!initialized) {
                    PineConfig.debuggable = debuggable;
                    initialize();
                    initialized = true;
                    return;
                }
            }
        }
        PineConfig.debuggable = debuggable;
        setDebuggable0(debuggable);
    }

    /**
     * Disable the hidden api restriction policy in the current process.
     * @param application whether the restriction policy for application domain should be disabled
     * @param platform whether the restriction policy for platform domain should be disabled
     * @see PineConfig#disableHiddenApiPolicy
     * @see PineConfig#disableHiddenApiPolicyForPlatformDomain
     */
    public static void disableHiddenApiPolicy(boolean application, boolean platform) {
        if (initialized) {
            disableHiddenApiPolicy0(application, platform);
        } else {
            PineConfig.disableHiddenApiPolicy = application;
            PineConfig.disableHiddenApiPolicyForPlatformDomain = platform;
            ensureInitialized();
        }
    }

    public static Object handleCall(HookRecord hookRecord, Object thisObject, Object[] args)
            throws Throwable {
        // WARNING: DO NOT print thisObject or args, else the toString() method will be called on it
        // At this time the object may not "ready"
        if (PineConfig.debug)
            /*Log.d(TAG, "handleCall: target=" + hookRecord.target + " thisObject=" +
                    thisObject + " args=" + Arrays.toString(args));*/
            Log.d(TAG, "handleCall for method " + hookRecord.target);

        if (PineConfig.disableHooks || hookRecord.emptyCallbacks()) {
            try {
                return callBackupMethod(hookRecord.target, hookRecord.backup, thisObject, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        CallFrame callFrame = new CallFrame(hookRecord, thisObject, args);
        MethodHook[] callbacks = hookRecord.getCallbacks();

        // call before callbacks
        int beforeIdx = 0;
        do {
            MethodHook callback = callbacks[beforeIdx];
            try {
                callback.beforeCall(callFrame);
            } catch (Throwable e) {
                Log.e(TAG, "Unexpected exception occurred when calling " + callback.getClass().getName() + ".beforeCall()", e);
                // reset result (ignoring what the unexpectedly exiting callback did)
                callFrame.resetResult();
                continue;
            }
            if (callFrame.returnEarly) {
                // skip remaining "before" callbacks and corresponding "after" callbacks
                beforeIdx++;
                break;
            }
        } while (++beforeIdx < callbacks.length);

        // call original method if not requested otherwise
        if (!callFrame.returnEarly) {
            try {
                callFrame.setResult(callFrame.invokeOriginalMethod());
            } catch (InvocationTargetException e) {
                callFrame.setThrowable(e.getTargetException());
            }
        }

        // call after callbacks
        int afterIdx = beforeIdx - 1;
        do {
            MethodHook callback = callbacks[afterIdx];
            Object lastResult = callFrame.getResult();
            Throwable lastThrowable = callFrame.getThrowable();
            try {
                callback.afterCall(callFrame);
            } catch (Throwable e) {
                Log.e(TAG, "Unexpected exception occurred when calling " + callback.getClass().getName() + ".afterCall()", e);

                // reset to last result (ignoring what the unexpectedly exiting callback did)
                if (lastThrowable == null)
                    callFrame.setResult(lastResult);
                else
                    callFrame.setThrowable(lastThrowable);
            }
        } while (--afterIdx >= 0);

        // return
        if (callFrame.hasThrowable())
            throw callFrame.getThrowable();
        else
            return callFrame.getResult();
    }

    /**
     * Print a log with "Pine" tag if {@code PineConfig#debug} is set, or do nothing.
     * @param message The message you want print.
     */
    public static void log(String message) {
        if (PineConfig.debug) {
            Log.i(TAG, message);
        }
    }

    /**
     * Print a log with "Pine" tag if {@code PineConfig#debug} is set, or do nothing.
     * @param fmt The message format you want print.
     * @param args The args used to format {@code format}.
     * @see String#format(String, Object...)
     */
    public static void log(String fmt, Object... args) {
        if (PineConfig.debug) {
            Log.i(TAG, String.format(fmt, args));
        }
    }

    private static native void init0(int androidVersion, boolean debug, boolean debuggable,
                                     boolean antiChecks, boolean disableHiddenApiPolicy,
                                     boolean disableHiddenApiPolicyForPlatformDomain);

    private static native void enableFastNative();

    private static native long getArtMethod(Member method);

    private static native Method hook0(long thread, Class<?> declaring, Member target, Method bridge,
                                       boolean isInlineHook, boolean jni, boolean proxy);

    private static native boolean compile0(long thread, Member method);

    private static native boolean decompile0(Member method, boolean disableJit);

    private static native boolean disableJitInline0();

    private static native void setJitCompilationAllowed0(boolean allowed);

    private static native boolean disableProfileSaver0();

    private static native Object getObject0(long thread, long address);

    private static native long getAddress0(long thread, Object o);

    public static native void getArgsArm32(int extras, int sp, int[] crOut, int[] stack, float[] fpOut);

    public static native void getArgsArm64(long extras, long sp, boolean[] typeWides, long[] crOut, long[] stack, double[] fpOut);

    public static native void getArgsX86(int extras, int[] out, int ebx);

    private static native void updateDeclaringClass(Member origin, Method backup);

    public static native long currentArtThread0();

    private static native void setDebuggable0(boolean debuggable);

    private static native void disableHiddenApiPolicy0(boolean application, boolean platform);

    private static native void makeClassesVisiblyInitialized(long thread);

    public static native long cloneExtras(long origin);

    /**
     * Interface definition for a callback to be invoked when a method is hooked.
     */
    public interface HookListener {
        /**
         * Invoked before a method hooking.
         * @param method The method that will be hooked
         * @param callback The hook that will be registered
         */
        void beforeHook(Member method, MethodHook callback);

        /**
         * Invoke after a method hooking.
         * @param method The hooked method
         * @param unhook The registered hook
         */
        void afterHook(Member method, MethodHook.Unhook unhook);
    }

    /**
     * Interface definition for an implementation to be invoked when load our native library (libpine.so)
     */
    public interface LibLoader {
        /**
         * Will be invoked when our native library (libpine.so) needs to be loaded.
         */
        void loadLib();
    }

    /**
     * Enum definition for how to hook method.
     * @see Pine#setHookMode(int)
     */
    public interface HookMode {
        /**
         * AUTO: Let Pine itself to decide how to hook. The default value.
         */
        int AUTO = 0;

        /**
         * INLINE: Use inline hook (overwrite the first few instructions to hook) first.
         * If the method cannot be hooked in this mode, fallback to {@code REPLACEMENT}.
         */
        int INLINE = 1;

        /**
         * REPLACEMENT: Always change entry point of the method to hook it.
         */
        int REPLACEMENT = 2;
    }

    /**
     * Internal API. Implement the hook logic by implementing this interface.
     * @see Pine#setHookHandler(HookHandler)
     */
    public interface HookHandler {
        MethodHook.Unhook handleHook(HookRecord hookRecord, MethodHook hook, int modifiers,
                                     boolean newMethod, boolean canInitDeclaringClass);
        void handleUnhook(HookRecord hookRecord, MethodHook hook);
    }

    /**
     * Internal API. Record hook info about a method.
     */
    public static final class HookRecord {
        public final Member target;
        public final long artMethod;
        public Method backup;
        public boolean isStatic;
        public int paramNumber;
        public Class<?>[] paramTypes;
        private Set<MethodHook> callbacks = new HashSet<>();

        public HookRecord(Member target, long artMethod) {
            this.target = target;
            this.artMethod = artMethod;
        }

        public synchronized void addCallback(MethodHook callback) {
            callbacks.add(callback);
        }

        public synchronized void removeCallback(MethodHook callback) {
            callbacks.remove(callback);
        }

        public synchronized boolean emptyCallbacks() {
            return callbacks.isEmpty();
        }

        public synchronized MethodHook[] getCallbacks() {
            return callbacks.toArray(new MethodHook[callbacks.size()]);
        }

        public boolean isPending() {
            return backup == null;
        }
    }

    /**
     * A Holder that holds the method, "this" object, arguments, result or exception of a method call.
     */
    public static class CallFrame {
        /**
         * The calling method.
         */
        public final Member method;

        /**
         * The "this" object of this call, {@code null} if executing a static method.
         * Change it in {@code beforeCall} to set new object as "this" when calling original method.
         */
        public Object thisObject;

        /**
         * The arguments passed to the method in this call. Will never be null.
         * Change it or its value in {@code beforeCall} to change arguments when calling original method.
         */
        public Object[] args;
        private Object result;
        private Throwable throwable;
        /* package */ boolean returnEarly;
        private HookRecord hookRecord;

        public CallFrame(HookRecord hookRecord, Object thisObject, Object[] args) {
            this.hookRecord = hookRecord;
            this.method = hookRecord.target;
            this.thisObject = thisObject;
            this.args = args;
        }

        /**
         * Get the result that will be returned in this method call.
         * @return The result that will be returned in this method call
         */
        public Object getResult() {
            return result;
        }

        /**
         * Set a result that will be returned in this method call.
         * If you call it {@code beforeCall}, the original method call will be prevented, and next
         * hooks will not be called.
         * @param result The return value you want to set.
         */
        public void setResult(Object result) {
            this.result = result;
            this.throwable = null;
            this.returnEarly = true;
        }

        /**
         * Like {@link CallFrame#setResult(Object)} but only set the return value if no exception will be thrown.
         * @param result The return value you want to set.
         */
        public void setResultIfNoException(Object result) {
            if (this.throwable == null) {
                this.result = result;
                this.returnEarly = true;
            }
        }

        /**
         * Get the exception that will be thrown in this method call.
         * @return The exception that will be thrown in this method call.
         */
        public Throwable getThrowable() {
            return throwable;
        }

        /**
         * Return whether an exception will be thrown as the result of this method call.
         * @return {@code true} If there is an exception will be thrown, {@code false} otherwise.
         */
        public boolean hasThrowable() {
            return throwable != null;
        }

        /**
         * Set the exception that will be thrown in this method call.
         * If you call it {@code beforeCall}, the original method call will be prevented, and next
         * hooks will not be called.
         * @param throwable The exception you want to throw.
         */
        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
            this.result = null;
            this.returnEarly = true;
        }

        /**
         * Like {@link CallFrame#getResult()} but throwing an exception if there is an exception set.
         * @return The result of this method call
         * @throws Throwable The exception happened in this method call
         */
        public Object getResultOrThrowable() throws Throwable {
            if (throwable != null)
                throw throwable;
            return result;
        }

        /**
         * Reset any previous result or exception, and allows the original method to be executed.
         */
        public void resetResult() {
            this.result = null;
            this.throwable = null;
            this.returnEarly = false;
        }

        /**
         * Invoke the original implementation of the method with current {@code thisObject} and {@code args}.
         * @return The return value of this method.
         * @throws InvocationTargetException If the original method throws an exception.
         * @throws IllegalAccessException Should never happen
         * @see #invokeOriginalMethod(Object, Object...)
         * @see Pine#invokeOriginalMethod(Member, Object, Object...)
         */
        public Object invokeOriginalMethod() throws InvocationTargetException, IllegalAccessException {
            return callBackupMethod(hookRecord.target, hookRecord.backup, thisObject, args);
        }

        /**
         * Like {@link #invokeOriginalMethod()} but use the passed {@code thisObject} and {@code args}.
         * @param thisObject The "this" object of this method call.
         * @param args The arguments of this method call.
         * @return The return value of this method.
         * @throws InvocationTargetException If the original method throws an exception.
         * @throws IllegalAccessException Should never happen
         * @see #invokeOriginalMethod()
         * @see #invokeOriginalMethod(Member, Object, Object...)
         */
        public Object invokeOriginalMethod(Object thisObject, Object... args) throws InvocationTargetException, IllegalAccessException {
            return callBackupMethod(hookRecord.target, hookRecord.backup, thisObject, args);
        }
    }
}
